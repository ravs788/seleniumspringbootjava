package com.example.seleniumspringbootjava.pages.theinternet;

import com.example.seleniumspringbootjava.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for https://the-internet.herokuapp.com/basic_auth
 */
public class BasicAuthPage extends BasePage {

    public static final String DEFAULT_BASIC_AUTH_URL = "https://the-internet.herokuapp.com/basic_auth";

    private final By authConfirmationMessage = By.xpath("//div[@id='content']/div/p");

    public BasicAuthPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Basic Auth is a browser-native dialog (HTTP auth), not a JavaScript alert.
     * Selenium cannot reliably interact with it via switchTo().alert().
     *
     * Workaround: navigate directly to the protected URL with credentials embedded.
     */
    public void handleAuth(String targetUrl, String userName, String password) {
        // Firefox (and some other browsers) may ignore credentials in the URL and show
        // a native auth prompt, which Selenium can't reliably handle. Use a protocol-
        // level Authorization header via CDP isn't available for Firefox, so the most
        // stable cross-browser approach is to inject credentials through the URL and
        // then retry once if the browser blocks it.
        try {
            driver.get(buildBasicAuthUrl(targetUrl, userName, password));
        } catch (org.openqa.selenium.UnhandledAlertException e) {
            // If a native auth dialog is present, the navigation may fail. Retry using
            // the same URL after dismissing any modal state.
            try {
                driver.switchTo().defaultContent();
            } catch (Exception ignored) {
            }
            driver.get(buildBasicAuthUrl(targetUrl, userName, password));
        }
    }

    /**
     * Convenience overload that uses the default endpoint.
     */
    public void handleAuth(String userName, String password) {
        handleAuth(DEFAULT_BASIC_AUTH_URL, userName, password);
    }

    private String buildBasicAuthUrl(String targetUrl, String userName, String password) {
        // Convert: https://host/path -> https://user:pass@host/path
        return targetUrl.replaceFirst("^(https?://)", "$1" + userName + ":" + password + "@");
    }

    public String getAuthMessage() {
        return driver.findElement(authConfirmationMessage).getText();
    }
}
