package framework.pages.pim;

import framework.pages.BasePage;
import org.openqa.selenium.By;

/**
 * OrangeHRM PIM employee list (entry to add employees).
 */
public final class PimEmployeeListPage extends BasePage {

    private static final String EMPLOYEE_LIST_PATH = "/web/index.php/pim/viewEmployeeList";

    // Why: list header exposes "Add" as a secondary OXD button (plus icon variant on some builds).
    private static final By ADD_EMPLOYEE_BUTTON =
            By.cssSelector("div.orangehrm-header-container button.oxd-button--secondary");

    public PimEmployeeListPage open() {
        navigateToAppPath(EMPLOYEE_LIST_PATH);
        return this;
    }

    public PimAddEmployeePage clickAddEmployee() {
        click(ADD_EMPLOYEE_BUTTON);
        return new PimAddEmployeePage().waitForForm();
    }
}
