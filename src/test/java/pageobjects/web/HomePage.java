package pageobjects.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//button[contains(.,'HOME')]")
    public WebElement shopNameHeader;

    @FindBy(xpath = "//input[@name='search']")
    public WebElement searchInput;

    @FindBy(xpath = "(//span[@class='prod_price_amount '])[1]")
    public WebElement priceTag;

    public boolean getShopNameText() {
        return shopNameHeader.isDisplayed();
    }

    public WebElement getSearchKeyword(String keyword) {
        return driver.findElement(By.xpath("(//div[@class='left-pane-results-container']/div/div/div[contains(.,'"+keyword+"')])[1]")); // Assuming the search button is the same as the input field
    }

}
