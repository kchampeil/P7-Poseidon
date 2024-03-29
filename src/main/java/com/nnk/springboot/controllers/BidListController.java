package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.services.contracts.IBidListService;
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
@RequestMapping("bidList")
public class BidListController {
    //DONE: Inject Bid service
    private final IBidListService bidListService;

    @Autowired
    public BidListController(IBidListService bidListService) {
        this.bidListService = bidListService;
    }

    /**
     * shows the bidList list
     *
     * @param model current model
     * @return list bidList page
     */
    @RequestMapping("list")
    public String home(Model model) {
        //DONE: call service find all bids to show to the view
        log.info(LogConstants.BIDLIST_LIST_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("bidListAll", bidListService.findAll());

        return "bidList/list";
    }


    /**
     * shows the add form for bidList
     *
     * @param model current model
     * @return add bidList page
     */
    @GetMapping("add")
    public String addBidForm(Model model) {

        log.info(LogConstants.BIDLIST_CREATION_FORM_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("bidList", new BidListDTO());

        return "bidList/add";
    }


    /**
     * creates a bidList
     *
     * @param bid bidList to create
     * @return bidList/list page if bid has been created, otherwise stay at bidList/add page
     */
    @PostMapping("validate")
    public String validate(@ModelAttribute("bidList") @Valid BidListDTO bid, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        //DONE: check data valid and save to db

        log.info(LogConstants.BIDLIST_CREATION_REQUEST_RECEIVED + bid.toString());

        if (result.hasErrors()) {
            log.error(LogConstants.BIDLIST_CREATION_REQUEST_NOT_VALID + "\n");
            return "bidList/add";
        }

        try {
            Optional<BidListDTO> bidListDTOCreated = bidListService.create(bid);

            if (bidListDTOCreated.isPresent()) {
                log.info(LogConstants.BIDLIST_CREATION_REQUEST_OK, bidListDTOCreated.get().getBidListId(),
                        UserUtil.getCurrentUser());

                //DONE: after saving return bid list
                redirectAttributes.addFlashAttribute("infoMessage",
                        formatOutputMessage("bidList.add.ok",
                                bidListDTOCreated.get().getBidListId().toString()));

                return "redirect:/bidList/list";
            } else {
                log.error(LogConstants.BIDLIST_CREATION_REQUEST_KO + " \n");
                model.addAttribute("errorMessage", "bidList.add.ko");
                return "bidList/add";
            }

        } catch (Exception exception) {
            log.error(LogConstants.BIDLIST_CREATION_REQUEST_KO + ": " + exception.getMessage() + " \n");
            model.addAttribute("errorMessage",
                    formatOutputMessage("bidList.add.ko", ": " + exception.getMessage()));
            return "bidList/add";
        }
    }


    /**
     * shows the update form for bidList
     *
     * @param id    of the bidList to update
     * @param model current model
     * @return bidList/update page if bidList has been found, otherwise return to bidList/list page
     */
    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        //DONE: get Bid by Id and to model then show to the form
        log.info(LogConstants.BIDLIST_UPDATE_FORM_REQUEST_RECEIVED, id,
                UserUtil.getCurrentUser());

        try {
            model.addAttribute("bidList", bidListService.findById(id));
            return "bidList/update";

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("bidList.id.not.valid", id.toString()));
            return "redirect:/bidList/list";
        }
    }


    /**
     * updates a bidList
     *
     * @param id      of the bidList to update
     * @param bidList bidList informations to update
     * @param result  go to bidList list page if bid has been updated, otherwise stay at bidList/update page
     * @param model   current model
     * @return bidList/list page if bid has been updated, otherwise stay at bidList/update page
     */
    @PostMapping("update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @ModelAttribute("bidList") @Valid BidListDTO bidList,
                            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        //DONE: check required fields, if valid call service to update Bid and return list Bid

        log.info(LogConstants.BIDLIST_UPDATE_REQUEST_RECEIVED, id,
                bidList.toString(),
                model.getAttribute("currentUser"));

        if (result.hasErrors()) {
            log.error(LogConstants.BIDLIST_UPDATE_REQUEST_NOT_VALID + "\n");
            return "bidList/update";
        }

        try {
            bidList.setBidListId(id);
            BidListDTO bidListDTOUpdated = bidListService.update(bidList);

            log.info(LogConstants.BIDLIST_UPDATE_REQUEST_OK, bidListDTOUpdated.getBidListId(),
                    UserUtil.getCurrentUser());

            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("bidList.update.ok", id.toString()));
            return "redirect:/bidList/list";

        } catch (Exception exception) {
            log.error(LogConstants.BIDLIST_UPDATE_REQUEST_KO, id, exception.getMessage());

            model.addAttribute("errorMessage",
                    formatOutputMessage("bidList.update.ko", id.toString() + ": " + exception.getMessage()));
            return "bidList/update";
        }
    }


    /**
     * deletes a bidList
     *
     * @param id of the bidList to delete
     * @return bidList list page
     */
    @GetMapping("delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        // DONE: Find Bid by Id and delete the bid, return to Bid list

        log.info(LogConstants.BIDLIST_DELETE_REQUEST_RECEIVED, id, UserUtil.getCurrentUser());

        try {
            bidListService.delete(id);
            log.info(LogConstants.BIDLIST_DELETE_REQUEST_OK, id, UserUtil.getCurrentUser());
            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("bidList.delete.ok", id.toString()));

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(LogConstants.BIDLIST_DELETE_REQUEST_KO, id, illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("bidList.id.not.valid", id.toString()));

        } catch (Exception exception) {
            log.error(LogConstants.BIDLIST_DELETE_REQUEST_KO, id, exception.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("bidList.delete.ko", id.toString()));
        }

        return "redirect:/bidList/list";
    }
}
