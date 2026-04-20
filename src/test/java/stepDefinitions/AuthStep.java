package stepDefinitions;

import config.Config;
import drivers.DriverManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.TopbarPage;

import java.time.Duration;
import java.util.List;

public class AuthStep {

    private WebDriver driver;
    private WebDriverWait wait;

    private final By usernameInput = By.xpath("//input[@name='username']");
    private final By passwordInput = By.xpath("//input[@type='password']");
    private final By loginButton = By.xpath("//button[@type='submit']");
    private final By alertMessage = By.xpath("//p[contains(@class,'oxd-alert-content-text')]");
    private final By requiredFieldErrors = By.xpath("//span[text()='Required']");
    private final By orangeHrmHeader = By.xpath("//img[@alt='client brand banner']");

    private void initDriver() {
        driver = DriverManager.getDriver();
        Assert.assertNotNull(driver, "WebDriver is not initialized. Check Cucumber hooks and tags.");
        wait = new WebDriverWait(driver, Duration.ofSeconds(Config.implicitWaitSeconds()));
    }

    @Given("I am on the OrangeHRM login page")
    public void i_am_on_the_orange_hrm_login_page() {
        initDriver();
        driver.navigate().to(Config.loginUrl());
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
    }

    @When("I enter username {string}")
    public void i_enter_username(String username) {
        WebElement usernameBox = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        usernameBox.clear();
        usernameBox.sendKeys(username);
    }

    @And("I enter password {string}")
    public void i_enter_password(String password) {
        WebElement passwordBox = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        passwordBox.clear();
        passwordBox.sendKeys(password);
    }

    @And("I click the Login button")
    public void i_click_the_login_button() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    @Then("I should be redirected to the Dashboard")
    public void i_should_be_redirected_to_the_dashboard() {
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        TopbarPage topbarPage = new TopbarPage(driver);
        Assert.assertEquals(topbarPage.getMenuTitle(), "Dashboard", "User is not on Dashboard page");
    }

    @And("I should see the OrangeHRM header")
    public void i_should_see_the_orange_hrm_header() {
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(orangeHrmHeader)).isDisplayed(),
                "OrangeHRM header is not visible");
    }

    @Then("I should see the error message {string}")
    public void i_should_see_the_error_message(String expectedMessage) {
        String actualMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage)).getText().trim();
        Assert.assertEquals(actualMessage, expectedMessage, "Unexpected login error message");
    }

    @And("I should remain on the login page")
    public void i_should_remain_on_the_login_page() {
        wait.until(ExpectedConditions.urlContains("/auth/login"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertNotNull(currentUrl, "Current URL is null");
        Assert.assertTrue(currentUrl.contains("/auth/login"), "User is not on login page");
    }

    @When("I leave the username field empty")
    public void i_leave_the_username_field_empty() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).clear();
    }

    @And("I leave the password field empty")
    public void i_leave_the_password_field_empty() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput)).clear();
    }

    @Then("I should see a required field error for username")
    public void i_should_see_a_required_field_error_for_username() {
        List<WebElement> errors = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(requiredFieldErrors));
        Assert.assertFalse(errors.isEmpty(), "Username required field error is not displayed");
    }

    @And("I should see a required field error for password")
    public void i_should_see_a_required_field_error_for_password() {
        List<WebElement> errors = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(requiredFieldErrors));
        Assert.assertTrue(errors.size() >= 2, "Password required field error is not displayed");
    }

    @Given("I am logged in as {string} with password {string}")
    public void i_am_logged_in_as_with_password(String username, String password) {
        i_am_on_the_orange_hrm_login_page();
        i_enter_username(username);
        i_enter_password(password);
        i_click_the_login_button();
        i_should_be_redirected_to_the_dashboard();
    }

    @When("I click the user profile menu")
    public void i_click_the_user_profile_menu() {
        TopbarPage topbarPage = new TopbarPage(driver);
        topbarPage.selectProfile();
    }

    @And("I click Logout")
    public void i_click_logout() {
        TopbarPage topbarPage = new TopbarPage(driver);
        topbarPage.clickLogout("Logout");
    }

    @Then("I should be redirected to the login page")
    public void i_should_be_redirected_to_the_login_page() {
        wait.until(ExpectedConditions.urlContains("/auth/login"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertNotNull(currentUrl, "Current URL is null");
        Assert.assertTrue(currentUrl.contains("/auth/login"), "User is not redirected to login page");
    }

    @When("the user session expires")
    public void the_user_session_expires() {
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();
    }

    @And("I should see the session expired message")
    public void i_should_see_the_session_expired_message() {
        List<WebElement> sessionMessages = driver.findElements(By.xpath(
                "//*[contains(translate(normalize-space(.),'SESSION EXPIRED','session expired'),'session expired')]"));
        Assert.assertFalse(sessionMessages.isEmpty(), "Session expired message is not displayed");
    }
}
