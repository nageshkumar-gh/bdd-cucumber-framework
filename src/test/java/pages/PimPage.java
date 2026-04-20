package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class PimPage extends BasePage {
    public PimPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//i[@class='oxd-icon bi-plus oxd-button-icon']")
    WebElement btnAdd;

    @FindBy(xpath = "//input[@name='firstName']")
    WebElement txtFirstName;

    @FindBy(xpath = "//input[@name='middleName']")
    WebElement txtMidName;

    @FindBy(xpath = "//input[@name='lastName']")
    WebElement txtLastName;

    @FindBy(xpath = "//button[@type='submit']")
    WebElement submitSave;

    @FindBy(id = "app")
    WebElement app;

    @FindBy(xpath = "//label[text()='License Expiry Date']/parent::div/following-sibling::div")
    WebElement calLicense;

    @FindBy(xpath = "//label[text()='Date of Birth']/parent::div/following-sibling::div")
    WebElement calDOB;

    @FindBy(xpath = "//div[@class='oxd-table-card']/div/div")
    List<WebElement> empList;

    // xpaths for Job Section
    @FindBy(xpath = "//div[@role='tablist']/div")
    List<WebElement> pimTabList;

    @FindBy(xpath = "//label[text()='Joined Date']/parent::div/following-sibling::div")
    WebElement calJoinedDate;

    @FindBy(xpath = "//label[text()='Job Title']/parent::div/following-sibling::div")
    WebElement drpdownJobTitle;

    @FindBy(xpath = "//label[text()='Job Specification']/parent::div/following-sibling::div")
    WebElement inpJobSpec;

    @FindBy(xpath = "//label[text()='Job Category']/parent::div/following-sibling::div")
    WebElement drpdownJobCat;

    @FindBy(xpath = "//label[text()='Sub Unit']/parent::div/following-sibling::div")
    WebElement drpSubUnit;

    @FindBy(xpath = "//label[text()='Location']/parent::div/following-sibling::div")
    WebElement drpLocation;

    @FindBy(xpath = "//label[text()='Employment Status']/parent::div/following-sibling::div")
    WebElement drpEmpStatus;

    @FindBy(xpath = "//div[@role='option']")
    List<WebElement> drpdownOptions;

    // Personal Details
    @FindBy(xpath = "//div[@class='oxd-date-input-calendar']")
    WebElement cal;

    @FindBy(xpath = "//li[@class='oxd-calendar-selector-month']")
    WebElement calMonths;

    @FindBy(xpath = "//ul[@class='oxd-calendar-dropdown']/li")
    List<WebElement> months;

    @FindBy(xpath = "//li[@class='oxd-calendar-selector-year']")
    WebElement calYears;

    @FindBy(xpath = "//ul[@class='oxd-calendar-dropdown']/li")
    List<WebElement> years;

    @FindBy(xpath = "//div[@class='oxd-calendar-dates-grid']/div")
    List<WebElement> days;

    public void clickAddEmployee() {
        btnAdd.click();
    }

    public void setFirstName(String firstName) {
        txtFirstName.sendKeys(firstName);
    }

    public void setMiddleName(String middleName) {
        txtMidName.sendKeys(middleName);
    }

    public void setLastName(String lastName) {
        txtLastName.sendKeys(lastName);
    }
}

