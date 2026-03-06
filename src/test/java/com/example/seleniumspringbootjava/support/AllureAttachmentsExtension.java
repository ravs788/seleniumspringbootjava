package com.example.seleniumspringbootjava.support;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * JUnit 5 extension that attaches evidence to Allure on test failure.
 *
 * Attachments:
 * - Screenshot (PNG) when the driver supports it
 * - Current URL
 * - Page source (HTML) (best effort)
 *
 * Usage:
 * - register via {@code @ExtendWith(AllureAttachmentsExtension.class)} on test
 * base or test class
 * - ensure the WebDriver is stored in {@link DriverStore}
 */
public final class AllureAttachmentsExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Optional<WebDriver> maybeDriver = DriverStore.get();
        if (maybeDriver.isEmpty()) {
            return;
        }
        WebDriver driver = maybeDriver.get();

        attachText("URL", safeGetCurrentUrl(driver));

        // Screenshot
        if (driver instanceof TakesScreenshot ts) {
            try {
                byte[] png = ts.getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot", "image/png", new ByteArrayInputStream(png), ".png");
            } catch (Exception ignored) {
                // best effort
            }
        }

        // Page source
        try {
            String html = driver.getPageSource();
            if (html != null) {
                Allure.addAttachment(
                        "Page Source",
                        "text/html",
                        new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)),
                        ".html");
            }
        } catch (Exception ignored) {
            // best effort
        }
    }

    private static void attachText(String name, String value) {
        if (value == null) {
            return;
        }
        Allure.addAttachment(name, "text/plain", new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)),
                ".txt");
    }

    private static String safeGetCurrentUrl(WebDriver driver) {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return null;
        }
    }
}
