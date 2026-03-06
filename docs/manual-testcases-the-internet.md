# Manual Test Cases (Comprehensive) — The Internet (Herokuapp)

Target application: https://the-internet.herokuapp.com/

This document captures a **comprehensive** set of manual test cases for The Internet app with priorities, automation candidacy, and **test-data references** (where applicable).

---

## Priority definitions

- **P0 (Critical / Smoke)**: must-pass checks; core navigation + critical widgets/pages.
- **P1 (High)**: key functional scenarios, validations, and common flows.
- **P2 (Medium/Low)**: edge cases, less frequent flows, UI checks, resiliency, non-blocking issues.

---

## Test data references (JSON)

Test data is stored under:

- `src/test/resources/testdata/TestInternetHeroku/common.json`

Common keys currently used/available:
- `baseUrl`

Notes:
- Most test cases use fixed UI values (Option 1/2, admin/admin) and do not require external data files.
- If you want to parameterize values (e.g., dropdown choice, credentials), we can add keys to JSON and reference them here.

---

## Assumptions / Notes

- This is a public demo site; availability/performance can vary.
- Basic Auth behavior may differ across browsers due to native auth prompts. A stable method is using embedded credentials in the URL.

---

## TI-P0 (Critical / Smoke)

### TI-P0-001 – Home page loads
- **Steps**: Open base URL
- **Expected**: Home page loads; list of example links visible
- **Test data**: `TestInternetHeroku/common.json -> baseUrl`
- **Automation candidate**: Yes

### TI-P0-002 – Navigate to Add/Remove Elements page
- **Steps**: Click “Add/Remove Elements”
- **Expected**: Page loads; “Add Element” button visible
- **Test data**: N/A
- **Automation candidate**: Yes

### TI-P0-003 – Add elements increases delete buttons count
- **Steps**: Click “Add Element” 2 times
- **Expected**: 2 “Delete” buttons appear
- **Test data**: N/A
- **Automation candidate**: Yes

### TI-P0-004 – Delete element decreases count
- **Preconditions**: At least 1 “Delete” exists
- **Steps**: Click one “Delete”
- **Expected**: Count decreases by 1
- **Test data**: N/A
- **Automation candidate**: Yes

### TI-P0-005 – Dropdown page loads
- **Steps**: Navigate to “Dropdown”
- **Expected**: Dropdown control is visible
- **Test data**: N/A
- **Automation candidate**: Yes

### TI-P0-006 – Dropdown select Option 1
- **Steps**: Select “Option 1”
- **Expected**: Selected value becomes “Option 1”
- **Test data**: N/A (parameterize if desired)
- **Automation candidate**: Yes

### TI-P0-007 – Basic Auth valid credentials
- **Steps**: Open Basic Auth with valid creds
- **Expected**: Success message includes “Congratulations!”
- **Test data**: N/A (parameterize creds if desired)
- **Automation candidate**: Yes

---

## TI-P1 (High) — Add/Remove Elements

### TI-P1-001 – Delete all elements leaves zero
- **Preconditions**: Multiple delete buttons exist
- **Steps**: Delete all
- **Expected**: 0 delete buttons remain; no errors
- **Test data**: N/A
- **Automation candidate**: Yes

### TI-P1-002 – Add 0 elements (do nothing) is stable
- **Steps**: Open page and do not click Add
- **Expected**: 0 delete buttons
- **Test data**: N/A
- **Automation candidate**: Yes

### TI-P1-003 – Add many elements (stress small)
- **Steps**: Click Add Element 10 times
- **Expected**: 10 delete buttons visible
- **Test data**: N/A
- **Automation candidate**: Optional

### TI-P1-004 – Delete after many elements remains stable
- **Preconditions**: 10 delete buttons exist
- **Steps**: Delete all
- **Expected**: Returns to 0
- **Test data**: N/A
- **Automation candidate**: Optional

---

## TI-P1 (High) — Dropdown

### TI-P1-010 – Select Option 2
- **Steps**: Select “Option 2”
- **Expected**: Selection becomes “Option 2”
- **Test data**: N/A
- **Automation candidate**: Yes

### TI-P1-011 – Switch selections Option 1 -> Option 2
- **Steps**: Select Option 1 then Option 2
- **Expected**: Selection updates correctly
- **Test data**: N/A
- **Automation candidate**: Yes

### TI-P1-012 – Switch selections Option 2 -> Option 1
- **Steps**: Select Option 2 then Option 1
- **Expected**: Selection updates correctly
- **Test data**: N/A
- **Automation candidate**: Yes

---

## TI-P1 (High) — Basic Auth

### TI-P1-020 – Valid credentials show expected exact message
- **Steps**: Authenticate with admin/admin
- **Expected**: Message exactly matches expected (or contains a stable substring)
- **Test data**: N/A
- **Automation candidate**: Yes

### TI-P1-021 – Invalid credentials are rejected
- **Steps**: Use invalid creds (e.g., admin/wrong)
- **Expected**: 401/unauthorized behavior; page does not show congratulations message
- **Test data**: Optional to add creds to JSON
- **Automation candidate**: Optional (browser-dependent)

### TI-P1-022 – Empty credentials are rejected
- **Steps**: Use empty creds
- **Expected**: Unauthorized
- **Test data**: N/A
- **Automation candidate**: Optional

---

## TI-P2 (Medium/Low) — Navigation & UI checks

### TI-P2-001 – Browser back returns to home
- **Steps**: Open any module; press back
- **Expected**: Returns to home list
- **Test data**: N/A
- **Automation candidate**: Optional

### TI-P2-002 – Refresh on module page remains usable
- **Steps**: Open module; refresh
- **Expected**: Page still functional
- **Test data**: N/A
- **Automation candidate**: Optional

### TI-P2-003 – Home page has consistent title/header
- **Steps**: Open home
- **Expected**: Heading “Welcome to the-internet” visible
- **Test data**: N/A
- **Automation candidate**: Optional

---

## TI-P2 (Medium/Low) — Multi-browser sanity (manual)

### TI-P2-010 – Smoke subset works on Chrome
- **Steps**: Run TI-P0-001..TI-P0-006 on Chrome
- **Expected**: Pass
- **Test data**: `TestInternetHeroku/common.json`
- **Automation candidate**: Covered via grid multi-browser

### TI-P2-011 – Smoke subset works on Edge
- **Steps**: Run TI-P0-001..TI-P0-006 on Edge
- **Expected**: Pass
- **Test data**: `TestInternetHeroku/common.json`
- **Automation candidate**: Covered via grid multi-browser

### TI-P2-012 – Smoke subset works on Firefox
- **Steps**: Run TI-P0-001..TI-P0-006 on Firefox
- **Expected**: Pass
- **Test data**: `TestInternetHeroku/common.json`
- **Automation candidate**: Covered via grid multi-browser

---

## Notes

- Keep IDs stable so they can be referenced in automation and reporting.
- If you want to expand coverage beyond current automated modules, suggest the next modules to prioritize (e.g., Form Authentication, File Upload, Frames, Alerts).
