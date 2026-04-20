package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.WaitUtils;

import java.util.List;

public class LoginPage extends BasePage {

    private final By usernameInput = By.xpath("//input[@name='username']");
    private final By passwordInput = By.xpath("//input[@type='password']");
    private final By loginButton = By.xpath("//button[@type='submit']");
    private final By alertMessage = By.xpath("//p[contains(@class,'oxd-alert-content-text')]");
    private final By requiredFieldErrors = By.xpath("//span[text()='Required']");
    private final By orangeHrmHeader = By.xpath("//img[@alt='client brand banner']");

    public LoginPage(WaitUtils waits) {
        super(waits);
    }

    public void open(String url) {
        driver().navigate().to(url);
        explicitWait().until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
    }

    public void enterUsername(String username) {
        WebElement usernameBox = explicitWait().until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        usernameBox.clear();
        usernameBox.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement passwordBox = explicitWait().until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        passwordBox.clear();
        passwordBox.sendKeys(password);
    }

    public void clickLogin() {
        explicitWait().until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    public void clearUsername() {
        explicitWait().until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).clear();
    }

    public void clearPassword() {
        explicitWait().until(ExpectedConditions.visibilityOfElementLocated(passwordInput)).clear();
    }

    public String getAlertMessageText() {
        return explicitWait().until(ExpectedConditions.visibilityOfElementLocated(alertMessage)).getText().trim();
    }

    public boolean isOrangeHrmHeaderVisible() {
        return explicitWait().until(ExpectedConditions.visibilityOfElementLocated(orangeHrmHeader)).isDisplayed();
    }

    public List<WebElement> requiredFieldErrors() {
        return explicitWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(requiredFieldErrors));
    }
}
