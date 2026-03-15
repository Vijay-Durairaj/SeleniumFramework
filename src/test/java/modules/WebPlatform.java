package modules;

import helper.ConfigReader;
import interfaces.Web;
import model.User;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageobjects.LoginPage;
import helper.DriverFactory;
import pageobjects.HomePage;

import java.time.Duration;

public class WebPlatform implements Web {

    protected WebDriver driver;

    private final HomePage homePage;
    private final LoginPage loginPage;

    public  WebPlatform() {
        driver = DriverFactory.getDriver();
        homePage = new HomePage(driver);
        loginPage = new LoginPage(driver);
    }

    @Override
    public void launchApplication() {
        driver.navigate().to(ConfigReader.get("login.url"));
    }

    @Override
    public void loginAs(User validUser) {
        loginPage.enterUsername(validUser.getUsername());
        loginPage.enterPassword(validUser.getPassword());
        loginPage.clickLogin();
    }

    @Override
    public void validateHomePage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(homePage.getShopNameHeader()));

        // Validate that the shop name header is displayed
        if (!homePage.getShopNameText()) {
            throw new AssertionError("Home page validation failed: Shop name header is not displayed.");
        }
        System.out.println("Home page validation passed: Shop name header is displayed.");
    }

    @Override
    public void searchForKeyword(String keyword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(homePage.getSearchInput()));

        homePage.getSearchInput().click();
        homePage.getSearchInput().sendKeys(keyword);

        wait.until(ExpectedConditions.visibilityOf(homePage.getPriceTag()));
    }
}
