package com.example.seleniumspringbootjava.config;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * Spring Boot test configuration for Selenium.
 *
 * WebDriver bean is prototype-scoped so each injection point can request a new
 * driver. For "per-test-class" lifecycle, tests should create exactly one
 * WebDriver per class and quit it in @AfterAll.
 */
@TestConfiguration
public class SeleniumTestConfig {

    /**
     * Kept for backward compatibility: in tests we now create drivers via
     * {@link WebDriverFactory} so we can support multiple browsers per run and
     * config-driven setup.
     *
     * This bean still provides a scoped proxy WebDriver if any legacy code is
     * autowiring WebDriver directly.
     */
    @Bean
    @Scope(value = TestScopeConfig.TEST_CLASS_SCOPE_NAME, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
    public WebDriver webDriver() {
        TestConfig cfg = TestConfigLoader.load();
        BrowserType browser = BrowserType.from(cfg.getBrowsers().getFirst());
        return WebDriverFactory.create(browser, cfg);
    }
}
