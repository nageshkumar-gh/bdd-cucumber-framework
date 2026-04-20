package utils;

import config.Config;
import drivers.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Centralized explicit waits (prefer explicit waits over broad implicit waits).
 */
public final class WaitUtils {

    public WebDriverWait defaultWait() {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized. Ensure Hooks ran before steps.");
        }
        return new WebDriverWait(driver, Duration.ofSeconds(Config.explicitWaitSeconds()));
    }

    public WebDriverWait waitForSeconds(long seconds) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized.");
        }
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }
}
