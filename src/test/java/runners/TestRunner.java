package framework.runners;

import framework.logging.LogDirectory;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG runner for executing the @smoke Cucumber scenarios.
 *
 * <p><b>Why this exists</b>:
 * We intentionally execute a tiny, high-signal subset first (@smoke) to validate the vertical slice.
 * This runner becomes the Phase 1 checkpoint: one green scenario proves config, driver lifecycle,
 * page objects, and step glue all work together.
 *
 * <p><b>Design choice</b>:
 * Uses {@link AbstractTestNGCucumberTests} so Cucumber scenarios show up as TestNG tests, enabling
 * suite XML selection, parallelization later, and TestNG reporting integrations.
 */
@CucumberOptions(
        features = "classpath:features",
        glue = {"framework.stepdefinitions", "framework.hooks"},
        tags = "@smoke",
        plugin = {
                "pretty",
                "summary",
                // Why: emits Allure-compatible results for Cucumber 7 (steps, tags, timing) into target/allure-results.
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    static {
        LogDirectory.configure();
    }

    /**
     * TestNG/Cucumber requires a public no-arg constructor for runner instantiation.
     *
     * <p><b>Why explicit</b>: documenting the implicit default constructor makes the runner entry point
     * obvious for engineers onboarding to the framework.
     */
    public TestRunner() {
        super();
    }

    /**
     * Provides Cucumber scenarios to TestNG.
     *
     * <p><b>Why override</b>:
     * Cucumber scenarios are executed via TestNG's {@code @DataProvider} mechanism. Enabling {@code parallel=true}
     * allows multiple scenarios to run concurrently, which is only safe because {@code DriverManager} uses
     * {@link java.lang.ThreadLocal} to isolate WebDriver per thread.
     *
     * <p><b>Why suite XML matters too</b>:
     * Thread pools for parallelized data providers are controlled via {@code testng.xml} attributes such as
     * {@code data-provider-thread-count}.
     *
     * @return scenarios as a TestNG data provider
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
