package framework.runners;

import framework.logging.LogDirectory;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG runner for PIM employee scenarios ({@code @employee}).
 */
@CucumberOptions(
        features = "classpath:features/pim",
        glue = {"framework.stepdefinitions", "framework.hooks"},
        tags = "@employee",
        plugin = {
                "pretty",
                "summary",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class PimEmployeeRunner extends AbstractTestNGCucumberTests {

    static {
        LogDirectory.configure();
    }

    public PimEmployeeRunner() {
        super();
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
