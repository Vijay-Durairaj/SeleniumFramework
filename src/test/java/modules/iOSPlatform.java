package modules;

import model.User;
import org.openqa.selenium.remote.RemoteWebDriver;
import helper.DriverFactory;
import interfaces.IOS;

public class iOSPlatform implements IOS {

    protected RemoteWebDriver driver;

    private RemoteWebDriver getDriver() {
        // DriverFactory resolves IOS LOCAL (Appium) or IOS BROWSERSTACK from config.properties
        if (driver == null) {
            driver = DriverFactory.getDriver();
        }
        return driver;
    }

    @Override
    public void loginAs(User user) {
        getDriver();
    }

    @Override
    public void launchApplication() {
        getDriver();
    }

    @Override
    public void validateHomePage() {
        // TODO: implement iOS home page validation
        getDriver();
    }

    @Override
    public void searchForKeyword(String keyword) {
        // TODO: implement iOS search
        getDriver();
    }

    @Override
    public void deepLogin() {
        getDriver();
    }
}
