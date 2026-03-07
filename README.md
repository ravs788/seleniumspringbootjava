# Selenium Spring Boot Java — UI Automation Framework

An end-to-end UI automation framework built with **Java 21**, **Spring Boot**, **Selenium 4**, and **JUnit 5**, following the **Page Object Model (POM)** with **safe parallel execution**.

[![Issues](https://img.shields.io/github/issues/ravs788/seleniumspringbootjava)](https://github.com/ravs788/seleniumspringbootjava/issues) [![Forks](https://img.shields.io/github/forks/ravs788/seleniumspringbootjava?style=social)](https://github.com/ravs788/seleniumspringbootjava/network/members) [![Stars](https://img.shields.io/github/stars/ravs788/seleniumspringbootjava?style=social)](https://github.com/ravs788/seleniumspringbootjava/stargazers) [![License](https://img.shields.io/github/license/ravs788/seleniumspringbootjava)](https://github.com/ravs788/seleniumspringbootjava/blob/main/LICENSE)

> Note: If badges don’t render in VS Code Markdown Preview, it’s typically because external image loading is blocked by your network/proxy/settings. They render on GitHub.

---

## 📊 Summary of Features

| Feature | Status |
| --- | --- |
| Spring Boot-based test scaffolding | ✅ Implemented (`boot 4.0.3`) |
| Page Object Model (BasePage + site pages) | ✅ Implemented |
| Parallel execution (JUnit 5: methods + classes concurrent) | ✅ Implemented |
| Parallel safety cap (max 3 concurrent browsers) | ✅ Implemented (`Semaphore`) |
| One WebDriver per test method | ✅ Implemented |
| JSON test data per test class | ✅ Implemented (`DataLoaders`) |
| Selenium Grid support (Windows runbook + scripts) | ✅ Implemented |
| Reporting (Allure) | ✅ Implemented |
| CI badges/workflows | ✅ Implemented (GitHub Actions: `ui-smoke`, `ui-regression`) |

---

## 👥 Intended audience

- SDETs/QA engineers building a scalable Selenium framework baseline
- Developers wanting a reference for JUnit 5 parallelism + safe WebDriver lifecycle
- Learners practicing Page Objects, data-driven testing, and reporting (Allure)

---

## 🔗 Quick links

- Architecture: [`architecture.md`](architecture.md)
- Selenium Grid (Windows): [`docs/selenium-grid-windows.md`](docs/selenium-grid-windows.md)
- Manual test cases:
  - DemoBlaze: [`docs/manual-testcases-demoblaze.md`](docs/manual-testcases-demoblaze.md)
  - The Internet: [`docs/manual-testcases-the-internet.md`](docs/manual-testcases-the-internet.md)
- Scripts (Windows): [`scripts/bat/`](scripts/bat/)
  - Grid: `start-grid-hub.bat`, `start-grid-nodes.bat`, `stop-grid-hub.bat`, `stop-grid-nodes.bat`, `run-grid-tests.bat`
  - Local: `run-tests.bat`

---

## 📁 Project structure

| Path | Purpose |
| --- | --- |
| `src/main/java/.../pages/` | Page Objects (POM) |
| `src/main/java/.../model/` | Domain models |
| `src/main/java/.../dataloader/` | JSON/data loading helpers |
| `src/test/java/.../` | JUnit 5 tests + test infra (extensions/config) |
| `src/test/resources/config/` | Test configuration (`test-config.json`) |
| `src/test/resources/testdata/` | Per-suite JSON test data |
| `docs/` | Runbooks + manual test cases |
| `scripts/bat/` | Windows helper scripts (local + Grid runs) |

```text
seleniumspringbootjava/
├── pom.xml
├── mvnw / mvnw.cmd
├── scripts/
│   └── bat/
├── docs/
├── src/
│   ├── main/java/.../pages
│   ├── main/java/.../model
│   ├── main/java/.../dataloader
│   └── test/
│       ├── java/...
│       └── resources/...
└── target/                           # build output (ignored)
```

---

## 🛠️ Prerequisites

- Java 21
- Maven (or Maven Wrapper `mvnw.cmd`)
- Browser(s): Chrome / Edge / Firefox
- (Optional) Allure CLI if you want `allure serve` locally

---

## 🚀 Getting started (local)

### Run all tests (recommended: Maven wrapper)

```powershell
.\mvnw.cmd -B test
```

### Run a single test class

```powershell
.\mvnw.cmd -B "-Dtest=com.example.seleniumspringbootjava.DemoBlazeTests" test
```

---

## 🧪 Running tests

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

Quick run:

```powershell
cmd /c scripts\bat\run-grid-tests.bat smoke true http://localhost:5555/ true
```

---

## 📊 Reporting (Allure)

Allure results directory:
- `target/allure-results`

Generate an HTML report:

```powershell
mvn allure:report
```

Open the generated report:
- `target/site/allure-maven-plugin/index.html`

---

## 🧩 Test suites

- **DemoBlaze**: `com.example.seleniumspringbootjava.DemoBlazeTests`
- **The Internet**: `com.example.seleniumspringbootjava.TestInternetHeroku`

---

## 🧰 Troubleshooting

### Grid “address already in use”
- Identify who is listening on ports and stop the process, or change Grid ports.
- This repo’s Grid runbook and scripts use:
  - Hub/UI/API: `5555`
  - Event bus: `5552/5553`
  - Nodes: `5556/5557/5558`

### External site timeouts
These tests run against real public websites. If `demoblaze.com` or `the-internet.herokuapp.com` are slow/unreachable, the test run can fail due to network timeouts.

---

## 🗺️ Roadmap

- Stabilize external-site tests (timeouts/retries) and add a small “offline” smoke suite (`about:blank`) for CI reliability
- Add CI documentation and badges once workflows are enabled on the default branch
- Expand coverage and add more sample sites/test suites

---

## 📝 Documentation maintenance

When making major changes (driver lifecycle, parallelism, config), update:
- `README.md`
- `architecture.md`
- `docs/selenium-grid-windows.md`
