# tests/e2e-flows/performance-cycle.feature
@e2e @flow
Feature: FLOW003 - Performance Review Cycle
  As an Admin
  I want to complete a full performance review cycle
  So that employee performance is formally evaluated

  Scenario: Full performance review cycle
    Given I am logged in as "Admin" with password "Admin123"

    # Step 1: Create KPI
    Given I navigate to Performance > Configure > KPIs
    When I click Add
    And I enter KPI name "Delivery Quality"
    And I select job title "QA Engineer"
    And I enter minimum rating "1"
    And I enter maximum rating "5"
    And I click Save
    Then KPI "Delivery Quality" should appear in the KPI list

    # Step 2: Add Performance Tracker
    When I navigate to Performance > Performance Trackers
    And I click Add
    And I enter tracker name "Flow003 Tracker"
    And I select an employee
    And I add reviewer
    And I click Save
    Then tracker "Flow003 Tracker" should appear in the trackers list

    # Step 3: Log Performance Entry
    When I click on tracker "Flow003 Tracker"
    And I click Add Log
    And I enter log comment "Consistently high quality deliverables"
    And I set achievement value "5"
    And I click Save
    Then the log entry should appear in the tracker

    # Step 4: Create Review
    When I navigate to Performance > Manage Reviews > Activate Reviews
    And I click Add
    And I enter review name "Flow003 Annual Review"
    And I select the employee
    And I select the supervisor
    And I set review period dates
    And I click Activate
    Then I should see a success toast notification

    # Step 5: Verify Review Details
    When I navigate to Performance > Manage Reviews
    And I search for review "Flow003 Annual Review"
    Then the review should be listed with status "Activated"
    And the review should display the correct employee details
