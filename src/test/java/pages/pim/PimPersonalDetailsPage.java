package framework.pages.pim;

import framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * OrangeHRM PIM employee personal details (post add-employee redirect).
 */
public final class PimPersonalDetailsPage extends BasePage {

    private static final By LOADING_SPINNER = By.cssSelector(".oxd-loading-spinner");

    private static final By FIRST_NAME_INPUT = By.cssSelector("input[name='firstName']");
    private static final By MIDDLE_NAME_INPUT = By.cssSelector("input[name='middleName']");
    private static final By LAST_NAME_INPUT = By.cssSelector("input[name='lastName']");
    private static final By SAVE_BUTTON = By.cssSelector("button[type='submit']");
    private static final By TOAST = By.cssSelector("div.oxd-toast");

    /**
     * Waits for the async personal-details panel (spinner clears and name fields hydrate).
     */
    public PimPersonalDetailsPage waitForPersonalDetailsReady() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_SPINNER));
        waitForElementVisible(LAST_NAME_INPUT);
        wait.until(drv -> {
            String v = drv.findElement(LAST_NAME_INPUT).getDomProperty("value");
            return v != null && !v.isBlank();
        });
        return this;
    }

    public PimPersonalDetailsPage enterMiddleName(String middleName) {
        replaceText(MIDDLE_NAME_INPUT, middleName);
        return this;
    }

    public PimPersonalDetailsPage savePersonalDetails() {
        click(SAVE_BUTTON);
        waitUntilInvisible(TOAST);
        return waitForPersonalDetailsReady();
    }

    public String getMiddleNameValue() {
        String v = getDomPropertyValue(MIDDLE_NAME_INPUT, "value");
        return v == null ? "" : v;
    }

    public String getLastNameValue() {
        String v = getDomPropertyValue(LAST_NAME_INPUT, "value");
        return v == null ? "" : v;
    }

    public String getFirstNameValue() {
        String v = getDomPropertyValue(FIRST_NAME_INPUT, "value");
        return v == null ? "" : v;
    }
}
