# Voucher API Test Plan & Test Cases

## 1. API Overview

**Base URL:** `/api/vouchers`

**Controller:** `VoucherController`

**Endpoints:**
- `POST /api/vouchers` - Tạo voucher mới
- `PUT /api/vouchers/{id}` - Cập nhật voucher
- `GET /api/vouchers/{id}` - Lấy voucher theo ID
- `GET /api/vouchers/code/{code}` - Lấy voucher theo code
- `GET /api/vouchers/code/exists?code=<code>` - Kiểm tra code tồn tại
- `GET /api/vouchers` - Lấy tất cả vouchers (paginated)
- `GET /api/vouchers/valid` - Lấy vouchers hợp lệ (paginated)
- `GET /api/vouchers/search?searchTerm=<term>` - Tìm kiếm vouchers
- `DELETE /api/vouchers/{id}` - Xóa voucher (soft delete)
- `POST /api/vouchers/{id}/restore` - Khôi phục voucher
- `POST /api/vouchers/validate` - Validate voucher cho order
- `POST /api/vouchers/use` - Sử dụng voucher
- `GET /api/vouchers/expired` - Lấy vouchers đã hết hạn
- `GET /api/vouchers/expiring-soon?days=<n>` - Lấy vouchers sắp hết hạn
- `GET /api/vouchers/usage/user/{userId}` - Lấy lịch sử sử dụng của user
- `GET /api/vouchers/{voucherId}/usage-count` - Đếm số lần sử dụng voucher

---

## 2. Data Models

### VoucherCreateRequest
```json
{
  "code": "string", // @NotBlank, @Size(3-50), @Pattern(A-Z0-9_-)
  "voucherType": "enum", // @NotNull, PERCENTAGE|FIXED_AMOUNT
  "value": "decimal", // @NotNull, @DecimalMin(0.01), @DecimalMax(999999.99)
  "minOrderAmount": "decimal", // @NotNull, @DecimalMin(0.00)
  "usageLimit": "integer", // @NotNull, @Min(1), @Max(1000000)
  "validFrom": "datetime", // @NotNull, @Future
  "validUntil": "datetime" // @NotNull, @Future, after validFrom
}
```

### VoucherValidationRequest
```json
{
  "code": "string", // @NotBlank
  "userId": "integer", // @NotNull
  "orderAmount": "decimal" // @NotNull
}
```

### VoucherUseRequest
```json
{
  "code": "string", // @NotBlank
  "userId": "integer", // @NotNull
  "orderId": "integer" // @NotNull
}
```

### VoucherResponse
```json
{
  "id": "integer",
  "code": "string",
  "voucherType": "enum",
  "value": "decimal",
  "minOrderAmount": "decimal",
  "usageLimit": "integer",
  "usedCount": "integer",
  "validFrom": "datetime",
  "validUntil": "datetime",
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "isActive": "boolean",
  "isValid": "boolean",
  "hasUsageLeft": "boolean",
  "displayValue": "string",
  "displayValidityPeriod": "string",
  "status": "string"
}
```

---

## 3. Test Strategy

### 3.1 Test Categories
- **Functional Testing:** CRUD operations và business logic
- **Voucher Logic Testing:** Discount calculations, expiration, usage limits
- **Validation Testing:** Input validation và business rules
- **Date/Time Testing:** Validity periods, expiration handling
- **Usage Tracking Testing:** Usage counts, user history
- **Search & Filtering:** Pagination, search functionality
- **Business Rules Testing:** Complex voucher validation scenarios

### 3.2 Test Levels
- **Unit Test:** Controller methods và business logic
- **Integration Test:** Service + Repository + Database
- **API Test:** End-to-end HTTP requests
- **Business Logic Test:** Voucher calculations và rules

---

## 4. Detailed Test Cases

### 4.1 POST /api/vouchers - Tạo voucher mới

#### TC_VOUCHER_001: Tạo percentage voucher thành công
- **Input:**
  ```json
  {
    "code": "SAVE20",
    "voucherType": "PERCENTAGE",
    "value": 20.00,
    "minOrderAmount": 100.00,
    "usageLimit": 100,
    "validFrom": "2025-10-01T00:00:00",
    "validUntil": "2025-12-31T23:59:59"
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: VoucherResponse với thông tin đã tạo
  - Database: Voucher mới được tạo

#### TC_VOUCHER_002: Tạo fixed amount voucher thành công
- **Input:**
  ```json
  {
    "code": "FLAT50",
    "voucherType": "FIXED_AMOUNT",
    "value": 50.00,
    "minOrderAmount": 200.00,
    "usageLimit": 50,
    "validFrom": "2025-10-01T00:00:00",
    "validUntil": "2025-11-30T23:59:59"
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: VoucherResponse với voucherType = FIXED_AMOUNT

#### TC_VOUCHER_003: Tạo voucher với code trống
- **Input:**
  ```json
  {
    "code": "",
    "voucherType": "PERCENTAGE",
    "value": 10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Voucher code is required"

#### TC_VOUCHER_004: Tạo voucher với code quá ngắn
- **Input:**
  ```json
  {
    "code": "AB",
    "voucherType": "PERCENTAGE",
    "value": 10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Voucher code must be between 3 and 50 characters"

#### TC_VOUCHER_005: Tạo voucher với code quá dài
- **Input:**
  ```json
  {
    "code": "VERY_LONG_VOUCHER_CODE_THAT_EXCEEDS_FIFTY_CHARACTERS_LIMIT_TEST",
    "voucherType": "PERCENTAGE",
    "value": 10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Voucher code must be between 3 and 50 characters"

#### TC_VOUCHER_006: Tạo voucher với code có ký tự không hợp lệ
- **Input:**
  ```json
  {
    "code": "save@20%",
    "voucherType": "PERCENTAGE",
    "value": 20.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Voucher code can only contain uppercase letters, numbers, underscores and hyphens"

#### TC_VOUCHER_007: Tạo voucher với code trùng lặp
- **Điều kiện tiên quyết:** Voucher với code "DUPLICATE" đã tồn tại
- **Input:**
  ```json
  {
    "code": "DUPLICATE",
    "voucherType": "PERCENTAGE",
    "value": 15.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về duplicate code

#### TC_VOUCHER_008: Tạo voucher thiếu voucherType
- **Input:**
  ```json
  {
    "code": "TEST001",
    "value": 20.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Voucher type is required"

#### TC_VOUCHER_009: Tạo voucher với value = 0
- **Input:**
  ```json
  {
    "code": "ZERO",
    "voucherType": "PERCENTAGE",
    "value": 0.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Value must be greater than 0"

#### TC_VOUCHER_010: Tạo voucher với value âm
- **Input:**
  ```json
  {
    "code": "NEGATIVE",
    "voucherType": "FIXED_AMOUNT",
    "value": -10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Value must be greater than 0"

#### TC_VOUCHER_011: Tạo percentage voucher với value > 100
- **Input:**
  ```json
  {
    "code": "OVER100",
    "voucherType": "PERCENTAGE",
    "value": 150.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "For percentage vouchers, value must be between 1 and 100"

#### TC_VOUCHER_012: Tạo voucher với minOrderAmount âm
- **Input:**
  ```json
  {
    "code": "NEGATIVE_MIN",
    "voucherType": "PERCENTAGE",
    "value": 10.00,
    "minOrderAmount": -50.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Minimum order amount must be non-negative"

#### TC_VOUCHER_013: Tạo voucher với usageLimit = 0
- **Input:**
  ```json
  {
    "code": "ZERO_USAGE",
    "voucherType": "PERCENTAGE",
    "value": 10.00,
    "usageLimit": 0
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Usage limit must be at least 1"

#### TC_VOUCHER_014: Tạo voucher với validFrom trong quá khứ
- **Input:**
  ```json
  {
    "code": "PAST_DATE",
    "voucherType": "PERCENTAGE",
    "value": 10.00,
    "validFrom": "2024-01-01T00:00:00"
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Valid from date must be in the future"

#### TC_VOUCHER_015: Tạo voucher với validUntil trước validFrom
- **Input:**
  ```json
  {
    "code": "INVALID_PERIOD",
    "voucherType": "PERCENTAGE",
    "value": 10.00,
    "validFrom": "2025-12-01T00:00:00",
    "validUntil": "2025-11-01T00:00:00"
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Valid until must be after valid from"

### 4.2 GET /api/vouchers/{id} - Lấy voucher theo ID

#### TC_VOUCHER_016: Lấy voucher theo ID hợp lệ
- **Điều kiện tiên quyết:** Voucher với ID = 1 tồn tại
- **Input:** GET `/api/vouchers/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: VoucherResponse với ID = 1
  - Verify calculated fields: isValid, hasUsageLeft, status

#### TC_VOUCHER_017: Lấy voucher với ID không tồn tại
- **Input:** GET `/api/vouchers/999999`
- **Expected Output:**
  - Status Code: 404 Not Found

#### TC_VOUCHER_018: Lấy voucher với ID không hợp lệ
- **Input:** GET `/api/vouchers/abc`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.3 GET /api/vouchers/code/{code} - Lấy voucher theo code

#### TC_VOUCHER_019: Lấy voucher theo code hợp lệ
- **Điều kiện tiên quyết:** Voucher với code "SAVE20" tồn tại
- **Input:** GET `/api/vouchers/code/SAVE20`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: VoucherResponse với code = "SAVE20"

#### TC_VOUCHER_020: Lấy voucher theo code không tồn tại
- **Input:** GET `/api/vouchers/code/NONEXISTENT`
- **Expected Output:**
  - Status Code: 404 Not Found

#### TC_VOUCHER_021: Lấy voucher theo code case sensitivity
- **Điều kiện tiên quyết:** Voucher với code "SAVE20" tồn tại
- **Input:** GET `/api/vouchers/code/save20`
- **Expected Output:**
  - Status Code: 404 Not Found (nếu case sensitive)

### 4.4 GET /api/vouchers/code/exists - Kiểm tra code tồn tại

#### TC_VOUCHER_022: Kiểm tra code tồn tại
- **Điều kiện tiên quyết:** Voucher với code "SAVE20" tồn tại
- **Input:** GET `/api/vouchers/code/exists?code=SAVE20`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `true`

#### TC_VOUCHER_023: Kiểm tra code không tồn tại
- **Input:** GET `/api/vouchers/code/exists?code=NONEXISTENT`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_VOUCHER_024: Kiểm tra code rỗng
- **Input:** GET `/api/vouchers/code/exists?code=`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_VOUCHER_025: Kiểm tra không có parameter code
- **Input:** GET `/api/vouchers/code/exists`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.5 GET /api/vouchers - Lấy tất cả vouchers (paginated)

#### TC_VOUCHER_026: Lấy vouchers với pagination mặc định
- **Input:** GET `/api/vouchers`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Page<VoucherResponse> với default pagination
  - Verify page=0, size=10, sortBy=createdAt, sortDir=DESC

#### TC_VOUCHER_027: Lấy vouchers với custom pagination
- **Input:** GET `/api/vouchers?page=1&size=5&sortBy=code&sortDir=ASC`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Page với page=1, size=5, sorted by code ASC

#### TC_VOUCHER_028: Lấy vouchers với page vượt quá giới hạn
- **Input:** GET `/api/vouchers?page=999&size=10`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Empty page với page=999

#### TC_VOUCHER_029: Lấy vouchers với size không hợp lệ
- **Input:** GET `/api/vouchers?page=0&size=-1`
- **Expected Output:**
  - Status Code: 400 Bad Request (hoặc default size)

### 4.6 GET /api/vouchers/valid - Lấy vouchers hợp lệ

#### TC_VOUCHER_030: Lấy valid vouchers
- **Điều kiện tiên quyết:** Có vouchers hợp lệ (chưa hết hạn, còn usage)
- **Input:** GET `/api/vouchers/valid`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Page chỉ chứa vouchers hợp lệ
  - Verify tất cả vouchers có isValid = true

#### TC_VOUCHER_031: Lấy valid vouchers khi không có voucher hợp lệ
- **Điều kiện tiên quyết:** Tất cả vouchers đã hết hạn hoặc hết usage
- **Input:** GET `/api/vouchers/valid`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Empty page

### 4.7 GET /api/vouchers/search - Tìm kiếm vouchers

#### TC_VOUCHER_032: Tìm kiếm vouchers có kết quả
- **Điều kiện tiên quyết:** Có vouchers với codes chứa "SAVE"
- **Input:** GET `/api/vouchers/search?searchTerm=SAVE`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Page chứa vouchers có code match "SAVE"

#### TC_VOUCHER_033: Tìm kiếm vouchers không có kết quả
- **Input:** GET `/api/vouchers/search?searchTerm=NONEXISTENT`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Empty page

#### TC_VOUCHER_034: Tìm kiếm với searchTerm rỗng
- **Input:** GET `/api/vouchers/search?searchTerm=`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Empty page hoặc all vouchers

#### TC_VOUCHER_035: Tìm kiếm không có parameter
- **Input:** GET `/api/vouchers/search`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.8 POST /api/vouchers/validate - Validate voucher

#### TC_VOUCHER_036: Validate voucher hợp lệ - percentage
- **Điều kiện tiên quyết:** Voucher "SAVE20" (20%, min 100) tồn tại và hợp lệ
- **Input:**
  ```json
  {
    "code": "SAVE20",
    "userId": 1,
    "orderAmount": 150.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: VoucherValidationResponse với:
    - isValid = true
    - discountAmount = 30.00 (20% of 150)
    - finalAmount = 120.00

#### TC_VOUCHER_037: Validate voucher hợp lệ - fixed amount
- **Điều kiện tiên quyết:** Voucher "FLAT50" (50 fixed, min 200) tồn tại
- **Input:**
  ```json
  {
    "code": "FLAT50",
    "userId": 1,
    "orderAmount": 250.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: VoucherValidationResponse với:
    - isValid = true
    - discountAmount = 50.00
    - finalAmount = 200.00

#### TC_VOUCHER_038: Validate voucher với order amount thấp hơn minimum
- **Input:**
  ```json
  {
    "code": "SAVE20",
    "userId": 1,
    "orderAmount": 50.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: VoucherValidationResponse với:
    - isValid = false
    - errorMessage = "Order amount below minimum required"

#### TC_VOUCHER_039: Validate voucher không tồn tại
- **Input:**
  ```json
  {
    "code": "NONEXISTENT",
    "userId": 1,
    "orderAmount": 100.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: VoucherValidationResponse với:
    - isValid = false
    - errorMessage = "Voucher not found"

#### TC_VOUCHER_040: Validate voucher đã hết hạn
- **Điều kiện tiên quyết:** Voucher "EXPIRED" đã hết hạn
- **Input:**
  ```json
  {
    "code": "EXPIRED",
    "userId": 1,
    "orderAmount": 100.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: VoucherValidationResponse với:
    - isValid = false
    - errorMessage = "Voucher has expired"

#### TC_VOUCHER_041: Validate voucher đã hết usage limit
- **Điều kiện tiên quyết:** Voucher "MAXUSED" đã đạt usage limit
- **Input:**
  ```json
  {
    "code": "MAXUSED",
    "userId": 1,
    "orderAmount": 100.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: VoucherValidationResponse với:
    - isValid = false
    - errorMessage = "Voucher usage limit reached"

#### TC_VOUCHER_042: Validate voucher chưa đến thời gian hiệu lực
- **Điều kiện tiên quyết:** Voucher "FUTURE" có validFrom trong tương lai
- **Input:**
  ```json
  {
    "code": "FUTURE",
    "userId": 1,
    "orderAmount": 100.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: VoucherValidationResponse với:
    - isValid = false
    - errorMessage = "Voucher not yet valid"

#### TC_VOUCHER_043: Validate với input validation lỗi
- **Input:**
  ```json
  {
    "code": "",
    "userId": null,
    "orderAmount": -10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Validation error messages

### 4.9 POST /api/vouchers/use - Sử dụng voucher

#### TC_VOUCHER_044: Sử dụng voucher thành công
- **Điều kiện tiên quyết:** Voucher "SAVE20" hợp lệ, chưa đạt usage limit
- **Input:**
  ```json
  {
    "code": "SAVE20",
    "userId": 1,
    "orderId": 100
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: "Voucher used successfully"
  - Database: usedCount tăng lên, UserVoucher record được tạo

#### TC_VOUCHER_045: Sử dụng voucher không tồn tại
- **Input:**
  ```json
  {
    "code": "NONEXISTENT",
    "userId": 1,
    "orderId": 100
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Voucher not found"

#### TC_VOUCHER_046: Sử dụng voucher đã hết hạn
- **Input:**
  ```json
  {
    "code": "EXPIRED",
    "userId": 1,
    "orderId": 100
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Voucher has expired"

#### TC_VOUCHER_047: Sử dụng voucher đã hết usage limit
- **Input:**
  ```json
  {
    "code": "MAXUSED",
    "userId": 1,
    "orderId": 100
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Voucher usage limit reached"

#### TC_VOUCHER_048: Sử dụng voucher với orderId đã tồn tại
- **Điều kiện tiên quyết:** Order 100 đã sử dụng voucher
- **Input:**
  ```json
  {
    "code": "SAVE20",
    "userId": 1,
    "orderId": 100
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Voucher already used for this order"

### 4.10 GET /api/vouchers/expired - Lấy vouchers hết hạn

#### TC_VOUCHER_049: Lấy expired vouchers
- **Điều kiện tiên quyết:** Có vouchers đã hết hạn
- **Input:** GET `/api/vouchers/expired`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: List vouchers có validUntil < current time

#### TC_VOUCHER_050: Lấy expired vouchers khi không có
- **Input:** GET `/api/vouchers/expired`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

### 4.11 GET /api/vouchers/expiring-soon - Lấy vouchers sắp hết hạn

#### TC_VOUCHER_051: Lấy vouchers sắp hết hạn với days mặc định
- **Điều kiện tiên quyết:** Có vouchers hết hạn trong 7 ngày tới
- **Input:** GET `/api/vouchers/expiring-soon`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: List vouchers hết hạn trong 7 ngày

#### TC_VOUCHER_052: Lấy vouchers sắp hết hạn với days tùy chỉnh
- **Input:** GET `/api/vouchers/expiring-soon?days=3`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: List vouchers hết hạn trong 3 ngày

#### TC_VOUCHER_053: Lấy vouchers sắp hết hạn với days = 0
- **Input:** GET `/api/vouchers/expiring-soon?days=0`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` hoặc vouchers hết hạn hôm nay

### 4.12 GET /api/vouchers/usage/user/{userId} - Lịch sử sử dụng của user

#### TC_VOUCHER_054: Lấy usage history của user có sử dụng vouchers
- **Điều kiện tiên quyết:** User 1 đã sử dụng vouchers
- **Input:** GET `/api/vouchers/usage/user/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: List UserVoucher records của user 1
  - Verify chứa orderId, usedAt, voucher details

#### TC_VOUCHER_055: Lấy usage history của user chưa sử dụng voucher
- **Input:** GET `/api/vouchers/usage/user/999`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

#### TC_VOUCHER_056: Lấy usage history với userId không hợp lệ
- **Input:** GET `/api/vouchers/usage/user/abc`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.13 GET /api/vouchers/{voucherId}/usage-count - Đếm usage

#### TC_VOUCHER_057: Đếm usage của voucher có sử dụng
- **Điều kiện tiên quyết:** Voucher ID = 1 đã được sử dụng 5 lần
- **Input:** GET `/api/vouchers/1/usage-count`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `5`

#### TC_VOUCHER_058: Đếm usage của voucher chưa sử dụng
- **Input:** GET `/api/vouchers/1/usage-count`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `0`

#### TC_VOUCHER_059: Đếm usage của voucher không tồn tại
- **Input:** GET `/api/vouchers/999999/usage-count`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `0` (hoặc 404 tùy implementation)

---

## 5. Business Logic Testing

### 5.1 Discount Calculation Logic
- **TC_VOUCHER_060:** Percentage discount với order amount lớn
- **TC_VOUCHER_061:** Fixed amount discount lớn hơn order amount
- **TC_VOUCHER_062:** Precision handling cho decimal calculations
- **TC_VOUCHER_063:** Multiple vouchers validation (business rule)

### 5.2 Date/Time Logic
- **TC_VOUCHER_064:** Timezone handling cho validFrom/validUntil
- **TC_VOUCHER_065:** Edge case: validation đúng lúc expire
- **TC_VOUCHER_066:** Daylight saving time transitions
- **TC_VOUCHER_067:** Different date formats input

### 5.3 Usage Tracking Logic
- **TC_VOUCHER_068:** Concurrent usage attempts (race conditions)
- **TC_VOUCHER_069:** Usage rollback khi order fails
- **TC_VOUCHER_070:** Bulk usage tracking
- **TC_VOUCHER_071:** Usage analytics accuracy

---

## 6. Edge Cases & Special Scenarios

### 6.1 Performance Testing
- **TC_VOUCHER_072:** Load test với 10,000+ vouchers
- **TC_VOUCHER_073:** Concurrent validation requests
- **TC_VOUCHER_074:** Large pagination requests
- **TC_VOUCHER_075:** Complex search queries performance

### 6.2 Security Testing
- **TC_VOUCHER_076:** SQL injection trong search terms
- **TC_VOUCHER_077:** Voucher code brute force attempts
- **TC_VOUCHER_078:** Authorization bypassing
- **TC_VOUCHER_079:** Rate limiting cho validation requests

### 6.3 Data Integrity
- **TC_VOUCHER_080:** Database constraints enforcement
- **TC_VOUCHER_081:** Transaction rollback scenarios
- **TC_VOUCHER_082:** Data corruption recovery
- **TC_VOUCHER_083:** Audit trail accuracy

---

## 7. Test Data Setup

### 7.1 Initial Data
```sql
-- Valid vouchers
INSERT INTO vouchers (id, code, voucher_type, value, min_order_amount, usage_limit, valid_from, valid_until, created_at, updated_at) VALUES
(1, 'SAVE20', 'PERCENTAGE', 20.00, 100.00, 100, '2025-10-01 00:00:00', '2025-12-31 23:59:59', NOW(), NOW()),
(2, 'FLAT50', 'FIXED_AMOUNT', 50.00, 200.00, 50, '2025-10-01 00:00:00', '2025-11-30 23:59:59', NOW(), NOW()),
(3, 'WELCOME10', 'PERCENTAGE', 10.00, 0.00, 1000, '2025-10-01 00:00:00', '2025-12-31 23:59:59', NOW(), NOW());

-- Expired voucher
INSERT INTO vouchers (id, code, voucher_type, value, min_order_amount, usage_limit, valid_from, valid_until, created_at, updated_at) VALUES
(4, 'EXPIRED', 'PERCENTAGE', 15.00, 50.00, 100, '2024-01-01 00:00:00', '2024-12-31 23:59:59', NOW(), NOW());

-- Maxed usage voucher
INSERT INTO vouchers (id, code, voucher_type, value, min_order_amount, usage_limit, used_count, valid_from, valid_until, created_at, updated_at) VALUES
(5, 'MAXUSED', 'PERCENTAGE', 25.00, 100.00, 10, 10, '2025-10-01 00:00:00', '2025-12-31 23:59:59', NOW(), NOW());

-- Future voucher
INSERT INTO vouchers (id, code, voucher_type, value, min_order_amount, usage_limit, valid_from, valid_until, created_at, updated_at) VALUES
(6, 'FUTURE', 'PERCENTAGE', 30.00, 150.00, 50, '2026-01-01 00:00:00', '2026-12-31 23:59:59', NOW(), NOW());

-- Usage history
INSERT INTO user_vouchers (user_id, voucher_id, order_id, used_at) VALUES
(1, 1, 100, '2025-10-15 10:30:00'),
(1, 2, 101, '2025-10-16 14:45:00'),
(2, 1, 102, '2025-10-17 09:15:00');
```

### 7.2 Cleanup Data
```sql
DELETE FROM user_vouchers WHERE voucher_id IN (SELECT id FROM vouchers WHERE code LIKE 'TEST%');
DELETE FROM vouchers WHERE code LIKE 'TEST%';
```

---

## 8. Expected Outcomes

### 8.1 Success Criteria
- All CRUD operations work correctly
- Voucher validation logic accurate
- Discount calculations precise
- Usage tracking consistent
- Date/time handling correct
- Search và pagination functional
- Error handling comprehensive

### 8.2 Risk Assessment
- **High Risk:** Incorrect discount calculations, usage tracking failures, security vulnerabilities
- **Medium Risk:** Performance issues, validation bypass, data inconsistency
- **Low Risk:** UI/UX issues, minor edge cases

---

*Document Version: 1.0*  
*Last Updated: September 30, 2025*  
*Author: Test Team*