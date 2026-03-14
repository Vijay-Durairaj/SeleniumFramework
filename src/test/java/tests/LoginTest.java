package tests;

import model.User;
import org.testng.annotations.Test;
import utils.AbstractBaseTest;
import utils.ConfigReader;

public class LoginTest extends AbstractBaseTest {

    @Test
    public void validLoginTest() {
        User validUser = new User(ConfigReader.get("login.username"), ConfigReader.get("login.password"));
        platform.loginAs(validUser);
        platform.validateHomePage();
    }
}
