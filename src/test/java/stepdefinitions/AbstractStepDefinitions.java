package stepdefinitions;

import helper.DriverFactory;
import helper.PlatformHelper;
import interfaces.IPlatformInterface;
import org.testng.annotations.AfterMethod;

import java.util.HashMap;

public abstract class AbstractStepDefinitions {

    protected IPlatformInterface platform;
    private final HashMap<String, String> data;

    public AbstractStepDefinitions() {
        platform = PlatformHelper.getCurrentPlatform();
        data = new HashMap<>();
    }

    @AfterMethod
    public void teardown() {
        DriverFactory.quitDriver();
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

