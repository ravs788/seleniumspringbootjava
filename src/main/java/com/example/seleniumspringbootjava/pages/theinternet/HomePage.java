package com.example.seleniumspringbootjava.pages.theinternet;

import com.example.seleniumspringbootjava.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for https://the-internet.herokuapp.com/ (landing page).
 */
public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToPage(String linkText) {
        if (checkLink(linkText)) {
            click(By.linkText(linkText));
            // Ensure navigation completes before the next page object method tries to
            // locate elements. This reduces flakiness in slower environments.
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/"));
        }
    }

    public boolean checkLink(String linkText) {
        return driver.findElements(By.linkText(linkText)).size() > 0;
    }
}
