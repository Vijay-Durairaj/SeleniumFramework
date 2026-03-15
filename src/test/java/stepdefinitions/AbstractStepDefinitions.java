package stepdefinitions;

import helper.PlatformHelper;
import interfaces.IPlatformInterface;

import java.util.HashMap;

public abstract class AbstractStepDefinitions {

    protected IPlatformInterface platform;
    private final HashMap<String, String> data;

    public AbstractStepDefinitions() {
        platform = PlatformHelper.getCurrentPlatform();
        data = new HashMap<>();
    }

    protected String getOrSaveData(String key, String defaultValue) {
        if (!data.containsKey(key)) {
            data.put(key, defaultValue);
            return defaultValue;
        } else {
            return data.get(key);
        }
    }
}

