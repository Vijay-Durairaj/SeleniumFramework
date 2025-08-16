package tests;

import org.testng.annotations.Test;
import utils.BaseTest;
import utils.ConfigReader;

public class HomePage extends BaseTest {

    @Test
    public void searchKeywordTest() {
        homePageController.searchForKeyword(ConfigReader.get("home.searchbox"));
    }
}
