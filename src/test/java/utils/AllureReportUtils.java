package utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Small helpers for richer Allure results (beyond Cucumber's built-in integration).
 */
public final class AllureReportUtils {

    private AllureReportUtils() {
    }

    public static void attachText(String name, String text) {
        if (text == null) {
            return;
        }
        Allure.addAttachment(name, "text/plain", text, ".txt");
    }

    public static void attachScreenshotIfPossible(String name, WebDriver driver) {
        if (!(driver instanceof TakesScreenshot takesScreenshot)) {
            return;
        }
        byte[] bytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
        Allure.getLifecycle().addAttachment(name, "image/png", ".png", bytes);
    }
}
