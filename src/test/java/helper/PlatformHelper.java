package helper;

import interfaces.IPlatformInterface;
import modules.AndroidPlatform;
import modules.WebPlatform;
import modules.iOSPlatform;
import java.util.HashMap;

public class PlatformHelper {

    private static final HashMap<Platforms, IPlatformInterface> platforms = new HashMap<>();

    public static <T extends IPlatformInterface> T getPlatformInstance(Platforms type) {
        if (!platforms.containsKey(type)) {
            IPlatformInterface platform = switch (type) {
                case ANDROID -> new AndroidPlatform();
                case IOS -> new iOSPlatform();
                case WEB -> new WebPlatform();
                default -> throw new RuntimeException("Invalid platform");
            };
            platforms.put(type, platform);
        }
        return (T) platforms.get(type);
    }

    public static <T extends IPlatformInterface> T getCurrentPlatform() {
        return PlatformHelper.getPlatformInstance(ConfigurationHelper.getCurrentPlatform());
    }
}
