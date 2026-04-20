package stepDefinitions;

import io.cucumber.java.en.*;
import drivers.DriverManager;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import org.testng.Assert;
import pages.TopbarPage;

public class LoginStep {

    private WebDriver driver;
    private LoginPage loginPage;
    private TopbarPage topbarPage;

    private static final String LOGIN_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @Given("the user is on the login page")
    public void user_is_on_the_login_page() {
        driver = DriverManager.getDriver();
        driver.navigate().to(LOGIN_URL);
        loginPage = new LoginPage(driver);
    }

    @When("the user enters valid credentials")
    public void user_enters_valid_username_and_password() {
        loginPage.setUsername("Admin");
        loginPage.setPassword("admin123");
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

