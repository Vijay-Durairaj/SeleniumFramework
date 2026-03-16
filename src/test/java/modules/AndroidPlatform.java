package modules;

import helper.BrowserStackConfigReader;
import helper.DriverFactory;
import interfaces.Android;
import model.User;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.HashMap;
import java.util.Map;

public class AndroidPlatform implements Android {

    protected RemoteWebDriver driver;

    private RemoteWebDriver getDriver() {
        // DriverFactory resolves ANDROID LOCAL (Appium) or ANDROID BROWSERSTACK from config.properties
        if (driver == null) {
            driver = DriverFactory.getDriver();
        }
        return driver;
    }

    @Override
    public void loginAs(User user) {
        // TODO: implement Android login flow
        getDriver();
    }

    @Override
    public void launchApplication() {
        getDriver();
    }

    @Override
    public void validateHomePage() {
        // TODO: implement Android home page validation
        getDriver();
    }

    @Override
    public void searchForKeyword(String keyword) {
        // TODO: implement Android search
        getDriver();
    }

    @Override
    public void deepLogin(){
        String appPackage  = BrowserStackConfigReader.get("android", "local", "appPackage",  null, "org.wikipedia.alpha");
        String appActivity = BrowserStackConfigReader.get("android", "local", "appActivity", null, "org.wikipedia.main.MainActivity");
        String deepLinkUrl = "https://en.m.wikipedia.org/wiki/Main_Page";

        // UiAutomator2's "mobile: startActivity" requires a single "intent" string
        // that is passed directly as args to `am start-activity`.
        // Format: -a <action> -d <data_uri> -n <component>
        String intentStr = String.format(
                "-a android.intent.action.VIEW -d %s -n %s/%s",
                deepLinkUrl, appPackage, appActivity);

        Map<String, Object> params = new HashMap<>();
        params.put("intent", intentStr);

        System.out.printf("[AndroidPlatform] Deep-linking via startActivity: %s%n", intentStr);
        getDriver().executeScript("mobile: startActivity", params);
    }
}
