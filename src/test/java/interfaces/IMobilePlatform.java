package interfaces;

/**
 * Mobile-only contract — extends IPlatformInterface with mobile-specific methods.
 * Android and IOS extend this interface.
 * Access from step definitions via: platform.asMobile().deepLogin()
 */
public interface IMobilePlatform extends IPlatformInterface {
    void deepLogin();
    void enterValue(String value);
    void clickAccessibilityTab();
    void validateAccessibilityTab();
    void installApplication(String appPath);
    void terminateApplication(String appPackage);

}
