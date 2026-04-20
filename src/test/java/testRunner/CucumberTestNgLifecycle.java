package testRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.TestNGCucumberRunner;

import java.lang.reflect.Field;

/**
 * TestNG can invoke the {@code scenarios} data provider before {@link org.testng.annotations.BeforeClass},
 * which leaves Cucumber's private {@code testNGCucumberRunner} field null and yields zero scenarios.
 */
public final class CucumberTestNgLifecycle {

    private CucumberTestNgLifecycle() {
    }

    public static void ensureRunner(AbstractTestNGCucumberTests testClass) {
        if (readRunner(testClass) != null) {
            return;
        }
        try {
            Field field = AbstractTestNGCucumberTests.class.getDeclaredField("testNGCucumberRunner");
            field.setAccessible(true);
            field.set(testClass, new TestNGCucumberRunner(testClass.getClass(), key -> null));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to bootstrap Cucumber TestNG runner", e);
        }
    }

    public static TestNGCucumberRunner readRunner(AbstractTestNGCucumberTests testClass) {
        try {
            Field field = AbstractTestNGCucumberTests.class.getDeclaredField("testNGCucumberRunner");
            field.setAccessible(true);
            return (TestNGCucumberRunner) field.get(testClass);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
