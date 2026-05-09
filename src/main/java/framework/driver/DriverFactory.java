package framework.driver;

import framework.exceptions.FrameworkException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Creates configured {@link WebDriver} instances without leaking driver construction details to callers.
 *
 * <p><b>Why this exists</b>:
 * WebDriver creation has many moving parts (browser choice, headless mode, options, driver binaries).
 * Using a Factory keeps the rest of the framework (Hooks, DriverManager) independent from those details,
 * which is essential when we later add remote execution (Grid) or additional browsers.
 *
 * <p><b>Pattern</b>: Factory — callers request a driver, but do not know (or care) how it is constructed.
 */
public final class DriverFactory {

    private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);

    /**
     * Supported browser identifiers.
     *
     * <p><b>Why</b>: we keep them as constants to avoid string drift between config and code.
     */
    public static final String BROWSER_CHROME = "chrome";
    public static final String BROWSER_FIREFOX = "firefox";

    /**
     * Private constructor to prevent instantiation.
     *
     * <p><b>Why</b>: factory is stateless; instantiation invites accidental state and concurrency issues.
     */
    private DriverFactory() {
        // Intentionally empty.
    }

    /**
     * Creates a new {@link WebDriver} instance for the requested browser and execution mode.
     *
     * <p><b>Why parameters (instead of reading config here)</b>:
     * Keeping this method pure (inputs → driver) makes it testable and keeps config concerns
     * in {@code ConfigReader}. Hooks/DriverManager will pass the resolved values.
     *
     * <p><b>Why WebDriverManager here</b>:
     * Although Selenium Manager exists, WebDriverManager gives us explicit control and predictable
     * behavior across developer machines and CI runners, which reduces environment-specific failures.
     *
     * @param browser  browser name (e.g., {@code chrome}, {@code firefox}); not null/blank
     * @param headless whether to run the browser in headless mode
     * @return a configured {@link WebDriver} instance
     */
    public static WebDriver createDriver(String browser, boolean headless) {
        if (browser == null || browser.isBlank()) {
            throw new FrameworkException("Invalid browser configuration: browser must not be null/blank");
        }

        String normalized = browser.trim().toLowerCase();
        LOGGER.info("Creating WebDriver. browser={}, headless={}", normalized, headless);

        return switch (normalized) {
            case BROWSER_CHROME -> createChromeDriver(headless);
            case BROWSER_FIREFOX -> createFirefoxDriver(headless);
            default -> throw new FrameworkException("Unsupported browser: '" + browser + "'"
                    + " (supported: " + BROWSER_CHROME + ", " + BROWSER_FIREFOX + ")");
        };
    }

    /**
     * Creates a {@link ChromeDriver} configured with {@link BrowserOptions}.
     *
     * <p><b>Why split into a method</b>: isolates per-browser decisions and keeps
     * {@link #createDriver(String, boolean)} readable as we add more browsers later.
     *
     * @param headless whether to enable headless mode
     * @return configured {@link ChromeDriver}
     */
    private static WebDriver createChromeDriver(boolean headless) {
        // Why: explicit setup prevents "it works on my machine" issues when chromedriver is missing/mismatched.
        try {
            WebDriverManager.chromedriver().setup();
            return new ChromeDriver(BrowserOptions.builder().headless(headless).buildChrome());
        } catch (RuntimeException ex) {
            throw FrameworkException.withCause("Failed to create Chrome WebDriver (headless=" + headless + ")", ex);
        }
    }

    /**
     * Creates a {@link FirefoxDriver} configured with {@link BrowserOptions}.
     *
     * @param headless whether to enable headless mode
     * @return configured {@link FirefoxDriver}
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        try {
            WebDriverManager.firefoxdriver().setup();
            return new FirefoxDriver(BrowserOptions.builder().headless(headless).buildFirefox());
        } catch (RuntimeException ex) {
            throw FrameworkException.withCause("Failed to create Firefox WebDriver (headless=" + headless + ")", ex);
        }
    }
}

