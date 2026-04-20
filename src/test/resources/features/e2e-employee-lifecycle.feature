# tests/e2e-flows/employee-lifecycle.feature
@e2e @flow
Feature: FLOW001 - Full Employee Lifecycle
  As an Admin
  I want to onboard an employee end-to-end
  So that the employee can login and manage their leave

  @smoke
  Scenario: Full employee onboarding and leave request flow
    # Step 1: Create Employee in PIM
    Given I am logged in as "Admin" with password "Admin123"
    And I navigate to PIM > Employee List
    When I click Add Employee
    And I enter first name "Lifecycle"
    And I enter last name "Employee"
    And I enter employee ID "EMP-FLOW001"
    And I click Save
    Then I should see the employee personal details page

    # Step 2: Assign User Account
    When I navigate to Admin > User Management > Users
    And I click Add to create a new user
    And I set user role to "ESS"
    And I set status to "Enabled"
    And I enter employee name "Lifecycle Employee"
    And I enter username "lifecycle.employee"
    And I enter password "Password@123"
    And I confirm the password "Password@123"
    And I click Save
    Then the new user "lifecycle.employee" should appear in the users list

    # Step 3: Login as Employee
    When I log out as Admin
    And I log in with username "lifecycle.employee" and password "Password@123"
    Then I should be redirected to the Dashboard

    # Step 4: Apply Leave
    When I navigate to Leave > Apply
    And I select leave type "Annual Leave"
    And I select a future start date
    And I select a future end date
    And I enter comment "Personal leave"
    And I click Apply
    Then I should see a success toast notification
    And the leave request should appear with status "Pending"

    # Step 5: Admin Approves Leave
    When I log out
    And I log in with username "Admin" and password "Admin123"
    And I navigate to Leave > Leave List
    And I find the pending leave for "Lifecycle Employee"
    And I click Approve
    Then the leave request status should change to "Approved"
