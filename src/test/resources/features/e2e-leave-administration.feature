# tests/e2e-flows/leave-administration.feature
@e2e @flow
Feature: FLOW004 - Leave Administration
  As an Admin
  I want to manage leave types and approvals
  So that leave balances are accurately maintained

  Scenario: Leave type creation and full approval cycle with balance verification
    Given I am logged in as "Admin" with password "Admin123"

    # Step 1: Admin Creates Leave Type
    Given I navigate to Leave > Configure > Leave Types
    When I click Add
    And I enter leave type name "Flow004 Test Leave"
    And I click Save
    Then leave type "Flow004 Test Leave" should appear in the leave types list

    # Step 2: Employee Applies Leave
    When I note the current Annual Leave balance for employee "Lifecycle Employee"
    And I log in as employee "lifecycle.employee" with password "Password@123"
    And I navigate to Leave > Apply
    And I select leave type "Annual Leave"
    And I select a future start date
    And I select a future end date for "1" day duration
    And I click Apply
    Then the leave request should appear with status "Pending"

    # Step 3: Admin Approves
    When I log out
    And I log in with username "Admin" and password "Admin123"
    And I navigate to Leave > Leave List
    And I find the pending leave for "Lifecycle Employee"
    And I click Approve
    Then the leave request status should change to "Approved"

    # Step 4: Verify Balance Updated
    When I navigate to Leave > Entitlements > Employee Leave Entitlements
    And I search for employee "Lifecycle Employee"
    Then the Annual Leave balance for "Lifecycle Employee" should be reduced by "1" day
