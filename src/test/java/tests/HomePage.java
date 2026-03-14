package tests;

import org.testng.annotations.Test;
import utils.AbstractBaseTest;
import utils.ConfigReader;

public class HomePage extends AbstractBaseTest {

    @Test
    public void searchKeywordTest() {
        shoppingCart.searchForKeyword(ConfigReader.get("home.searchbox"));
    }
}
