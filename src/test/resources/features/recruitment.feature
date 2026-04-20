# tests/recruitment/recruitment.feature
@recruitment
Feature: Recruitment Management
  As an Admin
  I want to manage job vacancies and candidates
  So that I can run an end-to-end hiring pipeline

  Background:
    Given I am logged in as "Admin" with password "Admin123"
    And I navigate to Recruitment > Vacancies

  @smoke @P1
  Scenario: TC028 - Admin can add a new job vacancy
    When I click Add
    And I enter vacancy name "Software QA Engineer"
    And I select job title "QA Engineer"
    And I enter number of positions "2"
    And I click Save
    Then I should see a success toast notification
    And vacancy "Software QA Engineer" should appear in the vacancies list

  @P1
  Scenario: TC029 - Admin can add a candidate to a vacancy
    Given vacancy "Software QA Engineer" exists
    When I click on vacancy "Software QA Engineer"
    And I click Add Candidate
    And I enter candidate first name "Alice"
    And I enter candidate last name "Candidate"
    And I enter candidate email "alice.candidate@example.com"
    And I click Save
    Then I should see a success toast notification
    And candidate "Alice Candidate" should appear in the candidates list

  @P2
  Scenario: TC030 - Admin can shortlist a candidate
    Given candidate "Alice Candidate" is in "Application Initiated" stage
    When I navigate to the candidate's application
    And I click Shortlist
    And I enter shortlist notes "Strong profile"
    And I click Save
    Then the candidate stage should change to "Shortlisted"

  @P2
  Scenario: TC031 - Admin can schedule an interview
    Given candidate "Alice Candidate" is in "Shortlisted" stage
    When I navigate to the candidate's application
    And I click Schedule Interview
    And I enter interview name "Technical Round 1"
    And I select an interview date
    And I click Save
    Then I should see a success toast notification
    And the candidate stage should show "Interview Scheduled"

  @P2
  Scenario: TC032 - Admin can mark interview as passed
    Given candidate "Alice Candidate" has a scheduled interview
    When I navigate to the candidate's application
    And I click Mark Interview Passed
    And I click Save
    Then the interview should be marked as "Passed"

  @P3
  Scenario: TC033 - Admin can add candidate to talent pool
    Given candidate "Alice Candidate" is in any active stage
    When I navigate to the candidate's application
    And I click Add to Talent Pool
    And I confirm the action
    Then I should see a success toast notification
