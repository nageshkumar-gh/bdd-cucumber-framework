# tests/user-management/user-management.feature
@user-management
Feature: Admin User Management
  As an Admin
  I want to manage system users
  So that I can control access to OrangeHRM

  Background:
    Given I am logged in as "Admin" with password "Admin123"
    And I navigate to Admin > User Management > Users

  @smoke @P1
  Scenario: TC007 - Admin can create a new ESS user
    When I click Add to create a new user
    And I set user role to "ESS"
    And I set status to "Enabled"
    And I enter employee name "John Smith"
    And I enter username "ess.user.tc007"
    And I enter password "Password@123"
    And I confirm the password "Password@123"
    And I click Save
    Then I should see a success toast notification
    And the new user "ess.user.tc007" should appear in the users list

  @P1
  Scenario: TC008 - Admin can create a new Admin user
    When I click Add to create a new user
    And I set user role to "Admin"
    And I set status to "Enabled"
    And I enter employee name "Jane Doe"
    And I enter username "admin.user.tc008"
    And I enter password "Password@123"
    And I confirm the password "Password@123"
    And I click Save
    Then I should see a success toast notification
    And the new user "admin.user.tc008" should appear in the users list

  @P2
  Scenario: TC009 - Admin can edit an existing user's role
    Given a user with username "ess.user.tc007" exists
    When I search for username "ess.user.tc007"
    And I click the edit icon for user "ess.user.tc007"
    And I change user role to "Admin"
    And I click Save
    Then I should see a success toast notification
    And user "ess.user.tc007" should have role "Admin"

  @P2
  Scenario: TC010 - Admin can enable a disabled user
    Given a user with username "disabled.user" exists with status "Disabled"
    When I search for username "disabled.user"
    And I click the edit icon for user "disabled.user"
    And I set status to "Enabled"
    And I click Save
    Then I should see a success toast notification
    And user "disabled.user" should have status "Enabled"

  @P2
  Scenario: TC011 - Admin can disable an active user
    Given a user with username "active.user" exists with status "Enabled"
    When I search for username "active.user"
    And I click the edit icon for user "active.user"
    And I set status to "Disabled"
    And I click Save
    Then I should see a success toast notification
    And user "active.user" should have status "Disabled"

  @destructive @P2
  Scenario: TC012 - Admin can delete a user
    Given a user with username "delete.me.user" exists
    When I search for username "delete.me.user"
    And I select the checkbox for user "delete.me.user"
    And I click the Delete button
    And I confirm the deletion
    Then I should see a success toast notification
    And user "delete.me.user" should no longer appear in the list

  @search @P2
  Scenario: TC013 - Admin can search user by username
    Given multiple users exist in the system
    When I enter "Admin" in the Username search field
    And I click Search
    Then I should see only users with "Admin" in their username
    And the results table should not be empty

  @negative @P2
  Scenario: TC014 - Duplicate username shows validation error
    Given a user with username "existing.user" exists
    When I click Add to create a new user
    And I set user role to "ESS"
    And I set status to "Enabled"
    And I enter employee name "Another Person"
    And I enter username "existing.user"
    And I enter password "Password@123"
    And I confirm the password "Password@123"
    And I click Save
    Then I should see the error message "Already exists"
