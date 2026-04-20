package config;

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
}

