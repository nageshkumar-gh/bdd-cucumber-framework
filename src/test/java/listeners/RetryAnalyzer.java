package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG-level retry for flaky scenarios (Cucumber exposes each scenario as a TestNG test method).
 * <p>
 * Enable with {@code -Dtest.retry.count=1} (retries once after the first failure).
 * </p>
 */
public final class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY = parseMaxRetry();
    private static final String RETRY_ATTR = "io.automation.retryCount";

    private static int parseMaxRetry() {
        try {
            return Integer.parseInt(System.getProperty("test.retry.count", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public boolean retry(ITestResult result) {
        if (MAX_RETRY <= 0) {
            return false;
        }
        if (result.getStatus() != ITestResult.FAILURE) {
            return false;
        }
        Object previous = result.getAttribute(RETRY_ATTR);
        int count = previous instanceof Integer i ? i : 0;
        if (count < MAX_RETRY) {
            result.setAttribute(RETRY_ATTR, count + 1);
            return true;
        }
        return false;
    }
}
