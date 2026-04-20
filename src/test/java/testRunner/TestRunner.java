package testRunner;

import io.cucumber.picocontainer.PicoFactory;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import listeners.RetryAnalyzer;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepDefinitions", "hooks"},
        objectFactory = PicoFactory.class,
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "rerun:target/rerun.txt",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @BeforeClass(alwaysRun = true)
    public void setUpClass(ITestContext context) {
        if (CucumberTestNgLifecycle.readRunner(this) == null) {
            super.setUpClass(context);
        }
    }

    @Override
    @Test(
            groups = "cucumber",
            description = "Runs Cucumber Scenarios",
            dataProvider = "scenarios",
            retryAnalyzer = RetryAnalyzer.class
    )
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        super.runScenario(pickleWrapper, featureWrapper);
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        CucumberTestNgLifecycle.ensureRunner(this);
        return super.scenarios();
    }
}

