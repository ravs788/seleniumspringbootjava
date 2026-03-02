# Architecture (Current) – seleniumspringbootjava

This file describes the **current state** of the Spring Boot-based automation framework solution.

> Update this file whenever you introduce major changes: driver lifecycle, parallel execution model, base utilities, page structure, test structure, reporting/CI, etc.

## High-level view (current architecture)

This describes the current architecture implemented in this repository.

```mermaid
flowchart TB
  subgraph SB_Project["Spring Boot Maven Project: seleniumspringbootjava"]
    direction TB

    subgraph Test_Layer["Test Layer (src/test/java)"]
      BaseTest["SpringSeleniumTestBase\n- ThreadLocal<WebDriver>\n- @BeforeEach/@AfterEach\n- Semaphore(max=3)\n- driver() accessor"]
      Tests["JUnit 5 Tests\nextend SpringSeleniumTestBase"]
    end

    subgraph Page_Layer["Page Layer (src/main/java)"]
      BasePage["BasePage\n- protected driver\n- protected wait\n- scrollIntoView\n- waitPresent/Visible/Clickable\n- click/type/select helpers"]
      Pages["Page Objects\nextend BasePage"]
    end

    subgraph Config_Layer["Config (src/test/resources)"]
      JunitProps["junit-platform.properties\n- JUnit5 parallel enabled\n- method + class concurrent\n- fixed parallelism = 3"]
      TestData["testdata/<TestClassSimpleName>/*.json\n- per-test-class common.json\n- optional per-test-case json"]
    end

    subgraph Data_Layer["Data Loading (src/main/java)"]
      DataLoader["DataLoader (interface)\n- loadCommon/loadTestCase/load"]
      JsonLoader["JsonDataLoader\n- Jackson\n- loads from classpath:testdata/..."]
      DataLoaders["DataLoaders\n- json() accessor"]
    end

    BaseTest -->|creates per-test WebDriver| Driver["FirefoxDriver instance\n(one per test method / thread)"]
    Tests -->|uses| Pages
    Tests -->|loads test data| DataLoaders
    DataLoaders --> JsonLoader
    JsonLoader --> TestData

    Pages --> BasePage
    BasePage -->|uses| Driver
    BasePage -->|creates| Wait["WebDriverWait\n(per page object)"]

    JunitProps -->|enables| Parallel["JUnit5 Parallel Execution"]
    Parallel --> BaseTest
  end
```

## Current implementation status

- Spring Boot project scaffold: ✅
- Selenium dependency added: ✅
- JUnit 5 parallel execution: ✅ (`src/test/resources/junit-platform.properties`)
- Driver lifecycle: ✅ one WebDriver per test method (`SpringSeleniumTestBase`)
- Browser concurrency limit: ✅ max 3 browsers (Semaphore in `SpringSeleniumTestBase`)
- Page Object model: ✅ (`BasePage` + site-specific pages)
- Test suites: ✅ DemoBlaze + The-Internet Herokuapp
- Test data: ✅ JSON per test class (classpath `testdata/<TestClassSimpleName>/common.json`) via `DataLoaders.json()`

## Key design decisions

### 1) One WebDriver per test method

Each test method gets a fresh `FirefoxDriver`. This avoids shared mutable state and allows method-level parallelism safely.

### 2) Parallelism is controlled by JUnit 5 (not Surefire)

Surefire uses the JUnit Platform provider. Parallel settings live in `junit-platform.properties`.

### 3) Hard max browser cap = 3

Even if JUnit parallelism is increased, the framework will not exceed 3 browsers due to a `Semaphore(3)` around driver creation.

### 4) Page objects are instantiated per test method

Tests create page objects with the current method’s driver, avoiding Spring proxying issues with WebDriver interfaces (e.g., `JavascriptExecutor`).

### 5) Test data is loaded from classpath resources (JSON)

Test data is stored under `src/test/resources/testdata/...` and loaded using the generic data-loader abstraction located in `src/main/java`:

- `DataLoader` interface (extendable to other formats in future)
- `JsonDataLoader` implementation (Jackson)
- `DataLoaders` accessor (central place to retrieve loader instances)

## Notes

These are real external-site UI tests. Occasional flakiness can still occur due to network/site behavior.
