package com.f1community.backend.util;

public class UserUpdateUtils {
    public static boolean isValid(String value) {
        return value != null && !"string".equals(value);
    }

    private UserUpdateUtils() {}
}
