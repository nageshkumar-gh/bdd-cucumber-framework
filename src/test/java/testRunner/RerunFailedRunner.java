package testRunner;

import io.cucumber.picocontainer.PicoFactory;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "@target/rerun.txt",
        glue = {"stepDefinitions", "hooks"},
        objectFactory = PicoFactory.class,
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "html:target/cucumber-reports/rerun-cucumber.html",
                "json:target/cucumber-reports/rerun-cucumber.json"
        },
        monochrome = true
)
public class RerunFailedRunner extends AbstractTestNGCucumberTests {

    @Override
    @BeforeClass(alwaysRun = true)
    public void setUpClass(ITestContext context) {
        if (CucumberTestNgLifecycle.readRunner(this) == null) {
            super.setUpClass(context);
        }
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        CucumberTestNgLifecycle.ensureRunner(this);
        return super.scenarios();
    }
}

