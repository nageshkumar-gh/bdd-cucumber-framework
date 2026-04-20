@pim
Feature: PIM

Scenario: Admin should be able to Add Employee
  When Admin clicks on the PIM module
  And Admin clicks on the Add Employee button
  And Admin enters employee details
  And Admin clicks on the Save button
  Then Employee should be added successfully

Scenario: Admin should be able to Search Employee
  When Admin clicks on the PIM module
  And Admin enters employee name in the search box
  And Admin clicks on the Search button
  Then Employee details should be displayed in the search results

Scenario: Admin should be able to Edit Employee Details
  When Admin clicks on the PIM module
  And Admin searches for an employee
  And Admin clicks on the Edit button for the employee
  And Admin updates employee details
  And Admin clicks on the Save button
  Then Employee details should be updated successfully