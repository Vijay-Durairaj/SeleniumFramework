package utils;

import helper.PlatformHelper;
import interfaces.ShoppingCart;
import org.testng.annotations.BeforeMethod;

/**
 * AbstractBaseTest acts as the single entry point for all test classes.
 * All setup, teardown, driver, and controller logic is inherited from BaseTest.
 */
public abstract class AbstractBaseTest extends BaseTest {

    protected ShoppingCart shoppingCart;

    @BeforeMethod(alwaysRun = true)
    public void initializePlatform() {
        shoppingCart = PlatformHelper.getCurrentPlatform();
    }
}
