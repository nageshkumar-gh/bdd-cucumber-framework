package reporting;

import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thin wrapper around Allure APIs used by hooks and (optionally) actions.
 *
 * <p><b>Why this exists</b>:
 * Allure attachment APIs are easy to misuse (wrong MIME type, missing names, swallowing IO failures).
 * Centralizing calls here keeps {@code Hooks} small and makes reporting behavior consistent across modules.
 *
 * <p><b>Why this class is under {@code src/test/java}</b>:
 * Allure dependencies in {@code pom.xml} are scoped to {@code test}; placing this helper alongside test code
 * avoids widening dependency scopes into {@code main} while still keeping a single reusable reporting utility.
 */
public final class AllureManager {

    private static final Logger LOGGER = LogManager.getLogger(AllureManager.class);

    /**
     * Private constructor to prevent instantiation.
     *
     * <p><b>Why</b>: this is a static utility; instantiation adds no value and encourages misuse.
     */
    private AllureManager() {
        // Intentionally empty.
    }

    /**
     * Attaches a PNG screenshot to the current Allure test container.
     *
     * <p><b>Why bytes + stream</b>: Allure expects stream-based attachments for binary content; passing a
     * {@link java.io.File} path is fine too, but streams are easier to standardize and test.
     *
     * @param name  attachment name shown in the report
     * @param pngBytes PNG bytes (must not be null)
     */
    public static void attachPng(String name, byte[] pngBytes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("attachment name must not be null/blank");
        }
        if (pngBytes == null) {
            throw new IllegalArgumentException("pngBytes must not be null");
        }

        try {
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(pngBytes), ".png");
        } catch (RuntimeException ex) {
            // Why: never fail the test run because reporting failed; log and continue.
            LOGGER.warn("Failed to attach PNG to Allure. name={}", name, ex);
        }
    }

    /**
     * Attaches a PNG screenshot file to the current Allure test container.
     *
     * <p><b>Why this overload exists</b>: Selenium commonly writes screenshots to disk first; this avoids
     * duplicating read/copy logic in hooks.
     *
     * @param name attachment name shown in the report
     * @param file screenshot file path (must exist)
     */
    public static void attachPngFile(String name, Path file) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("attachment name must not be null/blank");
        }
        if (file == null) {
            throw new IllegalArgumentException("file must not be null");
        }

        try {
            byte[] bytes = Files.readAllBytes(file);
            attachPng(name, bytes);
        } catch (Exception ex) {
            LOGGER.warn("Failed to read screenshot for Allure attachment. file={}", file.toAbsolutePath(), ex);
        }
    }

    /**
     * Attaches plain text (useful for HTTP snippets, console excerpts, or debug context).
     *
     * @param name attachment name shown in the report
     * @param text text content (null becomes empty string)
     */
    public static void attachText(String name, String text) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("attachment name must not be null/blank");
        }

        String safe = text == null ? "" : text;
        try {
            Allure.addAttachment(name, "text/plain", new ByteArrayInputStream(safe.getBytes(StandardCharsets.UTF_8)), ".txt");
        } catch (RuntimeException ex) {
            LOGGER.warn("Failed to attach text to Allure. name={}", name, ex);
        }
    }
}
