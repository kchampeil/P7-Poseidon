package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.contracts.IBidListService;
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

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
public class BidListController {
    // KC-done: Inject Bid service
    private final IBidListService bidListService;

    @Autowired
    public BidListController(IBidListService bidListService) {
        this.bidListService = bidListService;
    }

    @RequestMapping("/bidList/list")
    public String home(Model model) {
        // KC-done: call service find all bids to show to the view
        log.info(LogConstants.BIDLIST_LIST_REQUEST_RECEIVED
                + " for user: " + model.getAttribute("currentUsername"));
        model.addAttribute("bidListAll", bidListService.findAllBidList());

        return "bidList/list";
    }

    /**
     * shows the add form for bidList
     *
     * @param model a bidList to create/add
     * @return add bidList page
     */
    @GetMapping("/bidList/add")
    public String addBidForm(Model model) {

        log.info(LogConstants.BIDLIST_CREATION_FORM_REQUEST_RECEIVED
                + " for user: " + model.getAttribute("currentUsername") + "\n");
        model.addAttribute("bidList", new BidListDTO());

        return "bidList/add";
    }


    /**
     * creates a bidList
     *
     * @param bid bidList to create
     * @return go to Bid List Screen if bid has been created, otherwise stay at bidList/add page
     */
    @PostMapping("/bidList/validate")
    public String validate(@ModelAttribute("bidList") @Valid BidListDTO bid, BindingResult result, Model model) {
        // KC-done: check data valid and save to db

        log.info(LogConstants.BIDLIST_CREATION_REQUEST_RECEIVED
                + bid.getAccount() + " // " + bid.getType() + " // " + bid.getBidQuantity());

        if (result.hasErrors()) {
            log.error(LogConstants.BIDLIST_CREATION_REQUEST_NOT_VALID + "\n");
            return "bidList/add";
        }

        try {
            Optional<BidListDTO> bidListDTOCreated = bidListService.createBidList(bid);

            if (bidListDTOCreated.isPresent()) {
                log.info(LogConstants.BIDLIST_CREATION_REQUEST_OK + bidListDTOCreated.get().getBidListId()
                        + " by user: " + model.getAttribute("currentUsername")+ "\n");

                //KC-DONE: after saving return bid list
                return "redirect:/bidList/list";
            } else {
                log.error(LogConstants.BIDLIST_CREATION_REQUEST_KO + " \n");
                return "bidList/add";
            }

        } catch (Exception exception) {
            log.error(LogConstants.BIDLIST_CREATION_REQUEST_KO + ": " + exception.getMessage() + " \n");
            return "bidList/add";
        }
    }

    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Bid by Id and to model then show to the form
        return "bidList/update";
    }

    @PostMapping("/bidList/update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid BidList bidList,
                            BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Bid and return list Bid
        return "redirect:/bidList/list";
    }

    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Bid by Id and delete the bid, return to Bid list
        return "redirect:/bidList/list";
    }
}
