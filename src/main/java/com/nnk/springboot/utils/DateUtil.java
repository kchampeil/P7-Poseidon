package com.nnk.springboot.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateUtil {

    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

}
