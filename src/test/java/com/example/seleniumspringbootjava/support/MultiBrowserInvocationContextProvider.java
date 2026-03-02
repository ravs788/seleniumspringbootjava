package com.example.seleniumspringbootjava.support;

import com.example.seleniumspringbootjava.config.BrowserType;
import com.example.seleniumspringbootjava.config.TestConfig;
import com.example.seleniumspringbootjava.config.TestConfigLoader;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.List;
import java.util.stream.Stream;

public class MultiBrowserInvocationContextProvider implements TestTemplateInvocationContextProvider {

    private static final TestConfig CONFIG = TestConfigLoader.load();

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        // If user forces a single browser, provide only that invocation.
        String forced = System.getProperty("browser");
        if (forced != null && !forced.isBlank()) {
            BrowserType forcedBrowser = BrowserType.from(forced);
            return Stream.of(invocation(forcedBrowser));
        }

        List<String> configured = CONFIG.getBrowsers();
        return configured.stream()
                .map(BrowserType::from)
                .distinct()
                .map(this::invocation);
    }

    private TestTemplateInvocationContext invocation(BrowserType browser) {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return "[" + browser.name().toLowerCase() + "]";
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return List.of(
                        new BrowserParameterResolver(browser),
                        (Extension) (org.junit.jupiter.api.extension.BeforeEachCallback) context -> MultiBrowserRuntime
                                .setBrowserFromTemplate(browser),
                        (Extension) (org.junit.jupiter.api.extension.AfterEachCallback) context -> MultiBrowserRuntime
                                .clear());
            }
        };
    }

    private static final class BrowserParameterResolver implements org.junit.jupiter.api.extension.ParameterResolver {
        private final BrowserType browser;

        private BrowserParameterResolver(BrowserType browser) {
            this.browser = browser;
        }

        @Override
        public boolean supportsParameter(org.junit.jupiter.api.extension.ParameterContext parameterContext,
                ExtensionContext extensionContext) {
            return parameterContext.getParameter().getType().equals(BrowserType.class);
        }

        @Override
        public Object resolveParameter(org.junit.jupiter.api.extension.ParameterContext parameterContext,
                ExtensionContext extensionContext) {
            return browser;
        }
    }
}
