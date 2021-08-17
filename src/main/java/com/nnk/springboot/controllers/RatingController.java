package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.RatingDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.services.contracts.IRatingService;
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
public class RatingController {
    // DONE: Inject Rating service
    private final IRatingService ratingService;

    @Autowired
    public RatingController(IRatingService ratingService) {
        this.ratingService = ratingService;
    }


    /**
     * shows the rating list
     *
     * @param model current model
     * @return list rating page
     */
    @RequestMapping("/rating/list")
    public String home(Model model) {
        // DONE: find all Rating, add to model
        log.info(LogConstants.RATING_LIST_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("ratingAll", ratingService.findAllRating());

        return "rating/list";
    }


    /**
     * shows the add form for rating
     *
     * @param model current model
     * @return add rating page
     */
    @GetMapping("/rating/add")
    public String addRatingForm(Model model) {
        log.info(LogConstants.RATING_CREATION_FORM_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("rating", new RatingDTO());

        return "rating/add";
    }


    /**
     * creates a rating
     *
     * @param rating to create
     * @return rating/list page if rating has been created, otherwise stay at rating/add page
     */
    @PostMapping("/rating/validate")
    public String validate(@ModelAttribute("rating") @Valid RatingDTO rating,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        //TODO revoir si suppression model
        // DONE: check data valid and save to db
        log.info(LogConstants.RATING_CREATION_REQUEST_RECEIVED + rating.toString());

        if (result.hasErrors()) {
            log.error(LogConstants.RATING_CREATION_REQUEST_NOT_VALID + "\n");
            return "rating/add";
        }

        try {
            Optional<RatingDTO> ratingDTOCreated = ratingService.createRating(rating);

            if (ratingDTOCreated.isPresent()) {
                log.info(LogConstants.RATING_CREATION_REQUEST_OK, ratingDTOCreated.get().getId(),
                        UserUtil.getCurrentUser());

                //DONE: after saving return Rating List
                redirectAttributes.addFlashAttribute("infoMessage",
                        formatOutputMessage("rating.add.ok",
                                ratingDTOCreated.get().getId().toString()));

                return "redirect:/rating/list";
            } else {
                log.error(LogConstants.RATING_CREATION_REQUEST_KO + " \n");
                redirectAttributes.addFlashAttribute("errorMessage", "rating.add.ko");
                return "rating/add";
            }

        } catch (Exception exception) {
            log.error(LogConstants.RATING_CREATION_REQUEST_KO + ": " + exception.getMessage() + " \n");
            redirectAttributes.addFlashAttribute("errorMessage", "rating.add.ko");
            return "rating/add";
        }
    }


    /**
     * shows the update form for rating
     *
     * @param id    of the rating to update
     * @param model current model
     * @return rating/update page if rating has been found, otherwise return to rating/list page
     */
    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model,
                                 RedirectAttributes redirectAttributes) {
        // DONE: get Rating by Id and to model then show to the form
        log.info(LogConstants.RATING_UPDATE_FORM_REQUEST_RECEIVED, id,
                UserUtil.getCurrentUser());

        try {
            model.addAttribute("rating", ratingService.findRatingById(id));
            return "rating/update";

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("rating.id.not.valid", id.toString()));
            return "redirect:/rating/list";
        }
    }


    /**
     * updates a rating
     *
     * @param id     of the rating to update
     * @param rating rating informations to update
     * @param result go to rating list page if Rating has been updated, otherwise stay at rating/update page
     * @param model  current model
     * @return rating/list page if Rating has been updated, otherwise stay at rating/update page
     */
    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @ModelAttribute("rating") @Valid RatingDTO rating,
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // DONE: check required fields, if valid call service to update Rating and return Rating list
        log.info(LogConstants.RATING_UPDATE_REQUEST_RECEIVED, id,
                rating.toString(),
                model.getAttribute("currentUser"));

        if (result.hasErrors()) {
            log.error(LogConstants.RATING_UPDATE_REQUEST_NOT_VALID + "\n");
            return "rating/update";
        }

        try {
            rating.setId(id);
            RatingDTO ratingDTOUpdated = ratingService.updateRating(rating);

            log.info(LogConstants.RATING_UPDATE_REQUEST_OK, ratingDTOUpdated.getId(),
                    UserUtil.getCurrentUser());

            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("rating.update.ok", id.toString()));
            return "redirect:/rating/list";

        } catch (Exception exception) {
            log.error(LogConstants.RATING_UPDATE_REQUEST_KO, id, exception.getMessage());

            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("rating.update.ko", id.toString()));
            return "rating/update";
        }
    }


    /**
     * deletes a rating
     *
     * @param id    of the rating to delete
     * @param model current model
     * @return rating list page
     */
    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        //TODO revoir si suppression model
        // DONE: Find Rating by Id and delete the Rating, return to Rating list
        log.info(LogConstants.RATING_DELETE_REQUEST_RECEIVED, id, UserUtil.getCurrentUser());

        try {
            ratingService.deleteRating(id);
            log.info(LogConstants.RATING_DELETE_REQUEST_OK, id, UserUtil.getCurrentUser());
            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("rating.delete.ok", id.toString()));

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(LogConstants.RATING_DELETE_REQUEST_KO, id, illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("rating.id.not.valid", id.toString()));
        } catch (Exception exception) {
            log.error(LogConstants.RATING_DELETE_REQUEST_KO, id, exception.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("rating.delete.ko", id.toString()));
        }

        return "redirect:/rating/list";
    }
}
