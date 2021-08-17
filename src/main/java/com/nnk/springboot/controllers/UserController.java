package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.UserDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.services.contracts.IUserService;
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
@RequestMapping("user")
public class UserController {
    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }


    /**
     * shows the user list
     *
     * @param model current model
     * @return list user page
     */
    @RequestMapping("list")
    public String home(Model model) {
        log.info(LogConstants.USER_LIST_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }


    /**
     * shows the add form for user
     *
     * @param model current model
     * @return add user page
     */
    @GetMapping("add")
    public String addUser(Model model) {
        log.info(LogConstants.USER_CREATION_FORM_REQUEST_RECEIVED, UserUtil.getCurrentUser());
        model.addAttribute("user", new UserDTO());

        return "user/add";
    }


    /**
     * creates a user
     *
     * @param user to create
     * @return user/list page if user has been created, otherwise stay at user/add page
     */
    @PostMapping("validate")
    public String validate(@ModelAttribute("user") @Valid UserDTO user, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        log.info(LogConstants.USER_CREATION_REQUEST_RECEIVED + user.toString());

        if (result.hasErrors()) {
            log.error(LogConstants.USER_CREATION_REQUEST_NOT_VALID + "\n");
            return "user/add";
        }

        try {
            Optional<UserDTO> userDTOCreated = userService.create(user);

            if (userDTOCreated.isPresent()) {
                log.info(LogConstants.USER_CREATION_REQUEST_OK, userDTOCreated.get().getId(),
                        UserUtil.getCurrentUser());

                redirectAttributes.addFlashAttribute("infoMessage",
                        formatOutputMessage("user.add.ok",
                                userDTOCreated.get().getId().toString()));

                return "redirect:/user/list";
            } else {
                log.error(LogConstants.USER_CREATION_REQUEST_KO + " \n");
                model.addAttribute("errorMessage", "user.add.ko");
                return "user/add";
            }

        } catch (Exception exception) {
            log.error(LogConstants.USER_CREATION_REQUEST_KO + ": " + exception.getMessage() + " \n");
            model.addAttribute("errorMessage",
                    formatOutputMessage("user.add.ko", ": " + exception.getMessage()));
            return "user/add";
        }
    }


    /**
     * shows the update form for user
     *
     * @param id    of the user to update
     * @param model current model
     * @return user/update page if user has been found, otherwise return to user/list page
     */
    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model,
                                 RedirectAttributes redirectAttributes) {
        log.info(LogConstants.USER_UPDATE_FORM_REQUEST_RECEIVED, id,
                UserUtil.getCurrentUser());

        try {
            model.addAttribute("user", userService.findById(id));
            return "user/update";

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("user.id.not.valid", id.toString()));
            return "redirect:/user/list";
        }
    }


    /**
     * updates a user
     *
     * @param id     of the user to update
     * @param user   user informations to update
     * @param result go to user list page if user has been updated, otherwise stay at user/update page
     * @param model  current model
     * @return user/list page if user has been updated, otherwise stay at user/update page
     */
    @PostMapping("update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @ModelAttribute("user") @Valid UserDTO user,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        log.info(LogConstants.USER_UPDATE_REQUEST_RECEIVED, id,
                user.toString(), UserUtil.getCurrentUser());

        if (result.hasErrors()) {
            log.error(LogConstants.USER_UPDATE_REQUEST_NOT_VALID + "\n");
            return "user/update";
        }

        try {
            user.setId(id);
            UserDTO userDTOUpdated = userService.update(user);

            log.info(LogConstants.USER_UPDATE_REQUEST_OK, userDTOUpdated.getId(),
                    UserUtil.getCurrentUser());

            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("user.update.ok", id.toString()));
            return "redirect:/user/list";

        } catch (Exception exception) {
            log.error(LogConstants.USER_UPDATE_REQUEST_KO, id, exception.getMessage());

            model.addAttribute("errorMessage",
                    formatOutputMessage("user.update.ko", id.toString())
                            + ": " + exception.getMessage());
            return "user/update";
        }
    }

    /**
     * deletes a user
     *
     * @param id    of the user to delete
     * @return user list page
     */
    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

        log.info(LogConstants.USER_DELETE_REQUEST_RECEIVED, id, UserUtil.getCurrentUser());

        try {
            userService.delete(id);
            log.info(LogConstants.USER_DELETE_REQUEST_OK, id, UserUtil.getCurrentUser());
            redirectAttributes.addFlashAttribute("infoMessage",
                    formatOutputMessage("user.delete.ok", id.toString()));

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(LogConstants.USER_DELETE_REQUEST_KO, id, illegalArgumentException.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("user.id.not.valid", id.toString()));

        } catch (Exception exception) {
            log.error(LogConstants.USER_DELETE_REQUEST_KO, id, exception.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    formatOutputMessage("user.delete.ko", id.toString()));
        }

        return "redirect:/user/list";
    }
}
