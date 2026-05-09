package com.framework.pages;

import com.framework.config.ConfigReader;
import com.framework.driver.DriverManager;
import com.framework.exceptions.FrameworkException;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base class for all Page Objects.
 *
 * <p><b>Why this exists</b>:
 * We enforce one rule across the framework: every UI interaction goes through a single, wait-wrapped
 * API to reduce flaky tests. If each page/step uses {@code driver.findElement()} directly, timing
 * issues spread everywhere and become hard to debug and fix.
 *
 * <p><b>Important constraint</b>:
 * This class is the ONLY place in our test code where element lookup/wait logic should live.
 * Page Objects should only define locators and call these methods.
 */
public abstract class BasePage {

    private static final Logger LOGGER = LogManager.getLogger(BasePage.class);

    /**
     * WebDriver for the current thread.
     *
     * <p><b>Why</b>: DriverManager isolates WebDriver per thread for parallel safety.
     */
    protected final WebDriver driver;

    /**
     * Explicit wait used by all element interactions.
     *
     * <p><b>Why</b>: explicit waits are deterministic; implicit waits tend to hide timing issues and
     * interact poorly with explicit waits.
     */
    protected final WebDriverWait wait;

    /**
     * Creates a BasePage bound to the current thread's {@link WebDriver}.
     *
     * <p><b>Why</b>: Page Objects should not decide how drivers are created or stored; they simply
     * consume the current thread's driver.
     */
    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInstance().getWaitTimeoutSeconds()));
    }

    /**
     * Clicks an element after waiting for it to be clickable.
     *
     * <p><b>Why</b>: click timing is one of the most common causes of flakiness (overlays, animations,
     * not-yet-enabled buttons). Waiting for clickability makes the action resilient.
     *
     * @param locator element locator
     */
    protected void click(By locator) {
        try {
            WebElement element = waitForElementClickable(locator);
            element.click();
        } catch (TimeoutException ex) {
            throw FrameworkException.withCause("Timed out waiting to click element: " + locator, ex);
        }
    }

    /**
     * Types text into an element after waiting for it to be visible.
     *
     * <p><b>Why</b>: visibility is the minimal readiness signal for safe typing; it prevents
     * sending keys to detached/hidden elements.
     *
     * @param locator element locator
     * @param text    text to type (null becomes empty string)
     */
    protected void type(By locator, String text) {
        try {
            WebElement element = waitForElementVisible(locator);
            element.clear();
            element.sendKeys(text == null ? "" : text);
        } catch (TimeoutException ex) {
            throw FrameworkException.withCause("Timed out waiting to type into element: " + locator, ex);
        }
    }

    /**
     * Reads visible text from an element after waiting for it to be visible.
     *
     * @param locator element locator
     * @return element text
     */
    protected String getText(By locator) {
        try {
            return waitForElementVisible(locator).getText();
        } catch (TimeoutException ex) {
            throw FrameworkException.withCause("Timed out waiting to get text from element: " + locator, ex);
        }
    }

    /**
     * Checks whether an element is displayed.
     *
     * <p><b>Why this method exists</b>:
     * Visibility checks are frequently used in step assertions. We return false when the element
     * is absent instead of throwing, so steps can assert on state cleanly.
     *
     * @param locator element locator
     * @return true if present and displayed; otherwise false
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    /**
     * Waits for an element to become visible and returns it.
     *
     * @param locator element locator
     * @return visible {@link WebElement}
     */
    protected WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for an element to become clickable and returns it.
     *
     * @param locator element locator
     * @return clickable {@link WebElement}
     */
    protected WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until the current URL contains the expected fragment.
     *
     * <p><b>Why this exists</b>:
     * Modern web apps often update state asynchronously after a click (login redirects, SPA routing).
     * Waiting on URL change is a low-maintenance synchronization point that avoids adding
     * premature dashboard locators just to confirm navigation.
     *
     * @param urlFragment fragment that must be present in the current URL
     */
    protected void waitForUrlContains(String urlFragment) {
        if (urlFragment == null || urlFragment.isBlank()) {
            throw new FrameworkException("Invalid wait configuration: urlFragment must not be null/blank");
        }
        try {
            wait.until(ExpectedConditions.urlContains(urlFragment));
        } catch (TimeoutException ex) {
            throw FrameworkException.withCause("Timed out waiting for URL to contain '" + urlFragment + "'", ex);
        }
    }

    /**
     * Waits until the browser reports the document is fully loaded.
     *
     * <p><b>Why</b>: some pages trigger JS-heavy transitions; this adds a lightweight guard that
     * complements element-level waits.
     */
    protected void waitForPageLoad() {
        try {
            wait.until(driver -> {
                Object state = ((JavascriptExecutor) driver).executeScript("return document.readyState");
                return "complete".equals(state);
            });
        } catch (TimeoutException ex) {
            LOGGER.warn("Timed out waiting for page load completion (document.readyState=complete).");
        }
    }

    /**
     * Returns the current page title.
     *
     * <p><b>Why</b>: title checks are occasionally useful as a cheap navigation assertion in steps.
     *
     * @return current page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }
}

