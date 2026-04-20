package base;

import drivers.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.WaitUtils;

/**
 * Thin base for page objects: resolves the {@link WebDriver} from {@link drivers.DriverManager}
 * (ThreadLocal) and shares explicit waits via {@link WaitUtils}.
 */
public abstract class BasePage {
    protected final WaitUtils waits;

    protected BasePage(WaitUtils waits) {
        this.waits = waits;
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }

    protected WebDriverWait explicitWait() {
        return waits.defaultWait();
    }
}
