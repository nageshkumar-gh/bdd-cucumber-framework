package com.framework.hooks;

import com.framework.config.ConfigReader;
import com.framework.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber hooks that manage browser lifecycle for each scenario.
 *
 * <p><b>Why this exists</b>:
 * We want a single, predictable place to:
 * - Create/tear down WebDriver (thread-safe via DriverManager).
 * - Navigate to the application under test before steps execute.
 * - Capture evidence (screenshots) automatically on failure.
 *
 * <p><b>Design constraint</b>:
 * Hooks should orchestrate the lifecycle; they should not contain test assertions.
 */
public class Hooks {

    private static final Logger LOGGER = LogManager.getLogger(Hooks.class);
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS");

    /**
     * Starts a browser session and navigates to the base URL before each scenario.
     *
     * <p><b>Why before each scenario</b>:
     * It keeps scenarios isolated and avoids cross-scenario state leakage (cookies, local storage).
     *
     * @param scenario current Cucumber scenario (for logging)
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        ConfigReader config = ConfigReader.getInstance();

        LOGGER.info("Starting scenario: name='{}', tags={}", scenario.getName(), scenario.getSourceTagNames());
        LOGGER.info("Config: env={}, baseUrl={}, browser={}, headless={}, waitTimeoutSeconds={}",
                config.getEnvironment(),
                config.getBaseUrl(),
                config.getBrowser(),
                config.isHeadless(),
                config.getWaitTimeoutSeconds());

        DriverManager.initDriver(config.getBrowser(), config.isHeadless());

        WebDriver driver = DriverManager.getDriver();
        driver.manage().deleteAllCookies();
        driver.get(config.getBaseUrl());
    }

    /**
     * Captures a screenshot on failure and quits the browser after each scenario.
     *
     * <p><b>Why screenshot on failure</b>:
     * Failures are cheapest to debug when you have UI evidence at the point of failure.
     * We store screenshots under {@code target/screenshots} so they are easy to archive in CI.
     *
     * @param scenario current Cucumber scenario (used to detect failure)
     */
    @After
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverManager.isDriverInitialized()) {
                saveFailureScreenshot(scenario);
            }
        } finally {
            if (DriverManager.isDriverInitialized()) {
                DriverManager.quitDriver();
            }
            LOGGER.info("Finished scenario: name='{}', status={}", scenario.getName(), scenario.getStatus());
        }
    }

    /**
     * Saves a screenshot to disk for a failed scenario.
     *
     * <p><b>Why not attach to report yet</b>:
     * Allure attachment wiring comes in Phase 3; for Phase 1 we focus on producing evidence on disk.
     *
     * @param scenario failed scenario
     */
    private void saveFailureScreenshot(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();
        if (!(driver instanceof TakesScreenshot)) {
            LOGGER.warn("Driver does not support screenshots. driverClass={}", driver.getClass().getName());
            return;
        }

        Path screenshotDir = Path.of("target", "screenshots");
        try {
            Files.createDirectories(screenshotDir);
        } catch (IOException ex) {
            LOGGER.warn("Failed to create screenshot directory: {}", screenshotDir.toAbsolutePath(), ex);
            return;
        }

        String safeScenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9._-]+", "_");
        String filename = safeScenarioName + "-" + LocalDateTime.now().format(TS_FORMAT) + ".png";
        Path destination = screenshotDir.resolve(filename);

        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            Files.copy(screenshotFile.toPath(), destination);
            LOGGER.info("Saved failure screenshot: {}", destination.toAbsolutePath());
        } catch (IOException ex) {
            LOGGER.warn("Failed to save screenshot to: {}", destination.toAbsolutePath(), ex);
        }
    }
}

