package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//input[@name='username']")
    WebElement txt_username;

    @FindBy(xpath = "//input[@type='password']")
    WebElement txt_password;

    @FindBy(xpath = "//button[@type='submit']")
    WebElement btn_login;

    public void goto_url() {
        driver.navigate().to("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
    }

    public void setUsername(String username) {
        txt_username.sendKeys(username);
    }

    public void setPassword(String password) {
        txt_password.sendKeys(password);
    }

    public void clickLogin() {
        btn_login.click();
    }
}

