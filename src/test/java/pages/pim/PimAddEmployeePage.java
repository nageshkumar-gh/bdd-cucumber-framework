package framework.pages.pim;

import framework.pages.BasePage;
import org.openqa.selenium.By;

/**
 * OrangeHRM PIM "Add Employee" form.
 */
public final class PimAddEmployeePage extends BasePage {

    private static final By FIRST_NAME_INPUT = By.cssSelector("input[name='firstName']");
    private static final By MIDDLE_NAME_INPUT = By.cssSelector("input[name='middleName']");
    private static final By LAST_NAME_INPUT = By.cssSelector("input[name='lastName']");
    private static final By SAVE_BUTTON = By.cssSelector("button[type='submit']");

    public PimAddEmployeePage waitForForm() {
        waitForElementVisible(FIRST_NAME_INPUT);
        return this;
    }

    public PimAddEmployeePage enterFirstName(String firstName) {
        type(FIRST_NAME_INPUT, firstName);
        return this;
    }

    public PimAddEmployeePage enterMiddleName(String middleName) {
        type(MIDDLE_NAME_INPUT, middleName);
        return this;
    }

    public PimAddEmployeePage enterLastName(String lastName) {
        type(LAST_NAME_INPUT, lastName);
        return this;
    }

    /**
     * Saves the new employee record and waits for navigation to personal details.
     */
    public PimPersonalDetailsPage saveNewEmployee() {
        click(SAVE_BUTTON);
        waitForUrlContains("viewPersonalDetails");
        return new PimPersonalDetailsPage().waitForPersonalDetailsReady();
    }
}
