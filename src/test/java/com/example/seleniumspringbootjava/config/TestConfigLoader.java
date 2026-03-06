package com.example.seleniumspringbootjava.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads framework-level test configuration from JSON under src/test/resources.
 *
 * Override file location with JVM system property:
 * -Dtest.config=classpath:config/test-config.json
 */
public final class TestConfigLoader {

    public static final String CONFIG_LOCATION_PROP = "test.config";
    public static final String CONFIG_LOCATION_PROP_NO_DOTS = "testConfig";

    private static final String DEFAULT_CONFIG_LOCATION = "classpath:config/test-config.json";

    private static volatile TestConfig cached;

    private TestConfigLoader() {
    }

    public static TestConfig load() {
        if (cached != null) {
            return cached;
        }
        synchronized (TestConfigLoader.class) {
            if (cached != null) {
                return cached;
            }

            String location = System.getProperty(CONFIG_LOCATION_PROP);
            if (location == null || location.isBlank()) {
                location = System.getProperty(CONFIG_LOCATION_PROP_NO_DOTS, DEFAULT_CONFIG_LOCATION);
            }
            cached = readConfig(location);
            validate(cached);
            return cached;
        }
    }

    private static TestConfig readConfig(String location) {
        String classpathResource = toClasspathResource(location);

        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = TestConfigLoader.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (is == null) {
                throw new IllegalStateException("Could not find config resource on classpath: " + classpathResource
                        + " (from location: " + location + ")");
            }
            return mapper.readValue(is, TestConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read config: " + location, e);
        }
    }

    private static String toClasspathResource(String location) {
        if (location.startsWith("classpath:")) {
            return location.substring("classpath:".length());
        }
        // Allow passing raw classpath relative path like "config/test-config.json"
        return location;
    }

    private static void validate(TestConfig cfg) {
        if (cfg.getBrowsers() == null || cfg.getBrowsers().isEmpty()) {
            throw new IllegalStateException("Config must contain non-empty 'browsers' list.");
        }
        if (cfg.getBaseUrl() == null || cfg.getBaseUrl().isBlank()) {
            throw new IllegalStateException("Config must contain non-empty 'baseUrl'.");
        }
        if (cfg.getMaxConcurrentBrowsers() <= 0) {
            throw new IllegalStateException("Config 'maxConcurrentBrowsers' must be > 0.");
        }

        // Allow overriding headless mode from CLI without editing JSON:
        // -Dheadless=true|false
        String headlessOverride = System.getProperty("headless");
        if (headlessOverride != null && !headlessOverride.isBlank()) {
            cfg.setHeadless(Boolean.parseBoolean(headlessOverride));
        }

        if (cfg.getWindow() == null) {
            TestConfig.Window w = new TestConfig.Window();
            w.setMaximize(true);
            w.setWidth(1280);
            w.setHeight(800);
            cfg.setWindow(w);
        }
    }
}
