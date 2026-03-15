package pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * This class represents the login page of the application.
 * It contains methods to interact with the login form elements.
 */
public class LoginPage {
    WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "userEmail")
    private WebElement usernameInput;

    @FindBy(id = "userPassword")
    private WebElement passwordInput;

    @FindBy(id = "login")
    private WebElement loginButton;

    @FindBy(xpath ="//input[@type='checkbox']")
    private WebElement rememberMeCheckbox;

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public void clickRememberMe() {
        wait.until(ExpectedConditions.elementToBeClickable(rememberMeCheckbox));
        if (!rememberMeCheckbox.isSelected()) {
            rememberMeCheckbox.click();
        }
    }
    
    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }
}
