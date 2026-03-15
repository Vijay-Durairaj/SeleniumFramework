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

    private HomePage homePage;
    private LoginPage loginPage;

    public  WebPlatform() {
    }

    private void ensureInitialized() {
        if (driver == null) {
            driver = DriverFactory.getDriver();
        }
        if (homePage == null) {
            homePage = new HomePage(driver);
        }
        if (loginPage == null) {
            loginPage = new LoginPage(driver);
        }
    }

    @Override
    public void launchApplication() {
        ensureInitialized();
        driver.navigate().to(ConfigReader.get("login.url"));
        loginPage.waitUntilLoaded();
    }

    @Override
    public void loginAs(User validUser) {
        ensureInitialized();
        loginPage.enterUsername(validUser.getUsername());
        loginPage.enterPassword(validUser.getPassword());
        loginPage.clickLogin();
    }

    @Override
    public void validateHomePage() {
        ensureInitialized();
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
        ensureInitialized();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(homePage.getSearchInput()));

        homePage.getSearchInput().click();
        homePage.getSearchInput().sendKeys(keyword);

        wait.until(ExpectedConditions.visibilityOf(homePage.getPriceTag()));
    }
}
