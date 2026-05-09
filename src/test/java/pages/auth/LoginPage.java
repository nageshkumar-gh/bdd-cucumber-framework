package pages.auth;

import pages.BasePage;
import org.openqa.selenium.By;

/**
 * Page Object for the OrangeHRM login page.
 *
 * <p><b>Why this exists</b>:
 * We want locators and page-level user actions (enter credentials, click login) centralized so:
 * - Step definitions stay readable and focused on business intent.
 * - Locator changes are fixed in one place when the UI evolves.
 *
 * <p><b>Important rule</b>:
 * This class never calls Selenium APIs directly; all interactions must go through {@link BasePage}.
 */
public final class LoginPage extends BasePage {

    // Why: use stable attributes (name/type) and CSS selectors; avoid brittle absolute XPath.
    private static final By USERNAME_INPUT = By.cssSelector("input[name='username']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[name='password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit']");

    // Why: OrangeHRM displays authentication errors in a top alert region.
    private static final By AUTH_ERROR_MESSAGE = By.cssSelector("div.oxd-alert-content p.oxd-alert-content-text");

    // Why: required field validation appears as an inline error message under inputs.
    private static final By REQUIRED_FIELD_VALIDATION_MESSAGE = By.cssSelector("span.oxd-input-field-error-message");

    /**
     * Enters the username in the login form.
     *
     * @param username username to enter (null treated as empty)
     * @return this page for fluent chaining
     */
    public LoginPage enterUsername(String username) {
        type(USERNAME_INPUT, username);
        return this;
    }

    /**
     * Enters the password in the login form.
     *
     * @param password password to enter (null treated as empty)
     * @return this page for fluent chaining
     */
    public LoginPage enterPassword(String password) {
        type(PASSWORD_INPUT, password);
        return this;
    }

    /**
     * Clicks the Login button.
     *
     * <p><b>Why separate from {@link #login(String, String)}</b>:
     * Keeping atomic actions allows negative tests (empty fields) to be expressed clearly.
     *
     * @return this page for fluent chaining
     */
    public LoginPage clickLogin() {
        click(LOGIN_BUTTON);
        return this;
    }

    /**
     * Performs a full login attempt using the provided credentials.
     *
     * <p><b>Why this exists</b>: most scenarios need the combined action, but we still keep
     * the atomic methods for flexibility.
     *
     * @param username username value
     * @param password password value
     * @return this page for fluent chaining
     */
    public LoginPage login(String username, String password) {
        return enterUsername(username)
                .enterPassword(password)
                .clickLogin();
    }

    /**
     * Waits until the application navigates away from the login page to a dashboard URL.
     *
     * <p><b>Why this exists</b>:
     * OrangeHRM performs an async login + redirect. Without an explicit synchronization point,
     * step assertions may run while the app is still on the login page, causing false failures.
     *
     * <p><b>Important</b>: this is a wait/synchronization helper, not an assertion. Assertions remain
     * in step definitions.
     *
     * @return this page for fluent chaining
     */
    public LoginPage waitForSuccessfulLoginRedirect() {
        waitForUrlContains("/dashboard");
        return this;
    }

    /**
     * Returns the authentication error message text (e.g., invalid credentials).
     *
     * <p><b>Why</b>: assertions must live in step definitions; Page Objects only provide state.
     *
     * @return error message text
     */
    public String getErrorMessage() {
        return getText(AUTH_ERROR_MESSAGE);
    }

    /**
     * Returns the first required field validation message text.
     *
     * <p><b>Why</b>: empty-field scenarios should assert on UI validation instead of internal logic.
     *
     * @return validation message text
     */
    public String getRequiredFieldValidationMessage() {
        return getText(REQUIRED_FIELD_VALIDATION_MESSAGE);
    }
}

