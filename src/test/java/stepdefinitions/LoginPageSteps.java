package stepdefinitions;

import helper.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.User;

public class LoginPageSteps extends AbstractStepDefinitions {

    public LoginPageSteps() {
        super();
    }

    @Given("User is on the login page")
    public void givenUserIsOnTheLoginPage() {
        //platform.launchApplication();
            platform.deepLogin();
    }

    @When("User login to the application")
    public void userLoginToTheApplication() {
        User validUser = new User(ConfigReader.get("login.username"), ConfigReader.get("login.password"));
        platform.loginAs(validUser);
    }

    @Then("User should be redirected to the dashboard page")
    public void userShouldBeRedirectedToTheDashboardPage() {
        platform.validateHomePage();
    }

}
