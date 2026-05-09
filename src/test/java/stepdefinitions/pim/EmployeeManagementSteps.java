package framework.stepdefinitions.pim;

import framework.actions.PimEmployeeActions;
import framework.driver.DriverManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Step definitions for OrangeHRM PIM employee add and update flows.
 */
public class EmployeeManagementSteps {

    private static final Logger LOGGER = LogManager.getLogger(EmployeeManagementSteps.class);

    private final PimEmployeeActions pimEmployeeActions = new PimEmployeeActions();
    private String generatedLastName;

    @When("the admin opens the PIM employee list")
    public void theAdminOpensThePimEmployeeList() {
        pimEmployeeActions.openPimEmployeeList();
    }

    @When("the admin starts adding a new employee")
    public void theAdminStartsAddingANewEmployee() {
        pimEmployeeActions.startAddEmployee();
    }

    @When("the admin fills employee names first {string} middle {string} last {string}")
    public void theAdminFillsEmployeeNames(String firstName, String middleName, String lastNameBase) {
        generatedLastName = pimEmployeeActions.fillNewEmployeeNames(firstName, middleName, lastNameBase);
        LOGGER.info("Generated unique last name='{}'", generatedLastName);
    }

    @When("the admin saves the new employee from the add employee form")
    public void theAdminSavesTheNewEmployeeFromTheAddEmployeeForm() {
        pimEmployeeActions.saveNewEmployeeFromAddForm();
    }

    @Then("the personal details page should be open for the new employee")
    public void thePersonalDetailsPageShouldBeOpenForTheNewEmployee() {
        String url = DriverManager.getDriver().getCurrentUrl();
        LOGGER.info("Personal details URL={}", url);
        Assert.assertTrue(url.contains("viewPersonalDetails"),
                "Expected personal details URL, but was: " + url);
    }

    @Then("the employee last name field should contain {string}")
    public void theEmployeeLastNameFieldShouldContain(String lastNameFragment) {
        String last = pimEmployeeActions.getPersonalDetailsLastName();
        LOGGER.info("Last name field value='{}'", last);
        Assert.assertTrue(last.contains(lastNameFragment),
                "Expected last name to contain '" + lastNameFragment + "' but was: " + last);
    }

    @When("the admin updates the employee middle name to {string}")
    public void theAdminUpdatesTheEmployeeMiddleName(String middleName) {
        pimEmployeeActions.updateMiddleName(middleName);
    }

    @Then("the employee middle name field should show {string}")
    public void theEmployeeMiddleNameFieldShouldShow(String expected) {
        String actual = pimEmployeeActions.getPersonalDetailsMiddleName();
        LOGGER.info("Middle name field value='{}'", actual);
        Assert.assertEquals(actual.trim(), expected, "Unexpected middle name.");
    }

    @Then("the employee first name field should show {string}")
    public void theEmployeeFirstNameFieldShouldShow(String expected) {
        String actual = pimEmployeeActions.getPersonalDetailsFirstName();
        LOGGER.info("First name field value='{}'", actual);
        Assert.assertEquals(actual.trim(), expected, "Unexpected first name.");
    }
}
