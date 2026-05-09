package framework.exceptions;

/**
 * Framework-specific runtime exception used for failures that should be actionable for testers and CI.
 *
 * <p><b>Why this exists</b>:
 * Raw Selenium/WebDriverManager exceptions are often noisy and missing business context (which browser,
 * which locator, which environment). Wrapping them behind a single exception type makes failures
 * consistent and easier to triage in logs and reports.
 *
 * <p><b>Design choice</b>:
 * Extend {@link RuntimeException} so framework failures do not force broad checked-exception handling
 * across test code, while still preserving the original cause via {@link #getCause()}.
 */
public final class FrameworkException extends RuntimeException {

    /**
     * Creates a framework exception with a message only.
     *
     * @param message human-readable failure description
     */
    public FrameworkException(String message) {
        super(message);
    }

    /**
     * Creates a framework exception with a message and root cause.
     *
     * @param message human-readable failure description
     * @param cause   underlying exception (may be null, but usually should not be)
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Wraps an existing throwable with additional context.
     *
     * <p><b>Why static factory</b>: keeps call sites short and consistent while preserving the cause chain.
     *
     * @param context additional context to prepend to the original message
     * @param cause   original failure
     * @return a new {@link FrameworkException}
     */
    public static FrameworkException withCause(String context, Throwable cause) {
        if (cause == null) {
            return new FrameworkException(context);
        }
        String original = cause.getMessage();
        String suffix = (original == null || original.isBlank()) ? cause.getClass().getSimpleName() : original;
        return new FrameworkException(context + ": " + suffix, cause);
    }
}
