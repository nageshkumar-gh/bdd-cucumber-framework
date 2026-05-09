@pim
Feature: PIM employee management
  As an OrangeHRM administrator
  I want to add a new employee and update their personal details
  So that employee records stay accurate in PIM

  Background:
    Given the user is on the OrangeHRM login page

  @employee @regression
  Scenario: Admin adds a new employee and updates personal details
    When the user logs in with username "Admin" and password "admin123"
    Then the user should be logged in successfully
    When the admin opens the PIM employee list
    And the admin starts adding a new employee
    And the admin fills employee names first "QA" middle "M" last "AutoEmp"
    And the admin saves the new employee from the add employee form
    Then the personal details page should be open for the new employee
    And the employee last name field should contain "AutoEmp"
    When the admin updates the employee middle name to "Michael"
    Then the employee middle name field should show "Michael"
    And the employee first name field should show "QA"
