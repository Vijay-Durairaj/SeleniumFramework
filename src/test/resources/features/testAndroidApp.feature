Feature: Testing Mobile Application

#  @Test
  Scenario: User check the accessibility tab in the application
    Given User is on the home page
    When User click on the accessibility tab
    Then User should be able to see the accessibility options

  @Test
  Scenario: User tries to install, launch and terminate the application
    Then User uninstall the application
    When User install the application
    Then User launch the application
    Then User should be able to see the application installed successfully
