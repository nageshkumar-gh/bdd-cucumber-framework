package stepDefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PIMStep {

    @When("Admin clicks on the PIM module")
    public void admin_clicks_on_the_pim_module() {
        System.out.println("Admin clicks on the PIM module");
    }

    @And("Admin clicks on the Add Employee button")
    public void admin_clicks_on_the_add_employee_button() {
        System.out.println("Admin clicks on the Add Employee button");
    }

    @And("Admin enters employee details")
    public void admin_enters_employee_details() {
        System.out.println("Admin enters employee details");
    }

    @And("Admin clicks on the Save button")
    public void admin_clicks_on_the_save_button() {
        System.out.println("Admin clicks on the Save button");
    }

    @Then("Employee should be added successfully")
    public void employee_should_be_added_successfully() {
        System.out.println("Employee should be added successfully");
    }

    @And("Admin enters employee name in the search box")
    public void admin_enters_employee_name_in_the_search_box() {
        System.out.println("Admin enters employee name in the search box");
    }

    @And("Admin clicks on the Search button")
    public void admin_clicks_on_the_search_button() {
        System.out.println("Admin clicks on the Search button");
    }

    @Then("Employee details should be displayed in the search results")
    public void employee_details_should_be_displayed_in_the_search_results() {
        System.out.println("Employee details should be displayed in the search results");
    }

    @And("Admin searches for an employee")
    public void admin_searches_for_an_employee() {
        System.out.println("Admin searches for an employee");
    }

    @And("Admin clicks on the Edit button for the employee")
    public void admin_clicks_on_the_edit_button_for_the_employee() {
        System.out.println("Admin clicks on the Edit button for the employee");
    }

    @And("Admin updates employee details")
    public void admin_updates_employee_details() {
        System.out.println("Admin updates employee details");
    }

    @Then("Employee details should be updated successfully")
    public void employee_details_should_be_updated_successfully() {
        System.out.println("Employee details should be updated successfully");
    }
}

