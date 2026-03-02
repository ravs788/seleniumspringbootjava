package com.example.seleniumspringbootjava.support;

import com.example.seleniumspringbootjava.config.BrowserType;

/**
 * Bridges JUnit @TestTemplate invocation selection to driver creation done in
 * {@link SpringSeleniumTestBase}'s @BeforeEach.
 *
 * Implementation uses a ThreadLocal because JUnit executes each invocation on a
 * single thread and our WebDriver is also stored per-thread.
 */
public final class MultiBrowserRuntime {

    private static final ThreadLocal<BrowserType> TEMPLATE_BROWSER = new ThreadLocal<>();

    private MultiBrowserRuntime() {
    }

    public static void setBrowserFromTemplate(BrowserType browser) {
        TEMPLATE_BROWSER.set(browser);
    }

    public static BrowserType browserFromTemplate() {
        return TEMPLATE_BROWSER.get();
    }

    public static void clear() {
        TEMPLATE_BROWSER.remove();
    }
}
