package com.example.seleniumspringbootjava.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestConfig {

    private List<String> browsers;
    private String baseUrl;
    private long implicitWaitSeconds;
    private long pageLoadTimeoutSeconds;
    private long scriptTimeoutSeconds;
    private boolean headless;
    private int maxConcurrentBrowsers;
    private Window window;
    private Retry retry;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Window {
        private boolean maximize;
        private int width;
        private int height;

        public boolean isMaximize() {
            return maximize;
        }

        public void setMaximize(boolean maximize) {
            this.maximize = maximize;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public List<String> getBrowsers() {
        return browsers;
    }

    public void setBrowsers(List<String> browsers) {
        this.browsers = browsers;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public long getImplicitWaitSeconds() {
        return implicitWaitSeconds;
    }

    public void setImplicitWaitSeconds(long implicitWaitSeconds) {
        this.implicitWaitSeconds = implicitWaitSeconds;
    }

    public long getPageLoadTimeoutSeconds() {
        return pageLoadTimeoutSeconds;
    }

    public void setPageLoadTimeoutSeconds(long pageLoadTimeoutSeconds) {
        this.pageLoadTimeoutSeconds = pageLoadTimeoutSeconds;
    }

    public long getScriptTimeoutSeconds() {
        return scriptTimeoutSeconds;
    }

    public void setScriptTimeoutSeconds(long scriptTimeoutSeconds) {
        this.scriptTimeoutSeconds = scriptTimeoutSeconds;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public int getMaxConcurrentBrowsers() {
        return maxConcurrentBrowsers;
    }

    public void setMaxConcurrentBrowsers(int maxConcurrentBrowsers) {
        this.maxConcurrentBrowsers = maxConcurrentBrowsers;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Retry {
        /**
         * Total attempts including the initial attempt. Example:
         * - maxAttempts=1 => no retries
         * - maxAttempts=2 => 1 retry
         */
        private int maxAttempts = 1;

        /**
         * Fixed delay between attempts. Useful to smooth out transient grid/network
         * issues.
         */
        private long backoffMillis = 0;

        /**
         * Retry only known transient Selenium issues (recommended).
         */
        private boolean onlyOnSeleniumTransientFailures = true;

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public long getBackoffMillis() {
            return backoffMillis;
        }

        public void setBackoffMillis(long backoffMillis) {
            this.backoffMillis = backoffMillis;
        }

        public boolean isOnlyOnSeleniumTransientFailures() {
            return onlyOnSeleniumTransientFailures;
        }

        public void setOnlyOnSeleniumTransientFailures(boolean onlyOnSeleniumTransientFailures) {
            this.onlyOnSeleniumTransientFailures = onlyOnSeleniumTransientFailures;
        }
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
