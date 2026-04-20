# tests/leave/leave.feature
@leave
Feature: Leave Management
  As an employee or Admin
  I want to manage leave requests and approvals
  So that employee absences are tracked accurately

  Background:
    Given I am logged in as "Admin" with password "Admin123"

  @smoke @P1
  Scenario: TC021 - Employee can apply for Annual Leave
    Given I navigate to Leave > Apply
    When I select leave type "Annual Leave"
    And I select a future start date
    And I select a future end date
    And I enter comment "Family holiday"
    And I click Apply
    Then I should see a success toast notification
    And the leave request should appear in My Leave list with status "Pending"

  @negative @P2
  Scenario: TC022 - Employee cannot apply leave for past dates
    Given I navigate to Leave > Apply
    When I select leave type "Annual Leave"
    And I select a past start date
    And I select a past end date
    And I click Apply
    Then I should see a validation error for past dates

  @P2
  Scenario: TC023 - Employee can cancel a pending leave request
    Given I have a pending leave request
    And I navigate to Leave > My Leave
    When I click Cancel on the pending leave request
    And I confirm the cancellation
    Then the leave request status should change to "Cancelled"

  @smoke @P1
  Scenario: TC024 - Admin can approve a leave request
    Given an employee has submitted a pending leave request
    And I navigate to Leave > Leave List
    When I find the pending leave request
    And I click Approve
    Then I should see a success toast notification
    And the leave request status should change to "Approved"

  @P2
  Scenario: TC025 - Admin can reject a leave request
    Given an employee has submitted a pending leave request
    And I navigate to Leave > Leave List
    When I find the pending leave request
    And I click Reject
    Then I should see a success toast notification
    And the leave request status should change to "Rejected"

  @validation @P2
  Scenario: TC026 - Leave balance reduces after approval
    Given an employee has "10" days of Annual Leave balance
    And the employee has submitted a pending Annual Leave request for "2" days
    When I navigate to Leave > Leave List
    And I approve the leave request
    Then the employee's Annual Leave balance should be "8" days

  @reporting @P3
  Scenario: TC027 - Admin can view employees on leave today
    Given I navigate to Leave > Leave List
    When I filter by date "today"
    And I click Search
    Then I should see a list of employees on leave today
    And each record should show the employee name and leave type
