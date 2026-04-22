package com.framework.stepdefinitions.auth;

import com.framework.actions.LoginActions;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Step definitions for OrangeHRM login scenarios.
 *
 * <p><b>Why this exists</b>:
 * Step definitions translate business-readable Gherkin into executable actions and assertions.
 * We keep assertions here (not in Page Objects) so pages remain reusable and side-effect focused.
 *
 * <p><b>Layering rule</b>:
 * Steps should delegate orchestration to {@link LoginActions} so they stay readable and stable
 * when UI flows evolve.
 */
public class LoginSteps {

    private static final Logger LOGGER = LogManager.getLogger(LoginSteps.class);

    // Why: Action layer owns orchestration so step definitions stay clean and stable.
    private final LoginActions loginActions = new LoginActions();

    /**
     * Ensures we are on the login page before executing login actions.
     *
     * <p><b>Why</b>: we want a clear precondition step for readability and to support reuse via Background.
     * Navigation itself is handled in Hooks later; here we only assert the state.
     */
    @Given("the user is on the OrangeHRM login page")
    public void theUserIsOnTheOrangeHrmLoginPage() {
        String currentUrl = loginActions.getCurrentUrl();
        LOGGER.info("Current URL={}", currentUrl);

        // Why: URL is a lightweight signal that avoids element-level Selenium calls in steps.
        Assert.assertTrue(currentUrl.contains("/auth/login"),
                "Expected to be on OrangeHRM login page, but URL was: " + currentUrl);
    }

    /**
     * Performs a login attempt using the provided credentials.
     *
     * @param username username value (can be empty)
     * @param password password value (can be empty)
     */
    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsInWithUsernameAndPassword(String username, String password) {
        LOGGER.info("Attempting login with username='{}' and password='{}'", username, mask(password));
        loginActions.login(username, password);
    }

    /**
     * Asserts that the user is logged in successfully.
     *
     * <p><b>Why URL assertion</b>:
     * It's stable and does not require dashboard locators yet (vertical slice first).
     */
    @Then("the user should be logged in successfully")
    public void theUserShouldBeLoggedInSuccessfully() {
        loginActions.waitForSuccessfulLoginRedirect();

        String currentUrl = loginActions.getCurrentUrl();
        LOGGER.info("Post-login URL={}", currentUrl);

        Assert.assertTrue(currentUrl.contains("/dashboard"),
                "Expected to land on dashboard after login, but URL was: " + currentUrl);
    }

    /**
     * Asserts that an authentication error message is displayed for invalid credentials.
     */
    @Then("an authentication error message should be displayed")
    public void anAuthenticationErrorMessageShouldBeDisplayed() {
        String error = loginActions.getAuthenticationErrorMessage();
        LOGGER.info("Auth error message='{}'", error);

        // Why: OrangeHRM demo shows a consistent message; asserting exact string helps catch regressions.
        Assert.assertEquals(error.trim(), "Invalid credentials", "Unexpected authentication error message.");
    }

    /**
     * Asserts that required field validation appears when username/password are empty.
     */
    @Then("a required field validation message should be displayed")
    public void aRequiredFieldValidationMessageShouldBeDisplayed() {
        String validation = loginActions.getRequiredFieldValidationMessage();
        LOGGER.info("Validation message='{}'", validation);

        Assert.assertEquals(validation.trim(), "Required", "Expected required field validation message.");
    }

    /**
     * Masks a password for logging.
     *
     * <p><b>Why</b>: logs must be useful without leaking secrets. Even though demo creds are public,
     * we enforce this pattern early so it becomes muscle memory.
     *
     * @param password raw password
     * @return masked value
     */
    private static String mask(String password) {
        if (password == null) {
            return "null";
        }
        if (password.isEmpty()) {
            return "";
        }
        return "*".repeat(Math.min(password.length(), 8));
    }
}

