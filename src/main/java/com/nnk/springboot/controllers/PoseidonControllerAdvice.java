package com.nnk.springboot.controllers;

import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.services.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@Slf4j
@ControllerAdvice
public class PoseidonControllerAdvice {

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public PoseidonControllerAdvice(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @ModelAttribute
    public void addUserToModel(Principal principal, Model model) {

        UserDetails currentUser = null;

        if (principal != null) {
            currentUser = userDetailsService.getUserInfoByUsername(principal.getName());

        } else {
            log.info(LogConstants.CURRENT_USER_UNKNOWN);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUsername", currentUser.getUsername());

    }
}
