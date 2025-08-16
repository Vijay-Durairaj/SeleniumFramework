package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import controller.HomePageController;
import controller.LoginController;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * BaseTest class for setting up and tearing down test reports.
 * This class initializes the ExtentReports instance and manages test logging.
 * It provides methods to create a test, log its status, and flush the report after all tests are run.
 */
public class BaseTest {

    protected static ExtentReports extent;
    protected ExtentTest test;

    protected WebDriver driver;
    protected LoginController loginController;
    protected HomePageController homePageController;

    /**
     * Initializes the ExtentReports instance before any tests are run.
     * This method is called once before the test suite starts to set up the reporting environment.
     */
    @BeforeSuite
    public void setupReport() {
        extent = ExtentManager.getExtentReports();
    }

    /**
     * Sets up the WebDriver and navigates to the login page before each test method.
     * This method initializes the LoginController and HomePageController instances.
     */
    @BeforeMethod
    public void setup() {
        driver = DriverFactory.getDriver();
        driver.get(ConfigReader.get("login.url"));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        loginController = new LoginController(driver);
        homePageController = new HomePageController(driver);
    }

    /**
     * Creates a new test in the ExtentReports instance.
     * This method is called before each test method to initialize a new test entry in the report.
     *
     * @param method the test method being executed
     */
    @BeforeMethod
    public void createTest(Method method) {
        test = extent.createTest(method.getName());
    }

    /**
     * Logs the status of the test after its execution.
     * This method is called after each test method to log whether it passed, failed, or was skipped.
     *
     * @param result the result of the executed test
     */
    @AfterMethod
    public void logTestStatus(org.testng.ITestResult result) {
        if (result.getStatus() == org.testng.ITestResult.SUCCESS) {
            test.pass("Test passed");
        } else if (result.getStatus() == org.testng.ITestResult.FAILURE) {
            test.fail("Test failed");
        } else if (result.getStatus() == org.testng.ITestResult.SKIP) {
            test.skip("Test skipped");
        }
    }

    /**
     * Flushes the ExtentReports instance to write all logs to the report file.
     * This method is called after all tests have been executed.
     */
    @AfterSuite
    public void flushReport() {
        extent.flush();
    }

    /**
     * Tears down the WebDriver instance after each test method.
     * This method is called after each test to ensure the browser is closed and resources are released.
     */
    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

}
