package drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        BrowserType browser = BrowserType.valueOf(System.getProperty("browser", "CHROME").toUpperCase());
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        return switch (browser) {
            case CHROME -> new ChromeDriver(OptionsFactory.chrome(headless));
            case FIREFOX -> new FirefoxDriver(OptionsFactory.firefox(headless));
            case EDGE -> new EdgeDriver(OptionsFactory.edge(headless));
        };
    }
}

