package framework.config;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents supported runtime environments and maps them to classpath configuration resources.
 *
 * <p><b>Why this exists</b>:
 * {@code ConfigReader} should focus on loading and exposing configuration, not on encoding
 * environment naming rules across the codebase. Centralizing mapping prevents subtle bugs where
 * {@code -Denv=uat} works locally but fails in CI due to inconsistent string handling.
 *
 * <p><b>Design choice</b>:
 * Keep environments as an explicit enum so unsupported values fail fast with a clear message.
 */
public enum EnvironmentConfig {

    /**
     * Default QA environment.
     *
     * <p><b>Why a dedicated enum value</b>: QA is the default local execution mode and maps to the
     * conventional {@code config/config.properties} file name.
     */
    QA("qa", "config/config.properties"),

    /**
     * UAT environment.
     *
     * <p><b>Why separate file</b>: keeps UAT-specific defaults isolated while preserving the same key
     * contract as QA.
     */
    UAT("uat", "config/config-uat.properties");

    private final String code;
    private final String classpathResource;

    /**
     * Associates an environment code with its classpath properties resource.
     *
     * @param code               stable environment code (lowercase)
     * @param classpathResource  classpath resource path (relative to classpath root)
     */
    EnvironmentConfig(String code, String classpathResource) {
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.classpathResource = Objects.requireNonNull(classpathResource, "classpathResource must not be null");
    }

    /**
     * Returns the stable environment code (lowercase).
     *
     * @return environment code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the classpath resource path for this environment's properties file.
     *
     * @return classpath resource path
     */
    public String getClasspathResource() {
        return classpathResource;
    }

    /**
     * Resolves an environment from a raw string (typically {@code System.getProperty("env")}).
     *
     * <p><b>Why normalize</b>: CI and local shells often pass values with inconsistent casing or
     * accidental whitespace. Normalizing avoids flaky "works on my machine" failures.
     *
     * @param raw raw environment string (may be null/blank)
     * @return resolved {@link EnvironmentConfig} (defaults to {@link #QA} when blank)
     */
    public static EnvironmentConfig fromSystemProperty(String raw) {
        if (raw == null || raw.isBlank()) {
            return QA;
        }

        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        for (EnvironmentConfig env : values()) {
            if (env.code.equals(normalized)) {
                return env;
            }
        }

        throw new IllegalArgumentException("Unsupported env value: '" + raw
                + "'. Supported values: qa, uat");
    }
}
