package com.nnk.springboot.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateUtil {

    /**
     * return current local date time
     * @return current local date time
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

}
