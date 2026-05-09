package framework.driver;

import framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Stores and manages the {@link WebDriver} instance per executing thread.
 *
 * <p><b>Why this exists</b>:
 * Test frameworks become flaky and unsafe when a single static WebDriver is shared across parallel tests.
 * Using {@link ThreadLocal} ensures each test thread has an isolated browser session, enabling
 * safe parallel execution later in Phase 3 without WebDriver collisions.
 *
 * <p><b>Pattern</b>: ThreadLocal — one driver instance per thread, managed centrally.
 */
public final class DriverManager {

    private static final Logger LOGGER = LogManager.getLogger(DriverManager.class);

    /**
     * Thread-confined WebDriver storage.
     *
     * <p><b>Why</b>: {@link ThreadLocal} binds data to the current thread, preventing cross-thread access
     * and the unpredictable failures that come with shared mutable state.
     */
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation.
     *
     * <p><b>Why</b>: DriverManager is a static utility; instantiation invites hidden state.
     */
    private DriverManager() {
        // Intentionally empty.
    }

    /**
     * Creates a {@link WebDriver} using {@link DriverFactory} and stores it for the current thread.
     *
     * <p><b>Why fail fast</b>:
     * If a driver is already set for the thread, re-initializing would leak browser instances and
     * create confusing behavior (e.g., steps operating on the wrong window).
     *
     * @param browser  browser name (e.g., chrome, firefox)
     * @param headless whether to run headless
     */
    public static void initDriver(String browser, boolean headless) {
        if (DRIVER.get() != null) {
            throw new FrameworkException("WebDriver is already initialized for thread="
                    + Thread.currentThread().getName() + ". This usually indicates Hooks ran twice or parallel "
                    + "lifecycle wiring is incorrect.");
        }

        WebDriver driver = DriverFactory.createDriver(browser, headless);
        DRIVER.set(driver);
        LOGGER.info("WebDriver initialized for thread={}", Thread.currentThread().getName());
    }

    /**
     * Returns the {@link WebDriver} for the current thread.
     *
     * <p><b>Why</b>: callers should never store WebDriver in instance fields inside steps/pages
     * once parallel execution is introduced. This method provides the correct thread-bound driver.
     *
     * @return current thread's {@link WebDriver}
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new FrameworkException("WebDriver is not initialized for thread="
                    + Thread.currentThread().getName() + ". Ensure Hooks initializes the driver before steps/pages run.");
        }
        return driver;
    }

    /**
     * Quits the current thread's WebDriver and removes it from {@link ThreadLocal}.
     *
     * <p><b>Why remove()</b>:
     * Threads can be reused by TestNG thread pools; removing ensures the next test on the same thread
     * starts clean and avoids memory leaks.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            LOGGER.warn("quitDriver() called but no WebDriver was set for thread={}", Thread.currentThread().getName());
            return;
        }

        try {
            try {
                driver.quit();
                LOGGER.info("WebDriver quit for thread={}", Thread.currentThread().getName());
            } catch (RuntimeException ex) {
                throw FrameworkException.withCause("Failed to quit WebDriver for thread="
                        + Thread.currentThread().getName(), ex);
            }
        } finally {
            DRIVER.remove();
        }
    }

    /**
     * Convenience method to check whether a driver exists for the current thread.
     *
     * <p><b>Why</b>: Hooks can use this guard to avoid double-quit or to decide whether screenshots are possible.
     *
     * @return true if initialized for current thread
     */
    public static boolean isDriverInitialized() {
        return DRIVER.get() != null;
    }
}

