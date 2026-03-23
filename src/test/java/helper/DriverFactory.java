package helper;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

    private static final ThreadLocal<RemoteWebDriver> DRIVER = new ThreadLocal<>();
    private static final String BROWSERSTACK_HUB_URL    = "https://hub-cloud.browserstack.com/wd/hub";
    private static final String BROWSERSTACK_APPIUM_URL = "https://hub-cloud.browserstack.com/wd/hub";
    private static final String LOCAL_APPIUM_URL        = "http://127.0.0.1:4723";
    private static final int Local_APPIUM_PORT        = 4723;
    private static final String Local_APPIUM_IP        = "127.0.0.1";
    private static AppiumDriverLocalService service;
    // ── Public entry point ────────────────────────────────────────────────────

    /**
     * Returns a driver for the platform declared in browserstack.yml / -Dplatform.
     * Supports Web (ChromeDriver), Android and iOS (RemoteWebDriver / AppiumDriver).
     * Call this from BaseTest or platform controllers.
     */
    public static RemoteWebDriver getDriver() {
        RemoteWebDriver current = DRIVER.get();
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
        RemoteWebDriver current = DRIVER.get();
        if (current != null) {
            current.quit();
            DRIVER.remove();
        }
    }

    // ── Platform routing ──────────────────────────────────────────────────────

    private static RemoteWebDriver createDriver(Platforms platform, boolean remote) {
        switch (platform) {
            case WEB:     return remote ? createBrowserStackWebDriver()     : createLocalWebDriver();
            case ANDROID: return remote ? createBrowserStackAndroidDriver() : createLocalAndroidDriver();
            case IOS:     return remote ? createBrowserStackIOSDriver()     : createLocalIOSDriver();
            default:
                throw new RuntimeException("[DriverFactory] Unknown platform: " + platform);
        }
    }

    // ── WEB ───────────────────────────────────────────────────────────────────

    private static RemoteWebDriver createLocalWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        // Run headless when CI env variable is set (GitHub Actions sets CI=true)
        if ("true".equalsIgnoreCase(System.getenv("CI"))) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        return driver;
    }

    private static RemoteWebDriver createBrowserStackWebDriver() {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName",    BrowserStackConfigReader.get("web", "browser",        "BROWSERSTACK_BROWSER",         "Chrome"));
        capabilities.setCapability("browserVersion", BrowserStackConfigReader.get("web", "browserVersion", "BROWSERSTACK_BROWSER_VERSION", "latest"));
        capabilities.setCapability("bstack:options", buildBstackOptions());
        return remoteDriver(BROWSERSTACK_HUB_URL, capabilities);
    }

    // ── ANDROID ───────────────────────────────────────────────────────────────

    private static RemoteWebDriver createLocalAndroidDriver() {
        String serverUrl = startAppiumServer();
        checkServiceRunning();
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:deviceName",      BrowserStackConfigReader.get("android", "local", "deviceName",       null, "emulator-5554"));
        caps.setCapability("appium:platformVersion",  BrowserStackConfigReader.get("android", "local", "platformVersion",  null, "13.0"));
        caps.setCapability("appium:automationName",   "UiAutomator2");
        caps.setCapability("appium:appPackage",       BrowserStackConfigReader.get("android", "local", "appPackage",       null, "org.wikipedia"));
        caps.setCapability("appium:appActivity",      BrowserStackConfigReader.get("android", "local", "appActivity",      null, "org.wikipedia.main.MainActivity"));
        System.out.println("[DriverFactory] Connecting to local Appium for Android...");
        return appiumAndroidDriver(serverUrl, caps);
    }

    private static RemoteWebDriver createBrowserStackAndroidDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName",   "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("browserName",    BrowserStackConfigReader.get("android", "local", "browserName", null, "Chrome"));

        Map<String, Object> bstackOptions = buildBstackOptions();
        bstackOptions.put("deviceName", BrowserStackConfigReader.get("android", "bstack", "deviceName", "BSTACK_ANDROID_DEVICE",  "Samsung Galaxy S23"));
        bstackOptions.put("osVersion",  BrowserStackConfigReader.get("android", "bstack", "osVersion",  "BSTACK_ANDROID_VERSION", "13.0"));
        caps.setCapability("bstack:options", bstackOptions);
        return appiumAndroidDriver(BROWSERSTACK_APPIUM_URL, caps);
    }

    // ── IOS ───────────────────────────────────────────────────────────────────

    private static RemoteWebDriver createLocalIOSDriver() {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("platformName",           "iOS");
        caps.setCapability("appium:deviceName",      BrowserStackConfigReader.get("ios", "local", "deviceName",      null, "iPhone 15"));
        caps.setCapability("appium:platformVersion",  BrowserStackConfigReader.get("ios", "local", "platformVersion", null, "17.0"));
        caps.setCapability("appium:automationName",   "XCUITest");
        caps.setCapability("appium:browserName",      BrowserStackConfigReader.get("ios", "local", "browserName",     null, "Safari"));
        System.out.println("[DriverFactory] Connecting to local Appium for iOS...");
        return appiumIOSDriver(LOCAL_APPIUM_URL, caps);
    }

    private static RemoteWebDriver createBrowserStackIOSDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName",   "iOS");
        caps.setCapability("automationName", "XCUITest");
        caps.setCapability("browserName",    BrowserStackConfigReader.get("ios", "local", "browserName", null, "Safari"));

        Map<String, Object> bstackOptions = buildBstackOptions();
        bstackOptions.put("deviceName", BrowserStackConfigReader.get("ios", "bstack", "deviceName", "BSTACK_IOS_DEVICE",  "iPhone 15"));
        bstackOptions.put("osVersion",  BrowserStackConfigReader.get("ios", "bstack", "osVersion",  "BSTACK_IOS_VERSION", "17"));
        caps.setCapability("bstack:options", bstackOptions);
        return appiumIOSDriver(BROWSERSTACK_APPIUM_URL, caps);
    }

    // ── BrowserStack shared options ───────────────────────────────────────────

    private static Map<String, Object> buildBstackOptions() {
        String username  = BrowserStackConfigReader.get("credentials", "username",  "BROWSERSTACK_USERNAME",   null);
        // fallback alias for username
        if (isBlank(username)) username = System.getenv("BROWSERSTACK_USER");

        String accessKey = BrowserStackConfigReader.get("credentials", "accessKey", "BROWSERSTACK_ACCESS_KEY", null);
        // fallback alias for accessKey
        if (isBlank(accessKey)) accessKey = System.getenv("BROWSERSTACK_KEY");

        System.out.println("[DriverFactory] BrowserStack username : " + (isBlank(username)  ? "NOT FOUND" : username));
        System.out.println("[DriverFactory] BrowserStack accessKey: " + (isBlank(accessKey) ? "NOT FOUND" : "***hidden***"));

        if (isBlank(username) || isBlank(accessKey)) {
            throw new IllegalStateException(
                "[DriverFactory] BrowserStack credentials missing. " +
                "Set BROWSERSTACK_USERNAME + BROWSERSTACK_ACCESS_KEY env vars " +
                "or set them in src/test/resources/config/browserstack.yml."
            );
        }

        Map<String, Object> opts = new HashMap<>();
        opts.put("userName",    username);
        opts.put("accessKey",   accessKey);
        opts.put("projectName", BrowserStackConfigReader.get("session", "projectName", "BROWSERSTACK_PROJECT_NAME", "SeleniumFramework"));
        opts.put("buildName",   BrowserStackConfigReader.get("session", "buildName",   "BROWSERSTACK_BUILD_NAME",   "Local Build"));
        opts.put("sessionName", BrowserStackConfigReader.get("session", "sessionName", "BROWSERSTACK_SESSION_NAME", "Test Session"));

        boolean localEnabled = "true".equalsIgnoreCase(
            BrowserStackConfigReader.get("local", "enabled", "BROWSERSTACK_LOCAL", "false"));
        if (localEnabled) {
            opts.put("local", true);
            String localId = BrowserStackConfigReader.get("local", "identifier", "BROWSERSTACK_LOCAL_IDENTIFIER", null);
            if (!isBlank(localId)) {
                opts.put("localIdentifier", localId);
            }
        }
        return opts;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static RemoteWebDriver remoteDriver(String hubUrl, MutableCapabilities caps) {
        try {
            return new RemoteWebDriver(new URL(hubUrl), caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("[DriverFactory] Invalid hub URL: " + hubUrl, e);
        }
    }

    private static AndroidDriver appiumAndroidDriver(String hubUrl, MutableCapabilities caps) {
        try {
            return new AndroidDriver(new URL(hubUrl), caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("[DriverFactory] Invalid hub URL: " + hubUrl, e);
        }
    }

    private static IOSDriver appiumIOSDriver(String hubUrl, MutableCapabilities caps) {
        try {
            return new IOSDriver(new URL(hubUrl), caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("[DriverFactory] Invalid hub URL: " + hubUrl, e);
        }
    }

    private static boolean isRemoteRunEnabled() {
        // 1. JVM arg: -Drun.remote=true
        String fromJvm = System.getProperty("run.remote");
        if (!isBlank(fromJvm)) return "true".equalsIgnoreCase(fromJvm);

        // 2. Env var: RUN_REMOTE
        String fromEnv = System.getenv("RUN_REMOTE");
        if (!isBlank(fromEnv)) return "true".equalsIgnoreCase(fromEnv);

        // 3. browserstack.yml  execution.remote
        String fromYaml = BrowserStackConfigReader.get("execution", "remote");
        return "true".equalsIgnoreCase(fromYaml);
    }

    private static boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private static String startAppiumServer() {
        service = new AppiumServiceBuilder()
                .usingPort(Local_APPIUM_PORT)
                .withIPAddress(Local_APPIUM_IP)
                .build();
        service.start();
        return service.getUrl().toString();
     }

     private static void checkServiceRunning() {
         if (service.isRunning()) {
             System.out.println("Appium Server Started!");
         } else {
             throw new RuntimeException("Appium Server failed to start!");
         }
     }
}
