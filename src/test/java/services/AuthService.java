package services;

import config.Config;
import constants.ContextKeys;
import context.ScenarioContext;
import drivers.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pages.LoginPage;
import pages.TopbarPage;
import org.testng.Assert;
import utils.WaitUtils;

import java.util.List;

/**
 * Authentication flows and assertions (business orchestration over page objects).
 */
public class AuthService {
    private final WaitUtils waitUtils;
    private final ScenarioContext scenarioContext;

    public AuthService(WaitUtils waitUtils, ScenarioContext scenarioContext) {
        this.waitUtils = waitUtils;
        this.scenarioContext = scenarioContext;
    }

    public void openLoginPage() {
        Allure.step("Open login page: " + Config.loginUrl(), () -> {
            LoginPage loginPage = new LoginPage(waitUtils);
            loginPage.open(Config.loginUrl());
        });
    }

    public void enterUsername(String username) {
        Allure.step("Enter username: " + username, () -> {
            new LoginPage(waitUtils).enterUsername(username);
            scenarioContext.put(ContextKeys.LAST_USERNAME, username);
        });
    }

    public void enterPassword(String password) {
        Allure.step("Enter password", () -> new LoginPage(waitUtils).enterPassword(password));
    }

    public void clickLogin() {
        Allure.step("Click Login", () -> new LoginPage(waitUtils).clickLogin());
    }

    public void assertDashboardLoaded() {
        Allure.step("Assert Dashboard is loaded", () -> {
            waitUtils.defaultWait().until(d -> d.getCurrentUrl().contains("/dashboard"));
            TopbarPage topbarPage = new TopbarPage(waitUtils);
            Assert.assertEquals(topbarPage.getMenuTitle(), "Dashboard", "User is not on Dashboard page");
        });
    }

    public void assertOrangeHrmHeaderVisible() {
        Allure.step("Assert OrangeHRM header is visible", () -> {
            boolean visible = new LoginPage(waitUtils).isOrangeHrmHeaderVisible();
            Assert.assertTrue(visible, "OrangeHRM header is not visible");
        });
    }

    public void assertLoginErrorMessage(String expectedMessage) {
        Allure.step("Assert login error message: " + expectedMessage, () -> {
            String actual = new LoginPage(waitUtils).getAlertMessageText();
            Assert.assertEquals(actual, expectedMessage, "Unexpected login error message");
        });
    }

    public void assertOnLoginPage() {
        Allure.step("Assert user remains on login page", () -> {
            waitUtils.defaultWait().until(d -> d.getCurrentUrl().contains("/auth/login"));
            String currentUrl = waitUtils.defaultWait().until(d -> d.getCurrentUrl());
            Assert.assertNotNull(currentUrl, "Current URL is null");
            Assert.assertTrue(currentUrl.contains("/auth/login"), "User is not on login page");
        });
    }

    public void clearUsername() {
        new LoginPage(waitUtils).clearUsername();
    }

    public void clearPassword() {
        new LoginPage(waitUtils).clearPassword();
    }

    public void assertUsernameRequiredError() {
        List<WebElement> errors = new LoginPage(waitUtils).requiredFieldErrors();
        Assert.assertFalse(errors.isEmpty(), "Username required field error is not displayed");
    }

    public void assertPasswordRequiredError() {
        List<WebElement> errors = new LoginPage(waitUtils).requiredFieldErrors();
        Assert.assertTrue(errors.size() >= 2, "Password required field error is not displayed");
    }

    public void loginWithCredentials(String username, String password) {
        openLoginPage();
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        assertDashboardLoaded();
    }

    public void openProfileMenu() {
        Allure.step("Open user profile menu", () -> new TopbarPage(waitUtils).selectProfile());
    }

    public void logoutFromProfileMenu() {
        Allure.step("Logout from profile menu", () -> new TopbarPage(waitUtils).clickLogout("Logout"));
    }

    public void assertRedirectedToLoginPage() {
        Allure.step("Assert redirected to login page", () -> {
            waitUtils.defaultWait().until(d -> d.getCurrentUrl().contains("/auth/login"));
            String currentUrl = waitUtils.defaultWait().until(d -> d.getCurrentUrl());
            Assert.assertNotNull(currentUrl, "Current URL is null");
            Assert.assertTrue(currentUrl.contains("/auth/login"), "User is not redirected to login page");
        });
    }

    public void expireSession() {
        Allure.step("Expire session (clear cookies + refresh)", () -> {
            var driver = DriverManager.getDriver();
            driver.manage().deleteAllCookies();
            driver.navigate().refresh();
        });
    }

    public void assertSessionExpiredMessagePresent() {
        Allure.step("Assert session expired message is present", () -> {
            var driver = DriverManager.getDriver();
            List<WebElement> sessionMessages = driver.findElements(By.xpath(
                    "//*[contains(translate(normalize-space(.),'SESSION EXPIRED','session expired'),'session expired')]"));
            Assert.assertFalse(sessionMessages.isEmpty(), "Session expired message is not displayed");
        });
    }
}
