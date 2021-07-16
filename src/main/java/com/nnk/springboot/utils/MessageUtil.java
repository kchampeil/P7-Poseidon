package com.nnk.springboot.utils;

import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class MessageUtil {

    /**
     * Generate the message to be shown in UI
     *
     * @param patternCode pattern code of the message (with unique value)
     * @param value       value to be integrated in the message
     * @return the formatted message (String)
     */
    public static String formatOutputMessage(String patternCode, String value) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.UK);
        String pattern = bundle.getString(patternCode);
        MessageFormat formatter = new MessageFormat(pattern);
        return formatter.format(new Object[]{value});
    }
}
