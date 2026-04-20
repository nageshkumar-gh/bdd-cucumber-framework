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


# tests/e2e-flows/recruitment-pipeline.feature
@e2e @flow
Feature: FLOW002 - Full Recruitment Pipeline
  As an Admin
  I want to run a complete hiring workflow
  So that a candidate moves from vacancy to talent pool

  Scenario: Full recruitment pipeline from vacancy to talent pool
    Given I am logged in as "Admin" with password "Admin123"
    And I navigate to Recruitment > Vacancies

    # Step 1: Create Vacancy
    When I click Add
    And I enter vacancy name "Senior QA Engineer"
    And I select job title "QA Engineer"
    And I enter number of positions "1"
    And I click Save
    Then vacancy "Senior QA Engineer" should appear in the vacancies list

    # Step 2: Add Candidate
    When I click on vacancy "Senior QA Engineer"
    And I click Add Candidate
    And I enter candidate first name "Pipeline"
    And I enter candidate last name "Candidate"
    And I enter candidate email "pipeline.candidate@example.com"
    And I click Save
    Then candidate "Pipeline Candidate" should appear in the candidates list

    # Step 3: Shortlist Candidate
    When I navigate to the application for "Pipeline Candidate"
    And I click Shortlist
    And I enter shortlist notes "Strong technical background"
    And I click Save
    Then the candidate stage should change to "Shortlisted"

    # Step 4: Schedule Interview
    When I click Schedule Interview
    And I enter interview name "Technical Interview"
    And I select a future interview date
    And I click Save
    Then the candidate stage should show "Interview Scheduled"

    # Step 5: Mark Interview Passed
    When I click Mark Interview Passed
    And I click Save
    Then the interview status should be "Passed"

    # Step 6: Add to Talent Pool
    When I click Add to Talent Pool
    And I confirm the action
    Then I should see a success toast notification


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
