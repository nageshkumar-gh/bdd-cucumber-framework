package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import config.Config;
import drivers.DriverFactory;
import drivers.DriverManager;
import io.qameta.allure.Attachment;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.time.Duration;

public class Hooks {

    @Before(order = 0)
    public void setUp() {
        WebDriver driver = DriverFactory.createDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Config.implicitWaitSeconds()));
        DriverManager.setDriver(driver);
    }

    @After(order = 100)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            attachScreenshot();
        }
        DriverManager.quitDriver();
    }

    @Attachment(value = "Failure screenshot", type = "image/png")
    public byte[] attachScreenshot() {
        WebDriver driver = DriverManager.getDriver();
        if (driver instanceof TakesScreenshot takesScreenshot) {
            return takesScreenshot.getScreenshotAs(OutputType.BYTES);
        }
        return new byte[0];
    }
}
