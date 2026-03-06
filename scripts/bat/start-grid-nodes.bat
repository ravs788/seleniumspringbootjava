@echo off
setlocal enabledelayedexpansion

REM ==========================================================
REM Start Selenium Grid Nodes (local) + verify they register
REM
REM This script assumes the HUB is already running on localhost:5555.
REM
REM Prereq:
REM   C:\tools\selenium-grid\selenium-server-4.40.0.jar
REM
REM Node ports:
REM   Chrome:   5558
REM   Edge:     5556
REM   Firefox:  5557
REM ==========================================================

set "JAR=C:\tools\selenium-grid\selenium-server-4.40.0.jar"

set "HUB_URL=http://127.0.0.1:5555"
set "STATUS_URL=http://127.0.0.1:5555/status"

REM EventBus (ZeroMQ) endpoints exposed by the Hub process
set "EVENT_BUS_PUBLISH=tcp://127.0.0.1:5552"
set "EVENT_BUS_SUBSCRIBE=tcp://127.0.0.1:5553"

if not exist "%JAR%" (
  echo ERROR: Selenium Server jar not found at: %JAR%
  echo Copy selenium-server-4.40.0.jar to C:\tools\selenium-grid\
  exit /b 1
)

REM Quick hub reachability check
powershell -NoProfile -Command "try { $r = Invoke-RestMethod %STATUS_URL% -TimeoutSec 2; if ($r.value) { exit 0 } else { exit 1 } } catch { exit 1 }"
if not "%ERRORLEVEL%"=="0" (
  echo ERROR: Hub not reachable at %HUB_URL%
  echo Start it first with: start-grid-hub.bat
  exit /b 1
)

echo Starting CHROME node on port 5558...
start "Selenium Node - Chrome" cmd /k java -jar "%JAR%" node --host 127.0.0.1 --port 5558 --detect-drivers true --selenium-manager true --driver-implementation "chrome" --publish-events %EVENT_BUS_PUBLISH% --subscribe-events %EVENT_BUS_SUBSCRIBE% --hub %HUB_URL%

echo Starting EDGE node on port 5556...
start "Selenium Node - Edge" cmd /k java -jar "%JAR%" node --host 127.0.0.1 --port 5556 --detect-drivers true --selenium-manager true --driver-implementation "edge" --publish-events %EVENT_BUS_PUBLISH% --subscribe-events %EVENT_BUS_SUBSCRIBE% --hub %HUB_URL%

echo Starting FIREFOX node on port 5557...
start "Selenium Node - Firefox" cmd /k java -jar "%JAR%" node --host 127.0.0.1 --port 5557 --detect-drivers true --selenium-manager true --driver-implementation "firefox" --publish-events %EVENT_BUS_PUBLISH% --subscribe-events %EVENT_BUS_SUBSCRIBE% --hub %HUB_URL%

echo.
echo Waiting for nodes to register...
set "REGISTERED="

for /L %%i in (1,1,30) do (
  powershell -NoProfile -Command "try { $r = Invoke-RestMethod %STATUS_URL% -TimeoutSec 2; if ($r.value.nodes.Count -gt 0) { exit 0 } else { exit 1 } } catch { exit 1 }"
  if "!ERRORLEVEL!"=="0" (
    set "REGISTERED=1"
    goto :registered
  )
  timeout /t 1 >nul
)

:registered
if not defined REGISTERED (
  echo ERROR: No nodes registered within timeout.
  echo Open hub UI to inspect: http://localhost:5555/ui
  exit /b 1
)

echo Nodes registered successfully.
echo Hub UI:
echo   http://localhost:5555/ui
exit /b 0
