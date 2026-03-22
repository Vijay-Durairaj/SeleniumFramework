Feature: Login Page
#    @LoginTest
    Scenario: User should be able to login with valid credentials
        Given User is on the login page
        When User login to the application
        Then User should be redirected to the dashboard page

    #@LoginTest
    Scenario: User login to application using deeplink
        Given User is on the skip login page

    @LoginTest
    Scenario: User navigate to BitBar Application
        Given User enter the details as "bitbar"