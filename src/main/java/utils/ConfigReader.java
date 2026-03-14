package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream classpathStream = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (classpathStream != null) {
                properties.load(classpathStream);
            } else {
                try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
                    properties.load(fis);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
