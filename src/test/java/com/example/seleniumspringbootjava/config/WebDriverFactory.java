package com.example.seleniumspringbootjava.config;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

public final class WebDriverFactory {

    private WebDriverFactory() {
    }

    public static WebDriver create(BrowserType browser, TestConfig cfg) {
        WebDriver driver = isRemoteEnabled()
                ? createRemote(browser, cfg)
                : switch (browser) {
                    case FIREFOX -> createFirefox(cfg);
                    case CHROME -> createChrome(cfg);
                    case EDGE -> createEdge(cfg);
                };

        applyWindow(cfg, driver);

        // Prefer explicit waits; keep implicit at 0 by default.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(cfg.getImplicitWaitSeconds()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(cfg.getPageLoadTimeoutSeconds()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(cfg.getScriptTimeoutSeconds()));
        return driver;
    }

    private static WebDriver createFirefox(TestConfig cfg) {
        return new FirefoxDriver(buildFirefoxOptions(cfg));
    }

    private static WebDriver createChrome(TestConfig cfg) {
        return new ChromeDriver(buildChromeOptions(cfg));
    }

    private static WebDriver createEdge(TestConfig cfg) {
        return new EdgeDriver(buildEdgeOptions(cfg));
    }

    private static boolean isRemoteEnabled() {
        return Boolean.parseBoolean(System.getProperty("selenium.remote", "false"));
    }

    private static URI gridUri() {
        String raw = System.getProperty("selenium.gridUrl", "http://localhost:4444/");
        return URI.create(raw);
    }

    private static WebDriver createRemote(BrowserType browser, TestConfig cfg) {
        try {
            return switch (browser) {
                case FIREFOX -> new RemoteWebDriver(gridUri().toURL(), buildFirefoxOptions(cfg));
                case CHROME -> new RemoteWebDriver(gridUri().toURL(), buildChromeOptions(cfg));
                case EDGE -> new RemoteWebDriver(gridUri().toURL(), buildEdgeOptions(cfg));
            };
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid selenium.gridUrl: " + gridUri(), e);
        }
    }

    private static FirefoxOptions buildFirefoxOptions(TestConfig cfg) {
        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        if (cfg.isHeadless()) {
            options.addArguments("-headless");
        }

        options.addPreference("toolkit.startup.max_resumed_crashes", -1);
        options.addPreference("browser.tabs.remote.autostart", true);
        options.addPreference("browser.tabs.remote.autostart.2", true);
        options.addPreference("browser.sessionstore.resume_from_crash", false);

        return options;
    }

    private static ChromeOptions buildChromeOptions(TestConfig cfg) {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        if (cfg.isHeadless()) {
            // new headless mode
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        return options;
    }

    private static EdgeOptions buildEdgeOptions(TestConfig cfg) {
        EdgeOptions options = new EdgeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        if (cfg.isHeadless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        return options;
    }

    private static void applyWindow(TestConfig cfg, WebDriver driver) {
        if (cfg.getWindow() != null && cfg.getWindow().isMaximize()) {
            driver.manage().window().maximize();
            return;
        }
        int w = cfg.getWindow() != null ? cfg.getWindow().getWidth() : 1280;
        int h = cfg.getWindow() != null ? cfg.getWindow().getHeight() : 800;
        driver.manage().window().setSize(new Dimension(w, h));
    }
}
