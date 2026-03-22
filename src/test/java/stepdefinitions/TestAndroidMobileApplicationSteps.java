package stepdefinitions;

import helper.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TestAndroidMobileApplicationSteps extends AbstractStepDefinitions {

     public TestAndroidMobileApplicationSteps() {
         super();
     }

     @Given("User is on the home page")
     public void givenUserIsOnTheAndroidHomePage() {
         platform.launchApplication();
     }

     @When("User click on the accessibility tab")
     public void userClickOnTheAccessibilityTab() {
        platform.asMobile().clickAccessibilityTab();
     }

    @Then("User should be able to see the accessibility options")
    public void userShouldBeableToSeeTheAccessibilityOptions() {
        platform.asMobile().validateAccessibilityTab();
     }

    @When("User install the application")
    public void whenUserInstallTheApplication() {
        platform.asMobile().installApplication(ConfigReader.get("android.apk"));
    }

    @Then("User should be able to see the application installed successfully")
    public void thenUserShouldBeableToSeeTheApplicationInstalledSuccessfully() {
         platform.validateHomePage();
    }

    @Then("User launch the application")
    public void thenUserShouldBeableToLaunchTheApplication() {
        platform.launchApplication();
    }

    @When("User terminate the application")
    public void whenUserTerminateTheApplication() {
        platform.asMobile().terminateApplication(ConfigReader.get("android.appPackage"));
    }

    @Then("User uninstall the application")
    public void thenUserUninstallTheApplication() {
        platform.asAndroid().uninstallApplication(ConfigReader.get("android.appPackage"));
    }


}
