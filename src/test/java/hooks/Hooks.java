package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import config.Config;
import drivers.DriverFactory;
import drivers.DriverManager;
import org.openqa.selenium.WebDriver;
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
    public void tearDown() {
        DriverManager.quitDriver();
    }
}

