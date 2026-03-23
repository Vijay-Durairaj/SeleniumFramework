package utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

public class GestureUtils {

    private AppiumDriver driver;

    public GestureUtils(AppiumDriver driver) {
        this.driver = driver;
    }

    private void swipe(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipe));
    }

    public void scrollDown() {
        Dimension size = driver.manage().window().getSize();

        int x = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);

        swipe(x, startY, x, endY);
    }

    public void scrollUp() {
        Dimension size = driver.manage().window().getSize();

        int x = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * 0.8);

        swipe(x, startY, x, endY);
    }

    // ⬅️ Scroll Left
    public void scrollLeft() {
        Dimension size = driver.manage().window().getSize();

        int y = size.height / 2;
        int startX = (int) (size.width * 0.8);
        int endX = (int) (size.width * 0.2);

        swipe(startX, y, endX, y);
    }

    // ➡️ Scroll Right
    public void scrollRight() {
        Dimension size = driver.manage().window().getSize();

        int y = size.height / 2;
        int startX = (int) (size.width * 0.2);
        int endX = (int) (size.width * 0.8);

        swipe(startX, y, endX, y);
    }

    // 🎯 Scroll until element visible (generic)
    public WebElement scrollToElement(By locator, int maxScrolls) {
        int count = 0;

        while (count < maxScrolls) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception e) {
                // ignore
            }

            scrollDown();
            count++;
        }

        throw new NoSuchElementException("Element not found after " + maxScrolls + " scrolls");
    }

    // 📦 Scroll inside specific element (like RecyclerView / Table)
    public void scrollInsideElement(WebElement element) {
        Rectangle rect = element.getRect();

        int startX = rect.x + rect.width / 2;
        int startY = rect.y + (int)(rect.height * 0.8);
        int endY = rect.y + (int)(rect.height * 0.2);

        swipe(startX, startY, startX, endY);
    }

    public void dragAndDrop(WebElement source, WebElement target) {

        Point src = source.getLocation();
        Point dest = target.getLocation();

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence drag = new Sequence(finger, 1);

        drag.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), src.x, src.y));
        drag.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        drag.addAction(finger.createPointerMove(Duration.ofSeconds(1),
                PointerInput.Origin.viewport(), dest.x, dest.y));

        drag.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(drag));
    }

    public void scrollTillEnd() {
        String previousPage = "";

        while (true) {
            String currentPage = driver.getPageSource();

            if (currentPage.equals(previousPage)) {
                break; // no more content
            }

            scrollDown();
            previousPage = currentPage;
        }
    }

    public void zoom(int centerX, int centerY) {
        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

        Sequence seq1 = new Sequence(finger1, 1);
        Sequence seq2 = new Sequence(finger2, 1);

        // Finger 1
        seq1.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
        seq1.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        seq1.addAction(finger1.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), centerX - 100, centerY - 100));
        seq1.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // Finger 2
        seq2.addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
        seq2.addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        seq2.addAction(finger2.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), centerX + 100, centerY + 100));
        seq2.addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(seq1, seq2));
    }
}
