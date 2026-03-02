package com.example.seleniumspringbootjava.pages.theinternet;

import com.example.seleniumspringbootjava.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for https://the-internet.herokuapp.com/dropdown
 */
public class DropDownPage extends BasePage {

    // Locators
    private final By eleDropDown = By.id("dropdown");

    public DropDownPage(WebDriver driver) {
        super(driver);
    }

    public void selectElement(String selection) {
        if (!verifySelection(selection)) {
            selectByVisibleText(eleDropDown, selection);
        }
    }

    public boolean verifySelection(String selection) {
        Select select = new Select(waitVisible(eleDropDown));
        return select.getFirstSelectedOption().getText().equals(selection);
    }

    public String getSelection() {
        Select select = new Select(waitVisible(eleDropDown));
        return select.getFirstSelectedOption().getText();
    }
}
