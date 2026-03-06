package com.example.seleniumspringbootjava.support;

/**
 * Central place for JUnit tags used across the test suite.
 */
public final class Tags {

    private Tags() {
    }

    /**
     * Smoke: fast, critical health checks (P0).
     */
    public static final String SMOKE = "smoke";

    /**
     * Regression: all tests intended to run in regression cycles (P0+).
     */
    public static final String REGRESSION = "regression";
}
