package com.esport.util;

import java.util.UUID;

public class AppUtil {
    public static String generateTicketCode() {
        return "TKT-" + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 8).toUpperCase();
    }
    public static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(field + " est obligatoire.");
    }
}