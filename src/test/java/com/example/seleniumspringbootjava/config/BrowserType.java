package com.example.seleniumspringbootjava.config;

import java.util.Locale;

public enum BrowserType {
    FIREFOX,
    CHROME,
    EDGE;

    public static BrowserType from(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("browser cannot be null");
        }
        String v = raw.trim().toLowerCase(Locale.ROOT);
        return switch (v) {
            case "firefox", "ff" -> FIREFOX;
            case "chrome", "gc" -> CHROME;
            case "edge", "msedge" -> EDGE;
            default -> throw new IllegalArgumentException("Unsupported browser: " + raw);
        };
    }
}
