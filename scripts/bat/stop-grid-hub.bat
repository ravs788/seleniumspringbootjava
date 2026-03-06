@echo off
setlocal

REM ==========================================================
REM Stop Selenium Grid Hub (local) by killing the process
REM listening on the Grid hub/router port.
REM
REM Hub/router/UI port: 5555
REM ==========================================================

set "HUB_PORT=5555"

echo Finding process listening on port %HUB_PORT% ...
set "PID="
for /f "usebackq delims=" %%A in (`powershell -NoProfile -Command "(Get-NetTCPConnection -State Listen -LocalPort %HUB_PORT% -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty OwningProcess)"`) do (
  set "PID=%%A"
)

if not defined PID (
  echo No LISTENING process found on port %HUB_PORT%. Hub already stopped.
  exit /b 0
)

echo Killing PID %PID% (hub/router on %HUB_PORT%) ...
taskkill /PID %PID% /F >nul 2>&1

if "%ERRORLEVEL%"=="0" (
  echo Hub stopped (PID %PID%).
  exit /b 0
) else (
  echo ERROR: Failed to stop hub (PID %PID%). Try running as Administrator.
  exit /b 1
)
