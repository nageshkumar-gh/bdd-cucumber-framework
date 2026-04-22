@auth
Feature: OrangeHRM Login
  In order to access OrangeHRM features securely
  As a valid or invalid user
  I want the system to allow or deny login appropriately

  Background:
    Given the user is on the OrangeHRM login page

  @smoke @regression
  Scenario: Valid login with correct credentials
    When the user logs in with username "Admin" and password "admin123"
    Then the user should be logged in successfully

  @regression
  Scenario: Invalid login with wrong password
    When the user logs in with username "Admin" and password "wrongPassword"
    Then an authentication error message should be displayed

  @regression
  Scenario: Login with empty fields
    When the user logs in with username "" and password ""
    Then a required field validation message should be displayed

