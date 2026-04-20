# tests/pim/employee.feature
@pim
Feature: PIM Employee Management
  As an Admin
  I want to manage employee records
  So that I can maintain accurate HR data

  Background:
    Given I am logged in as "Admin" with password "Admin123"
    And I navigate to PIM > Employee List

  @smoke @P1
  Scenario: TC015 - Admin can add a new employee
    When I click Add Employee
    And I enter first name "Test"
    And I enter last name "Employee"
    And I enter employee ID "EMP-TC015"
    And I click Save
    Then I should see a success toast notification
    And I should be on the employee personal details page for "Test Employee"

  @search @P1
  Scenario: TC016 - Admin can search employee by name
    Given an employee with name "Test Employee" exists
    When I enter "Test Employee" in the Employee Name search field
    And I click Search
    Then I should see employee "Test Employee" in the results
    And the results table should not be empty

  @P2
  Scenario: TC017 - Admin can edit employee personal details
    Given an employee with name "Test Employee" exists
    When I search for employee "Test Employee"
    And I click on employee "Test Employee"
    And I navigate to Personal Details tab
    And I update the nationality to "Irish"
    And I click Save
    Then I should see a success toast notification
    And the nationality should show "Irish"

  @P3
  Scenario: TC018 - Admin can upload employee profile picture
    Given an employee with name "Test Employee" exists
    When I search for employee "Test Employee"
    And I click on employee "Test Employee"
    And I click the profile picture upload area
    And I upload image file "test-profile.jpg"
    And I click Save
    Then I should see a success toast notification
    And the employee profile picture should be updated

  @destructive @P2
  Scenario: TC019 - Admin can delete an employee
    Given an employee with name "Delete Me Employee" exists
    When I select the checkbox for employee "Delete Me Employee"
    And I click the Delete button
    And I confirm the deletion
    Then I should see a success toast notification
    And employee "Delete Me Employee" should no longer appear in the list

  @validation @P2
  Scenario: TC020 - Employee list shows correct record count
    Given I am on the Employee List page
    When I click Search without applying any filters
    Then the total record count should be displayed
    And the count should match the number of rows in the results table
