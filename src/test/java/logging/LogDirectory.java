package framework.logging;

import java.nio.file.Path;

/**
 * Sets {@code framework.log.dir} before Log4j2 initializes from {@code log4j2.xml}.
 *
 * <p>Why this exists:
 * <ul>
 *   <li>Maven Surefire passes an absolute {@code framework.log.dir} via {@code pom.xml}.</li>
 *   <li>IDE runs often omit that; Log4j may initialize from glue classes (static loggers) before
 *       anything else runs, so we set a stable default from {@link Path#of(String, String...) Path.of("")}.</li>
 * </ul>
 */
public final class LogDirectory {

    private LogDirectory() {}

    public static void configure() {
        if (System.getProperty("framework.log.dir") != null) {
            return;
        }
        Path dir = Path.of("").toAbsolutePath().normalize().resolve("target").resolve("logs");
        System.setProperty("framework.log.dir", dir.toString());
    }
}
