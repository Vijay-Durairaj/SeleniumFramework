package helper;

import controller.AndroidPlatform;
import controller.WebPlatform;
import controller.iOSPlatform;
import interfaces.ShoppingCart;

public class PlatformHelper {

    public static ShoppingCart getPlatformInstance(Platforms type) {
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

    public static ShoppingCart getCurrentPlatform() {
        return PlatformHelper.getPlatformInstance(ConfigurationHelper.getCurrentPlatform());
    }
}
