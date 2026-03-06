package com.example.seleniumspringbootjava.support;

import com.example.seleniumspringbootjava.config.TestConfig;
import com.example.seleniumspringbootjava.config.TestConfigLoader;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * JUnit 5 retry extension for transient failures in Selenium tests.
 *
 * <p>
 * Notes:
 * <ul>
 * <li>Retries happen within the same Maven/Surefire run.</li>
 * <li>Each attempt re-executes the full test lifecycle, including
 * {@code @BeforeEach}/{@code @AfterEach},
 * so a new WebDriver instance is created per attempt (as per
 * {@link SpringSeleniumTestBase}).</li>
 * </ul>
 */
public final class RetryExtension implements InvocationInterceptor {

    private static final TestConfig CONFIG = TestConfigLoader.load();

    private static boolean isDisabledBySystemProperty() {
        // Enables turning off JUnit-level retries when you prefer Maven/bat level
        // reruns (e.g. Selenium Grid runs).
        String v = System.getProperty("noJUnitRetry");
        return v != null && v.equalsIgnoreCase("true");
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext) throws Throwable {

        if (isDisabledBySystemProperty()) {
            invocation.proceed();
            return;
        }

        RetrySettings settings = RetrySettings.fromConfig(CONFIG);

        // maxAttempts=1 => do not retry
        int maxAttempts = Math.max(1, settings.maxAttempts());

        Throwable last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                if (attempt > 1) {
                    addAllureNote("Retry attempt " + attempt + " of " + maxAttempts, extensionContext);

                    // Minimal visibility in console output as well (Allure attachments are not
                    // visible during mvn test).
                    System.out.println("[RETRY] " + extensionContext.getDisplayName()
                            + " attempt " + attempt + "/" + maxAttempts);
                }
                invocation.proceed();
                return;
            } catch (Throwable t) {
                last = t;

                boolean retryable = isRetryable(t, settings);
                boolean hasMore = attempt < maxAttempts;

                if (!retryable || !hasMore) {
                    throw t;
                }

                System.out.println("[RETRY] Will retry after failure: " + t.getClass().getSimpleName()
                        + " - " + safeMessage(t));

                sleepBackoff(settings.backoffMillis());
            }
        }

        // Should never happen, but keep compiler happy.
        if (last != null) {
            throw last;
        }
    }

    private static boolean isRetryable(Throwable t, RetrySettings settings) {
        if (!settings.onlyOnSeleniumTransientFailures()) {
            return true;
        }

        // Minimal, pragmatic set of transient exceptions seen in Selenium/Grid runs.
        // We avoid importing selenium exception classes directly to keep dependency
        // surface small and
        // to allow wrapping exceptions.
        String name = t.getClass().getName();

        if (name.endsWith("StaleElementReferenceException"))
            return true;
        if (name.endsWith("TimeoutException"))
            return true;
        if (name.endsWith("WebDriverException"))
            return true;
        if (name.endsWith("NoSuchSessionException"))
            return true;
        if (name.endsWith("SessionNotCreatedException"))
            return true;

        // Also treat common network-ish issues as retryable.
        String msg = Optional.ofNullable(t.getMessage()).orElse("").toLowerCase();
        if (msg.contains("disconnected") || msg.contains("connection refused") || msg.contains("unable to establish") ||
                msg.contains("chrome not reachable") || msg.contains("timed out")) {
            return true;
        }

        // If wrapped, check causes too.
        Throwable cause = t.getCause();
        if (cause != null && cause != t) {
            return isRetryable(cause, settings);
        }

        return false;
    }

    private static String safeMessage(Throwable t) {
        try {
            return Optional.ofNullable(t.getMessage()).orElse("");
        } catch (Exception ignored) {
            return "";
        }
    }

    private static void sleepBackoff(long backoffMillis) {
        if (backoffMillis <= 0) {
            return;
        }
        try {
            Thread.sleep(backoffMillis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private static void addAllureNote(String message, ExtensionContext context) {
        try {
            String displayName = context.getDisplayName();
            Allure.addAttachment("Retry", "text/plain",
                    "Test: " + displayName + "\n" + message + "\n",
                    ".txt");
        } catch (Exception ignored) {
            // best effort
        }
    }

    private record RetrySettings(int maxAttempts, long backoffMillis, boolean onlyOnSeleniumTransientFailures) {
        static RetrySettings fromConfig(TestConfig config) {
            if (config == null || config.getRetry() == null) {
                return new RetrySettings(1, 0, true);
            }
            TestConfig.Retry r = config.getRetry();
            return new RetrySettings(
                    Math.max(1, r.getMaxAttempts()),
                    Math.max(0, r.getBackoffMillis()),
                    r.isOnlyOnSeleniumTransientFailures());
        }
    }
}
