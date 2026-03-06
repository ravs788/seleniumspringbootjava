# Manual Test Cases (Comprehensive) — DemoBlaze

Target application: https://www.demoblaze.com

This document captures a **comprehensive** set of manual test cases for DemoBlaze with priorities, automation candidacy, and **test-data references** (where applicable).

---

## Priority definitions

- **P0 (Critical / Smoke)**: must-pass checks; basic navigation + core purchase/cart flow.
- **P1 (High)**: key functional scenarios, validations, and common user flows.
- **P2 (Medium/Low)**: edge cases, less frequent flows, UI checks, resiliency, non-blocking issues.

---

## Test data references (JSON)

When a test case needs data, it references JSON test data on the test classpath:

- `src/test/resources/testdata/DemoBlazeTests/common.json`

Common keys currently used/available:

- `baseUrl`
- `purchase.phoneProductName`
- `purchase.laptopProductName`
- `purchase.orderDetails.name`
- `purchase.orderDetails.country`
- `purchase.orderDetails.city`
- `purchase.orderDetails.card`
- `purchase.orderDetails.month`
- `purchase.orderDetails.year`

---

## Assumptions / Notes

- DemoBlaze is a public demo site; availability/performance can vary.
- Some behaviors may be flaky due to external dependencies; mark such cases with notes.
- Where possible, verify using visible UI state (cart contents, confirmation modal) rather than relying only on alerts.

---

## DB-P0 (Critical / Smoke)

### DB-P0-001 – Home page loads
- **Steps**: Open home URL
- **Expected**: Page loads; product grid visible
- **Test data**: `DemoBlazeTests/common.json -> baseUrl`
- **Automation candidate**: Yes

### DB-P0-002 – Category navigation works (Phones/Laptops/Monitors/Home)
- **Steps**: Click each category, then Home
- **Expected**: Product list changes appropriately; no errors
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P0-003 – Open product details page
- **Steps**: Open a known product (e.g., “Samsung galaxy s6”)
- **Expected**: Details page shows title/price/description and Add to cart button
- **Test data**: (Optional) add a key in JSON if you want to parameterize this product too
- **Automation candidate**: Yes

### DB-P0-004 – Add product to cart from product page
- **Steps**: Click Add to cart; accept alert; open Cart
- **Expected**: Cart contains product
- **Test data**: Choose a product name (optional to parameterize)
- **Automation candidate**: Yes

### DB-P0-005 – Purchase flow with valid data
- **Preconditions**: At least 1 item in cart
- **Steps**: Place Order; fill form; Purchase
- **Expected**: Thank you/confirmation shown
- **Test data**:
  - `DemoBlazeTests/common.json -> purchase.phoneProductName`
  - `DemoBlazeTests/common.json -> purchase.laptopProductName`
  - `DemoBlazeTests/common.json -> purchase.orderDetails.*`
- **Automation candidate**: Yes

### DB-P0-006 – Cart page loads from header
- **Steps**: Click Cart link
- **Expected**: URL contains `cart`; table visible
- **Test data**: N/A
- **Automation candidate**: Yes

---

## DB-P1 (High) — Navigation & Catalog

### DB-P1-001 – Navbar Home link returns to home
- **Steps**: Navigate away; click Home
- **Expected**: Returns to home product grid
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P1-002 – Product list is clickable (open multiple products)
- **Steps**: Open 3 different products from home/category
- **Expected**: Each product page loads correctly
- **Test data**: Optional (parameterize via JSON if needed)
- **Automation candidate**: Yes

### DB-P1-003 – Category filter shows only that category’s products
- **Steps**: Select Phones; observe items; select Laptops; observe items
- **Expected**: Items reflect category selection
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P1-004 – Next/Previous pagination works
- **Steps**: Click Next; verify page changes; click Previous
- **Expected**: Different products appear; returns back on Previous
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P1-005 – Pagination retains category filter (if applicable)
- **Steps**: Select category; use Next/Previous
- **Expected**: Pagination does not reset unexpectedly
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P1-006 – Clicking site logo resets to home
- **Steps**: Navigate to product/cart; click logo
- **Expected**: Home loads
- **Test data**: N/A
- **Automation candidate**: Yes

---

## DB-P1 (High) — Cart behavior

### DB-P1-010 – Cart shows correct product name(s)
- **Preconditions**: Add 1 item to cart
- **Steps**: Open Cart
- **Expected**: Product name matches selected
- **Test data**: Optional (parameterize chosen product)
- **Automation candidate**: Yes

### DB-P1-011 – Cart delete removes item
- **Preconditions**: Add 2 items
- **Steps**: Delete one item
- **Expected**: Row removed; cart updated
- **Test data**:
  - If you want deterministic products: use `purchase.phoneProductName` and `purchase.laptopProductName`
- **Automation candidate**: Yes

### DB-P1-012 – Cart delete all items results in empty cart
- **Preconditions**: Add items
- **Steps**: Delete all rows
- **Expected**: Empty cart table state (no item rows)
- **Test data**: Optional
- **Automation candidate**: Yes

### DB-P1-013 – Cart total updates when items removed (if total shown)
- **Preconditions**: Add 2 items
- **Steps**: Note total; delete 1
- **Expected**: Total changes accordingly
- **Test data**: Optional
- **Automation candidate**: Yes

### DB-P1-014 – Cart persists after navigating away and back (same session)
- **Preconditions**: Add item
- **Steps**: Go Home; return Cart
- **Expected**: Item still present
- **Test data**: Optional
- **Automation candidate**: Yes

### DB-P1-015 – Add same product twice (quantity behavior)
- **Steps**: Add same product twice via product page
- **Expected**: Defined behavior (either two rows or one row). Record actual behavior.
- **Test data**: Optional (add a `repeatProductName` key if desired)
- **Automation candidate**: Yes (assert based on observed expected)

---

## DB-P1 (High) — Order modal validations (positive / basic negative)

### DB-P1-020 – Place Order modal opens
- **Preconditions**: Cart has at least 1 item
- **Steps**: Click Place Order
- **Expected**: Modal opens with fields and Purchase button
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P1-021 – Close Place Order modal (X button)
- **Steps**: Open modal; click X
- **Expected**: Modal closes
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P1-022 – Close Place Order modal (Close button)
- **Steps**: Open modal; click Close
- **Expected**: Modal closes
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P1-023 – Purchase with required fields only (determine required)
- **Steps**: Fill minimal fields; Purchase
- **Expected**: Purchase succeeds if site permits; otherwise validation shown. Record site rule.
- **Test data**: Use `purchase.orderDetails.*` but leave out non-required fields
- **Automation candidate**: Yes

### DB-P1-024 – Purchase with empty required fields shows validation/does not proceed
- **Steps**: Leave fields empty; Purchase
- **Expected**: Does not succeed; validation or no confirmation appears
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P1-025 – Confirmation dialog contains key text
- **Preconditions**: Successful purchase
- **Steps**: Observe confirmation
- **Expected**: Contains Thank you; includes some order info
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P1-026 – OK button on confirmation closes it
- **Preconditions**: Successful purchase
- **Steps**: Click OK
- **Expected**: Confirmation closes; returns to app
- **Test data**: N/A
- **Automation candidate**: Yes

---

## DB-P1 (High) — Alerts and JS dialogs

### DB-P1-030 – Add to cart shows alert
- **Steps**: Add to cart
- **Expected**: Alert appears; can be accepted
- **Test data**: Optional (product name)
- **Automation candidate**: Yes

### DB-P1-031 – Dismissing alert (Cancel) behavior
- **Steps**: Add to cart; dismiss alert (if cancel available)
- **Expected**: Defined behavior; record (often only OK)
- **Test data**: Optional
- **Automation candidate**: Optional

---

## DB-P2 (Medium/Low) — UI consistency & resiliency

### DB-P2-001 – Product page layout has image/title/price/description
- **Steps**: Open product page
- **Expected**: Key elements visible (not broken)
- **Test data**: Optional
- **Automation candidate**: Optional

### DB-P2-002 – Browser refresh on home keeps app usable
- **Steps**: Refresh home
- **Expected**: App still functional
- **Test data**: N/A
- **Automation candidate**: Optional

### DB-P2-003 – Browser refresh on cart keeps cart (session behavior)
- **Preconditions**: Cart has item
- **Steps**: Refresh cart
- **Expected**: Cart content persists or clears; record expected based on actual behavior
- **Test data**: Optional
- **Automation candidate**: Optional

### DB-P2-004 – Back button from product returns to listing
- **Steps**: Open product; press browser back
- **Expected**: Returns to listing
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P2-005 – Back button from cart returns to prior page
- **Steps**: Go cart; press back
- **Expected**: Returns to prior page
- **Test data**: N/A
- **Automation candidate**: Yes

### DB-P2-006 – Rapid navigation does not crash the app
- **Steps**: Quickly switch categories and open cart/home repeatedly
- **Expected**: No obvious JS errors; app remains responsive
- **Test data**: N/A
- **Automation candidate**: No (manual)

---

## DB-P2 (Medium/Low) — Data validation edge cases (manual heavy)

### DB-P2-010 – Purchase with very long name (boundary)
- **Preconditions**: Item in cart
- **Steps**: Name = 256 chars; Purchase
- **Expected**: Either validation or truncation; app does not crash
- **Test data**: Add to JSON if you want to track specific long values
- **Automation candidate**: No (optional)

### DB-P2-011 – Purchase with special characters in name/city
- **Steps**: Use `Ravi!@#$` etc.
- **Expected**: App handles input; purchase behavior defined
- **Test data**: Add to JSON if you want to track specific values
- **Automation candidate**: No (optional)

### DB-P2-012 – Purchase with invalid card format (letters)
- **Steps**: Card = `ABCD`
- **Expected**: Validation or still allowed; record actual behavior
- **Test data**: Add to JSON if you want to track invalid values
- **Automation candidate**: Optional

### DB-P2-013 – Purchase with invalid month/year values
- **Steps**: Month = `13`, Year = `abcd`
- **Expected**: Validation or acceptance; record
- **Test data**: Add to JSON if you want to track invalid values
- **Automation candidate**: Optional

---

## DB-P2 (Medium/Low) — Performance / stability observations

### DB-P2-020 – Home loads within acceptable time
- **Steps**: Load home; measure
- **Expected**: Under agreed threshold (record)
- **Test data**: N/A
- **Automation candidate**: No (manual)

### DB-P2-021 – Product page loads within acceptable time
- **Steps**: Open product; measure
- **Expected**: Under threshold
- **Test data**: Optional
- **Automation candidate**: No (manual)

---

## DB-P2 (Medium/Low) — Multi-browser sanity (manual)

### DB-P2-030 – Smoke subset works on Chrome
- **Steps**: Run DB-P0-001..DB-P0-006 on Chrome
- **Expected**: Pass
- **Test data**: `DemoBlazeTests/common.json`
- **Automation candidate**: Covered via grid multi-browser

### DB-P2-031 – Smoke subset works on Edge
- **Steps**: Run DB-P0-001..DB-P0-006 on Edge
- **Expected**: Pass
- **Test data**: `DemoBlazeTests/common.json`
- **Automation candidate**: Covered via grid multi-browser

### DB-P2-032 – Smoke subset works on Firefox
- **Steps**: Run DB-P0-001..DB-P0-006 on Firefox
- **Expected**: Pass
- **Test data**: `DemoBlazeTests/common.json`
- **Automation candidate**: Covered via grid multi-browser

---

## Coverage checklist (what this doc covers)

- Navigation: categories, pagination, back/home/cart
- Catalog: product details
- Cart: add/remove/persistence
- Order: modal open/close, purchase happy path + key negatives
- UI sanity and resiliency
- Multi-browser sanity

---

## Notes

- Keep the IDs stable so they can be referenced in automation and reporting.
- If you want, we can further parameterize product names in JSON for DB-P0-003/DB-P0-004 and DB-P1-002.
