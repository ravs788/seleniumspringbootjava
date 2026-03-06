@echo off
setlocal enabledelayedexpansion

REM Always run from the repo root (so relative paths like target/ work)
pushd "%~dp0\..\.."

set "LOG_FILE=target\run-grid-tests-output.txt"
if not exist "target" mkdir "target"

REM Truncate log file at start of run (avoid appending across runs)
break > "%LOG_FILE%"

echo Writing log to %LOG_FILE%

REM ==========================================================
REM Run tests on Selenium Grid + Allure report
REM
REM Defaults:
REM   TAG = smoke
REM   HEADLESS = true
REM   GRID_URL = http://localhost:5555/
REM
REM Usage:
REM   run-grid-tests.bat
REM   run-grid-tests.bat smoke
REM   run-grid-tests.bat regression
REM   run-grid-tests.bat smoke false
REM   run-grid-tests.bat regression true
REM   run-grid-tests.bat smoke true http://localhost:5555/
REM ==========================================================

set "TAG=%~1"
if "%TAG%"=="" set "TAG=smoke"

set "HEADLESS=%~2"
if "%HEADLESS%"=="" set "HEADLESS=true"

set "GRID_URL=%~3"
if "%GRID_URL%"=="" set "GRID_URL=http://localhost:5555/"

echo ==========================================================
echo Running tests on Selenium Grid
echo TAG      : %TAG%
echo HEADLESS : %HEADLESS%
echo GRID URL : %GRID_URL%
echo ==========================================================

REM Clean previous Allure results to avoid mixing runs
if exist "target\allure-results" (
  rmdir /s /q "target\allure-results"
)

set "NO_RETRY=%~4"
if "%NO_RETRY%"=="" set "NO_RETRY=false"

echo NO_RETRY: %NO_RETRY%

REM Log the same header to the log file as well
echo ==========================================================>> "%LOG_FILE%"
echo Running tests on Selenium Grid>> "%LOG_FILE%"
echo TAG      : %TAG%>> "%LOG_FILE%"
echo HEADLESS : %HEADLESS%>> "%LOG_FILE%"
echo GRID URL : %GRID_URL%>> "%LOG_FILE%"
echo NO_RETRY : %NO_RETRY%>> "%LOG_FILE%"
echo ==========================================================>> "%LOG_FILE%"

set "TEST_EXIT_CODE=0"

echo Starting Maven test run 1...>> "%LOG_FILE%"
call mvn -DtestFailureIgnore=true -DnoJUnitRetry=true -Dselenium.remote=true -Dselenium.gridUrl=%GRID_URL% -Dgroups=%TAG% -Dheadless=%HEADLESS% test >> "%LOG_FILE%" 2>&1
set "TEST_EXIT_CODE=!ERRORLEVEL!"
echo Maven test run 1 exit code: !TEST_EXIT_CODE!>> "%LOG_FILE%"

if /I "%NO_RETRY%"=="true" (
  echo.
  echo Retry disabled (-DnoRetry). Skipping rerun of failing tests.
) else (
  if "!TEST_EXIT_CODE!"=="0" (
    echo.
    echo Tests passed on run 1. Skipping rerunFailingTestsCount.
  ) else (
    echo.
    echo ==========================================================
    echo Rerun failing tests (up to 2 times) - Maven-level rerun
    echo ==========================================================
    echo Starting Maven rerunFailingTestsCount run...>> "%LOG_FILE%"
    call mvn -DtestFailureIgnore=true -DnoJUnitRetry=true -Dselenium.remote=true -Dselenium.gridUrl=%GRID_URL% -Dgroups=%TAG% -Dheadless=%HEADLESS% -Dsurefire.rerunFailingTestsCount=2 test >> "%LOG_FILE%" 2>&1
    echo Maven rerunFailingTestsCount exit code: !ERRORLEVEL!>> "%LOG_FILE%"
    if not "!ERRORLEVEL!"=="0" set "TEST_EXIT_CODE=!ERRORLEVEL!"
  )
)

echo.
echo ==========================================================
echo Generating Allure report (always)
echo ==========================================================
echo Starting Allure report generation...>> "%LOG_FILE%"
call mvn allure:report >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
  echo.
  echo Failed to generate Allure report.
  endlocal
  exit /b 1
)

echo.
echo Allure report generated at:
echo   target\site\allure-maven-plugin\index.html

REM NOTE: We do not auto-run `allure:serve` from the batch file because it is a long-running
REM process and can make the script look "stuck". Run it manually when needed:
REM   mvn allure:serve

echo.
if not "!TEST_EXIT_CODE!"=="0" (
  echo ==========================================================
  echo TESTS FAILED (exit code !TEST_EXIT_CODE!) - report generated above
  echo ==========================================================
) else (
  echo ==========================================================
  echo TESTS PASSED - report generated above
  echo ==========================================================
)

popd
endlocal
exit /b !TEST_EXIT_CODE!
