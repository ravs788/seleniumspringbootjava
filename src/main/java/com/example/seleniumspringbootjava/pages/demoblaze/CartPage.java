package com.example.seleniumspringbootjava.pages.demoblaze;

import com.example.seleniumspringbootjava.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Page Object for DemoBlaze cart page.
 */
public class CartPage extends BasePage {

    private final By cartTableRows = By.cssSelector("#tbodyid tr");
    private final By placeOrderButton = By.xpath("//button[normalize-space()='Place Order']");
    private final By orderModal = By.id("orderModal");

    public CartPage(WebDriver driver) {
        super(driver, Duration.ofSeconds(10));
    }

    public CartPage waitForLoaded() {
        // Cart may be legitimately empty; in that case there are no rows to wait for.
        // We only need to ensure the cart page has rendered.
        wait.until(ExpectedConditions.or(
                // cart has at least one row
                ExpectedConditions.presenceOfElementLocated(cartTableRows),
                // or "Place Order" button is present (cart page loaded even if empty)
                ExpectedConditions.presenceOfElementLocated(placeOrderButton)));
        return this;
    }

    public OrderModal clickPlaceOrder() {
        click(placeOrderButton);
        waitVisible(orderModal);
        return new OrderModal(driver).waitForOpened();
    }
}
