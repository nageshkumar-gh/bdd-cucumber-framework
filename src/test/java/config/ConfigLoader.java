package config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public final class ConfigLoader {
    private ConfigLoader() {
    }

    private static final Properties PROPS = loadProps();

    private static Properties loadProps() {
        Properties props = new Properties();

        // Load defaults first, then env-specific override.
        loadFromClasspath(props, "config/config.properties");

        String env = System.getProperty("env", "local").trim();
        if (!env.isEmpty()) {
            loadFromClasspath(props, "config/config-" + env + ".properties");
        }

        // System properties override everything.
        System.getProperties().forEach((k, v) -> {
            if (k != null && v != null) {
                props.setProperty(String.valueOf(k), String.valueOf(v));
            }
        });

        return props;
    }

    private static void loadFromClasspath(Properties props, String resourcePath) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                return;
            }
            // Properties supports ISO-8859-1 natively; enforce UTF-8 by reading bytes and re-decoding.
            byte[] bytes = is.readAllBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);
            try (InputStream decoded = new java.io.ByteArrayInputStream(content.getBytes(StandardCharsets.ISO_8859_1))) {
                props.load(decoded);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed loading config resource: " + resourcePath, e);
        }
    }

    public static String get(String key, String defaultValue) {
        String value = PROPS.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }

    public static String getRequired(String key) {
        String value = PROPS.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value.trim();
    }

    public static long getLong(String key, long defaultValue) {
        String value = PROPS.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Config key is not a number: " + key + "=" + value, e);
        }
    }

    public static String getFromEnvOrProperty(String envVar, String propertyKey, String defaultValue) {
        String envValue = System.getenv(envVar);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }
        return get(propertyKey, defaultValue);
    }
}

