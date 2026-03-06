# Selenium Spring Boot Java — UI Automation Framework

An end-to-end UI automation framework built with **Java 21**, **Spring Boot**, **Selenium 4**, and **JUnit 5** using the **Page Object Model** and **parallel execution**.

---

## Status

Current implementation highlights:

- Spring Boot test scaffolding (`boot 4.0.3`)
- Page Object Model (`BasePage` + site-specific pages)
- Parallel execution via JUnit 5 (methods + classes concurrent)
- Hard cap of **max 3 concurrent browser instances** via `Semaphore` (parallel-safe)
- One WebDriver per test method
- Test suites implemented:
  - DemoBlaze (`DemoBlazeTests`)
  - The Internet (`TestInternetHeroku`)
- JSON test data per test class via loader abstraction (`DataLoaders`)

---

## Intended audience

- SDETs/QA engineers building a scalable Selenium framework baseline
- Developers wanting a reference for JUnit 5 parallelism + safe WebDriver lifecycle
- Learners practicing Page Objects, data-driven testing, and reporting (Allure)

---

## Quick links

- Architecture: [`architecture.md`](architecture.md)
- Selenium Grid (Windows): [`docs/selenium-grid-windows.md`](docs/selenium-grid-windows.md)
- Manual test cases:
  - DemoBlaze: [`docs/manual-testcases-demoblaze.md`](docs/manual-testcases-demoblaze.md)
  - The Internet: [`docs/manual-testcases-the-internet.md`](docs/manual-testcases-the-internet.md)
- Scripts (Windows): [`scripts/bat/`](scripts/bat/)
  - Grid: `start-grid-hub.bat`, `start-grid-nodes.bat`, `stop-grid-hub.bat`, `stop-grid-nodes.bat`, `run-grid-tests.bat`
  - Local: `run-tests.bat`

---

## Project structure (current)

```text
seleniumspringbootjava/
├── pom.xml
├── mvnw / mvnw.cmd
├── scripts/
│   └── bat/                          # Windows helper scripts (Grid + local runs)
├── docs/
│   ├── selenium-grid-windows.md      # Grid setup/runbook (Windows)
│   ├── manual-testcases-*.md         # Manual test case documentation
│   └── migration-status.md
├── src/
│   ├── main/java/.../pages           # Page Objects
│   └── test/
│       ├── java/...                  # JUnit 5 tests + test infrastructure
│       └── resources/
│           ├── junit-platform.properties
│           ├── config/test-config.json
│           └── testdata/...
└── target/                           # build output (ignored)
```

---

## Getting started (local)

### Prerequisites

- Java 21
- Maven (or use Maven Wrapper `mvnw.cmd`)
- Browsers installed: Chrome / Edge / Firefox

### Run all tests (recommended: Maven wrapper)

```powershell
.\mvnw.cmd -B test
```

### Run a single test class

```powershell
.\mvnw.cmd -B "-Dtest=com.example.seleniumspringbootjava.DemoBlazeTests" test
```

---

## Running tests

### Local runs by tag/browser (Windows helper)

```powershell
.\scripts\bat\run-tests.bat smoke chrome true
```

Arguments (see script header):
- Tag (default: `smoke`)
- Browser override (default: `chrome`)
- Disable retry (default: `false`; pass `true` to disable reruns)

### Selenium Grid runs (Windows)

See [`docs/selenium-grid-windows.md`](docs/selenium-grid-windows.md).

Grid URL used by this repo:
- `http://localhost:5555/`

Quick run (PowerShell):

```powershell
cmd /c scripts\bat\run-grid-tests.bat smoke true http://localhost:5555/ true
```

---

## Reporting (Allure)

This project writes Allure results to:
- `target/allure-results`

Generate an HTML report:

```powershell
mvn allure:report
```

Open the generated report:
- `target/site/allure-maven-plugin/index.html`

---

## Troubleshooting

### Grid “address already in use”
- Identify who is listening on ports and stop the process, or change Grid ports.
- This repo’s Grid runbook and scripts use:
  - Hub/UI/API: `5555`
  - Event bus: `5552/5553`
  - Nodes: `5556/5557/5558`

### External site timeouts
These tests run against real public websites. If `demoblaze.com` or `the-internet.herokuapp.com` are slow/unreachable, the test run can fail due to network timeouts.

---

## Roadmap

- Stabilize external-site tests (timeouts/retries) and add a small “offline” smoke suite (`about:blank`) for CI reliability
- Add CI documentation and badges once workflows are enabled on the default branch
- Expand coverage and add more sample sites/test suites

---

## Documentation maintenance

When making major changes (driver lifecycle, parallelism, config), update:
- `README.md`
- `architecture.md`
- `docs/selenium-grid-windows.md`
