package com.example.seleniumspringbootjava.pages.demoblaze;

import com.example.seleniumspringbootjava.pages.BasePage;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Page Object for a DemoBlaze product details page (e.g. Nokia lumia 1520
 * page).
 */
public class ProductPage extends BasePage {

    private final By productTitle = By.cssSelector("#tbodyid h2");
    private final By addToCart = By.xpath("//a[normalize-space()='Add to cart']");

    public ProductPage(WebDriver driver) {
        super(driver, Duration.ofSeconds(10));
    }

    public ProductPage waitForLoaded(String expectedProductName) {
        // Product name appears in the header.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(productTitle, expectedProductName));
        return this;
    }

    public ProductPage addToCartAndAcceptAlert() {
        click(addToCart);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // After accepting the alert, the page remains on product page.
        return this;
    }

    public String title() {
        return normalizeText(text(productTitle));
    }
}
