package com.example.seleniumspringbootjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point.
 *
 * Even though this project is primarily used as a Selenium test framework,
 * having a proper @SpringBootApplication class enables @SpringBootTest and
 * other
 * Spring testing features.
 */
@SpringBootApplication
public class SeleniumspringbootjavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeleniumspringbootjavaApplication.class, args);
    }
}
