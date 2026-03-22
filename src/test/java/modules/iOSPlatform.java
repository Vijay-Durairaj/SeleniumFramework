package modules;

import model.User;
import org.openqa.selenium.remote.RemoteWebDriver;
import helper.DriverFactory;
import interfaces.IOS;

public class iOSPlatform implements IOS {

    protected RemoteWebDriver driver;

    private RemoteWebDriver getDriver() {
        if (driver == null) {
            driver = DriverFactory.getDriver();
        }
        return driver;
    }

    @Override
    public void deepLogin() {
        getDriver();
    }

    @Override
    public void enterValue(String value) {

    }

    @Override
    public void clickAccessibilityTab() {

    }

    @Override
    public void validateAccessibilityTab() {

    }

    @Override
    public void installApplication(String appPath) {

    }

    @Override
    public void terminateApplication(String appPath) {

    }

    @Override
    public void launchApplication() {

    }

    @Override
    public void validateHomePage() {

    }

    @Override
    public void searchForKeyword(String keyword) {

    }

    @Override
    public void loginAs(User user) {

    }
}
