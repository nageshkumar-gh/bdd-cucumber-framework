# BDD Cucumber Framework

[![CI](https://github.com/nageshkumar-gh/bdd-cucumber-framework/actions/workflows/ci.yml/badge.svg)](https://github.com/nageshkumar-gh/bdd-cucumber-framework/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-17-437291?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Selenium](https://img.shields.io/badge/Selenium-4.43-43B02A?logo=selenium&logoColor=white)](https://www.selenium.dev/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.34-23D96C?logo=cucumber&logoColor=white)](https://cucumber.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.12-FF6C37?logo=testng&logoColor=white)](https://testng.org/)
[![Allure](https://img.shields.io/badge/Allure-2.29-E40046?logo=allure&logoColor=white)](https://docs.qameta.io/allure/)

Production-style **Behavior-Driven Development (BDD)** UI automation for web applications. The project combines **Gherkin** feature files with **Selenium WebDriver**, **TestNG** for parallel execution and retries, **Cucumber** as the BDD runner, and **Allure** + **Extent** for reporting. Dependency injection uses **PicoContainer** so step definitions stay thin and shared state is scenario-scoped.

> **Demo target:** scenarios are written against [OrangeHRM Live Demo](https://opensource-demo.orangehrmlive.com/) unless you point `app.loginUrl` elsewhere.

---

## Table of contents

- [Highlights](#highlights)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Repository layout](#repository-layout)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Running tests](#running-tests)
- [Parallel execution & retries](#parallel-execution--retries)
- [Docker & Selenium Grid](#docker--selenium-grid)
- [CI/CD](#cicd)
- [Reporting](#reporting)
- [Troubleshooting](#troubleshooting)

---

## Highlights

| Area | What you get |
|------|----------------|
| **Layers** | Feature → Step definitions → **Services** (flows) → **Page objects** → **Base page** + `WaitUtils` → **Driver** |
| **DI** | `cucumber-picocontainer` (`PicoFactory`): constructors inject services, `ScenarioContext`, hooks |
| **Driver** | `ThreadLocal` via `DriverManager`; local Chrome/Firefox/Edge or **remote** Selenium Grid |
| **Headless** | `-Dheadless=true`, `browser.headless`, or **`CI=true`** (GitHub Actions sets this) |
| **Stability** | TestNG `IRetryAnalyzer` (`-Dtest.retry.count=N`), Cucumber **rerun** file + `RerunFailedRunner` |
| **Observability** | SLF4J + Logback (`StepHooks`), Allure steps in services, screenshots + URL on failure |
| **Test data** | JSON under `src/test/resources/testdata/` (Jackson) + **DataFaker** for synthetic values |

---

## Tech stack

| Component | Version | Role |
|-----------|---------|------|
| Java | 17 | Language |
| Maven | 3.9+ | Build & test runner |
| Selenium | 4.43 | Browser automation |
| Cucumber JVM | 7.34.x | Gherkin execution, glue code |
| TestNG | 7.12 | Test orchestration, parallel scenarios, retries |
| PicoContainer | (via `cucumber-picocontainer`) | Per-scenario dependency injection |
| Allure | 2.29 (BOM) | Rich HTML reports, attachments |
| ExtentReports | 1.14.0 (Cucumber 7 adapter) | Secondary Cucumber report |
| Jackson | 2.20.x | JSON test data |
| DataFaker | 2.4.x | Randomized test strings |
| SLF4J + Logback | 2.x / 1.5.x | Structured logging |

---

## Architecture

### Execution flow

```text
Gherkin Feature
       │
       ▼
Cucumber Step Definition  (thin — calls services only)
       │
       ▼
Service layer             (AuthService, LoginService — business flows, Allure steps)
       │
       ▼
Page Object Model         (LoginPage, TopbarPage — extend BasePage)
       │
       ▼
WaitUtils / DriverManager.getDriver()   (ThreadLocal WebDriver)
       │
       ▼
DriverFactory             (local or RemoteWebDriver + OptionsFactory)
```

### Design patterns in use

- **Page Object Model (POM)** — UI locators and page actions live under `pages/`, not in steps.
- **Factory** — `DriverFactory` + `OptionsFactory` build browser-specific `WebDriver` instances.
- **Singleton-like config** — `Config` / `ConfigLoader` expose static accessors (classpath + env + system properties).
- **ThreadLocal driver** — `DriverManager` keeps one `WebDriver` per TestNG thread for **parallel** runs.
- **Dependency injection** — PicoContainer creates glue classes per scenario; no manual `new` for services in steps.

### TestNG + Cucumber bootstrap

`AbstractTestNGCucumberTests` normally initializes the internal `TestNGCucumberRunner` in `@BeforeClass`, but TestNG can invoke the **`scenarios` data provider first**, which used to yield **zero scenarios**. This project uses `CucumberTestNgLifecycle` to **bootstrap the runner early** when needed, and `TestRunner#setUpClass` skips duplicate initialization if the runner already exists.

---

## Repository layout

```text
src/test/java/
├── base/                 # BasePage — driver + explicit waits
├── config/               # Config, ConfigLoader
├── constants/            # e.g. ContextKeys for ScenarioContext
├── context/              # ScenarioContext (shared scenario state)
├── drivers/              # DriverManager, DriverFactory, OptionsFactory, BrowserType
├── hooks/                # Hooks (@Before/@After driver), StepHooks (step logging)
├── listeners/            # RetryAnalyzer (TestNG retry)
├── pages/                # Page objects (LoginPage, TopbarPage, …)
├── services/             # AuthService, LoginService — orchestration + assertions
├── stepDefinitions/      # Cucumber glue (one logical area per feature where possible)
├── testdata/             # TestDataLoader, UserCredentials, FakeDataFactory
├── testRunner/           # TestRunner, RerunFailedRunner, CucumberTestNgLifecycle
└── utils/                # WaitUtils, AllureReportUtils

src/test/resources/
├── config/
│   └── config.properties # URLs, timeouts, browser defaults
├── features/             # *.feature (Gherkin) — one Feature keyword per file
├── testdata/             # e.g. users.json
└── logback-test.xml      # Logging for tests

.github/workflows/        # CI (GitHub Actions)
docker-compose.yml         # Optional Selenium Standalone Chrome
```

---

## Prerequisites

- **JDK 17** (Eclipse Temurin, Oracle, or compatible)
- **Maven 3.9+**
- A supported browser locally (**Chrome**, **Firefox**, or **Edge**) **or** Docker for **Selenium Grid** (see below)

---

## Configuration

Primary file: `src/test/resources/config/config.properties`.

| Key / mechanism | Purpose |
|-----------------|--------|
| `env` | Logical environment name; loads `config/config-<env>.properties` when present |
| `app.loginUrl` | Application under test (required) |
| `app.username` / `app.password` | Defaults; override with **`APP_USERNAME`** / **`APP_PASSWORD`** env vars |
| `timeouts.implicitWaitSeconds` | Implicit wait (seconds) |
| `timeouts.explicitWaitSeconds` | Default explicit wait for `WaitUtils` |
| `timeouts.pageLoadTimeoutSeconds` | `WebDriver` page load timeout |
| `browser` | `CHROME` \| `FIREFOX` \| `EDGE` (also `-Dbrowser=…`) |
| `browser.headless` | `true` / `false`; CI often relies on **`CI=true`** instead |
| `selenium.grid.url` | Optional; also **`SELENIUM_GRID_URL`** for remote `RemoteWebDriver` |

System properties override file values (merged in `ConfigLoader`).

---

## Running tests

### All scenarios (default runner)

`RerunFailedRunner` is **excluded** from the default Surefire run so only `TestRunner` executes.

```bash
mvn test
```

### Filter by Cucumber tags

```bash
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@login or @auth"
```

### Browser and headless

```bash
mvn test -Dbrowser=CHROME -Dheadless=false
mvn test -Dbrowser=FIREFOX -Dheadless=true
```

On **CI**, set **`CI=true`** (GitHub Actions does this) so headless mode is selected unless you override with `-Dheadless=false`.

### Rerun only failed scenarios (from last run)

After a run, Cucumber may write:

- `target/rerun.txt`

Execute:

```bash
mvn test -Dtest=RerunFailedRunner
```

---

## Parallel execution & retries

### Parallel scenarios

`TestRunner` overrides the `scenarios` **DataProvider** with `parallel = true`. Tune TestNG data-provider threads when needed:

```bash
mvn test -Ddataproviderthreadcount=4
```

### Flaky-test retries (TestNG)

`TestRunner` attaches `RetryAnalyzer` to `runScenario`. Enable retries:

```bash
mvn test -Dtest.retry.count=1
```

(`0` = no retry, default.)

---

## Docker & Selenium Grid

Start a local Grid (Chrome) — see `docker-compose.yml`:

```bash
docker compose up -d
```

Run tests against the hub (URL must match your compose port mapping):

```bash
export SELENIUM_GRID_URL=http://localhost:4444
mvn test -Dbrowser=CHROME -Dheadless=true
```

VNC/noVNC is often exposed on port **7900** for the standalone image (see image documentation).

---

## CI/CD

Workflow: [`.github/workflows/ci.yml`](.github/workflows/ci.yml)

- Triggers on pushes and pull requests to `main` / `master`
- Uses **Temurin 17**, **Maven** with cache
- Sets **`CI=true`** and **`MAVEN_OPTS=-Dheadless=true -Dbrowser=CHROME`**
- Uploads **`target/allure-results`** as a workflow artifact

**CI badge:** If you fork the repo, update the badge URLs at the top of this README to match your `owner/repo`.

---

## Reporting

### Allure

- Cucumber results are emitted to **`target/allure-results`** (`allure.properties` sets the directory).
- Generate/serve locally (requires [Allure CLI](https://github.com/allure-framework/allure2)):

```bash
mvn allure:serve
# or
allure serve target/allure-results
```

Services use `io.qameta.allure.Allure.step` for nested steps; hooks attach **screenshots** and **current URL** on failure.

### ExtentReports

Configured via the Cucumber adapter in `TestRunner`; output paths follow the adapter defaults (see `extent.properties` if present).

### Surefire / TestNG

Surefire is configured with **`usedefaultlisteners=false`** to avoid TestNG’s JUnit XML reporter calling `InetAddress.getLocalHost()` on restricted agents. Standard Surefire reports still appear under **`target/surefire-reports`**.

---

## Troubleshooting

| Symptom | What to check |
|--------|----------------|
| **No scenarios executed** | Ensure Gherkin is valid (**one `Feature:` per file**). Split multi-feature files. |
| **Duplicate step definitions** | Remove overlapping `@Given`/`@When` in different classes for the same text. |
| **Login failures on demo** | Align Gherkin credentials with `config.properties` / env (`Admin` / `admin123` for OrangeHRM demo). |
| **Grid connection errors** | `SELENIUM_GRID_URL` / `selenium.grid.url` must point at a live `/wd/hub` or Grid 4 endpoint. |
| **Parallel flakiness** | Reduce `dataproviderthreadcount`; avoid shared mutable state outside `ScenarioContext`. |

---

## Contributing

1. Keep **step definitions** thin — push logic into **services** and **pages**.
2. Add **locators** only in page objects; use **`WaitUtils`** for explicit waits.
3. Prefer **tags** (`@smoke`, `@P1`) for selective runs in CI.

---

<p align="center">
  <sub>Selenium · Cucumber · TestNG · Allure · Page Object Model · PicoContainer</sub>
</p>
