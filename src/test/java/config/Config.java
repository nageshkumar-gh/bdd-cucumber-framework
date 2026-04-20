package config;

import drivers.BrowserType;

import java.util.Optional;

public final class Config {
    private Config() {
    }

    public static String env() {
        return ConfigLoader.get("env", "local");
    }

    public static String loginUrl() {
        return ConfigLoader.getRequired("app.loginUrl");
    }

    public static String username() {
        return ConfigLoader.getFromEnvOrProperty("APP_USERNAME", "app.username", "Admin");
    }

    public static String password() {
        return ConfigLoader.getFromEnvOrProperty("APP_PASSWORD", "app.password", "admin123");
    }

    public static long implicitWaitSeconds() {
        return ConfigLoader.getLong("timeouts.implicitWaitSeconds", 10);
    }

    public static long explicitWaitSeconds() {
        return ConfigLoader.getLong("timeouts.explicitWaitSeconds", 15);
    }

    public static long pageLoadTimeoutSeconds() {
        return ConfigLoader.getLong("timeouts.pageLoadTimeoutSeconds", 30);
    }

    public static BrowserType browserType() {
        return BrowserType.from(ConfigLoader.get("browser", "CHROME"));
    }

    /**
     * Headless when explicitly enabled via {@code -Dheadless=true}, {@code browser.headless=true},
     * or when the common CI environment variable {@code CI=true} is present.
     */
    public static boolean headless() {
        String headlessProp = System.getProperty("headless", "").trim();
        if (!headlessProp.isEmpty()) {
            return Boolean.parseBoolean(headlessProp);
        }
        if (Boolean.parseBoolean(ConfigLoader.get("browser.headless", "false"))) {
            return true;
        }
        String ci = System.getenv("CI");
        return ci != null && ci.equalsIgnoreCase("true");
    }

    public static Optional<String> seleniumGridUrl() {
        String url = ConfigLoader.getFromEnvOrProperty("SELENIUM_GRID_URL", "selenium.grid.url", "").trim();
        if (url.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(url);
    }
}

