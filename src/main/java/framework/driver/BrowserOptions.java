package framework.driver;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Builds browser-specific {@link org.openqa.selenium.MutableCapabilities} objects in one place.
 *
 * <p><b>Why this exists</b>:
 * Browser option flags can grow quickly (headless, window sizing, CI stability flags).
 * Centralizing them prevents drift between local runs and CI, and keeps {@code DriverFactory}
 * focused on creating the driver rather than knowing every option detail.
 *
 * <p><b>Pattern</b>: Builder (fluent) — callers can enable optional behaviors without
 * constructors that become unreadable as the framework evolves.
 */
public final class BrowserOptions {

    private boolean headless;
    private PageLoadStrategy pageLoadStrategy = PageLoadStrategy.NORMAL;

    /**
     * Creates a new {@link BrowserOptions} builder.
     *
     * <p><b>Why</b>: static factory keeps the fluent call site readable:
     * {@code BrowserOptions.builder().headless(true).buildChrome()}.
     *
     * @return a new builder instance
     */
    public static BrowserOptions builder() {
        return new BrowserOptions();
    }

    /**
     * Private constructor to encourage fluent usage via {@link #builder()}.
     *
     * <p><b>Why</b>: prevents callers from using {@code new} directly and bypassing defaults.
     */
    private BrowserOptions() {
        // Defaults are intentionally conservative for stability.
    }

    /**
     * Enables or disables headless mode.
     *
     * <p><b>Why</b>: CI typically runs headless for speed and reliability; local runs often do not.
     *
     * @param headless true to enable headless mode
     * @return this builder (fluent)
     */
    public BrowserOptions headless(boolean headless) {
        this.headless = headless;
        return this;
    }

    /**
     * Sets the page load strategy used by the browser.
     *
     * <p><b>Why</b>: PageLoadStrategy impacts speed vs determinism; keeping it configurable
     * lets us tune later without editing driver creation code.
     *
     * @param pageLoadStrategy desired strategy (must not be null)
     * @return this builder (fluent)
     */
    public BrowserOptions pageLoadStrategy(PageLoadStrategy pageLoadStrategy) {
        if (pageLoadStrategy == null) {
            throw new IllegalArgumentException("pageLoadStrategy must not be null");
        }
        this.pageLoadStrategy = pageLoadStrategy;
        return this;
    }

    /**
     * Builds {@link ChromeOptions} using the current builder settings.
     *
     * <p><b>Why these defaults</b>:
     * - Start maximized: reduces viewport-driven flakiness in UI assertions.
     * - Disable infobars/extensions: removes noise and instability in automation contexts.
     * - Headless options include a consistent window size to avoid responsive layout changes.
     *
     * @return configured {@link ChromeOptions}
     */
    public ChromeOptions buildChrome() {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(pageLoadStrategy);

        options.addArguments("--start-maximized");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");

        if (headless) {
            // Why: headless defaults can vary; fixed size reduces layout variability.
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            // Why: common CI containers need these flags for Chrome stability.
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        return options;
    }

    /**
     * Builds {@link FirefoxOptions} using the current builder settings.
     *
     * <p><b>Why these defaults</b>:
     * Firefox has fewer required stability flags than Chrome in CI, but we still keep
     * headless and page load strategy consistent across browsers.
     *
     * @return configured {@link FirefoxOptions}
     */
    public FirefoxOptions buildFirefox() {
        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(pageLoadStrategy);

        if (headless) {
            options.addArguments("-headless");
        }

        return options;
    }
}

