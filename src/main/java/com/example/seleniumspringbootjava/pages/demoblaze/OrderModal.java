package com.example.seleniumspringbootjava.pages.demoblaze;

import com.example.seleniumspringbootjava.model.demoblaze.OrderDetails;
import com.example.seleniumspringbootjava.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Page Object for the "Place Order" modal on DemoBlaze cart page.
 */
public class OrderModal extends BasePage {

    private final By modalRoot = By.id("orderModal");

    private final By name = By.id("name");
    private final By country = By.id("country");
    private final By city = By.id("city");
    private final By card = By.id("card");
    private final By month = By.id("month");
    private final By year = By.id("year");

    private final By purchaseButton = By.xpath("//button[normalize-space()='Purchase']");

    private final By thankYouHeader = By.xpath("//h2[normalize-space()='Thank you for your purchase!']");

    public OrderModal(WebDriver driver) {
        super(driver, Duration.ofSeconds(10));
    }

    public OrderModal waitForOpened() {
        waitVisible(modalRoot);
        waitVisible(name);
        return this;
    }

    public OrderModal fillName(String value) {
        type(name, value);
        return this;
    }

    public OrderModal fillCountry(String value) {
        type(country, value);
        return this;
    }

    public OrderModal fillCity(String value) {
        type(city, value);
        return this;
    }

    public OrderModal fillCard(String value) {
        type(card, value);
        return this;
    }

    public OrderModal fillMonth(String value) {
        type(month, value);
        return this;
    }

    public OrderModal fillYear(String value) {
        type(year, value);
        return this;
    }

    /**
     * Fill all fields using an {@link OrderDetails} model.
     */
    public OrderModal fill(OrderDetails details) {
        if (details == null) {
            throw new IllegalArgumentException("OrderDetails must not be null");
        }
        return fillName(details.name())
                .fillCountry(details.country())
                .fillCity(details.city())
                .fillCard(details.card())
                .fillMonth(details.month())
                .fillYear(details.year());
    }

    public OrderModal purchase() {
        click(purchaseButton);
        // Confirmation dialog shows on the page (SweetAlert); wait for thank you
        // header.
        wait.until(ExpectedConditions.visibilityOfElementLocated(thankYouHeader));
        return this;
    }

    /**
     * Convenience method: fill + purchase.
     */
    public OrderModal purchase(OrderDetails details) {
        return fill(details).purchase();
    }

    public boolean isThankYouDisplayed() {
        return !driver.findElements(thankYouHeader).isEmpty();
    }
}
