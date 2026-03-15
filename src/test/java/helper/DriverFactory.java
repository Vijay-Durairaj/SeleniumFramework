package helper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides one WebDriver instance per test thread.
 * Supports LOCAL and BROWSERSTACK execution for WEB, ANDROID and IOS platforms.
 * Platform is read from config.properties or -Dplatform JVM argument.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final String BROWSERSTACK_HUB_URL    = "https://hub-cloud.browserstack.com/wd/hub";
    private static final String BROWSERSTACK_APPIUM_URL = "https://hub-cloud.browserstack.com/wd/hub";
    private static final String LOCAL_APPIUM_URL        = "http://127.0.0.1:4723/wd/hub";

    // ── Public entry point ────────────────────────────────────────────────────

    /**
     * Returns a driver for the platform declared in config.properties / -Dplatform.
     * Call this from BaseTest or platform controllers.
     */
    public static WebDriver getDriver() {
        WebDriver current = DRIVER.get();
        if (current == null) {
            Platforms platform = ConfigurationHelper.getCurrentPlatform();
            boolean   remote   = isRemoteRunEnabled();
            System.out.printf("[DriverFactory] platform=%s | remote=%s%n", platform, remote);
            current = createDriver(platform, remote);
            DRIVER.set(current);
        }
        return current;
    }

    public static void quitDriver() {
        WebDriver current = DRIVER.get();
        if (current != null) {
            current.quit();
            DRIVER.remove();
        }
    }

    // ── Platform routing ──────────────────────────────────────────────────────

    private static WebDriver createDriver(Platforms platform, boolean remote) {
        switch (platform) {
            case WEB:     return remote ? createBrowserStackWebDriver()     : createLocalWebDriver();
            case ANDROID: return remote ? createBrowserStackAndroidDriver() : createLocalAndroidDriver();
            case IOS:     return remote ? createBrowserStackIOSDriver()     : createLocalIOSDriver();
            default:
                throw new RuntimeException("[DriverFactory] Unknown platform: " + platform);
        }
    }

    // ── WEB ───────────────────────────────────────────────────────────────────

    private static WebDriver createLocalWebDriver() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = WebDriverManager.chromedriver().create();
        driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver createBrowserStackWebDriver() {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName",    getSetting("browserstack.browser",        "BROWSERSTACK_BROWSER",         "Chrome"));
        capabilities.setCapability("browserVersion", getSetting("browserstack.browserVersion", "BROWSERSTACK_BROWSER_VERSION", "latest"));
        capabilities.setCapability("bstack:options", buildBstackOptions());
        return remoteDriver(BROWSERSTACK_HUB_URL, capabilities);
    }

    // ── ANDROID ───────────────────────────────────────────────────────────────

    private static WebDriver createLocalAndroidDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName",         "Android");
        caps.setCapability("deviceName",           getSetting("android.deviceName",    null, "emulator-5554"));
        caps.setCapability("platformVersion",      getSetting("android.platformVersion", null, "13.0"));
        caps.setCapability("automationName",       "UiAutomator2");
        caps.setCapability("browserName",          getSetting("android.browserName",   null, "Chrome"));
        System.out.println("[DriverFactory] Connecting to local Appium for Android...");
        return remoteDriver(LOCAL_APPIUM_URL, caps);
    }

    private static WebDriver createBrowserStackAndroidDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName",    "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("browserName",    getSetting("android.browserName", null, "Chrome"));

        Map<String, Object> bstackOptions = buildBstackOptions();
        bstackOptions.put("deviceName",       getSetting("android.deviceName",      "BSTACK_ANDROID_DEVICE",   "Samsung Galaxy S23"));
        bstackOptions.put("osVersion",        getSetting("android.platformVersion", "BSTACK_ANDROID_VERSION",  "13.0"));
        caps.setCapability("bstack:options", bstackOptions);
        return remoteDriver(BROWSERSTACK_APPIUM_URL, caps);
    }

    // ── IOS ───────────────────────────────────────────────────────────────────

    private static WebDriver createLocalIOSDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName",    "iOS");
        caps.setCapability("deviceName",      getSetting("ios.deviceName",      null, "iPhone 15"));
        caps.setCapability("platformVersion", getSetting("ios.platformVersion", null, "17.0"));
        caps.setCapability("automationName",  "XCUITest");
        caps.setCapability("browserName",     getSetting("ios.browserName",     null, "Safari"));
        System.out.println("[DriverFactory] Connecting to local Appium for iOS...");
        return remoteDriver(LOCAL_APPIUM_URL, caps);
    }

    private static WebDriver createBrowserStackIOSDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName",   "iOS");
        caps.setCapability("automationName", "XCUITest");
        caps.setCapability("browserName",    getSetting("ios.browserName", null, "Safari"));

        Map<String, Object> bstackOptions = buildBstackOptions();
        bstackOptions.put("deviceName",  getSetting("ios.deviceName",      "BSTACK_IOS_DEVICE",   "iPhone 15"));
        bstackOptions.put("osVersion",   getSetting("ios.platformVersion", "BSTACK_IOS_VERSION",  "17"));
        caps.setCapability("bstack:options", bstackOptions);
        return remoteDriver(BROWSERSTACK_APPIUM_URL, caps);
    }

    // ── BrowserStack shared options ───────────────────────────────────────────

    private static Map<String, Object> buildBstackOptions() {
        String username  = getCredential("browserstack.username",  "BROWSERSTACK_USERNAME",   "BROWSERSTACK_USER");
        String accessKey = getCredential("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY", "BROWSERSTACK_KEY");

        System.out.println("[DriverFactory] BrowserStack username : " + (isBlank(username)  ? "NOT FOUND" : username));
        System.out.println("[DriverFactory] BrowserStack accessKey: " + (isBlank(accessKey) ? "NOT FOUND" : "***hidden***"));

        if (isBlank(username) || isBlank(accessKey)) {
            throw new IllegalStateException(
                "[DriverFactory] BrowserStack credentials missing. " +
                "Set BROWSERSTACK_USERNAME + BROWSERSTACK_ACCESS_KEY env vars " +
                "or browserstack.username/browserstack.accessKey in JVM props."
            );
        }

        Map<String, Object> opts = new HashMap<>();
        opts.put("userName",     username);
        opts.put("accessKey",    accessKey);
        opts.put("projectName",  getSetting("browserstack.projectName",  "BROWSERSTACK_PROJECT_NAME",  "SeleniumTestNGFramework"));
        opts.put("buildName",    getSetting("browserstack.buildName",    "BROWSERSTACK_BUILD_NAME",    "Local Build"));
        opts.put("sessionName",  getSetting("browserstack.sessionName",  "BROWSERSTACK_SESSION_NAME",  "Test Session"));

        boolean localEnabled = "true".equalsIgnoreCase(getSetting("browserstack.local", "BROWSERSTACK_LOCAL", "false"));
        if (localEnabled) {
            opts.put("local", true);
            String localId = getSetting("browserstack.localIdentifier", "BROWSERSTACK_LOCAL_IDENTIFIER", null);
            if (!isBlank(localId)) {
                opts.put("localIdentifier", localId);
            }
        }
        return opts;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static WebDriver remoteDriver(String hubUrl, MutableCapabilities caps) {
        try {
            return new RemoteWebDriver(new URL(hubUrl), caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("[DriverFactory] Invalid hub URL: " + hubUrl, e);
        }
    }

    private static boolean isRemoteRunEnabled() {
        String flag = getSetting("run.remote", "RUN_REMOTE", "false");
        return "true".equalsIgnoreCase(flag);
    }

    private static String getSetting(String key, String envKey, String fallback) {
        String fromSystem = System.getProperty(key);
        if (!isBlank(fromSystem)) return fromSystem;

        if (!isBlank(envKey)) {
            String fromEnv = System.getenv(envKey);
            if (!isBlank(fromEnv)) return fromEnv;
        }

        String fromConfig = ConfigReader.get(key);
        if (!isBlank(fromConfig)) return fromConfig;

        return fallback;
    }

    private static String getCredential(String key, String primaryEnv, String secondaryEnv) {
        String fromSystem = System.getProperty(key);
        if (!isBlank(fromSystem)) return fromSystem;

        String fromPrimary = System.getenv(primaryEnv);
        if (!isBlank(fromPrimary)) return fromPrimary;

        String fromSecondary = System.getenv(secondaryEnv);
        if (!isBlank(fromSecondary)) return fromSecondary;

        return ConfigReader.get(key);
    }

    private static boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}
