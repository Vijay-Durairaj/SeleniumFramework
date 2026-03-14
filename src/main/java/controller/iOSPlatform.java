package controller;

import interfaces.IMobilePlatform;
import model.User;
import org.openqa.selenium.WebDriver;
import utils.DriverFactory;

public class iOSPlatform implements IMobilePlatform {

    protected WebDriver driver;

    private WebDriver getDriver() {
        // DriverFactory resolves IOS LOCAL (Appium) or IOS BROWSERSTACK from config.properties
        if (driver == null) {
            driver = DriverFactory.getDriver();
        }
        return driver;
    }

    @Override
    public void loginAs(User user) {
        // TODO: implement iOS login flow using getDriver()
    }

    @Override
    public void validateHomePage() {
        // TODO: implement iOS home page validation using getDriver()
    }

    @Override
    public void searchForKeyword(String keyword) {
        // TODO: implement iOS search using getDriver()
    }
}
