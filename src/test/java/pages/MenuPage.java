package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class MenuPage extends BasePage {
    public MenuPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//span[@class='oxd-text oxd-text--span oxd-main-menu-item--name']")
    List<WebElement> mainMenuItems;

    public void clickMenuItem(String menuItem) {
        for (WebElement item : mainMenuItems) {
            if (item.getText().equalsIgnoreCase(menuItem)) {
                item.click();
                break;
            }
        }
    }
}

