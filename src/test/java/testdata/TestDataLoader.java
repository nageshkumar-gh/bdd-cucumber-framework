package testdata;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Loads structured test data from classpath resources (JSON).
 */
public final class TestDataLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestDataLoader() {
    }

    public static UserCredentials userCredentials(String resourcePath) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Missing test data resource: " + resourcePath);
            }
            return MAPPER.readValue(in, UserCredentials.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed reading test data: " + resourcePath, e);
        }
    }
}
