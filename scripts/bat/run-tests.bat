@echo off
setlocal enabledelayedexpansion

REM ==========================================================
REM Run tests by tag (smoke/regression/...) + Allure report
REM
REM Usage:
REM   run-tests.bat                          (defaults: tag=smoke, browser=chrome)
REM   run-tests.bat smoke
REM   run-tests.bat regression
REM   run-tests.bat smoke chrome
REM   run-tests.bat regression firefox
REM   run-tests.bat smoke edge
REM   run-tests.bat smoke chrome true     (disable retry)
REM ==========================================================

set "TAG=%~1"
if "%TAG%"=="" set "TAG=smoke"

set "BROWSER=%~2"
if "%BROWSER%"=="" set "BROWSER=chrome"

set "NO_RETRY=%~3"
if "%NO_RETRY%"=="" set "NO_RETRY=false"

echo ==========================================================
echo Running tests for tag: %TAG%
echo Browser override: %BROWSER%
echo ==========================================================

REM Clean previous Allure results to avoid mixing runs
if exist "target\allure-results" (
  rmdir /s /q "target\allure-results"
)

echo NO_RETRY: %NO_RETRY%

call mvn -DtestFailureIgnore=true "-Dgroups=%TAG%" "-Dbrowser=%BROWSER%" test

if /I "%NO_RETRY%"=="true" (
  echo.
  echo Retry disabled. Skipping rerun of failing tests.
) else (
  echo.
  echo ==========================================================
  echo Rerun failing tests (up to 2 times) - Maven-level rerun
  echo ==========================================================
  call mvn -DtestFailureIgnore=true "-Dgroups=%TAG%" "-Dbrowser=%BROWSER%" -Dsurefire.rerunFailingTestsCount=2 test
)

echo.
echo ==========================================================
echo Generating Allure report
echo ==========================================================
call mvn allure:report
if errorlevel 1 (
  echo.
  echo Failed to generate Allure report.
  exit /b 1
)

echo.
echo ==========================================================
echo Serving Allure report (will start a local server)
echo Close the server window to stop.
echo ==========================================================
call mvn allure:serve

endlocal
