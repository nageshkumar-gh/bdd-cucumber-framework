package com.framework.runners;

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
        features = "src/test/resources/features",
        glue = {"com.framework"},
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
     * Keeping this in place allows easy enablement of parallel execution in the future (Phase 3)
     * by toggling the DataProvider {@code parallel} flag without changing scenario code.
     *
     * @return scenarios as a TestNG data provider
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}

