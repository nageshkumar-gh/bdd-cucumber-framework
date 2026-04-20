package hooks;

import config.Config;
import context.ScenarioContext;
import drivers.DriverFactory;
import drivers.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AllureReportUtils;

import java.time.Duration;

public class Hooks {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    private final ScenarioContext scenarioContext;

    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(order = 0)
    public void setUp() {
        scenarioContext.clear();
        WebDriver driver = DriverFactory.createDriver();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Config.pageLoadTimeoutSeconds()));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Config.implicitWaitSeconds()));
        if (!Config.headless()) {
            driver.manage().window().maximize();
        }
        DriverManager.setDriver(driver);
        log.info("WebDriver started (headless={})", Config.headless());
    }

    @After(order = 100)
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();
        if (scenario.isFailed() && driver != null) {
            try {
                AllureReportUtils.attachScreenshotIfPossible("Failure screenshot", driver);
                AllureReportUtils.attachText("URL at failure", driver.getCurrentUrl());
            } catch (RuntimeException e) {
                log.warn("Failed attaching failure diagnostics: {}", e.toString());
            }
        }
        DriverManager.quitDriver();
        scenarioContext.clear();
        log.info("WebDriver quit for scenario: {}", scenario.getName());
    }
}
