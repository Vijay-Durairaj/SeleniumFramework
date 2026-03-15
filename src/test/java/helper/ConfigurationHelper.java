package helper;

public class ConfigurationHelper {

    public static Platforms getCurrentPlatform() {
        // 1. JVM arg: -Dplatform=web|android|ios
        String platformName = System.getProperty("platform");

        // 2. Environment variable: PLATFORM
        if (isBlank(platformName)) platformName = System.getenv("PLATFORM");

        // 3. browserstack.yml  execution.platform
        if (isBlank(platformName)) platformName = BrowserStackConfigReader.get("execution", "platform");

        Platforms current = Platforms.getPlatform(platformName);
        return current == Platforms.UNKNOWN ? Platforms.WEB : current;
    }

    private static boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}
