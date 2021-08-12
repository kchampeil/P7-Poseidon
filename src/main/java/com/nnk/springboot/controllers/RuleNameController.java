package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.RuleNameDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.services.contracts.IRuleNameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

import static com.nnk.springboot.utils.MessageUtil.formatOutputMessage;

@Slf4j
@Controller
public class RuleNameController {
    // DONE: Inject RuleName service
    private final IRuleNameService ruleNameService;

    @Autowired
    public RuleNameController(IRuleNameService ruleNameService) {
        this.ruleNameService = ruleNameService;
    }


    /**
     * shows the ruleName list
     *
     * @param model current model
     * @return list ruleName page
     */
    @RequestMapping("/ruleName/list")
    public String home(Model model) {
        // DONE: find all RuleName, add to model
        log.info(LogConstants.RULE_NAME_LIST_REQUEST_RECEIVED, model.getAttribute("currentUsername"));
        model.addAttribute("ruleNameAll", ruleNameService.findAllRuleName());

        return "ruleName/list";
    }

    /**
     * shows the add form for ruleName
     *
     * @param model current model
     * @return add ruleName page
     */
    @GetMapping("/ruleName/add")
    public String addRuleForm(Model model) {

        log.info(LogConstants.RULE_NAME_CREATION_FORM_REQUEST_RECEIVED, model.getAttribute("currentUsername"));
        model.addAttribute("ruleName", new RuleNameDTO());

        return "ruleName/add";
    }

    /**
     * creates a ruleName
     *
     * @param ruleName to create
     * @return ruleName/list page if ruleName has been created, otherwise stay at ruleName/add page
     */
    @PostMapping("/ruleName/validate")
    public String validate(@ModelAttribute("ruleName") @Valid RuleNameDTO ruleName,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // DONE: check data valid and save to db, after saving return RuleName list
        log.info(LogConstants.RULE_NAME_CREATION_REQUEST_RECEIVED + ruleName.toString());

        if (result.hasErrors()) {
            log.error(LogConstants.RULE_NAME_CREATION_REQUEST_NOT_VALID + "\n");
            return "ruleName/add";
        }

        try {
            Optional<RuleNameDTO> ruleNameDTOCreated = ruleNameService.createRuleName(ruleName);

            if (ruleNameDTOCreated.isPresent()) {
                log.info(LogConstants.RULE_NAME_CREATION_REQUEST_OK, ruleNameDTOCreated.get().getId(),
                        model.getAttribute("currentUsername"));

                //DONE: after saving return RuleName List
                redirectAttributes.addFlashAttribute("infoMessage",
                        formatOutputMessage("ruleName.add.ok",
                                ruleNameDTOCreated.get().getId().toString()));

                return "redirect:/ruleName/list";
            } else {
                log.error(LogConstants.RULE_NAME_CREATION_REQUEST_KO + " \n");
                redirectAttributes.addFlashAttribute("errorMessage", "ruleName.add.ko");
                return "ruleName/add";
            }

        } catch (Exception exception) {
            log.error(LogConstants.RULE_NAME_CREATION_REQUEST_KO + ": " + exception.getMessage() + " \n");
            redirectAttributes.addFlashAttribute("errorMessage", "ruleName.add.ko");
            return "ruleName/add";
        }
    }


    /**
     * shows the update form for ruleName
     *
     * @param id    of the ruleName to update
     * @param model current model
     * @return ruleName/update page if ruleName has been found, otherwise return to ruleName/list page
     */
    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model,
                                 RedirectAttributes redirectAttributes) {
        // DONE: get RuleName by Id and to model then show to the form
        log.info(LogConstants.RULE_NAME_UPDATE_FORM_REQUEST_RECEIVED, id,
                model.getAttribute("currentUsername"));

        try {
            model.addAttribute("ruleName", ruleNameService.findRuleNameById(id));
            return "ruleName/update";

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("ruleName.id.not.valid", id.toString()));
            return "redirect:/ruleName/list";
        }
    }

    /**
     * updates a ruleName
     *
     * @param id       of the ruleName to update
     * @param ruleName ruleName informations to update
     * @param result   go to ruleName list page if RuleName has been updated, otherwise stay at ruleName/update page
     * @param model    current model
     * @return ruleName/list page if RuleName has been updated, otherwise stay at ruleName/update page
     */
    @PostMapping("/ruleName/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id, @ModelAttribute("ruleName") @Valid RuleNameDTO ruleName,
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // DONE: check required fields, if valid call service to update RuleName and return RuleName list
        log.info(LogConstants.RULE_NAME_UPDATE_REQUEST_RECEIVED, id,
                ruleName.toString(),
                model.getAttribute("currentUser"));

        if (result.hasErrors()) {
            log.error(LogConstants.RULE_NAME_UPDATE_REQUEST_NOT_VALID + "\n");
            return "ruleName/update";
        }

        try {
            ruleName.setId(id);
            RuleNameDTO ruleNameDTOUpdated = ruleNameService.updateRuleName(ruleName);

            log.info(LogConstants.RULE_NAME_UPDATE_REQUEST_OK, ruleNameDTOUpdated.getId(),
                    model.getAttribute("currentUsername"));

            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("ruleName.update.ok", id.toString()));
            return "redirect:/ruleName/list";

        } catch (Exception exception) {
            log.error(LogConstants.RULE_NAME_UPDATE_REQUEST_KO, id, exception.getMessage());

            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("ruleName.update.ko", id.toString()));
            return "ruleName/update";
        }
    }

    /**
     * deletes a ruleName
     *
     * @param id    of the ruleName to delete
     * @param model current model
     * @return ruleName list page
     */
    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        // DONE: Find RuleName by Id and delete the RuleName, return to Rule list
        log.info(LogConstants.RULE_NAME_DELETE_REQUEST_RECEIVED, id, model.getAttribute("currentUsername"));

        try {
            ruleNameService.deleteRuleName(id);
            log.info(LogConstants.RULE_NAME_DELETE_REQUEST_OK, id, model.getAttribute("currentUsername"));
            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("ruleName.delete.ok", id.toString()));

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(LogConstants.RULE_NAME_DELETE_REQUEST_KO, id, illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("ruleName.id.not.valid", id.toString()));
        } catch (Exception exception) {
            log.error(LogConstants.RULE_NAME_DELETE_REQUEST_KO, id, exception.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("ruleName.delete.ko", id.toString()));
        }

        return "redirect:/ruleName/list";
    }
}
