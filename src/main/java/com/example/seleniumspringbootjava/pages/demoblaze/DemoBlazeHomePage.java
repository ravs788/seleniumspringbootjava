package com.example.seleniumspringbootjava.pages.demoblaze;

import com.example.seleniumspringbootjava.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Page Object for https://www.demoblaze.com/ home page.
 *
 * Note: DemoBlaze updates product grid dynamically; this page object uses
 * presence/visibility waits and avoids caching WebElements.
 */
public class DemoBlazeHomePage extends BasePage {

    private static final String DEFAULT_BASE_URL = "https://www.demoblaze.com/";

    private final String baseUrl;

    private final By homeLink = By.xpath("//a[contains(normalize-space(),'Home')]");
    private final By phonesCategory = By.xpath("//a[normalize-space()='Phones']");
    private final By laptopsCategory = By.xpath("//a[normalize-space()='Laptops']");
    private final By monitorsCategory = By.xpath("//a[normalize-space()='Monitors']");

    private final By cartLink = By.id("cartur");
    private final By productsContainer = By.id("tbodyid");

    public DemoBlazeHomePage(WebDriver driver) {
        this(driver, DEFAULT_BASE_URL);
    }

    public DemoBlazeHomePage(WebDriver driver, String baseUrl) {
        super(driver, Duration.ofSeconds(10));
        this.baseUrl = baseUrl;
    }

    public DemoBlazeHomePage open() {
        driver.get(baseUrl);
        waitVisible(productsContainer);
        return this;
    }

    public DemoBlazeHomePage goHome() {
        click(homeLink);
        waitVisible(productsContainer);
        return this;
    }

    public DemoBlazeHomePage selectPhones() {
        click(phonesCategory);
        waitForProductsReload();
        return this;
    }

    public DemoBlazeHomePage selectLaptops() {
        click(laptopsCategory);
        waitForProductsReload();
        return this;
    }

    public DemoBlazeHomePage selectMonitors() {
        click(monitorsCategory);
        waitForProductsReload();
        return this;
    }

    public ProductPage openProduct(String productName) {
        By productLink = By.xpath("//a[normalize-space()='" + productName + "']");
        click(productLink);
        // Product page contains product title in h2.name
        return new ProductPage(driver).waitForLoaded(productName);
    }

    public CartPage openCart() {
        click(cartLink);
        return new CartPage(driver).waitForLoaded();
    }

    private void waitForProductsReload() {
        // DemoBlaze product grid updates asynchronously; this makes sure at least one
        // product card is present
        // after selecting a category.
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(productsContainer, By.cssSelector(".card")));
    }
}
