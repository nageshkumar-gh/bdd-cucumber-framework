package services;

import config.Config;
import io.qameta.allure.Allure;
import pages.LoginPage;
import pages.TopbarPage;
import org.testng.Assert;
import testdata.TestDataLoader;
import testdata.UserCredentials;
import utils.WaitUtils;

/**
 * Legacy login.feature flows (kept separate from {@link AuthService} to avoid mixing Gherkin dialects).
 */
public class LoginService {
    private static final UserCredentials SAMPLE_USERS = TestDataLoader.userCredentials("testdata/users.json");

    private final WaitUtils waitUtils;

    public LoginService(WaitUtils waitUtils) {
        this.waitUtils = waitUtils;
    }

    public void openLoginPage() {
        Allure.step("Open login page", () -> {
            LoginPage loginPage = new LoginPage(waitUtils);
            loginPage.open(Config.loginUrl());
        });
    }

    public void loginWithConfiguredCredentials() {
        Allure.step("Login with configured credentials", () -> {
            LoginPage loginPage = new LoginPage(waitUtils);
            loginPage.enterUsername(Config.username());
            loginPage.enterPassword(Config.password());
            loginPage.clickLogin();
        });
    }

    public void loginWithInvalidCredentials() {
        Allure.step("Login with invalid credentials", () -> {
            LoginPage loginPage = new LoginPage(waitUtils);
            loginPage.enterUsername("InvalidUser");
            loginPage.enterPassword(SAMPLE_USERS.invalidPassword());
            loginPage.clickLogin();
        });
    }

    public void assertOnDashboard() {
        Allure.step("Assert Dashboard title", () -> {
            TopbarPage topbarPage = new TopbarPage(waitUtils);
            Assert.assertEquals(topbarPage.getMenuTitle(), "Dashboard", "User is not on the Dashboard page");
        });
    }

    public void logoutFromDashboard() {
        Allure.step("Logout", () -> {
            TopbarPage topbarPage = new TopbarPage(waitUtils);
            topbarPage.selectProfile();
            topbarPage.clickLogout("Logout");
        });
    }

    public void assertErrorMessageVisible() {
        Allure.step("Assert login error is visible", () -> {
            String text = new LoginPage(waitUtils).getAlertMessageText();
            Assert.assertFalse(text.isBlank(), "Expected a visible login error message");
        });
    }
}
