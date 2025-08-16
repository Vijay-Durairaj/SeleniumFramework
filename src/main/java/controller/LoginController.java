package controller;

import model.User;
import view.LoginPage;
import org.openqa.selenium.WebDriver;

public class LoginController {
    WebDriver driver;
    LoginPage loginPage;

    public LoginController(WebDriver driver) {
        this.driver = driver;
        loginPage = new LoginPage(driver);
    }

    public void loginAs(User user) {
        loginPage.enterUsername(user.getUsername());
        loginPage.enterPassword(user.getPassword());
        loginPage.clickRememberMe();
        loginPage.clickLogin();
    }
}
