# Selenium Grid (Non-Docker) Setup — Windows 11 (Chrome + Edge + Firefox)

This guide explains how to run **Selenium Grid 4** locally on **Windows 11** without Docker, and how to run this project’s tests against the Grid.

---

## Prerequisites

1. **Java 21** installed (or any supported Java for Selenium 4)
   - Verify: `java -version`
2. Browsers installed:
   - Google **Chrome**
   - Microsoft **Edge**
   - Mozilla **Firefox**
3. Network access to download Selenium Server jar (once)
4. This project builds locally (`mvn -DskipTests test`)

---

## 1) Download Selenium Server (Grid 4)

1. Create a folder to store the jar, for example:
   - `tools/selenium-grid/`

2. Download Selenium Server jar (Grid 4):
   - URL (official): https://www.selenium.dev/downloads/
   - Download **Selenium Server (Grid)** and place the jar in `tools/selenium-grid/`

Example file name:
- `selenium-server-4.xx.x.jar`

---

## 2) Start Selenium Grid (hub + nodes mode via project scripts)

This repo includes scripts to start Grid **hub/router** and **browser nodes** separately.

### Ports used (this repo)

Hub/router/UI/API:
- `http://localhost:5555/`
- UI: `http://localhost:5555/ui`
- Status: `http://localhost:5555/status`

Event bus (ZeroMQ):
- Publish: `tcp://127.0.0.1:5552`
- Subscribe: `tcp://127.0.0.1:5553`

Node ports:
- Edge node: `http://127.0.0.1:5556`
- Firefox node: `http://127.0.0.1:5557`
- Chrome node: `http://127.0.0.1:5558`

### Start hub

From the project root (PowerShell):

```powershell
.\scripts\bat\start-grid-hub.bat
```

### Start nodes

```powershell
.\scripts\bat\start-grid-nodes.bat
```

### Stop hub / nodes

```powershell
.\scripts\bat\stop-grid-nodes.bat
.\scripts\bat\stop-grid-hub.bat
```

Note: the hub script may print a “not reachable” message due to a Windows batch delayed-expansion check, but the hub can still be running. Always validate via `http://localhost:5555/status`.

---

## 3) Verify Grid is running

### Grid UI
Open:
- http://localhost:5555/ui

### Status endpoint
Open:
- http://localhost:5555/status

You should see `ready: true` and available node/capability information.

---

## 4) Run tests against Grid

### Configuration flags

Use system properties:

- `-Dselenium.remote=true`
- `-Dselenium.gridUrl=http://localhost:5555/`
- `-Dbrowser=chrome|firefox|edge` (optional override)
- If `-Dbrowser` is not set, the framework uses the first configured browser from `src/test/resources/config/test-config.json`.

### Example: run DemoBlazeTests on Chrome via Grid

```powershell
mvn "-Dselenium.remote=true" "-Dselenium.gridUrl=http://localhost:5555/" "-Dtest=com.example.seleniumspringbootjava.DemoBlazeTests" "-Dbrowser=chrome" test
```

### Example: run The Internet tests on Edge via Grid

```powershell
mvn "-Dselenium.remote=true" "-Dselenium.gridUrl=http://localhost:5555/" "-Dtest=com.example.seleniumspringbootjava.TestInternetHeroku" "-Dbrowser=edge" test
```

### Multi-browser execution
If the project is configured to execute the same tests across multiple browsers (via the multi-browser mechanism and the `browsers` list in `test-config.json`), then ensure the list includes:

- chrome
- firefox
- edge

---

## Troubleshooting

### Grid UI shows no nodes / no available browsers
- Ensure Chrome/Edge/Firefox are installed
- Ensure you started Grid with the correct jar
- Restart Grid

### Session creation fails / driver errors
- Check `http://localhost:5555/status`
- Check Grid console logs
- Try running a single test with `-Dbrowser=chrome` first

### External site timeouts
This project runs against real public sites. Network/site availability can cause failures unrelated to Grid.
Consider:
- Retrying
- Running a small “grid smoke test” that opens `about:blank` (no external dependency)

---

## Notes

- This guide uses hub+nodes mode because it makes ports explicit and avoids “address already in use” conflicts.
- If you change hub ports, also update:
  - `start-grid-hub.bat`, `start-grid-nodes.bat`
  - `run-grid-tests.bat` (default `GRID_URL`)
- For distributed setups (hub + remote nodes), Selenium Server can be started in hub/node mode as well.
