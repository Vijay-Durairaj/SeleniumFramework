package helper;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Reads BrowserStack configuration from src/test/resources/config/browserstack.yml.
 *
 * Resolution priority for every value (highest to lowest):
 *   1. JVM system property  →  -Dbrowserstack.<section>.<key>=value
 *   2. Environment variable →  as documented next to each key in browserstack.yml
 *   3. browserstack.yml value
 *   4. Hardcoded fallback passed by the caller
 */
public class BrowserStackConfigReader {

    private static final Map<String, Object> CONFIG;

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = BrowserStackConfigReader.class.getClassLoader()
                .getResourceAsStream("config/browserstack.yml")) {
            if (in == null) {
                throw new RuntimeException(
                    "[BrowserStackConfigReader] browserstack.yml not found in classpath under config/");
            }
            Map<String, Object> loaded = yaml.load(in);
            CONFIG = (loaded != null) ? loaded : Collections.emptyMap();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("[BrowserStackConfigReader] Failed to load browserstack.yml", e);
        }
    }

    /**
     * Resolves a BrowserStack setting using the priority chain.
     *
     * @param section  top-level YAML section  (e.g. "credentials", "web", "session")
     * @param key      key inside the section   (e.g. "username", "browser")
     * @param envKey   environment variable to check; pass null to skip env lookup
     * @param fallback returned when nothing else resolves; may be null
     */
    @SuppressWarnings("unchecked")
    public static String get(String section, String key, String envKey, String fallback) {
        // 1. JVM system property  (-Dbrowserstack.<section>.<key>=...)
        String fromJvm = System.getProperty("browserstack." + section + "." + key);
        if (!isBlank(fromJvm)) return fromJvm;

        // 2. Environment variable
        if (!isBlank(envKey)) {
            String fromEnv = System.getenv(envKey);
            if (!isBlank(fromEnv)) return fromEnv;
        }

        // 3. browserstack.yml
        Object sectionObj = CONFIG.get(section);
        if (sectionObj instanceof Map) {
            Object value = ((Map<String, Object>) sectionObj).get(key);
            if (value != null && !isBlank(value.toString())) return value.toString();
        }

        // 4. Fallback
        return fallback;
    }

    /** Convenience overload — no env var, no fallback. */
    public static String get(String section, String key) {
        return get(section, key, null, null);
    }

    /**
     * Resolves a nested YAML value (section → subsection → key).
     * Priority: JVM (-Dbrowserstack.section.subsection.key) → env var → YAML → fallback.
     *
     * @param section    top-level key  (e.g. "android", "ios")
     * @param subsection second-level key (e.g. "local", "bstack")
     * @param key        leaf key        (e.g. "deviceName", "osVersion")
     * @param envKey     environment variable to check; null to skip
     * @param fallback   default if nothing resolves
     */
    @SuppressWarnings("unchecked")
    public static String get(String section, String subsection, String key, String envKey, String fallback) {
        // 1. JVM system property  (-Dbrowserstack.<section>.<subsection>.<key>=...)
        String fromJvm = System.getProperty("browserstack." + section + "." + subsection + "." + key);
        if (!isBlank(fromJvm)) return fromJvm;

        // 2. Environment variable
        if (!isBlank(envKey)) {
            String fromEnv = System.getenv(envKey);
            if (!isBlank(fromEnv)) return fromEnv;
        }

        // 3. YAML  (section → subsection → key)
        Object sectionObj = CONFIG.get(section);
        if (sectionObj instanceof Map) {
            Object subsectionObj = ((Map<String, Object>) sectionObj).get(subsection);
            if (subsectionObj instanceof Map) {
                Object value = ((Map<String, Object>) subsectionObj).get(key);
                if (value != null && !isBlank(value.toString())) return value.toString();
            }
        }

        // 4. Fallback
        return fallback;
    }

    private static boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}

