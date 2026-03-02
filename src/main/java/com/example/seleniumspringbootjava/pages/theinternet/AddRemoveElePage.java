package com.example.seleniumspringbootjava.pages.theinternet;

import com.example.seleniumspringbootjava.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for https://the-internet.herokuapp.com/add_remove_elements/
 *
 * WebDriver is created/owned by the test. This page object receives the driver
 * via constructor and provides page-related operations.
 */
public class AddRemoveElePage extends BasePage {

    // Locators
    private final By addElementButton = By.xpath("//button[normalize-space()='Add Element']");
    private final By deleteButtons = By.cssSelector("#elements button.added-manually");

    public AddRemoveElePage(WebDriver driver) {
        super(driver);
    }

    public AddRemoveElePage clickAddElement() {
        click(addElementButton);
        return this;
    }

    /** Clicks "Add Element" n times. */
    public AddRemoveElePage addElements(int count) {
        for (int i = 0; i < count; i++) {
            clickAddElement();
        }
        return this;
    }

    /** Returns current number of Delete buttons. */
    public int getDeleteButtonCount() {
        return driver.findElements(deleteButtons).size();
    }

    /** Returns the current list of Delete buttons (fresh lookup). */
    public List<WebElement> getDeleteButtons() {
        return driver.findElements(deleteButtons);
    }

    /** Clicks the first Delete button if present. */
    public AddRemoveElePage deleteFirstIfPresent() {
        List<WebElement> buttons = getDeleteButtons();
        if (!buttons.isEmpty()) {
            buttons.get(0).click();
        }
        return this;
    }

    /** Deletes all Delete buttons currently present. */
    public AddRemoveElePage deleteAll() {
        while (getDeleteButtonCount() > 0) {
            deleteFirstIfPresent();
        }
        return this;
    }
}
