package framework.actions;

import framework.driver.DriverManager;
import framework.pages.auth.LoginPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Action-layer wrapper for login-related user flows.
 *
 * <p><b>Why this exists</b>:
 * Step definitions should read like business intent and remain stable even when UI mechanics change.
 * By putting orchestration here, we can later:
 * - Change the login flow (SSO, MFA, captcha handling) without rewriting step definitions.
 * - Centralize waiting/synchronization decisions so timing fixes happen once.
 *
 * <p><b>Design constraint</b>:
 * This class can coordinate Page Objects and read driver state, but it should NOT contain assertions.
 * Assertions remain in step definitions.
 */
public class LoginActions {

    private static final Logger LOGGER = LogManager.getLogger(LoginActions.class);

    // Why: page objects remain the single owner of locators and atomic UI interactions.
    private final LoginPage loginPage;

    /**
     * Creates a {@link LoginActions} instance with a default {@link LoginPage}.
     *
     * <p><b>Why</b>: keeps the refactor incremental; we can later introduce explicit wiring/factories
     * if we need shared page instances or cross-scenario state (without coupling steps to construction).
     */
    public LoginActions() {
        this(new LoginPage());
    }

    /**
     * Creates a {@link LoginActions} instance with an explicitly provided {@link LoginPage}.
     *
     * <p><b>Why</b>: constructor injection makes this class testable and avoids hidden globals.
     *
     * @param loginPage page object used to perform login actions
     */
    public LoginActions(LoginPage loginPage) {
        if (loginPage == null) {
            throw new IllegalArgumentException("loginPage must not be null");
        }
        this.loginPage = loginPage;
    }

    /**
     * Returns the current browser URL.
     *
     * <p><b>Why</b>: steps can assert navigation outcomes without containing WebDriver calls.
     *
     * @return current URL
     */
    public String getCurrentUrl() {
        return DriverManager.getDriver().getCurrentUrl();
    }

    /**
     * Performs a login attempt with the supplied credentials.
     *
     * @param username username (can be empty)
     * @param password password (can be empty)
     */
    public void login(String username, String password) {
        LOGGER.info("Executing login action for username='{}'", username);
        loginPage.login(username, password);
    }

    /**
     * Waits for the application to redirect to the dashboard after a successful login.
     *
     * <p><b>Why</b>: keeps synchronization in the action/page layers so steps remain clean.
     */
    public void waitForSuccessfulLoginRedirect() {
        loginPage.waitForSuccessfulLoginRedirect();
    }

    /**
     * Reads the authentication error message for invalid credentials.
     *
     * @return auth error message text
     */
    public String getAuthenticationErrorMessage() {
        return loginPage.getErrorMessage();
    }

    /**
     * Reads the required-field validation message when fields are empty.
     *
     * @return validation message text
     */
    public String getRequiredFieldValidationMessage() {
        return loginPage.getRequiredFieldValidationMessage();
    }
}

