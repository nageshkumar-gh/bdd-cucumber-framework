package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TopbarPage extends BasePage {

    public TopbarPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//h6[@class='oxd-text oxd-text--h6 oxd-topbar-header-breadcrumb-module']")
    WebElement menu_title;

    @FindBy(xpath = "//p[@class='oxd-userdropdown-name']")
    WebElement profile;

    @FindBy(xpath = "//a[@role='menuitem']")
    List<WebElement> profileOptionList;

    public String getMenuTitle() {
        return menu_title.getText();
    }

    public void selectProfile() {
        profile.click();
    }

    public void clickLogout(String option) {
        for (WebElement item : profileOptionList) {
            if (item.getText().equalsIgnoreCase(option)) {
                item.click();
                break;
            }
        }
    }
}

