package stepDefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import services.AuthService;

/**
 * Authentication steps for {@code auth.feature} — delegates to {@link AuthService}.
 */
public class AuthStep {

    private final AuthService authService;

    public AuthStep(AuthService authService) {
        this.authService = authService;
    }

    @Given("I am on the OrangeHRM login page")
    public void i_am_on_the_orange_hrm_login_page() {
        authService.openLoginPage();
    }

    @When("I enter username {string}")
    public void i_enter_username(String username) {
        authService.enterUsername(username);
    }

    @And("I enter password {string}")
    public void i_enter_password(String password) {
        authService.enterPassword(password);
    }

    @And("I click the Login button")
    public void i_click_the_login_button() {
        authService.clickLogin();
    }

    @Then("I should be redirected to the Dashboard")
    public void i_should_be_redirected_to_the_dashboard() {
        authService.assertDashboardLoaded();
    }

    @And("I should see the OrangeHRM header")
    public void i_should_see_the_orange_hrm_header() {
        authService.assertOrangeHrmHeaderVisible();
    }

    @Then("I should see the error message {string}")
    public void i_should_see_the_error_message(String expectedMessage) {
        authService.assertLoginErrorMessage(expectedMessage);
    }

    @And("I should remain on the login page")
    public void i_should_remain_on_the_login_page() {
        authService.assertOnLoginPage();
    }

    @When("I leave the username field empty")
    public void i_leave_the_username_field_empty() {
        authService.clearUsername();
    }

    @And("I leave the password field empty")
    public void i_leave_the_password_field_empty() {
        authService.clearPassword();
    }

    @Then("I should see a required field error for username")
    public void i_should_see_a_required_field_error_for_username() {
        authService.assertUsernameRequiredError();
    }

    @And("I should see a required field error for password")
    public void i_should_see_a_required_field_error_for_password() {
        authService.assertPasswordRequiredError();
    }

    @Given("I am logged in as {string} with password {string}")
    public void i_am_logged_in_as_with_password(String username, String password) {
        authService.loginWithCredentials(username, password);
    }

    @When("I click the user profile menu")
    public void i_click_the_user_profile_menu() {
        authService.openProfileMenu();
    }

    @And("I click Logout")
    public void i_click_logout() {
        authService.logoutFromProfileMenu();
    }

    @Then("I should be redirected to the login page")
    public void i_should_be_redirected_to_the_login_page() {
        authService.assertRedirectedToLoginPage();
    }

    @When("the user session expires")
    public void the_user_session_expires() {
        authService.expireSession();
    }

    @And("I should see the session expired message")
    public void i_should_see_the_session_expired_message() {
        authService.assertSessionExpiredMessagePresent();
    }
}
