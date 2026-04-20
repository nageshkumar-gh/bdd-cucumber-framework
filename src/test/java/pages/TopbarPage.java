package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.WaitUtils;

public class TopbarPage extends BasePage {

    private final By breadcrumbTitle = By.cssSelector(".oxd-topbar-header-breadcrumb h6");
    /** OrangeHRM 5.x top-right user area */
    private final By userDropdownTab = By.className("oxd-userdropdown-tab");

    public TopbarPage(WaitUtils waits) {
        super(waits);
    }

    public String getMenuTitle() {
        return explicitWait().until(ExpectedConditions.visibilityOfElementLocated(breadcrumbTitle)).getText().trim();
    }

    public void selectProfile() {
        explicitWait().until(ExpectedConditions.elementToBeClickable(userDropdownTab)).click();
    }

    public void clickLogout(String linkText) {
        By logout = By.linkText(linkText);
        explicitWait().until(ExpectedConditions.elementToBeClickable(logout)).click();
    }
}
