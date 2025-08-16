package utils;

import io.github.bonigarcia.wdm.*;
import org.openqa.selenium.WebDriver;

/**
 * This class provides a method to create and configure a WebDriver instance.
 * It uses WebDriverManager to manage the browser driver binaries.
 */
public class DriverFactory {
    public static WebDriver getDriver() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = WebDriverManager.chromedriver().create();
        driver.manage().window().maximize();
        return driver;
    }
}
