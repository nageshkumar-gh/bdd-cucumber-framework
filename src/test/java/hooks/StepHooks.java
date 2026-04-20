package hooks;

import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.PickleStepTestStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step-level hooks for traceability (logs feed SLF4J / logback during CI runs).
 */
public class StepHooks {

    private static final Logger log = LoggerFactory.getLogger(StepHooks.class);

    @BeforeStep
    public void logStep(PickleStepTestStep step, Scenario scenario) {
        log.info("[{}] {}", scenario.getName(), step.getStep().getText());
    }
}
