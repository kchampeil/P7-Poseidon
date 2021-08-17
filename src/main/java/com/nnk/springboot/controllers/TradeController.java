package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.TradeDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.services.contracts.ITradeService;
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
@RequestMapping("trade")
public class TradeController {
    // DONE: Inject Trade service
    private final ITradeService tradeService;

    @Autowired
    public TradeController(ITradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * shows the trade list
     *
     * @param model current model
     * @return list trade page
     */
    @RequestMapping("list")
    public String home(Model model) {
        // DONE: find all Trade, add to model
        log.info(LogConstants.TRADE_LIST_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("tradeAll", tradeService.findAll());

        return "trade/list";
    }


    /**
     * shows the add form for trade
     *
     * @param model current model
     * @return add trade page
     */
    @GetMapping("add")
    public String addUser(Model model) {

        log.info(LogConstants.TRADE_CREATION_FORM_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("trade", new TradeDTO());

        return "trade/add";
    }


    /**
     * creates a trade
     *
     * @param trade to create
     * @return trade/list page if trade has been created, otherwise stay at trade/add page
     */
    @PostMapping("validate")
    public String validate(@ModelAttribute("trade") @Valid TradeDTO trade, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        // DONE: check data valid and save to db, after saving return Trade list

        log.info(LogConstants.TRADE_CREATION_REQUEST_RECEIVED + trade.toString());

        if (result.hasErrors()) {
            log.error(LogConstants.TRADE_CREATION_REQUEST_NOT_VALID + "\n");
            return "trade/add";
        }

        try {
            Optional<TradeDTO> tradeDTOCreated = tradeService.create(trade);

            if (tradeDTOCreated.isPresent()) {
                log.info(LogConstants.TRADE_CREATION_REQUEST_OK, tradeDTOCreated.get().getTradeId(),
                        UserUtil.getCurrentUser());

                //DONE: after saving return trade list
                redirectAttributes.addFlashAttribute("infoMessage",
                        formatOutputMessage("trade.add.ok",
                                tradeDTOCreated.get().getTradeId().toString()));

                return "redirect:/trade/list";
            } else {
                log.error(LogConstants.TRADE_CREATION_REQUEST_KO + " \n");
                model.addAttribute("errorMessage", "trade.add.ko");
                return "trade/add";
            }

        } catch (Exception exception) {
            log.error(LogConstants.TRADE_CREATION_REQUEST_KO + ": " + exception.getMessage() + " \n");
            model.addAttribute("errorMessage",
                    formatOutputMessage("trade.add.ko", ": " + exception.getMessage()));
            return "trade/add";
        }
    }


    /**
     * shows the update form for trade
     *
     * @param id    of the trade to update
     * @param model current model
     * @return trade/update page if trade has been found, otherwise return to trade/list page
     */
    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model,
                                 RedirectAttributes redirectAttributes) {
        // DONE: get Trade by Id and to model then show to the form
        log.info(LogConstants.TRADE_UPDATE_FORM_REQUEST_RECEIVED, id,
                UserUtil.getCurrentUser());

        try {
            model.addAttribute("trade", tradeService.findById(id));
            return "trade/update";

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("trade.id.not.valid", id.toString()));
            return "redirect:/trade/list";
        }
    }


    /**
     * updates a trade
     *
     * @param id     of the trade to update
     * @param trade  trade informations to update
     * @param result go to trade list page if trade has been updated, otherwise stay at trade/update page
     * @param model  current model
     * @return trade/list page if trade has been updated, otherwise stay at trade/update page
     */
    @PostMapping("update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @ModelAttribute("trade") @Valid TradeDTO trade,
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // DONE: check required fields, if valid call service to update Trade and return Trade list

        log.info(LogConstants.TRADE_UPDATE_REQUEST_RECEIVED, id,
                trade.toString(),
                model.getAttribute("currentUser"));

        if (result.hasErrors()) {
            log.error(LogConstants.TRADE_UPDATE_REQUEST_NOT_VALID + "\n");
            return "trade/update";
        }

        try {
            trade.setTradeId(id);
            TradeDTO tradeDTOUpdated = tradeService.update(trade);

            log.info(LogConstants.TRADE_UPDATE_REQUEST_OK, tradeDTOUpdated.getTradeId(),
                    UserUtil.getCurrentUser());

            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("trade.update.ok", id.toString()));
            return "redirect:/trade/list";

        } catch (Exception exception) {
            log.error(LogConstants.TRADE_UPDATE_REQUEST_KO, id, exception.getMessage());

            model.addAttribute("errorMessage",
                    formatOutputMessage("trade.update.ko", id.toString() + ": " + exception.getMessage()));
            return "trade/update";
        }
    }


    /**
     * deletes a trade
     *
     * @param id of the trade to delete
     * @return trade list page
     */
    @GetMapping("delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        // DONE: Find Trade by Id and delete the Trade, return to Trade list

        log.info(LogConstants.TRADE_DELETE_REQUEST_RECEIVED, id, UserUtil.getCurrentUser());

        try {
            tradeService.delete(id);
            log.info(LogConstants.TRADE_DELETE_REQUEST_OK, id, UserUtil.getCurrentUser());
            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("trade.delete.ok", id.toString()));

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(LogConstants.TRADE_DELETE_REQUEST_KO, id, illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("trade.id.not.valid", id.toString()));

        } catch (Exception exception) {
            log.error(LogConstants.TRADE_DELETE_REQUEST_KO, id, exception.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("trade.delete.ko", id.toString()));
        }

        return "redirect:/trade/list";
    }
}
