# bdd-cucumber-framework

Portfolio-friendly BDD automation framework using **Selenium + TestNG + Cucumber**, with **Allure + Extent** reporting, **rerun failed scenarios**, and **parallel execution**.

## Prerequisites
- Java **17+**
- Maven **3.9+**
- Chrome/Firefox/Edge installed (depending on what you run)

## Run tests

### Run all scenarios
```bash
mvn test
```

### Run by tags
```bash
mvn test -Dcucumber.filter.tags="@login"
```

### Browser selection / headless
```bash
mvn test -Dbrowser=CHROME -Dheadless=false
mvn test -Dbrowser=FIREFOX -Dheadless=true
```

### Environment + config
Defaults live in:
- `src/test/resources/config/config.properties`

Override using:
- system properties: `-Denv=qa`
- environment variables (preferred for secrets):
  - `APP_USERNAME`
  - `APP_PASSWORD`

Example:
```bash
export APP_USERNAME="Admin"
export APP_PASSWORD="admin123"
mvn test -Denv=local
```

## Reports

### Allure
- Results directory: `target/allure-results`
- Generate report locally (requires Allure CLI installed):
```bash
allure serve target/allure-results
```

### Extent
- Report output: `target/extent-report/extent.html`

## Rerun failed scenarios

After the main run, Cucumber will write failed scenarios into:
- `target/rerun.txt`

Run only failures using the rerun runner:
```bash
mvn test -Dtest=RerunFailedRunner
```

## Parallel execution

Scenario-level parallel execution is enabled via TestNG DataProvider.

Control thread count using Surefire:
```bash
mvn test -Ddataproviderthreadcount=4
```

