package com.example.seleniumspringbootjava.config;

import com.example.seleniumspringbootjava.pages.demoblaze.DemoBlazeHomePage;
import com.example.seleniumspringbootjava.pages.theinternet.AddRemoveElePage;
import com.example.seleniumspringbootjava.pages.theinternet.BasicAuthPage;
import com.example.seleniumspringbootjava.pages.theinternet.DropDownPage;
import com.example.seleniumspringbootjava.pages.theinternet.HomePage;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * Spring Boot test configuration for Page Objects.
 *
 * Prototype scope is important so each test class (with its own WebDriver)
 * receives page objects bound to that specific driver instance.
 */
@TestConfiguration
public class PageObjectConfig {

    // The-Internet pages
    @Bean
    @Scope("prototype")
    public HomePage theInternetHomePage(WebDriver driver) {
        return new HomePage(driver);
    }

    @Bean
    @Scope("prototype")
    public AddRemoveElePage addRemoveElePage(WebDriver driver) {
        return new AddRemoveElePage(driver);
    }

    @Bean
    @Scope("prototype")
    public DropDownPage dropDownPage(WebDriver driver) {
        return new DropDownPage(driver);
    }

    @Bean
    @Scope("prototype")
    public BasicAuthPage basicAuthPage(WebDriver driver) {
        return new BasicAuthPage(driver);
    }

    // DemoBlaze pages
    @Bean
    @Scope("prototype")
    public DemoBlazeHomePage demoBlazeHomePage(WebDriver driver) {
        return new DemoBlazeHomePage(driver);
    }
}
