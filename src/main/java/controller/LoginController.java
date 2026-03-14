package controller;

import interfaces.ILoginPage;
import model.User;
import org.openqa.selenium.WebDriver;
import view.LoginPage;

public class LoginController implements ILoginPage {
    WebDriver driver;
    LoginPage loginPage;

    public LoginController(WebDriver driver) {
        this.driver = driver;
        this.loginPage = new LoginPage(driver);
    }

    @Override
    public void loginAs(User user) {
        loginPage.enterUsername(user.getUsername());
        loginPage.enterPassword(user.getPassword());
        loginPage.clickLogin();
    }
}
