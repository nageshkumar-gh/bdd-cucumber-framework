package stepDefinitions;

import io.cucumber.java.en.*;
import config.Config;
import drivers.DriverManager;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import org.testng.Assert;
import pages.TopbarPage;

public class LoginStep {

    private WebDriver driver;
    private LoginPage loginPage;
    private TopbarPage topbarPage;

    @Given("the user is on the login page")
    public void user_is_on_the_login_page() {
        driver = DriverManager.getDriver();
        driver.navigate().to(Config.loginUrl());
        loginPage = new LoginPage(driver);
    }

    @When("the user enters valid credentials")
    public void user_enters_valid_username_and_password() {
        loginPage.setUsername(Config.username());
        loginPage.setPassword(Config.password());
        loginPage.clickLogin();
    }

    @Then("the user should be redirected to the dashboard")
    public void user_should_be_redirected_to_the_dashboard() {
        topbarPage = new TopbarPage(driver);
        Assert.assertEquals(topbarPage.getMenuTitle(), "Dashboard", "User is not on the Dashboard page");
    }

    @And("the user logouts of the application")
    public void theUserLogoutsOfTheApplication() {
        topbarPage.selectProfile();
        topbarPage.clickLogout("Logout");
    }

    @When("the user enters invalid credentials")
    public void theUserEntersInvalidCredentials() {
        loginPage.setUsername("InvalidUser");
        loginPage.setPassword("InvalidPass");
        loginPage.clickLogin();
    }

    @Then("the user should see an error message")
    public void theUserShouldSeeAnErrorMessage() {
    }
}

