package com.example.seleniumspringbootjava;

import com.example.seleniumspringbootjava.dataloader.DataLoaders;
import com.example.seleniumspringbootjava.model.demoblaze.OrderDetails;
import com.example.seleniumspringbootjava.pages.demoblaze.CartPage;
import com.example.seleniumspringbootjava.pages.demoblaze.DemoBlazeHomePage;
import com.example.seleniumspringbootjava.pages.demoblaze.OrderModal;
import com.example.seleniumspringbootjava.pages.demoblaze.ProductPage;
import com.example.seleniumspringbootjava.support.MultiBrowserTest;
import com.example.seleniumspringbootjava.support.SpringSeleniumTestBase;
import com.example.seleniumspringbootjava.support.Tags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Additional stable DemoBlaze tests.
 *
 * These tests use the current per-test-method WebDriver lifecycle from
 * {@link SpringSeleniumTestBase}.
 */
class DemoBlazeTests extends SpringSeleniumTestBase {

    @BeforeEach
    void openBaseUrl() {
        WebDriver driver = driver();

        Map<String, Object> common = DataLoaders.json().loadCommon(getClass());
        String url = (String) common.getOrDefault("baseUrl", baseUrl());
        driver.get(url);
    }

    @MultiBrowserTest
    @Tag(Tags.SMOKE)
    @Tag(Tags.REGRESSION)
    void categoriesNavigationDoesNotError() {
        DemoBlazeHomePage home = new DemoBlazeHomePage(driver());

        assertDoesNotThrow(home::selectPhones);
        assertDoesNotThrow(home::selectLaptops);
        assertDoesNotThrow(home::selectMonitors);
        assertDoesNotThrow(home::goHome);
    }

    @MultiBrowserTest
    @Tag(Tags.SMOKE)
    @Tag(Tags.REGRESSION)
    void openProductDetailsPageLoads() {
        DemoBlazeHomePage home = new DemoBlazeHomePage(driver());
        home.selectPhones();

        ProductPage product = home.openProduct("Samsung galaxy s6");
        assertNotNull(product);
        // ProductPage has its own waitForLoaded call in openProduct, so reaching here
        // is a strong signal.
    }

    @MultiBrowserTest
    @Tag(Tags.SMOKE)
    @Tag(Tags.REGRESSION)
    void cartPageLoadsFromHome() {
        DemoBlazeHomePage home = new DemoBlazeHomePage(driver());

        CartPage cart = home.openCart();
        assertNotNull(cart);

        // Basic smoke assertion: we should be on a cart-like page with URL containing
        // "cart"
        String url = driver().getCurrentUrl().toLowerCase();
        assertTrue(url.contains("cart"), "Expected URL to contain 'cart' but was: " + url);
    }

    @MultiBrowserTest
    @Tag(Tags.SMOKE)
    @Tag(Tags.REGRESSION)
    void purchaseFlowWorks() {
        Map<String, Object> common = DataLoaders.json().loadCommon(getClass());

        @SuppressWarnings("unchecked")
        Map<String, Object> purchase = (Map<String, Object>) common.get("purchase");
        if (purchase == null) {
            throw new IllegalStateException(
                    "Missing 'purchase' section in test data common.json for " + getClass().getSimpleName());
        }

        String phoneProductName = (String) purchase.get("phoneProductName");
        String laptopProductName = (String) purchase.get("laptopProductName");

        @SuppressWarnings("unchecked")
        Map<String, Object> orderDetailsData = (Map<String, Object>) purchase.get("orderDetails");
        if (orderDetailsData == null) {
            throw new IllegalStateException("Missing 'purchase.orderDetails' section in test data common.json for "
                    + getClass().getSimpleName());
        }

        DemoBlazeHomePage home = new DemoBlazeHomePage(driver());

        // Phones -> product -> Add to cart
        home.selectPhones();
        ProductPage phone = home.openProduct(phoneProductName);
        phone.addToCartAndAcceptAlert();

        // Back home
        home.goHome();

        // Laptops -> product -> Add to cart
        home.selectLaptops();
        ProductPage laptop = home.openProduct(laptopProductName);
        laptop.addToCartAndAcceptAlert();

        // Cart + Place order + Purchase
        CartPage cart = home.openCart();
        OrderModal order = cart.clickPlaceOrder();

        OrderDetails details = OrderDetails.builder()
                .name((String) orderDetailsData.get("name"))
                .country((String) orderDetailsData.get("country"))
                .city((String) orderDetailsData.get("city"))
                .card((String) orderDetailsData.get("card"))
                .month((String) orderDetailsData.get("month"))
                .year((String) orderDetailsData.get("year"))
                .build();

        order.purchase(details);
        assertTrue(order.isThankYouDisplayed(), "Expected thank you confirmation after purchase.");
    }
}
