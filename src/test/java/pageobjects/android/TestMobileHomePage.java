package pageobjects.android;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class TestMobileHomePage {

    WebDriver driver;

    public TestMobileHomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public WebElement clickAccibilityTab(){
        return this.driver.findElement(AppiumBy.accessibilityId("Accessibility"));
    }

    public WebElement accessibilityTab() {
        return this.driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"API Demos\")"));
    }


}
