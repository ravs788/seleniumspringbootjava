package com.example.seleniumspringbootjava.support;

import com.example.seleniumspringbootjava.SeleniumspringbootjavaApplication;
import com.example.seleniumspringbootjava.config.BrowserType;
import com.example.seleniumspringbootjava.config.PageObjectConfig;
import com.example.seleniumspringbootjava.config.SeleniumTestConfig;
import com.example.seleniumspringbootjava.config.TestConfig;
import com.example.seleniumspringbootjava.config.TestConfigLoader;
import com.example.seleniumspringbootjava.config.TestScopeConfig;
import com.example.seleniumspringbootjava.config.WebDriverFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * Base class for Selenium tests using Spring Boot + JUnit 5.
 *
 * Lifecycle:
 * - One WebDriver per test method (PER_METHOD)
 * - Methods can run in parallel (JUnit 5 parallel execution)
 * - Global max of 3 concurrent browser instances (Semaphore)
 *
 * The WebDriver is created in @BeforeEach and quit in @AfterEach.
 */
@SpringBootTest(classes = SeleniumspringbootjavaApplication.class)
@ContextConfiguration(classes = { TestScopeConfig.class, SeleniumTestConfig.class, PageObjectConfig.class })
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith({ RetryExtension.class, AllureAttachmentsExtension.class })
public abstract class SpringSeleniumTestBase {

    private static final TestConfig CONFIG = TestConfigLoader.load();

    private static final Semaphore BROWSER_LIMITER = new Semaphore(CONFIG.getMaxConcurrentBrowsers());

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    /**
     * Browser used for the current test invocation. When multi-browser is enabled,
     * each test method is executed once per configured browser.
     */
    private static final ThreadLocal<BrowserType> CURRENT_BROWSER = new ThreadLocal<>();

    protected WebDriver driver() {
        WebDriver d = DRIVER.get();
        if (d == null) {
            throw new IllegalStateException("WebDriver not initialized for this thread. Did @BeforeEach run?");
        }
        return d;
    }

    @BeforeEach
    void createDriverPerTestMethod() throws InterruptedException {
        // Cap concurrent browsers regardless of JUnit thread pool config.
        BROWSER_LIMITER.acquire();

        // Ensure custom test scope is unique per test method/thread.
        TestClassScopeContext.setScopeId(getClass().getName() + "#" + UUID.randomUUID());

        BrowserType browser = determineBrowserForThisInvocation();
        CURRENT_BROWSER.set(browser);

        WebDriver driver = WebDriverFactory.create(browser, CONFIG);
        DRIVER.set(driver);
        DriverStore.set(driver);
    }

    @AfterEach
    void quitDriverPerTestMethod() {
        try {
            WebDriver d = DRIVER.get();
            if (d != null) {
                d.quit();
            }
        } finally {
            DRIVER.remove();
            CURRENT_BROWSER.remove();
            DriverStore.clear();
            TestClassScopeContext.clear();
            BROWSER_LIMITER.release();
        }
    }

    /**
     * Multi-browser support:
     * - If you run with -Dbrowser=chrome, it forces a single browser.
     * - Otherwise, tests can be executed once per browser by a JUnit extension
     * (added in next step). For now we pick the first configured browser.
     */
    private BrowserType determineBrowserForThisInvocation() {
        // 1) If a @MultiBrowserTest method declares a BrowserType parameter, JUnit will
        // provide it (via MultiBrowserInvocationContextProvider). We read it from a
        // ThreadLocal set by the test method itself (see
        // resolveBrowserFromTestParameter()).
        BrowserType fromTemplate = MultiBrowserRuntime.browserFromTemplate();
        if (fromTemplate != null) {
            return fromTemplate;
        }

        // 2) If user forces a single browser, always use that.
        String forced = System.getProperty("browser");
        if (forced != null && !forced.isBlank()) {
            return BrowserType.from(forced);
        }

        // 3) Default: first configured browser.
        List<String> configured = CONFIG.getBrowsers();
        return BrowserType.from(configured.getFirst());
    }

    protected String baseUrl() {
        return CONFIG.getBaseUrl();
    }

    protected BrowserType currentBrowser() {
        BrowserType b = CURRENT_BROWSER.get();
        if (b == null) {
            throw new IllegalStateException("Browser not initialized for this thread. Did @BeforeEach run?");
        }
        return b;
    }
}
