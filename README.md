# SeleniumFramework

Java UI automation framework using Selenium 4 with both TestNG and Cucumber execution paths, plus platform routing for Web, Android, and iOS.

## What This Framework Supports

- Single platform abstraction through `interfaces.IPlatformInterface`
- Runtime platform selection via `platform` config (`web`, `android`, `ios`)
- Local and BrowserStack execution via `helper.DriverFactory`
- Two entry paths:
  - TestNG tests in `src/test/java/tests`
  - Cucumber runner in `src/test/java/utils/TestRunner.java`

## Tech Stack

- Java 17+
- Selenium 4.20.0
- TestNG 7.9.0
- Cucumber 7.15.0 (`cucumber-java`, `cucumber-junit`)
- WebDriverManager 6.2.0
- ExtentReports 5.0.9
- Maven

## Current Framework Structure

```text
SeleniumFramework/
|- pom.xml
|- README.md
|- testng.xml
|- src/
|  |- test/
|  |  |- java/
|  |  |  |- helper/
|  |  |  |  |- BaseTest.java
|  |  |  |  |- ConfigReader.java
|  |  |  |  |- ConfigurationHelper.java
|  |  |  |  |- DriverFactory.java
|  |  |  |  |- ExtentManager.java
|  |  |  |  |- PlatformHelper.java
|  |  |  |  `- Platforms.java
|  |  |  |- interfaces/
|  |  |  |  |- Android.java
|  |  |  |  |- CommonAction.java
|  |  |  |  |- IHomePage.java
|  |  |  |  |- ILoginPage.java
|  |  |  |  |- IMobilePlatform.java
|  |  |  |  |- IPlatformInterface.java
|  |  |  |  |- ShoppingCart.java
|  |  |  |  `- Web.java
|  |  |  |- model/
|  |  |  |  `- User.java
|  |  |  |- modules/
|  |  |  |  |- AndroidPlatform.java
|  |  |  |  |- WebPlatform.java
|  |  |  |  `- iOSPlatform.java
|  |  |  |- pageobjects/
|  |  |  |  |- HomePage.java
|  |  |  |  `- LoginPage.java
|  |  |  |- stepdefinitions/
|  |  |  |  |- AbstractStepDefinitions.java
|  |  |  |  `- LoginPageSteps.java
|  |  |  |- tests/
|  |  |  |  |- HomePage.java
|  |  |  |  `- LoginTest.java
|  |  |  `- utils/
|  |  |     `- TestRunner.java
|  |  `- resources/
|  |     |- config/
|  |     |  |- browserstack.yml
|  |     |  `- config.properties
|  |     `- features/
|  |        `- loginpage.feature
`- target/
   `- cucumber-reports.html
```

## Layer Responsibilities

### `helper` layer

- `ConfigReader`: loads application test data from `config/config.properties`
- `BrowserStackConfigReader`: loads execution mode, BrowserStack settings, and mobile capabilities from `config/browserstack.yml`
- `ConfigurationHelper`: resolves the active platform from JVM args, environment variables, or `browserstack.yml`
- `PlatformHelper`: creates the correct platform implementation (`WebPlatform`, `AndroidPlatform`, `iOSPlatform`)
- `DriverFactory`: creates and caches one driver per thread via `ThreadLocal<WebDriver>`
- `BaseTest` + `ExtentManager`: TestNG reporting lifecycle

### `interfaces` layer

- `ILoginPage` defines login behavior: `loginAs(User user)`
- `IHomePage` defines post-login/home behavior: `validateHomePage()` and `searchForKeyword(String keyword)`
- `CommonAction` defines shared high-level actions such as `launchApplication()`, `loginAs(User)`, and `validateHomePage()`
- `ShoppingCart` is currently an empty marker/extension interface
- `IPlatformInterface` is the main abstraction consumed by tests and step definitions; it extends `ILoginPage`, `IHomePage`, `ShoppingCart`, and `CommonAction`
- Platform-specific specializations sit on top of it:
  - `Web extends IPlatformInterface`
  - `IMobilePlatform extends IPlatformInterface`
  - `Android extends IMobilePlatform`

> Note: `loginAs(...)` and `validateHomePage()` currently appear in multiple contracts. At runtime, consumers still interact through a single `IPlatformInterface` reference.

### `modules` layer

- `WebPlatform`: implemented login/home/search flow
- `AndroidPlatform`, `iOSPlatform`: driver wiring exists, business steps are placeholders

### `pageobjects` layer

- `LoginPage` and `HomePage` contain UI element actions and locators

### `stepdefinitions` layer

- `AbstractStepDefinitions` initializes:
  - `protected IPlatformInterface platform`
  - scenario-level key/value cache via `getOrSaveData(String key, String defaultValue)`
- `LoginPageSteps` maps Gherkin steps to platform calls

### `tests` layer

- TestNG tests (`LoginTest`, `HomePage`) directly invoke `platform` methods

## Interface Organization

The project uses a **contract-first design** so that tests and Cucumber step definitions never directly depend on `WebPlatform`, `AndroidPlatform`, or `iOSPlatform`.

Instead, the flow is:

1. Tests/steps work with `IPlatformInterface`
2. `PlatformHelper` resolves the active platform at runtime
3. A concrete module (`WebPlatform`, `AndroidPlatform`, or `iOSPlatform`) is returned
4. That module fulfills the interface contract and delegates UI work to page objects and driver code

### Interface Hierarchy Flowchart

```mermaid
flowchart LR
    ILoginPage --> IPlatformInterface
    IHomePage --> IPlatformInterface
    CommonAction --> IPlatformInterface
    ShoppingCart --> IPlatformInterface
    IPlatformInterface --> Web
    IPlatformInterface --> IMobilePlatform
    IMobilePlatform --> Android
```

### How These Interfaces Are Organized

- `ILoginPage`, `IHomePage`, and `CommonAction` contain the current behavior contracts.
- `ShoppingCart` is a placeholder interface for future cart-related features.
- `IPlatformInterface` is the main contract used by tests and step definitions.
- `Web`, `IMobilePlatform`, and `Android` are platform-specific extensions of that contract.

### Runtime Usage Flowchart

```mermaid
flowchart LR
    subgraph Consumers[Consumers]
        T1[tests.LoginTest / tests.HomePage]
        T2[stepdefinitions.LoginPageSteps]
    end

    T1 --> A[AbstractStepDefinitions]
    T2 --> A

    A -->|initializes| P[platform : IPlatformInterface]
    A --> H["PlatformHelper.getCurrentPlatform()"]
    H --> C["ConfigurationHelper.getCurrentPlatform()"]
    C --> SEL{platform value}

    SEL -->|web| WP[WebPlatform implements Web]
    SEL -->|android| AP[AndroidPlatform implements Android]
    SEL -->|ios| IPH[iOSPlatform implements IMobilePlatform]

    WP --> D["DriverFactory.getDriver()"]
    AP --> D
    IPH --> D

    WP --> PO[LoginPage / HomePage]
```

### Practical Example

- `LoginPageSteps` extends `AbstractStepDefinitions`
- `AbstractStepDefinitions` creates `protected IPlatformInterface platform`
- When `platform=web`, `PlatformHelper` returns `new WebPlatform()`
- The step definition calls `platform.launchApplication()` and `platform.loginAs(...)`
- `WebPlatform` executes the action using `LoginPage`, `HomePage`, and `DriverFactory`

## End-to-End Execution Flowchart

```mermaid
flowchart TD
    A["Start Test Execution"] --> B{"Entry Type"}

    B -->|TestNG| C["testng.xml or -Dtest=tests.LoginTest"]
    B -->|Cucumber| D["utils.TestRunner"]

    C --> E["tests.LoginTest / tests.HomePage"]
    D --> F["loginpage.feature + LoginPageSteps"]

    E --> G["AbstractStepDefinitions constructor"]
    F --> G

    G --> H["PlatformHelper.getCurrentPlatform"]
    H --> I["ConfigurationHelper.getCurrentPlatform"]
    I --> J{"platform value"}

    J -->|web| K["new WebPlatform"]
    J -->|android| L["new AndroidPlatform"]
    J -->|ios| M["new iOSPlatform"]

    K --> N["DriverFactory.getDriver"]
    L --> N
    M --> N

    N --> O{"run.remote"}
    O -->|false| P["Local WebDriver / Appium"]
    O -->|true| Q["BrowserStack RemoteWebDriver"]

    P --> R["Platform actions: launch / login / validate / search"]
    Q --> R

    R --> S["Assertions + report output"]
    S --> T["DriverFactory.quitDriver on teardown"]
```

## Runtime Selection Flow (Platform + Driver)

```mermaid
flowchart LR
    Jvm["-Dplatform / -Drun.remote"] --> Ch["ConfigurationHelper"]
    Env["PLATFORM / RUN_REMOTE"] --> Ch
    Yaml["browserstack.yml"] --> Ch
    Ch --> Ph["PlatformHelper"]
    Ph --> Pi["IPlatformInterface instance"]
    Pi --> Df["DriverFactory.getDriver"]
    Df --> Rt{"run.remote"}
    Rt -->|false| Loc["Local browser / Appium"]
    Rt -->|true| Bs["BrowserStack"]
```

## Test Execution Paths

### 1) TestNG path

- Class examples:
  - `src/test/java/tests/LoginTest.java`
  - `src/test/java/tests/HomePage.java`
- Each class extends `AbstractStepDefinitions`
- Tests call `platform.launchApplication()`, `platform.loginAs(...)`, `platform.validateHomePage()`, `platform.searchForKeyword(...)`

Run examples:

```bash
mvn test -DsuiteXmlFile=testng.xml
mvn -Dtest=tests.LoginTest test
mvn -Dtest=tests.HomePage test
```

### 2) Cucumber path

- Runner: `src/test/java/utils/TestRunner.java`
- Feature: `src/test/resources/features/loginpage.feature`
- Step definitions: `src/test/java/stepdefinitions/LoginPageSteps.java`

Run example:

```bash
mvn -Dtest=utils.TestRunner test
```

## Configuration

Files:

- `src/test/resources/config/config.properties`
- `src/test/resources/config/browserstack.yml`

### `config.properties`

Keep only application test data here:

- `login.url`
- `login.username`
- `login.password`
- `home.searchbox`

### `browserstack.yml`

Keep execution and platform settings here:

- `execution.platform=web|android|ios`
- `execution.remote=true|false`
- `credentials.username`, `credentials.accessKey`
- `web.browser`, `web.browserVersion`, `web.os`, `web.osVersion`
- `android.local.*`, `android.bstack.*`
- `ios.local.*`, `ios.bstack.*`
- `session.*`, `local.*`

Override at runtime with JVM args:

```bash
mvn test -Dplatform=web -Drun.remote=false
mvn test -Dplatform=android -Drun.remote=true
```

## BrowserStack Quick Start

```bash
export BROWSERSTACK_USERNAME="<your-user>"
export BROWSERSTACK_ACCESS_KEY="<your-key>"
mvn test -Dplatform=web -Drun.remote=true
```

You can keep default execution values in `browserstack.yml` and override them only when needed with JVM args or environment variables.

## Reports

- Cucumber HTML report: `target/cucumber-reports.html`
- ExtentReports support exists in `helper/BaseTest.java` and `helper/ExtentManager.java`

## Notes and Current Limitations

- `WebPlatform` has active business implementation.
- `AndroidPlatform` and `iOSPlatform` contain TODO implementations for flow methods.
- `AbstractStepDefinitions#getOrSaveData` is available for shared key/value data inside classes that extend it.
- `ShoppingCart` is present in the interface model but does not yet define any cart-specific actions.

## How To Add New Coverage

1. Add/extend a method contract in `interfaces` if needed.
2. Implement method in `modules/WebPlatform` (and mobile modules as required).
3. Reuse or add locators/actions in `pageobjects`.
4. Call the method from either:
   - a TestNG class in `src/test/java/tests`, or
   - a Cucumber step class in `src/test/java/stepdefinitions`.
