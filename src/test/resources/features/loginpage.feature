Feature: Login Page
    @LoginTest
    Scenario: User should be able to login with valid credentials
        Given User is on the login page
        When User login to the application
        Then User should be redirected to the dashboard page