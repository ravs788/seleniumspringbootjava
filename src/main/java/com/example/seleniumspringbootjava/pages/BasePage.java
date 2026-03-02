package com.example.seleniumspringbootjava.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base class for all Page Objects.
 *
 * Provides common operations (find/click/type/waits) and exposes the driver to
 * subclasses.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this(driver, Duration.ofSeconds(10));
    }

    protected BasePage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    protected WebElement find(By locator) {
        return driver.findElement(locator);
    }

    protected WebElement waitPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void scrollIntoView(By locator) {
        WebElement element = waitPresent(locator);

        WebDriver realDriver = unwrapDriver(this.driver);

        JavascriptExecutor js = asJavascriptExecutor(realDriver);
        js.executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    protected JavascriptExecutor asJavascriptExecutor(WebDriver driver) {
        if (driver instanceof JavascriptExecutor js) {
            return js;
        }

        // Fall back to Selenium 4 augmentation (works for RemoteWebDriver-like
        // drivers).
        try {
            WebDriver augmented = new org.openqa.selenium.remote.Augmenter().augment(driver);
            if (augmented instanceof JavascriptExecutor js) {
                return js;
            }
        } catch (IllegalArgumentException ignored) {
            // e.g. "Driver must have capabilities"
        }

        throw new IllegalStateException(
                "Driver does not implement JavascriptExecutor (type=" + driver.getClass().getName() + ").");
    }

    /**
     * Attempts to unwrap common Spring/Selenium wrapper/proxy layers to get the
     * underlying concrete driver (e.g., FirefoxDriver) so we can safely cast to
     * JavascriptExecutor / TakesScreenshot / etc.
     */
    protected WebDriver unwrapDriver(WebDriver candidate) {
        if (candidate == null) {
            return null;
        }

        WebDriver current = candidate;

        // 1) Selenium wrapper chain
        try {
            while (current instanceof org.openqa.selenium.WrapsDriver wraps) {
                WebDriver next = wraps.getWrappedDriver();
                if (next == null || next == current) {
                    break;
                }
                current = next;
            }
        } catch (Exception ignored) {
            // ignore and continue to other unwrapping mechanisms
        }

        // 2) Spring AOP proxy (CGLIB / JDK) unwrap
        try {
            if (org.springframework.aop.support.AopUtils.isAopProxy(current)) {
                Object target = org.springframework.aop.framework.AopProxyUtils.getSingletonTarget(current);
                if (target instanceof WebDriver targetDriver) {
                    current = targetDriver;
                }
            }
        } catch (Exception ignored) {
            // ignore; we'll fall back to whatever we have
        }

        // 3) Last resort: Spring scoped proxy usually implements DecoratingProxy
        // which can expose the underlying target class; it cannot always provide the
        // instance, but in many scoped-proxy cases the real driver is also available
        // as a WrapsDriver chain once Spring proxy is bypassed.
        try {
            if (current instanceof org.springframework.core.DecoratingProxy decoratingProxy) {
                // Touching the decorated class is useful mainly for debugging/logging;
                // keeping it avoids "unused variable" warnings while still documenting intent.
                @SuppressWarnings("unused")
                Class<?> decoratedClass = decoratingProxy.getDecoratedClass();

                // If we are still a proxy class, attempt to resolve the current scoped
                // target via AopProxyUtils again (sometimes the singleton target changes
                // after scope activation).
                Object target = org.springframework.aop.framework.AopProxyUtils.getSingletonTarget(current);
                if (target instanceof WebDriver targetDriver) {
                    current = targetDriver;
                }
            }
        } catch (Exception ignored) {
            // ignore
        }

        // 4) If still not a JavascriptExecutor, but it's a JDK proxy, invoke methods
        // via the WebDriver interface is OK; however casting won't work. Callers should
        // use this unwrapped driver only for capability checks/casts.
        return current;
    }

    /**
     * Default click: scroll -> wait visible -> wait clickable -> click.
     */
    protected void click(By locator) {
        scrollIntoView(locator);
        waitVisible(locator);

        // Handle dynamic pages where elements get replaced in the DOM (stale element).
        // Re-acquire the element and retry once.
        try {
            waitClickable(locator).click();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            waitClickable(locator).click();
        }
    }

    /**
     * Default typing: scroll -> wait visible -> clear -> sendKeys.
     */
    protected void type(By locator, String text) {
        scrollIntoView(locator);
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected void selectByVisibleText(By selectLocator, String visibleText) {
        scrollIntoView(selectLocator);
        WebElement el = waitVisible(selectLocator);
        waitClickable(selectLocator);
        new Select(el).selectByVisibleText(visibleText);
    }

    protected String text(By locator) {
        scrollIntoView(locator);
        return waitVisible(locator).getText();
    }

    protected String normalizeText(String raw) {
        // Useful when UI text includes newlines/indentation differences between
        // browsers.
        return raw == null ? null : raw.replace("\r\n", "\n").replace("\r", "\n").trim();
    }
}
