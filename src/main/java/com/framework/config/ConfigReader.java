package com.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Reads environment-specific configuration from the classpath and exposes strongly-typed getters.
 *
 * <p><b>Why this exists</b>:
 * We want a single, consistent place to resolve runtime configuration (URL, browser, timeouts)
 * without duplicating file I/O or parsing logic across Hooks, DriverFactory, and utilities.
 *
 * <p><b>Design choice</b>:
 * Implemented as a lazy-loading, thread-safe Singleton using volatile + double-checked locking.
 * This avoids repeated disk/classpath reads while keeping safe publication in multi-threaded test runs.
 */
public final class ConfigReader {

    /**
     * Framework logger.
     *
     * <p><b>Why</b>: we standardize on Log4j2 so diagnostics work consistently in IDE and CI,
     * and we avoid {@code System.out.println} which becomes noisy and non-structured at scale.
     */
    private static final Logger LOGGER = LogManager.getLogger(ConfigReader.class);

    /**
     * System property key used to select the environment configuration file.
     *
     * <p><b>Why</b>: a single standard key makes CI and local runs consistent:
     * {@code mvn test -Denv=uat}.
     */
    public static final String ENV_PROPERTY_KEY = "env";

    /**
     * Default environment name used when {@link #ENV_PROPERTY_KEY} is not provided.
     *
     * <p><b>Why</b>: we want a predictable default to reduce friction for local execution.
     */
    public static final String DEFAULT_ENV = "qa";

    /**
     * Singleton instance reference.
     *
     * <p><b>Why volatile</b>: guarantees visibility of the constructed instance across threads,
     * making double-checked locking correct under the Java Memory Model.
     */
    private static volatile ConfigReader instance;

    /**
     * Resolved, immutable configuration properties for this JVM process.
     *
     * <p><b>Why</b>: after loading, we do not want accidental mutation of configuration mid-run
     * which could create non-deterministic failures.
     */
    private final Properties properties;

    /**
     * Effective environment name used to choose the properties file.
     *
     * <p><b>Why</b>: keeping it on the instance makes debugging configuration issues straightforward
     * (logs can print which environment was actually loaded).
     */
    private final String environment;

    /**
     * Creates a new reader and loads the classpath properties file for the resolved environment.
     *
     * <p><b>Why private</b>: enforces the Singleton contract. All callers must use {@link #getInstance()}.
     */
    private ConfigReader() {
        this.environment = resolveEnvironment();
        this.properties = loadPropertiesForEnvironment(this.environment);
    }

    /**
     * Returns the single {@link ConfigReader} instance for this JVM.
     *
     * <p><b>Why double-checked locking</b>:
     * - Fast path avoids synchronization after initialization (common case in tests).
     * - Still safe for parallel TestNG execution because instance creation is synchronized once.
     *
     * @return singleton {@link ConfigReader} instance
     */
    public static ConfigReader getInstance() {
        ConfigReader localRef = instance;
        if (localRef == null) {
            synchronized (ConfigReader.class) {
                localRef = instance;
                if (localRef == null) {
                    localRef = new ConfigReader();
                    instance = localRef;
                }
            }
        }
        return localRef;
    }

    /**
     * Returns the base application URL.
     *
     * <p><b>Why typed access</b>: consumers should not hardcode property keys everywhere.
     *
     * @return base URL as configured for the active environment
     */
    public String getBaseUrl() {
        return requireNonBlank("base.url");
    }

    /**
     * Returns the browser name to use (e.g., chrome, firefox).
     *
     * @return browser name
     */
    public String getBrowser() {
        return requireNonBlank("browser");
    }

    /**
     * Returns whether tests should run in headless mode.
     *
     * @return true if headless mode is enabled
     */
    public boolean isHeadless() {
        return Boolean.parseBoolean(requireNonBlank("headless"));
    }

    /**
     * Returns the explicit wait timeout in seconds.
     *
     * <p><b>Why int</b>: wait utilities need a numeric value to build {@code Duration}.
     *
     * @return wait timeout in seconds
     */
    public int getWaitTimeoutSeconds() {
        String raw = requireNonBlank("wait.timeout");
        try {
            int seconds = Integer.parseInt(raw);
            if (seconds <= 0) {
                throw new IllegalStateException("wait.timeout must be > 0 but was: " + seconds);
            }
            return seconds;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("wait.timeout must be an integer but was: " + raw, ex);
        }
    }

    /**
     * Returns the environment name that was loaded (e.g., qa, uat).
     *
     * @return active environment
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Small verification entry point used during framework bootstrap (Phase 1, Step 4).
     *
     * <p><b>Why this exists</b>:
     * It gives you a fast feedback loop that the correct classpath files resolve without
     * requiring WebDriver, TestNG, or Cucumber to be wired yet.
     *
     * <p><b>How to verify both QA and UAT</b>:
     * Run this main twice, once with no {@code -Denv} (QA default), and once with {@code -Denv=uat}.
     *
     * @param args optional; not used
     */
    public static void main(String[] args) {
        ConfigReader reader = ConfigReader.getInstance();
        LOGGER.info("env={}", reader.getEnvironment());
        LOGGER.info("base.url={}", reader.getBaseUrl());
    }

    /**
     * Resolves the active environment from the {@link #ENV_PROPERTY_KEY} system property.
     *
     * <p><b>Why normalize</b>: avoids subtle bugs caused by whitespace and inconsistent casing in CI.
     *
     * @return normalized environment name (defaults to {@link #DEFAULT_ENV})
     */
    private static String resolveEnvironment() {
        String raw = System.getProperty(ENV_PROPERTY_KEY);
        if (raw == null || raw.isBlank()) {
            return DEFAULT_ENV;
        }
        return raw.trim().toLowerCase();
    }

    /**
     * Loads the properties file from the classpath for a given environment.
     *
     * <p><b>Why classpath</b>: keeps configuration versioned with the test code and works the same
     * locally and in GitHub Actions.
     *
     * @param env environment name (qa, uat, etc.)
     * @return loaded {@link Properties}
     */
    private static Properties loadPropertiesForEnvironment(String env) {
        Objects.requireNonNull(env, "env must not be null");

        String resourcePath = buildConfigResourcePath(env);
        Properties props = new Properties();

        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Config file not found on classpath: " + resourcePath
                        + " (env=" + env + ")");
            }
            props.load(is);
            return props;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load config file: " + resourcePath
                    + " (env=" + env + ")", ex);
        }
    }

    /**
     * Builds the classpath location of the environment configuration file.
     *
     * <p><b>Why this mapping</b>:
     * - QA uses {@code config/config.properties} as the convention-default.
     * - Non-QA environments use {@code config/config-{env}.properties} for clarity.
     *
     * @param env environment name
     * @return resource path relative to classpath root
     */
    private static String buildConfigResourcePath(String env) {
        if (DEFAULT_ENV.equals(env)) {
            return "config/config.properties";
        }
        return "config/config-" + env + ".properties";
    }

    /**
     * Reads a property and validates it is present and not blank.
     *
     * <p><b>Why fail fast</b>: configuration errors should be obvious at framework startup
     * rather than manifesting as Selenium timeouts or null pointers later.
     *
     * @param key property key
     * @return trimmed value
     */
    private String requireNonBlank(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing/blank required config key: " + key
                    + " (env=" + environment + ")");
        }
        return value.trim();
    }
}

