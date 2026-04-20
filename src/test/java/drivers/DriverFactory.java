package drivers;

import config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Optional;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        BrowserType browser = resolveBrowser();
        boolean headless = Config.headless();
        Optional<String> grid = Config.seleniumGridUrl();

        if (grid.isPresent()) {
            return createRemoteDriver(grid.get(), browser, headless);
        }

        return switch (browser) {
            case CHROME -> new ChromeDriver(OptionsFactory.chrome(headless));
            case FIREFOX -> new FirefoxDriver(OptionsFactory.firefox(headless));
            case EDGE -> new EdgeDriver(OptionsFactory.edge(headless));
        };
    }

    private static BrowserType resolveBrowser() {
        String sys = System.getProperty("browser", "").trim();
        if (!sys.isEmpty()) {
            return BrowserType.from(sys);
        }
        return Config.browserType();
    }

    private static WebDriver createRemoteDriver(String hubUrl, BrowserType browser, boolean headless) {
        try {
            var url = URI.create(hubUrl).toURL();
            return switch (browser) {
                case CHROME -> new RemoteWebDriver(url, OptionsFactory.chrome(headless));
                case FIREFOX -> new RemoteWebDriver(url, OptionsFactory.firefox(headless));
                case EDGE -> new RemoteWebDriver(url, OptionsFactory.edge(headless));
            };
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid Selenium Grid URL: " + hubUrl, e);
        }
    }
}

