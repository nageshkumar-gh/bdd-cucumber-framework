# tests/performance/performance.feature
@performance
Feature: Performance Management
  As an Admin or Supervisor
  I want to manage KPIs, trackers, and performance reviews
  So that employee performance is tracked and reviewed

  Background:
    Given I am logged in as "Admin" with password "Admin123"

  @smoke @P1
  Scenario: TC034 - Admin can create a KPI
    Given I navigate to Performance > Configure > KPIs
    When I click Add
    And I enter KPI name "Code Quality Score"
    And I select job title "QA Engineer"
    And I enter minimum rating "1"
    And I enter maximum rating "5"
    And I click Save
    Then I should see a success toast notification
    And KPI "Code Quality Score" should appear in the KPI list

  @P2
  Scenario: TC035 - Admin can add a performance tracker
    Given I navigate to Performance > Performance Trackers
    When I click Add
    And I enter tracker name "Q2 QA Tracker"
    And I select an employee
    And I add reviewer
    And I click Save
    Then I should see a success toast notification
    And tracker "Q2 QA Tracker" should appear in the trackers list

  @P2
  Scenario: TC036 - Admin can create a performance review
    Given I navigate to Performance > Manage Reviews > Activate Reviews
    When I click Add
    And I enter review name "Annual Review 2025"
    And I select the employee
    And I select the supervisor
    And I set review period dates
    And I click Activate
    Then I should see a success toast notification

  @P2
  Scenario: TC037 - Supervisor can add tracker log for employee
    Given performance tracker "Q2 QA Tracker" exists
    And I navigate to Performance > Performance Trackers
    When I click on tracker "Q2 QA Tracker"
    And I click Add Log
    And I enter log comment "Delivered sprint QA cycle on time"
    And I set achievement value "4"
    And I click Save
    Then I should see a success toast notification
    And the log entry should appear in the tracker

  @validation @P3
  Scenario: TC038 - Performance review shows correct employee details
    Given a performance review for employee "Test Employee" exists
    When I navigate to Performance > Manage Reviews
    And I search for review by employee "Test Employee"
    Then the review record should display the correct employee name
    And the review record should display the correct supervisor name
    And the review period dates should be accurate


# tests/time/attendance.feature
@time
Feature: Time and Attendance
  As an Employee or Admin
  I want to manage time records and attendance
  So that working hours are accurately captured

  Background:
    Given I am logged in as "Admin" with password "Admin123"

  @P2
  Scenario: TC039 - Employee can punch in
    Given I navigate to Time > Attendance > My Attendance
    When I click Punch In
    And I confirm the punch in time
    Then I should see a success confirmation
    And my punch in time should be recorded

  @P2
  Scenario: TC040 - Employee can punch out
    Given I have already punched in today
    And I navigate to Time > Attendance > My Attendance
    When I click Punch Out
    And I confirm the punch out time
    Then I should see a success confirmation
    And my punch out time should be recorded

  @reporting @P2
  Scenario: TC041 - Admin can view attendance records
    Given I navigate to Time > Attendance > Employee Records
    When I select an employee
    And I set a date range
    And I click View
    Then I should see the attendance records for the employee
    And each record should show punch in and punch out times

  @P3
  Scenario: TC042 - Admin can add attendance for an employee
    Given I navigate to Time > Attendance > Employee Records
    When I select an employee
    And I click on the date to add attendance
    And I enter punch in time "09:00"
    And I enter punch out time "17:30"
    And I click Save
    Then I should see a success toast notification
    And the attendance record should appear for the selected date


# tests/maintenance/maintenance.feature
@maintenance
Feature: Maintenance and My Info
  As an Admin or Employee
  I want to manage system maintenance and personal profile
  So that the system and my data stay up to date

  Background:
    Given I am logged in as "Admin" with password "Admin123"

  @destructive @P3
  Scenario: TC043 - Admin can run data purge
    Given I navigate to Admin > Maintenance > Purge Records
    When I select purge type "Employee Records"
    And I enter employee name to purge
    And I click Purge
    And I confirm the purge action
    Then I should see a success toast notification

  @my-info @P2
  Scenario: TC044 - Employee can update own contact details
    Given I navigate to My Info > Contact Details
    When I update the street address to "123 Test Street"
    And I update the city to "Dublin"
    And I click Save
    Then I should see a success toast notification
    And the contact details should reflect the updated values

  @my-info @security @P2
  Scenario: TC045 - Employee can change own password
    Given I navigate to My Info > Change Password
    When I enter current password "Admin123"
    And I enter new password "NewPassword@456"
    And I confirm new password "NewPassword@456"
    And I click Save
    Then I should see a success toast notification
