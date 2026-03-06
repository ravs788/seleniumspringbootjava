@echo off
setlocal

REM ==========================================================
REM Start Selenium Grid Hub (local) + verify it is reachable
REM
REM Prereq:
REM   C:\tools\selenium-grid\selenium-server-4.40.0.jar
REM
REM Hub ports:
REM   Web/UI:    5555
REM   EventBus:  5552 (publish), 5553 (subscribe)
REM ==========================================================

set "JAR=C:\tools\selenium-grid\selenium-server-4.40.0.jar"
set "HUB_HOST=0.0.0.0"
set "HUB_PORT=5555"

if not exist "%JAR%" (
  echo ERROR: Selenium Server jar not found at: %JAR%
  echo Copy selenium-server-4.40.0.jar to C:\tools\selenium-grid\
  exit /b 1
)

echo Starting HUB on %HUB_HOST%:%HUB_PORT% ...
start "Selenium Grid Hub" cmd /k java -jar "%JAR%" hub --host %HUB_HOST% --port %HUB_PORT% --publish-events tcp://%HUB_HOST%:5552 --subscribe-events tcp://%HUB_HOST%:5553

echo Waiting for hub /status endpoint to respond...
set "OK="

for /L %%i in (1,1,20) do (
  REM Use PowerShell to avoid curl alias issues on Windows (curl can map to Invoke-WebRequest)
  REM Any JSON response from /status counts as "reachable" (even if ready=false).
  powershell -NoProfile -Command "try { Invoke-RestMethod http://127.0.0.1:%HUB_PORT%/status -TimeoutSec 2 ^| Out-Null; exit 0 } catch { exit 1 }"
  if "!ERRORLEVEL!"=="0" (
    set "OK=1"
    goto :hub_ready
  )
  timeout /t 1 >nul
)

:hub_ready
if not defined OK (
  echo ERROR: Hub did not become reachable in time.
  echo Try opening: http://localhost:%HUB_PORT%/ui
  exit /b 1
)

echo Hub is reachable:
echo   http://localhost:%HUB_PORT%/ui
exit /b 0
