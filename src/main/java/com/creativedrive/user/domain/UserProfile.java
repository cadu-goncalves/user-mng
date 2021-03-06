package com.creativedrive.user.domain;

import java.util.Arrays;
import java.util.List;

/**
 * User profiles
 */
public final class UserProfile {

    /** String constants */
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    public static List<String> values() {
        return Arrays.asList(new String[]{ADMIN, USER});
    }

}