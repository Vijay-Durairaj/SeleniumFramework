package tests;

import helper.ConfigReader;
import org.testng.annotations.Test;
import stepdefinitions.AbstractStepDefinitions;

public class HomePageTest extends AbstractStepDefinitions {

    @Test
    public void searchKeywordTest() {
        platform.launchApplication();
        platform.searchForKeyword(ConfigReader.get("home.searchbox"));
    }
}

