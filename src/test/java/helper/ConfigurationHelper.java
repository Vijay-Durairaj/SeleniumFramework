package helper;

public class ConfigurationHelper {

    public static Platforms getCurrentPlatform() {
        String configuredPlatform = ConfigReader.get("platform");
        String platformName = System.getProperty("platform", configuredPlatform);

        Platforms current = Platforms.getPlatform(platformName);
        return current == Platforms.UNKNOWN ? Platforms.WEB : current;
    }
}
