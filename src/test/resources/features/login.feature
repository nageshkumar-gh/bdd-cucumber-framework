@login
Feature: Login

Scenario: User login to the application
Given the user is on the login page
When the user enters valid credentials
Then the user should be redirected to the dashboard
And the user logouts of the application

Scenario: User login with invalid credentials
Given the user is on the login page
When the user enters invalid credentials
Then the user should see an error message