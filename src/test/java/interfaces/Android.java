package interfaces;

/**
 * Android-specific contract.
 * Extends IMobilePlatform (common + mobile methods).
 * Add any Android-only method signatures here in the future.
 */
public interface Android extends IMobilePlatform {
    void uninstallApplication(String appPackage);
}
