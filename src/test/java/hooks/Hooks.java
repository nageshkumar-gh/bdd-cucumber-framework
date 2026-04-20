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
import pages.LoginPage;

import java.time.Duration;

public class Hooks {

    @Before("@login or @pim")
    public void setUp() {
        WebDriver driver = DriverFactory.createDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Config.implicitWaitSeconds()));
        DriverManager.setDriver(driver);
    }

    @Before("@pim")
    public void loginForPim() {
        WebDriver driver = DriverManager.getDriver();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.goto_url();
        loginPage.setUsername(Config.username());
        loginPage.setPassword(Config.password());
        loginPage.clickLogin();
    }

    @After("@login or @pim")
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

