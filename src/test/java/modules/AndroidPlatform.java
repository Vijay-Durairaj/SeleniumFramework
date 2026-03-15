package modules;

import interfaces.Android;
import model.User;
import org.openqa.selenium.WebDriver;
import helper.DriverFactory;

public class AndroidPlatform implements Android {

    protected WebDriver driver;

    private WebDriver getDriver() {
        // DriverFactory resolves ANDROID LOCAL (Appium) or ANDROID BROWSERSTACK from config.properties
        if (driver == null) {
            driver = DriverFactory.getDriver();
        }
        return driver;
    }

    @Override
    public void loginAs(User user) {
        // TODO: implement Android login flow using getDriver()
    }

    @Override
    public void launchApplication() {

    }

    @Override
    public void validateHomePage() {
        // TODO: implement Android home page validation using getDriver()
    }

    @Override
    public void searchForKeyword(String keyword) {
        // TODO: implement Android search using getDriver()
    }
}
