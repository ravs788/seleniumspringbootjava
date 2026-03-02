package com.example.seleniumspringbootjava.config;

import com.example.seleniumspringbootjava.support.TestClassScope;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import java.util.HashMap;
import java.util.Map;

/**
 * Registers custom Spring scopes for tests.
 */
@TestConfiguration
public class TestScopeConfig {

    public static final String TEST_CLASS_SCOPE_NAME = "testClass";

    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        Map<String, Object> scopes = new HashMap<>();
        scopes.put(TEST_CLASS_SCOPE_NAME, new TestClassScope());
        configurer.setScopes(scopes);
        return configurer;
    }
}
