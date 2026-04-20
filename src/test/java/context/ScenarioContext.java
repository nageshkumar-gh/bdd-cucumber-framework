package context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Scenario-scoped shared state (PicoContainer creates one instance per scenario).
 */
public final class ScenarioContext {
    private final Map<String, Object> data = new HashMap<>();

    public void put(String key, Object value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = data.get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(type.cast(value));
    }

    public void clear() {
        data.clear();
    }
}
