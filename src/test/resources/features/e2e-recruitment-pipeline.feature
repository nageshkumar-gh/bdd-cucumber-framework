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
