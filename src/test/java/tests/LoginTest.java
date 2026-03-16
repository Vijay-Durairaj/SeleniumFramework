package tests;

import helper.ConfigReader;
import model.User;
import org.testng.annotations.Test;
import stepdefinitions.AbstractStepDefinitions;

public class LoginTest extends AbstractStepDefinitions {

    @Test
    public void validLoginTest() {
        User validUser = new User(ConfigReader.get("login.username"), ConfigReader.get("login.password"));
        platform.launchApplication();
        platform.loginAs(validUser);
        platform.validateHomePage();
    }

    @Test
    public void deepLinkLoginTest() {
        platform.deepLogin();
    }
}

