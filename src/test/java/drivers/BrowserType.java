package drivers;

public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String raw) {
        if (raw == null || raw.isBlank()) {
            return CHROME;
        }
        return BrowserType.valueOf(raw.trim().toUpperCase());
    }
}

