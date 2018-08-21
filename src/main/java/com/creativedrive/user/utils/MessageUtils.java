package com.creativedrive.user.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Message utilities
 */
public final class MessageUtils {

    // Utility class private constructor
    private MessageUtils() {

    }

    /**
     * Get message from bundle
     *
     * @param path
     *      Message bundle path
     * @param key
     *      Message key
     * @return
     */
    public static String getMessage(String path, String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(path, Locale.ROOT);
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        } else {
            return key;
        }
    }

}
