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

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
