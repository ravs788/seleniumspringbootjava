@echo off
setlocal enabledelayedexpansion

REM ==========================================================
REM Stop Selenium Grid Nodes (local) by killing processes
REM listening on the node ports.
REM
REM Node ports:
REM   Edge:    5556
REM   Firefox: 5557
REM   Chrome:  5558
REM ==========================================================

call :killPort 5556 "Edge node"
call :killPort 5557 "Firefox node"
call :killPort 5558 "Chrome node"

echo Done.
exit /b 0

:killPort
set "PORT=%~1"
set "LABEL=%~2"

echo ----------------------------------------
echo %LABEL%: checking port %PORT% ...

REM Use PowerShell to reliably return the owning PID for the given local port.
for /f "usebackq delims=" %%A in (`powershell -NoProfile -Command "(Get-NetTCPConnection -State Listen -LocalPort %PORT% -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty OwningProcess)"`) do (
  set "PID=%%A"
)

if not defined PID (
  echo %LABEL%: no LISTENING process found on port %PORT%.
  goto :eof
)

echo %LABEL%: killing PID !PID! (port %PORT%) ...
taskkill /PID !PID! /F >nul 2>&1
if "!ERRORLEVEL!"=="0" (
  echo %LABEL%: stopped PID !PID!.
) else (
  echo %LABEL%: ERROR stopping PID !PID!. Try running as Administrator.
)

set "PID="
goto :eof
