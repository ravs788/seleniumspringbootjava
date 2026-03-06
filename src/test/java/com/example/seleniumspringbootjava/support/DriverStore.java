package com.example.seleniumspringbootjava.support;

import org.openqa.selenium.WebDriver;

import java.util.Optional;

/**
 * Stores the current thread's {@link WebDriver} so extensions (e.g. Allure) can
 * attach evidence
 * on failure.
 *
 * <p>
 * This is intentionally separate from the internal ThreadLocal in
 * {@link SpringSeleniumTestBase}
 * so reporting/diagnostics tooling can access it without exposing driver
 * lifecycle internals.
 * </p>
 */
public final class DriverStore {

    private static final ThreadLocal<WebDriver> CURRENT = new ThreadLocal<>();

    private DriverStore() {
    }

    public static void set(WebDriver driver) {
        CURRENT.set(driver);
    }

    public static Optional<WebDriver> get() {
        return Optional.ofNullable(CURRENT.get());
    }

    public static void clear() {
        CURRENT.remove();
    }
}
