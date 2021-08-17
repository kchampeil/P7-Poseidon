package com.nnk.springboot.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    /**
     * return the current username
     * @return current username
     */
    public static String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
