package stepDefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import services.LoginService;

/**
 * Legacy {@code login.feature} glue — delegates to {@link LoginService}.
 */
public class LoginStep {

    private final LoginService loginService;

    public LoginStep(LoginService loginService) {
        this.loginService = loginService;
    }

    @Given("the user is on the login page")
    public void user_is_on_the_login_page() {
        loginService.openLoginPage();
    }

    @When("the user enters valid credentials")
    public void user_enters_valid_username_and_password() {
        loginService.loginWithConfiguredCredentials();
    }

    @Then("the user should be redirected to the dashboard")
    public void user_should_be_redirected_to_the_dashboard() {
        loginService.assertOnDashboard();
    }

    @And("the user logouts of the application")
    public void theUserLogoutsOfTheApplication() {
        loginService.logoutFromDashboard();
    }

    @When("the user enters invalid credentials")
    public void theUserEntersInvalidCredentials() {
        loginService.loginWithInvalidCredentials();
    }

    @Then("the user should see an error message")
    public void theUserShouldSeeAnErrorMessage() {
        loginService.assertErrorMessageVisible();
    }
}
