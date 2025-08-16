package controller;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import view.HomePage;

import java.time.Duration;

public class HomePageController {

    WebDriver driver;
    HomePage homePage;
    /**
     * Constructor for HomePageController.
     * Initializes the WebDriver and HomePage instance.
     * @param driver the WebDriver instance to control the browser
     */
    public HomePageController(WebDriver driver) {
        this.driver = driver;
        homePage = new HomePage(driver);
    }

    public void validateHomePage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(homePage.getShopNameHeader()));

        // Validate that the shop name header is displayed
        if (!homePage.getShopNameText()) {
            throw new AssertionError("Home page validation failed: Shop name header is not displayed.");
        }
        System.out.println("Home page validation passed: Shop name header is displayed.");
    }

    public void searchForKeyword(String keyword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(homePage.getSearchInput()));

        homePage.getSearchInput().click();
        homePage.getSearchInput().sendKeys(keyword);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        wait.until(ExpectedConditions.visibilityOf(homePage.priceTag));
        Assert.assertEquals(homePage.priceTag.getText(),"$95.00");

        // Wait for the search results to be displayed
        //wait.until(ExpectedConditions.visibilityOf(homePage.getSearchKeyword(keyword)));
    }
}
