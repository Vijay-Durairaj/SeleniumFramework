package modules;

import helper.BrowserStackConfigReader;
import helper.DriverFactory;
import interfaces.Android;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import model.User;
import org.openqa.selenium.remote.RemoteWebDriver;
import pageobjects.android.TestMobileHomePage;

import java.util.HashMap;
import java.util.Map;

public class AndroidPlatform implements Android {

    protected RemoteWebDriver driver;
    TestMobileHomePage homePage;

    private RemoteWebDriver getDriver() {
        if (driver == null) {
            driver = DriverFactory.getDriver();
            homePage = new TestMobileHomePage(driver);
        }
        return driver;
    }

    @Override
    public void loginAs(User user) {
    }

    @Override
    public void launchApplication() {
        getDriver();
        String appPackage = BrowserStackConfigReader.get("android", "local", "appPackage", null, "io.appium.android.apis");
        ((AndroidDriver) driver).activateApp(appPackage);
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

        String intentStr = String.format(
                "-a android.intent.action.VIEW -d %s -n %s/%s",
                deepLinkUrl, appPackage, appActivity);

        Map<String, Object> params = new HashMap<>();
        params.put("intent", intentStr);

        System.out.printf("[AndroidPlatform] Deep-linking via startActivity: %s%n", intentStr);
        getDriver().executeScript("mobile: startActivity", params);
    }

    @Override
    public void enterValue(String value){
        getDriver().findElement(AppiumBy.id("com.bitbar.testdroid:id/editText1")).sendKeys(value);
        getDriver().findElement(AppiumBy.id("com.bitbar.testdroid:id/button1")).click();

        System.out.println("Value from app: " + getDriver().findElement(AppiumBy.id("com.bitbar.testdroid:id/textView1")).getText());
    }

    @Override
    public void clickAccessibilityTab(){
        homePage.clickAccibilityTab().click();
    }

    @Override
    public void validateAccessibilityTab() {
        homePage.accessibilityTab().getText().equals("API Demos");
    }

    @Override
    public void installApplication(String appPath) {
        ((AndroidDriver) getDriver()).installApp(appPath);
    }

    @Override
    public void terminateApplication(String appPath) {
        ((AndroidDriver) getDriver()).terminateApp(appPath);
    }

    @Override
    public void uninstallApplication(String appPackage) {
        ((AndroidDriver) getDriver()).removeApp(appPackage);
    }

    @Override
    public void validateHomePage(){
        homePage.clickAccibilityTab().click();
        String headerText = homePage.accessibilityTab().getText();
        if (!headerText.equals("API Demos")) {
            throw new AssertionError("Expected header text to be 'API Demos' but was '" + headerText + "'");
        }
    }

}
