package com.example.seleniumspringbootjava.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public final class WebDriverFactory {

    private WebDriverFactory() {
    }

    public static WebDriver create(BrowserType browser, TestConfig cfg) {
        WebDriver driver = switch (browser) {
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
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        if (cfg.isHeadless()) {
            options.addArguments("-headless");
        }

        options.addPreference("toolkit.startup.max_resumed_crashes", -1);
        options.addPreference("browser.tabs.remote.autostart", true);
        options.addPreference("browser.tabs.remote.autostart.2", true);
        options.addPreference("browser.sessionstore.resume_from_crash", false);

        return new FirefoxDriver(options);
    }

    private static WebDriver createChrome(TestConfig cfg) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        if (cfg.isHeadless()) {
            // new headless mode
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        return new ChromeDriver(options);
    }

    private static WebDriver createEdge(TestConfig cfg) {
        WebDriverManager.edgedriver().setup();

        EdgeOptions options = new EdgeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        if (cfg.isHeadless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        return new EdgeDriver(options);
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
