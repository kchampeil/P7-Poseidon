package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.CurvePointDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.services.contracts.ICurvePointService;
import com.nnk.springboot.utils.UserUtil;
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
public class CurveController {
    //DONE: Inject Curve Point service
    private final ICurvePointService curvePointService;

    @Autowired
    public CurveController(ICurvePointService curvePointService) {
        this.curvePointService = curvePointService;
    }


    /**
     * shows the curvePoint list
     *
     * @param model current model
     * @return list curvePoint page
     */
    @RequestMapping("/curvePoint/list")
    public String home(Model model) {
        // DONE: find all Curve Point, add to model
        log.info(LogConstants.CURVEPOINT_LIST_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("curvePointAll", curvePointService.findAllCurvePoint());

        return "curvePoint/list";
    }


    /**
     * shows the add form for curvePoint
     *
     * @param model current model
     * @return add curvePoint page
     */
    @GetMapping("/curvePoint/add")
    public String addCurvePointForm(Model model) {

        log.info(LogConstants.CURVEPOINT_CREATION_FORM_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("curvePoint", new CurvePointDTO());
        return "curvePoint/add";
    }

    /**
     * creates a curvePoint
     *
     * @param curvePoint to create
     * @return curvePoint/list page if curvePoint has been created, otherwise stay at curvePoint/add page
     */
    @PostMapping("/curvePoint/validate")
    public String validate(@ModelAttribute("curvePoint") @Valid CurvePointDTO curvePoint, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        //DONE: check data valid and save to db

        log.info(LogConstants.CURVEPOINT_CREATION_REQUEST_RECEIVED + curvePoint.toString());

        if (result.hasErrors()) {
            log.error(LogConstants.CURVEPOINT_CREATION_REQUEST_NOT_VALID + "\n");
            return "curvePoint/add";
        }

        try {
            Optional<CurvePointDTO> curvePointDTOCreated = curvePointService.createCurvePoint(curvePoint);

            if (curvePointDTOCreated.isPresent()) {
                log.info(LogConstants.CURVEPOINT_CREATION_REQUEST_OK, curvePointDTOCreated.get().getId(),
                        UserUtil.getCurrentUser());

                //DONE: after saving return CurvePoint
                redirectAttributes.addFlashAttribute("infoMessage",
                        formatOutputMessage("curvePoint.add.ok",
                                curvePointDTOCreated.get().getId().toString()));

                return "redirect:/curvePoint/list";
            } else {
                log.error(LogConstants.CURVEPOINT_CREATION_REQUEST_KO + " \n");
                model.addAttribute("errorMessage", "curvePoint.add.ko");
                return "curvePoint/add";
            }

        } catch (Exception exception) {
            log.error(LogConstants.CURVEPOINT_CREATION_REQUEST_KO + ": " + exception.getMessage() + " \n");
            model.addAttribute("errorMessage",
                    formatOutputMessage("curvePoint.add.ko", ": " + exception.getMessage()));
            return "curvePoint/add";
        }
    }


    /**
     * shows the update form for curvePoint
     *
     * @param id    of the curvePoint to update
     * @param model current model
     * @return curvePoint/update page if curvePoint has been found, otherwise return to curvePoint/list page
     */
    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        // DONE: get CurvePoint by Id and to model then show to the form
        log.info(LogConstants.CURVEPOINT_UPDATE_FORM_REQUEST_RECEIVED, id,
                UserUtil.getCurrentUser());

        try {
            model.addAttribute("curvePoint", curvePointService.findCurvePointById(id));
            return "curvePoint/update";

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("curvePoint.id.not.valid", id.toString()));
            return "redirect:/curvePoint/list";
        }
    }


    /**
     * updates a curvePoint
     *
     * @param id         of the curvePoint to update
     * @param curvePoint curvePoint informations to update
     * @param result     go to curvePoint list page if CurvePoint has been updated, otherwise stay at curvePoint/update page
     * @param model      current model
     * @return curvePoint/list page if CurvePoint has been updated, otherwise stay at curvePoint/update page
     */
    @PostMapping("/curvePoint/update/{id}")
    public String updateCurvePoint(@PathVariable("id") Integer id, @ModelAttribute("curvePoint") @Valid CurvePointDTO curvePoint,
                                   BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // DONE: check required fields, if valid call service to update Curve and return Curve list
        log.info(LogConstants.CURVEPOINT_UPDATE_REQUEST_RECEIVED, id,
                curvePoint.toString(),
                model.getAttribute("currentUser"));

        if (result.hasErrors()) {
            log.error(LogConstants.CURVEPOINT_UPDATE_REQUEST_NOT_VALID + "\n");
            return "curvePoint/update";
        }

        try {
            curvePoint.setId(id);
            CurvePointDTO curvePointDTOUpdated = curvePointService.updateCurvePoint(curvePoint);

            log.info(LogConstants.CURVEPOINT_UPDATE_REQUEST_OK, curvePointDTOUpdated.getId(),
                    UserUtil.getCurrentUser());

            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("curvePoint.update.ok", id.toString()));
            return "redirect:/curvePoint/list";

        } catch (Exception exception) {
            log.error(LogConstants.CURVEPOINT_UPDATE_REQUEST_KO, id, exception.getMessage());

            model.addAttribute("errorMessage",
                    formatOutputMessage("curvePoint.update.ko", id.toString() + ": " + exception.getMessage()));
            return "curvePoint/update";
        }
    }


    /**
     * deletes a curvePoint
     *
     * @param id of the curvePoint to delete
     * @return curvePoint list page
     */
    @GetMapping("/curvePoint/delete/{id}")
    public String deleteCurvePoint(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        // DONE: Find Curve by Id and delete the Curve, return to Curve list
        log.info(LogConstants.CURVEPOINT_DELETE_REQUEST_RECEIVED, id, UserUtil.getCurrentUser());

        try {
            curvePointService.deleteCurvePoint(id);
            log.info(LogConstants.CURVEPOINT_DELETE_REQUEST_OK, id, UserUtil.getCurrentUser());
            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("curvePoint.delete.ok", id.toString()));

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(LogConstants.CURVEPOINT_DELETE_REQUEST_KO, id, illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("curvePoint.id.not.valid", id.toString()));

        } catch (Exception exception) {
            log.error(LogConstants.CURVEPOINT_DELETE_REQUEST_KO, id, exception.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("curvePoint.delete.ko", id.toString()));
        }

        return "redirect:/curvePoint/list";
    }
}
