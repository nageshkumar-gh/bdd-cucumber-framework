# tests/auth/auth.feature
@auth
Feature: Authentication
  As a user of OrangeHRM
  I want to be able to log in and log out
  So that I can access the system securely

  Background:
    Given I am on the OrangeHRM login page

  @smoke @P1
  Scenario: TC001 - Admin can login with valid credentials
    When I enter username "Admin"
    And I enter password "Admin123"
    And I click the Login button
    Then I should be redirected to the Dashboard
    And I should see the OrangeHRM header

  @negative @P1
  Scenario: TC002 - User sees error message with invalid password
    When I enter username "Admin"
    And I enter password "WrongPassword"
    And I click the Login button
    Then I should see the error message "Invalid credentials"
    And I should remain on the login page

  @negative @P1
  Scenario: TC003 - User sees error message with invalid username
    When I enter username "invaliduser"
    And I enter password "Admin123"
    And I click the Login button
    Then I should see the error message "Invalid credentials"
    And I should remain on the login page

  @validation @P2
  Scenario: TC004 - User cannot login with empty credentials
    When I leave the username field empty
    And I leave the password field empty
    And I click the Login button
    Then I should see a required field error for username
    And I should see a required field error for password

  @smoke @P1
  Scenario: TC005 - Admin can logout successfully
    Given I am logged in as "Admin" with password "Admin123"
    When I click the user profile menu
    And I click Logout
    Then I should be redirected to the login page

  @security @P3
  Scenario: TC006 - Session expires and redirects to login
    Given I am logged in as "Admin" with password "Admin123"
    When the user session expires
    Then I should be redirected to the login page
    And I should see the session expired message
