package helper;

import modules.AndroidPlatform;
import modules.WebPlatform;
import modules.iOSPlatform;
import interfaces.IPlatformInterface;

public class PlatformHelper {

    public static IPlatformInterface getPlatformInstance(Platforms type) {
        switch (type) {
            case ANDROID:
                return new AndroidPlatform();
            case IOS:
                return new iOSPlatform();
            case WEB:
                return new WebPlatform();
            default:
                throw new RuntimeException("Invalid platform");
        }
    }

    public static IPlatformInterface getCurrentPlatform() {
        return PlatformHelper.getPlatformInstance(ConfigurationHelper.getCurrentPlatform());
    }
}
