# SeleniumFramework

A **cross-platform UI automation framework** built with Java, Selenium 4, Appium, Cucumber, and TestNG.  
One set of tests runs against **Web**, **Android**, and **iOS** — locally or on **BrowserStack** — by switching a single config value.

---

## Table of Contents

- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Interface Hierarchy](#interface-hierarchy)
- [How Platform Casting Works](#how-platform-casting-works)
- [Layer Responsibilities](#layer-responsibilities)
- [End-to-End Execution Flow](#end-to-end-execution-flow)
- [Runtime Platform and Driver Selection](#runtime-platform-and-driver-selection)
- [Configuration](#configuration)
- [How to Run](#how-to-run)
- [BrowserStack Execution](#browserstack-execution)
- [Reports](#reports)
- [How to Add New Coverage](#how-to-add-new-coverage)
- [Current Limitations](#current-limitations)

---

## Key Features

| Capability | Detail |
|---|---|
| **Contract-first design** | Tests depend on interfaces, never on concrete platform classes |
| **Clean interface separation** | Common, mobile-shared, Android-only, iOS-only, and web-only methods live at different levels |
| **Platform casting helpers** | `asMobile()`, `asAndroid()`, `asIOS()`, `asWeb()` give type-safe access to the right methods |
| **No empty stubs** | `WebPlatform` never implements mobile methods; `AndroidPlatform` never implements web methods |
| **Runtime platform selection** | JVM arg, env var, or YAML — one switch changes the target platform |
| **Local + BrowserStack** | Toggle with `-Drun.remote=true` |
| **Two test entry paths** | TestNG classes and Cucumber BDD |
| **Thread-safe drivers** | `DriverFactory` uses `ThreadLocal` for parallel execution |
| **Native Appium drivers** | `AndroidDriver` and `IOSDriver` for full Appium API access (installApp, removeApp, etc.) |

---

## Tech Stack

| Library | Version | Purpose |
|---|---|---|
| Java | 17+ | Language |
| Selenium | 4.20.0 | Browser automation |
| Appium Java Client | 9.3.0 | Mobile automation (`AndroidDriver`, `IOSDriver`) |
| TestNG | 7.9.0 | Test runner |
| Cucumber | 7.15.0 | BDD runner (`cucumber-java`, `cucumber-junit`) |
| WebDriverManager | 6.2.0 | Automatic driver binary management |
| ExtentReports | 5.0.9 | HTML test reports |
| BrowserStack SDK | 1.26.1 | Cloud device execution |
| SnakeYAML | 2.2 | YAML config parsing |
| Maven | — | Build and dependency management |

---

## Project Structure

```text
SeleniumFramework/
├── pom.xml                                    # Maven build config
├── testng.xml                                 # TestNG suite definition
├── README.md
└── src/test/
    ├── java/
    │   ├── helper/                            # Framework infrastructure
    │   │   ├── BaseTest.java                       # TestNG report lifecycle
    │   │   ├── BrowserStackConfigReader.java       # Reads browserstack.yml
    │   │   ├── ConfigReader.java                   # Reads config.properties
    │   │   ├── ConfigurationHelper.java            # Resolves active platform
    │   │   ├── DriverFactory.java                  # Thread-safe driver creation
    │   │   ├── ExtentManager.java                  # ExtentReports singleton
    │   │   ├── PlatformHelper.java                 # Factory for platform instances
    │   │   └── Platforms.java                      # Enum: ANDROID, IOS, WEB
    │   │
    │   ├── interfaces/                        # Contracts
    │   │   ├── CommonAction.java                   # launchApplication()
    │   │   ├── ILoginPage.java                     # loginAs(User)
    │   │   ├── IHomePage.java                      # validateHomePage(), searchForKeyword()
    │   │   ├── ShoppingCart.java                   # Marker (extends CommonAction)
    │   │   ├── IPlatformInterface.java             # Common contract + casting helpers
    │   │   ├── IMobilePlatform.java                # Shared mobile: deepLogin, install, etc.
    │   │   ├── Android.java                        # Android-only: uninstallApplication
    │   │   ├── IOS.java                            # iOS-only (extension point)
    │   │   └── Web.java                            # Web-only (extension point)
    │   │
    │   ├── model/                             # Data objects
    │   │   └── User.java                           # username + password
    │   │
    │   ├── modules/                           # Platform implementations
    │   │   ├── WebPlatform.java                    # implements Web
    │   │   ├── AndroidPlatform.java                # implements Android
    │   │   └── iOSPlatform.java                    # implements IOS
    │   │
    │   ├── pageobjects/                       # Page Object Model
    │   │   ├── web/
    │   │   │   ├── LoginPage.java                  # Web login form
    │   │   │   └── HomePage.java                   # Web home page
    │   │   └── android/
    │   │       └── TestMobileHomePage.java          # Android API Demos page
    │   │
    │   ├── stepdefinitions/                   # Cucumber glue
    │   │   ├── AbstractStepDefinitions.java        # Base: platform + data cache
    │   │   ├── LoginPageSteps.java                 # Login feature steps
    │   │   └── TestAndroidMobileApplicationSteps.java  # Android app steps
    │   │
    │   ├── tests/                             # TestNG tests
    │   │   ├── LoginTest.java                      # Login + deep-link
    │   │   └── HomePage.java                       # Search keyword
    │   │
    │   └── utils/
    │       └── TestRunner.java                     # Cucumber JUnit runner
    │
    └── resources/
        ├── config/
        │   ├── browserstack.yml                    # Platform, execution, device config
        │   └── config.properties                   # App test data (URLs, credentials)
        ├── features/
        │   ├── loginpage.feature                   # Login scenarios
        │   └── testAndroidApp.feature              # Android app install/launch scenarios
        └── application/
            └── ApiDemos-debug.apk                  # Android test APK
```

---

## Interface Hierarchy

This is the core design of the framework. Each level adds methods specific to its scope. A class only implements the interface it needs — no empty stubs.

```mermaid
flowchart TD
    CA["CommonAction\nlaunchApplication()"]
    ILP["ILoginPage\nloginAs(User)"]
    IHP["IHomePage\nvalidateHomePage()\nsearchForKeyword(String)"]
    SC["ShoppingCart"]

    CA --> SC
    SC --> IPI
    ILP --> IPI
    IHP --> IPI

    IPI["IPlatformInterface\nasMobile() | asAndroid()\nasIOS() | asWeb()"]

    IPI --> WEB["Web"]
    IPI --> IMP["IMobilePlatform\ndeepLogin()\nenterValue(String)\nclickAccessibilityTab()\nvalidateAccessibilityTab()\ninstallApplication(String)\nterminateApplication(String)"]

    IMP --> AND["Android\nuninstallApplication(String)"]
    IMP --> IOS["IOS"]

    WEB -.->|implemented by| WP["WebPlatform"]
    AND -.->|implemented by| AP["AndroidPlatform"]
    IOS -.->|implemented by| IP["iOSPlatform"]

    style IPI fill:#e1f5fe
    style IMP fill:#fff3e0
    style WEB fill:#e8f5e9
    style AND fill:#fff3e0
    style IOS fill:#fff3e0
```

**How to read:**
- Solid arrows = **extends** (interface inheritance)
- Dotted arrows = **implements** (class fulfills interface)
- Each level only adds what's new at that level
- `WebPlatform` only implements `Web` → inherits common methods, zero mobile stubs
- `AndroidPlatform` implements `Android` → gets common + mobile + Android-specific
- `iOSPlatform` implements `IOS` → gets common + mobile + iOS-specific

### What each level owns

| Level | Interface | Methods defined here |
|---|---|---|
| **Common** | `IPlatformInterface` | `launchApplication()`, `loginAs(User)`, `validateHomePage()`, `searchForKeyword(String)` + casting helpers |
| **Mobile shared** | `IMobilePlatform` | `deepLogin()`, `enterValue()`, `clickAccessibilityTab()`, `validateAccessibilityTab()`, `installApplication()`, `terminateApplication()` |
| **Android only** | `Android` | `uninstallApplication()` |
| **iOS only** | `IOS` | *(extension point — add iOS-specific methods here)* |
| **Web only** | `Web` | *(extension point — add web-specific methods here)* |

---

## How Platform Casting Works

`IPlatformInterface` provides four casting helpers. Each returns a more specific type, giving you access to that level's methods.

```java
// Common — works on all platforms
platform.launchApplication();
platform.loginAs(user);
platform.validateHomePage();
platform.searchForKeyword("watch");

// Mobile shared — Android + iOS
platform.asMobile().deepLogin();
platform.asMobile().installApplication(apkPath);
platform.asMobile().terminateApplication(packageName);

// Android only
platform.asAndroid().uninstallApplication(packageName);

// iOS only (future methods)
platform.asIOS().someIOSOnlyMethod();

// Web only (future methods)
platform.asWeb().someWebOnlyMethod();
```

### What the IDE shows at each level

| You type | Autocomplete shows |
|---|---|
| `platform.` | Common: `launchApplication`, `loginAs`, `validateHomePage`, `searchForKeyword`, `asMobile`, `asAndroid`, `asIOS`, `asWeb` |
| `platform.asMobile().` | Mobile shared: `deepLogin`, `enterValue`, `clickAccessibilityTab`, `validateAccessibilityTab`, `installApplication`, `terminateApplication` + all common |
| `platform.asAndroid().` | Android only: `uninstallApplication` + all mobile + all common |
| `platform.asIOS().` | iOS only (future) + all mobile + all common |
| `platform.asWeb().` | Web only (future) + all common |

### Runtime safety

Calling `platform.asMobile()` on a `WebPlatform` throws `UnsupportedOperationException` with a clear message. Same for `asAndroid()` on iOS, etc.

---

## Layer Responsibilities

### `interfaces/` — Contracts

| Interface | Extends | Methods |
|---|---|---|
| `CommonAction` | — | `launchApplication()` |
| `ILoginPage` | — | `loginAs(User)` |
| `IHomePage` | — | `validateHomePage()`, `searchForKeyword(String)` |
| `ShoppingCart` | `CommonAction` | *(marker — future cart features)* |
| `IPlatformInterface` | all above | `asMobile()`, `asAndroid()`, `asIOS()`, `asWeb()` |
| `IMobilePlatform` | `IPlatformInterface` | `deepLogin()`, `enterValue()`, `clickAccessibilityTab()`, `validateAccessibilityTab()`, `installApplication()`, `terminateApplication()` |
| `Android` | `IMobilePlatform` | `uninstallApplication()` |
| `IOS` | `IMobilePlatform` | *(extension point)* |
| `Web` | `IPlatformInterface` | *(extension point)* |

### `modules/` — Implementations

| Class | Implements | Driver type | Status |
|---|---|---|---|
| `WebPlatform` | `Web` | `ChromeDriver` / `RemoteWebDriver` | ✅ Full (login, home, search) |
| `AndroidPlatform` | `Android` | `AndroidDriver` | ✅ Deep-link, app install/uninstall/terminate, accessibility |
| `iOSPlatform` | `IOS` | `IOSDriver` | ⚙️ Stubs (driver wiring ready) |

### `helper/` — Framework Infrastructure

| Class | Purpose |
|---|---|
| `ConfigReader` | Loads `config.properties` — app test data |
| `BrowserStackConfigReader` | Loads `browserstack.yml` — priority: JVM arg → env var → YAML → fallback |
| `ConfigurationHelper` | Resolves active platform from JVM / env / YAML |
| `PlatformHelper` | Factory — returns cached `IPlatformInterface` instance |
| `DriverFactory` | Thread-safe driver creation: `ChromeDriver` for web, `AndroidDriver` for Android, `IOSDriver` for iOS. Local and BrowserStack |
| `Platforms` | Enum: `ANDROID`, `IOS`, `WEB`, `UNKNOWN` |
| `BaseTest` + `ExtentManager` | TestNG + ExtentReports lifecycle |

### `pageobjects/` — Page Object Model

| Class | Platform | Elements |
|---|---|---|
| `pageobjects.web.LoginPage` | Web | `usernameInput`, `passwordInput`, `loginButton` |
| `pageobjects.web.HomePage` | Web | `shopNameHeader`, `searchInput`, `priceTag` |
| `pageobjects.android.TestMobileHomePage` | Android | Accessibility tab, API Demos header |

### `stepdefinitions/` — Cucumber Glue

| Class | Scope | Key calls |
|---|---|---|
| `AbstractStepDefinitions` | Base | Initializes `IPlatformInterface platform`, provides `getOrSaveData()` |
| `LoginPageSteps` | Login flows | `platform.launchApplication()`, `platform.asMobile().deepLogin()` |
| `TestAndroidMobileApplicationSteps` | Android app lifecycle | `platform.asMobile().installApplication()`, `platform.asAndroid().uninstallApplication()` |

### `tests/` — TestNG Tests

| Class | Tests |
|---|---|
| `LoginTest` | `validLoginTest()` — full login flow; `deepLinkLoginTest()` — mobile deep-link |
| `HomePage` | `searchKeywordTest()` — launch + search |

---

## End-to-End Execution Flow

```mermaid
flowchart TD
    START["Start Test"] --> ENTRY{"Entry Type?"}

    ENTRY -->|TestNG| TNG["testng.xml\nor -Dtest=tests.LoginTest"]
    ENTRY -->|Cucumber| CUC["utils.TestRunner"]

    TNG --> TCLASS["LoginTest\nHomePage"]
    CUC --> FEAT["loginpage.feature\ntestAndroidApp.feature\n+ Step Definitions"]

    TCLASS --> ABS["AbstractStepDefinitions\nconstructor"]
    FEAT --> ABS

    ABS --> PH["PlatformHelper\ngetCurrentPlatform()"]
    PH --> CH["ConfigurationHelper\ngetCurrentPlatform()"]
    CH --> PLAT{"platform?"}

    PLAT -->|web| WP["new WebPlatform\nimplements Web"]
    PLAT -->|android| AP["new AndroidPlatform\nimplements Android"]
    PLAT -->|ios| IP["new iOSPlatform\nimplements IOS"]

    WP --> DF["DriverFactory\ngetDriver()"]
    AP --> DF
    IP --> DF

    DF --> REMOTE{"run.remote?"}
    REMOTE -->|false| LOCAL["Local\nChromeDriver / AndroidDriver / IOSDriver"]
    REMOTE -->|true| BS["BrowserStack\nRemoteWebDriver / AndroidDriver / IOSDriver"]

    LOCAL --> ACTIONS["Platform actions"]
    BS --> ACTIONS

    ACTIONS --> REPORT["Assertions + Reports"]
    REPORT --> QUIT["DriverFactory.quitDriver()"]
```

---

## Runtime Platform and Driver Selection

```mermaid
flowchart LR
    JVM["-Dplatform\n-Drun.remote"] --> CH["ConfigurationHelper"]
    ENV["PLATFORM\nRUN_REMOTE"] --> CH
    YML["browserstack.yml"] --> CH

    CH --> PH["PlatformHelper"]
    PH --> PI{"platform instance?"}

    PI -->|web| WP["WebPlatform"]
    PI -->|android| AP["AndroidPlatform"]
    PI -->|ios| IP["iOSPlatform"]

    WP --> DFW["DriverFactory\nChromeDriver"]
    AP --> DFA["DriverFactory\nAndroidDriver"]
    IP --> DFI["DriverFactory\nIOSDriver"]

    DFW --> RT{"run.remote?"}
    DFA --> RT
    DFI --> RT
    RT -->|false| LOC["Local browser\nor Appium"]
    RT -->|true| BS["BrowserStack"]
```

**Resolution priority** (highest wins):

1. JVM system property (`-Dplatform=android`)
2. Environment variable (`PLATFORM=android`)
3. `browserstack.yml` → `execution.platform`
4. Default: `web`

---

## Configuration

### `config.properties` — Application Test Data

```properties
login.url=https://rahulshettyacademy.com/client/#/auth/login
login.username=vijaydurairaj@mail.com
login.password=P@ssword@1
home.searchbox=apple watch
android.apk=src/test/resources/application/ApiDemos-debug.apk
android.appPackage=io.appium.android.apis
```

### `browserstack.yml` — Execution, Platform, and Device Settings

```yaml
execution:
  platform: android         # web | android | ios
  remote: false             # false = local | true = BrowserStack

credentials:
  username: ""              # env: BROWSERSTACK_USERNAME
  accessKey: ""             # env: BROWSERSTACK_ACCESS_KEY

web:
  browser: Chrome
  browserVersion: latest

android:
  local:
    deviceName: emulator-5554
    platformVersion: "11"
    appPackage: io.appium.android.apis
    appActivity: io.appium.android.apis.ApiDemos
  bstack:
    deviceName: Samsung Galaxy S23
    osVersion: "13.0"

ios:
  local:
    deviceName: iPhone 15
    platformVersion: "17.0"
    browserName: Safari
  bstack:
    deviceName: iPhone 15
    osVersion: "17"
```

Every YAML value can be overridden at runtime:

```bash
mvn test -Dbrowserstack.execution.platform=android
# or
export PLATFORM=android && mvn test
```

---

## How to Run

### Run a specific platform locally

```bash
# Web (default)
mvn test -Dplatform=web -Drun.remote=false

# Android (requires Appium server running + emulator)
mvn test -Dplatform=android -Drun.remote=false

# iOS (requires Appium server running + simulator)
mvn test -Dplatform=ios -Drun.remote=false
```

### Run via TestNG

```bash
mvn test -DsuiteXmlFile=testng.xml
mvn -Dtest=tests.LoginTest test
mvn -Dtest=tests.HomePage test
```

### Run via Cucumber

```bash
mvn -Dtest=utils.TestRunner test
```

### Run on BrowserStack

```bash
export BROWSERSTACK_USERNAME="your-user"
export BROWSERSTACK_ACCESS_KEY="your-key"
mvn test -Dplatform=web -Drun.remote=true
mvn test -Dplatform=android -Drun.remote=true
```

---

## BrowserStack Execution

| What | How |
|---|---|
| Set credentials | `BROWSERSTACK_USERNAME` / `BROWSERSTACK_ACCESS_KEY` env vars |
| Toggle remote | `-Drun.remote=true` or `execution.remote: true` in YAML |
| View results | [BrowserStack Automate Dashboard](https://automate.browserstack.com/) |
| Device selection | `browserstack.yml` → `android.bstack.*` / `ios.bstack.*` |

---

## Reports

| Report | Location |
|---|---|
| ExtentReports HTML | `target/ExtentReport.html` |
| Cucumber JSON | `target/cucumber-reports/Cucumber.json` |
| Cucumber XML | `target/cucumber-reports/Cucumber.xml` |
| Surefire reports | `target/surefire-reports/` |

---

## How to Add New Coverage

### Add a common method (all platforms)

1. Add the method to the relevant behavior interface (`IHomePage`, `ILoginPage`, `CommonAction`, or create new)
2. Wire it into `IPlatformInterface` if it's a new interface
3. Implement in `WebPlatform`, `AndroidPlatform`, and `iOSPlatform`

### Add a mobile-shared method (Android + iOS)

1. Add the method to `IMobilePlatform`
2. Implement in `AndroidPlatform` and `iOSPlatform`
3. Call via `platform.asMobile().newMethod()`

### Add an Android-only method

1. Add the method to `Android` interface
2. Implement in `AndroidPlatform`
3. Call via `platform.asAndroid().newMethod()`

### Add an iOS-only method

1. Add the method to `IOS` interface
2. Implement in `iOSPlatform`
3. Call via `platform.asIOS().newMethod()`

### Add a web-only method

1. Add the method to `Web` interface
2. Implement in `WebPlatform`
3. Call via `platform.asWeb().newMethod()`

### Add page objects

- Web pages go in `pageobjects/web/`
- Android pages go in `pageobjects/android/`
- iOS pages go in `pageobjects/ios/` (create as needed)

---

## Current Limitations

- `iOSPlatform` has stub implementations — driver wiring is ready but business flows need implementation
- Page objects for iOS (`pageobjects/ios/`) need to be created
- `ShoppingCart` interface is a placeholder — no cart-specific actions defined yet
- `BaseTest` and `ExtentManager` are wired for TestNG only; Cucumber uses its own reporter plugins
