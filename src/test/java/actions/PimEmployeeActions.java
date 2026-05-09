package framework.actions;

import framework.pages.pim.PimAddEmployeePage;
import framework.pages.pim.PimEmployeeListPage;
import framework.pages.pim.PimPersonalDetailsPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Action-layer orchestration for PIM employee add and personal-details update flows.
 */
public class PimEmployeeActions {

    private static final Logger LOGGER = LogManager.getLogger(PimEmployeeActions.class);

    private final PimEmployeeListPage employeeListPage = new PimEmployeeListPage();
    private PimAddEmployeePage addEmployeePage;
    private PimPersonalDetailsPage personalDetailsPage;

    public void openPimEmployeeList() {
        LOGGER.info("Opening PIM employee list");
        employeeListPage.open();
    }

    public void startAddEmployee() {
        LOGGER.info("Starting Add Employee flow");
        addEmployeePage = employeeListPage.clickAddEmployee();
    }

    /**
     * Fills mandatory employee name fields. The last name is suffixed with a timestamp so repeated
     * demo runs do not collide with existing employees.
     */
    public String fillNewEmployeeNames(String firstName, String middleName, String lastNameBase) {
        String uniqueLast = lastNameBase + System.currentTimeMillis();
        LOGGER.info("Filling employee first='{}' middle='{}' last(base)='{}' uniqueLast='{}'",
                firstName, middleName, lastNameBase, uniqueLast);
        addEmployeePage
                .enterFirstName(firstName)
                .enterMiddleName(middleName)
                .enterLastName(uniqueLast);
        return uniqueLast;
    }

    public void saveNewEmployeeFromAddForm() {
        LOGGER.info("Saving new employee");
        personalDetailsPage = addEmployeePage.saveNewEmployee();
    }

    public String getPersonalDetailsLastName() {
        return personalDetailsPage.getLastNameValue();
    }

    public void updateMiddleName(String middleName) {
        LOGGER.info("Updating personal details middle='{}'", middleName);
        personalDetailsPage.enterMiddleName(middleName).savePersonalDetails();
    }

    public String getPersonalDetailsMiddleName() {
        return personalDetailsPage.getMiddleNameValue();
    }

    public String getPersonalDetailsFirstName() {
        return personalDetailsPage.getFirstNameValue();
    }
}
