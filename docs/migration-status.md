# Spring Boot Migration – Current State, Objective, and Plan

**Workspace root:** `seleniumspringbootjava/`  
**Source project:** `../blazedemo` (plain Maven + Selenium + JUnit 5)  
**Target project:** this Spring Boot project (`Spring Boot 4.0.3`, Java 21)

---

## 1) Objective (what we want to achieve)

Migrate the Selenium/JUnit framework from `../blazedemo` into this Spring Boot project while keeping a **similar structure and capabilities**, including:

- A **Page Object Model** structure (e.g., `pages/` + `BasePage`)
- A reusable **BaseTest** (driver lifecycle, parallel-safe)
- **Parallel execution** support (JUnit 5 + Surefire)
- Clean separation between:
  - framework code (pages, utilities)
  - tests (test classes)
  - configuration (timeouts, browser, parallel settings)

The goal is that the Spring Boot project becomes the “framework home” while preserving the ergonomics of the original project.

---

## 2) Current state (what is already done)

### 2.0 JUnit 5 parallel configuration

- Added `src/test/resources/junit-platform.properties`
  - Parallel execution enabled
  - Classes run concurrently by default
  - Fixed parallelism set to 2 threads

### 2.1 Dependencies / build configuration

`pom.xml` has been updated to include:

- `org.seleniumhq.selenium:selenium-java:4.18.1` **without test scope**  
  Reason: Page Objects are now being placed in `src/main/java`, so Selenium must be available to main compilation.
- `io.github.bonigarcia:webdrivermanager:5.7.0` (test scope)  
  Reason: auto-manage ChromeDriver versions locally.
- `org.apache.maven.plugins:maven-surefire-plugin:3.2.5` configured  
- `org.junit.platform:junit-platform-launcher` (test scope)

### 2.2 Framework code migrated/added

**Page support:**
- `src/main/java/com/example/seleniumspringbootjava/pages/BasePage.java`
  - Ported from `../blazedemo/src/main/java/com/example/BasePage.java`
  - Contains common wait/click/type helpers
  - Added a small stability enhancement: retry once on `StaleElementReferenceException` inside `click()`.

**DemoBlaze page objects:**
- `src/main/java/com/example/seleniumspringbootjava/pages/demoblaze/DemoBlazeHomePage.java`
  - Home page navigation: categories, open product, open cart
- `src/main/java/com/example/seleniumspringbootjava/pages/demoblaze/ProductPage.java`
  - Product page: wait for loaded, add-to-cart and accept alert
- `src/main/java/com/example/seleniumspringbootjava/pages/demoblaze/CartPage.java`
  - Cart page: wait for rows, open Place Order modal
- `src/main/java/com/example/seleniumspringbootjava/pages/demoblaze/OrderModal.java`
  - Place order modal: fill fields, purchase, verify thank-you message

**The-Internet (Herokuapp) page objects:**
- `src/main/java/com/example/seleniumspringbootjava/pages/theinternet/HomePage.java`
- `src/main/java/com/example/seleniumspringbootjava/pages/theinternet/AddRemoveElePage.java`
- `src/main/java/com/example/seleniumspringbootjava/pages/theinternet/DropDownPage.java`
- `src/main/java/com/example/seleniumspringbootjava/pages/theinternet/BasicAuthPage.java`

**Test support:**
- `src/test/java/com/example/seleniumspringbootjava/BaseTest.java`
  - Uses `ThreadLocal<WebDriver>` (parallel-safe concept)
  - Uses `WebDriverManager.chromedriver().setup()`
  - Creates a new `ChromeDriver` per test, quits after each test

### 2.3 Test migrated/added

- `src/test/java/com/example/seleniumspringbootjava/MyTests.java`
  - DemoBlaze purchase test refactored to use Page Objects
  - Test flow: Phones -> Nokia -> Add to cart; Laptops -> MacBook -> Add to cart; Cart -> Place Order -> Purchase
  - Now avoids raw locators inside the test, improving readability and maintainability

- `src/test/java/com/example/seleniumspringbootjava/TestInternetHeroku.java`
  - Migrated from `../blazedemo/src/test/java/com/example/TestInternetHeroku.java`
  - Uses `pages/theinternet/*` page objects
  - Includes tests:
    - Add/Remove Elements
    - Dropdown selection
    - Basic Auth

### 2.4 Validation status

- Build compiles: **BUILD SUCCESS** with `mvn -DskipTests=true test`
- DemoBlaze test execution:
  - Command to run single test:
    ```bash
    mvn "-Dtest=com.example.seleniumspringbootjava.MyTests" test
    ```
  - Result: sometimes succeeds, sometimes fails due to dynamic DOM updates (stale element). This is expected until the page-object refactor is done with more robust waits and navigation patterns.

---

## 3) Current gaps vs target architecture

### 3.1 Missing page objects for DemoBlaze
We only migrated `BasePage`. We have not created page object classes like:
- `HomePage` / category navigation
- `ProductPage` (add-to-cart)
- `CartPage` (place order)
- `OrderModal` (purchase form)

**Impact:** test logic remains brittle and duplicated.

### 3.2 BaseTest not yet configurable
Current `BaseTest`:
- always uses Chrome
- uses implicit waits (10s) + explicit waits inside tests
- does not support headless mode, browser selection, baseUrl config, etc.

### 3.3 Parallel execution not yet fully enabled/configured
We have:
- ThreadLocal driver pattern (good starting point)
We still need:
- JUnit 5 parallel configuration (typically `src/test/resources/junit-platform.properties`)
- Surefire thread settings (optional depending on desired parallel model)
- Confirmation by running multiple tests in parallel safely

### 3.4 Structure not yet matching “original”
Original project has clear separation of:
- pages (BasePage + concrete pages)
- tests (BaseTest + tests)
- parallel execution configuration

Target project is partially there, but page objects are not yet present and tests still contain locators.

---

## 4) Proposed target structure (what we will create)

Recommended structure inside this Spring Boot project:

```
src/
  main/
    java/com/example/seleniumspringbootjava/
      pages/
        BasePage.java
        demoblaze/
          DemoBlazeHomePage.java
          ProductPage.java
          CartPage.java
          OrderModal.java
      support/   (optional)
        Retry.java / Waits.java / Config.java
  test/
    java/com/example/seleniumspringbootjava/
      tests/
        DemoBlazePurchaseTest.java
      support/
        BaseTest.java
  test/
    resources/
      junit-platform.properties
```

Notes:
- Keep POM classes in `src/main/java` so they can be reused by any runner (JUnit, Cucumber, REST-driven triggers, etc.)
- Keep driver lifecycle and test fixtures in `src/test/java`

---

## 5) Future plan (step-by-step)

### Step 1 — Add JUnit 5 parallel config
✅ Done:
- `src/test/resources/junit-platform.properties` added
- Parallel classes enabled with fixed parallelism = 2

### Step 2 — Refactor DemoBlaze test into page objects
✅ Done:
- Created page objects under `src/main/java/.../pages/demoblaze/`:
  - `DemoBlazeHomePage`, `ProductPage`, `CartPage`, `OrderModal`
- Refactored `MyTests` to use these page objects

### Step 3 — Improve BaseTest (configurable, reusable)
Add:
- browser selection (Chrome/Firefox) via system property
- optional headless mode
- unify timeouts (implicit wait ideally set to 0; rely on explicit waits inside BasePage)
- consistent driver creation per thread

### Step 4 — Stabilize against stale elements
Best practice approach:
- avoid storing WebElements; always locate via `By`
- use explicit waits around navigations and dynamic content
- centralize retry logic in BasePage helper methods

### Step 5 — Verify parallel execution
Add at least 2 independent tests and run:
- `mvn test` with JUnit parallel enabled
- confirm no driver collisions (ThreadLocal works)
- ensure reports are stable

### Step 6 — Documentation updates
- Update `architecture.md` to reflect final structure
- Update `README.md` to reflect:
  - how to run a single test
  - how to run in parallel
  - how to switch browser/headless

---

## 6) Quick commands (for continuing later)

From `seleniumspringbootjava/` project root:

- Compile only:
  ```bash
  mvn -DskipTests=true test
  ```

- Run the migrated DemoBlaze test only:
  ```bash
  mvn "-Dtest=com.example.seleniumspringbootjava.MyTests" test
  ```

---

## 7) Known issues / warnings (current)

- **StaleElementReferenceException** can occur on DemoBlaze (dynamic DOM updates).
  - Partially mitigated by a retry in `BasePage.click()`
  - Full fix expected after page-object refactor + improved waits.

- Selenium CDP warnings for Chrome 145:
  - Warning about CDP version mismatch can appear; currently not blocking test execution.

---

## 8) Files changed/added in this migration so far

- `pom.xml` (deps + surefire)
- `src/test/java/com/example/seleniumspringbootjava/BaseTest.java` (added)
- `src/test/java/com/example/seleniumspringbootjava/MyTests.java` (added)
- `src/main/java/com/example/seleniumspringbootjava/pages/BasePage.java` (added)
- `README.md` (run instructions; high-level notes)
