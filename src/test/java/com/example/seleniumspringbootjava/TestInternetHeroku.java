package com.example.seleniumspringbootjava;

import com.example.seleniumspringbootjava.pages.theinternet.AddRemoveElePage;
import com.example.seleniumspringbootjava.pages.theinternet.BasicAuthPage;
import com.example.seleniumspringbootjava.pages.theinternet.DropDownPage;
import com.example.seleniumspringbootjava.pages.theinternet.HomePage;
import com.example.seleniumspringbootjava.dataloader.DataLoaders;
import com.example.seleniumspringbootjava.support.SpringSeleniumTestBase;
import com.example.seleniumspringbootjava.support.Tags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Migrated from:
 * ../blazedemo/src/test/java/com/example/TestInternetHeroku.java
 *
 * Target site: https://the-internet.herokuapp.com/
 *
 * Uses Spring Boot + JUnit 5 with one WebDriver per test class.
 */
class TestInternetHeroku extends SpringSeleniumTestBase {

    private AddRemoveElePage addRemoveElePage;
    private HomePage homePage;
    private DropDownPage dropDownPage;
    private BasicAuthPage basicAuthPage;

    @BeforeEach
    void setup() {
        WebDriver driver = driver();
        initPages(driver);

        Map<String, Object> common = DataLoaders.json().loadCommon(getClass());
        String url = (String) common.getOrDefault("baseUrl", baseUrl());
        driver.get(url);
    }

    private void initPages(WebDriver driver) {
        this.addRemoveElePage = new AddRemoveElePage(driver);
        this.homePage = new HomePage(driver);
        this.dropDownPage = new DropDownPage(driver);
        this.basicAuthPage = new BasicAuthPage(driver);
    }

    @com.example.seleniumspringbootjava.support.MultiBrowserTest
    @Tag(Tags.SMOKE)
    @Tag(Tags.REGRESSION)
    void addAndRemoveElements() {
        homePage.navigateToPage("Add/Remove Elements");
        addRemoveElePage.addElements(2);
        assertEquals(2, addRemoveElePage.getDeleteButtonCount());

        addRemoveElePage.deleteFirstIfPresent();
        assertEquals(1, addRemoveElePage.getDeleteButtonCount());

        addRemoveElePage.deleteAll();
        assertEquals(0, addRemoveElePage.getDeleteButtonCount());

        driver().navigate().back();
    }

    @com.example.seleniumspringbootjava.support.MultiBrowserTest
    @Tag(Tags.REGRESSION)
    void addRemoveElementsZeroIsStable() {
        homePage.navigateToPage("Add/Remove Elements");
        addRemoveElePage.deleteAll();
        assertEquals(0, addRemoveElePage.getDeleteButtonCount());
    }

    @com.example.seleniumspringbootjava.support.MultiBrowserTest
    @Tag(Tags.SMOKE)
    @Tag(Tags.REGRESSION)
    void dropdownSelection() {
        homePage.navigateToPage("Dropdown");

        dropDownPage.selectElement("Option 1");
        assertEquals("Option 1", dropDownPage.getSelection());
    }

    @com.example.seleniumspringbootjava.support.MultiBrowserTest
    @Tag(Tags.REGRESSION)
    void dropdownOption2Selection() {
        homePage.navigateToPage("Dropdown");

        dropDownPage.selectElement("Option 2");
        assertEquals("Option 2", dropDownPage.getSelection());
    }

    @com.example.seleniumspringbootjava.support.MultiBrowserTest
    @Tag(Tags.SMOKE)
    @Tag(Tags.REGRESSION)
    void handleAuthPage() {
        // Basic auth is flaky in modern Firefox due to native auth prompt behavior.
        // Navigate directly with credentials (page object handles retry).
        basicAuthPage.handleAuth("admin", "admin");

        assertEquals("Congratulations! You must have the proper credentials.", basicAuthPage.getAuthMessage().trim());
    }

    @com.example.seleniumspringbootjava.support.MultiBrowserTest
    @Tag(Tags.REGRESSION)
    void basicAuthValidCredentialsShowsSuccessMessage() {
        basicAuthPage.handleAuth("admin", "admin");

        assertTrue(
                basicAuthPage.getAuthMessage().contains("Congratulations!"),
                "Expected auth success message but got: " + basicAuthPage.getAuthMessage());
    }
}
