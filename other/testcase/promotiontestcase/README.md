# Promotion & Campaign API Test Plan & Test Cases

## 1. API Overview

### Promotion APIs

**Base URL:** `/api/promotions`

**Controller:** `PromotionController`

**Endpoints:**
- `POST /api/promotions` - Tạo promotion mới
- `PUT /api/promotions/{id}` - Cập nhật promotion
- `GET /api/promotions/{id}` - Lấy promotion theo ID
- `GET /api/promotions` - Lấy tất cả promotions (paginated)
- `GET /api/promotions/campaign/{campaignId}` - Lấy promotions theo campaign
- `GET /api/promotions/product-variation/{productVariationId}` - Lấy promotions theo product variation
- `POST /api/promotions/calculate` - Tính toán discount
- `GET /api/promotions/product-variation/{productVariationId}/calculate` - Tính toán discount qua GET
- `DELETE /api/promotions/{id}` - Xóa promotion

### Campaign APIs

**Base URL:** `/api/campaigns`

**Controller:** `CampaignController`

**Endpoints:**
- `POST /api/campaigns` - Tạo campaign mới (multipart/form-data)
- `PUT /api/campaigns/{id}` - Cập nhật campaign (multipart/form-data)
- `GET /api/campaigns/{id}` - Lấy campaign theo ID
- `GET /api/campaigns` - Lấy tất cả campaigns (paginated)
- `GET /api/campaigns/active` - Lấy campaigns đang hoạt động
- `GET /api/campaigns/scheduled` - Lấy campaigns đã lên lịch
- `GET /api/campaigns/expired` - Lấy campaigns đã hết hạn
- `DELETE /api/campaigns/{id}` - Xóa campaign
- `POST /api/campaigns/{id}/restore` - Khôi phục campaign

---

## 2. Data Models

### PromotionCreateRequest
```json
{
  "campaignId": "integer", // @NotNull
  "ruleName": "string", // @NotBlank, @Size(3-255)
  "productVariationId": "integer", // @NotNull
  "discountType": "enum", // @NotNull, PERCENTAGE|FIXED_AMOUNT
  "discountValue": "decimal", // @NotNull, @DecimalMin(0.01)
  "minQuantity": "integer", // @Min(1), default=1
  "minOrderAmount": "decimal", // @DecimalMin(0.00), default=0.00
  "maxDiscountAmount": "decimal" // @DecimalMin(0.01)
}
```

### PromotionCalculationRequest
```json
{
  "productVariationId": "integer", // @NotNull
  "originalPrice": "decimal", // @NotNull, @DecimalMin(0.01)
  "quantity": "integer", // @NotNull, @Min(1)
  "orderAmount": "decimal" // @NotNull, @DecimalMin(0.00)
}
```

### CampaignCreateRequest
```json
{
  "name": "string", // @NotBlank, @Size(3-255)
  "description": "string",
  "image": "string",
  "imageID": "string",
  "startDate": "datetime", // @NotNull
  "endDate": "datetime" // @NotNull
}
```

### PromotionCalculationResponse
```json
{
  "productVariationId": "integer",
  "originalPrice": "decimal",
  "originalTotal": "decimal",
  "quantity": "integer",
  "orderAmount": "decimal",
  "totalDiscount": "decimal",
  "finalPrice": "decimal",
  "finalTotal": "decimal",
  "appliedPromotions": [
    {
      "promotionId": "integer",
      "ruleName": "string",
      "campaignName": "string",
      "discountType": "string",
      "discountAmount": "decimal",
      "discountDisplay": "string"
    }
  ]
}
```

---

## 3. Test Strategy

### 3.1 Test Categories
- **Functional Testing:** CRUD operations và business logic
- **Campaign Management:** Lifecycle management, status tracking
- **Promotion Logic:** Discount calculations, rule validation
- **File Upload Testing:** Image handling với Cloudinary
- **Date/Time Testing:** Campaign periods, expiration handling
- **Integration Testing:** Campaign-Promotion relationships
- **Calculation Engine:** Complex discount scenarios

### 3.2 Test Levels
- **Unit Test:** Controller methods và business logic
- **Integration Test:** Service + Repository + Database
- **API Test:** End-to-end HTTP requests
- **File Upload Test:** Multipart form data handling
- **Business Logic Test:** Campaign rules và promotion calculations

---

## 4. Detailed Test Cases - Promotions

### 4.1 POST /api/promotions - Tạo promotion mới

#### TC_PROMOTION_001: Tạo percentage promotion thành công
- **Điều kiện tiên quyết:** Campaign ID = 1 và ProductVariation ID = 1 tồn tại
- **Input:**
  ```json
  {
    "campaignId": 1,
    "ruleName": "Buy 2 Get 20% Off",
    "productVariationId": 1,
    "discountType": "PERCENTAGE",
    "discountValue": 20.00,
    "minQuantity": 2,
    "minOrderAmount": 100.00,
    "maxDiscountAmount": 50.00
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: PromotionResponse với thông tin đã tạo
  - Database: Promotion mới được tạo

#### TC_PROMOTION_002: Tạo fixed amount promotion thành công
- **Input:**
  ```json
  {
    "campaignId": 1,
    "ruleName": "Flat $10 Off",
    "productVariationId": 1,
    "discountType": "FIXED_AMOUNT",
    "discountValue": 10.00,
    "minQuantity": 1,
    "minOrderAmount": 50.00
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: PromotionResponse với discountType = FIXED_AMOUNT

#### TC_PROMOTION_003: Tạo promotion với campaign không tồn tại
- **Input:**
  ```json
  {
    "campaignId": 999999,
    "ruleName": "Invalid Campaign",
    "productVariationId": 1,
    "discountType": "PERCENTAGE",
    "discountValue": 10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về campaign not found

#### TC_PROMOTION_004: Tạo promotion với product variation không tồn tại
- **Input:**
  ```json
  {
    "campaignId": 1,
    "ruleName": "Invalid Product",
    "productVariationId": 999999,
    "discountType": "PERCENTAGE",
    "discountValue": 10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về product variation not found

#### TC_PROMOTION_005: Tạo promotion với rule name trống
- **Input:**
  ```json
  {
    "campaignId": 1,
    "ruleName": "",
    "productVariationId": 1,
    "discountType": "PERCENTAGE",
    "discountValue": 10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Rule name is required"

#### TC_PROMOTION_006: Tạo promotion với rule name quá ngắn
- **Input:**
  ```json
  {
    "campaignId": 1,
    "ruleName": "AB",
    "productVariationId": 1,
    "discountType": "PERCENTAGE",
    "discountValue": 10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Rule name must be between 3 and 255 characters"

#### TC_PROMOTION_007: Tạo promotion với discount value = 0
- **Input:**
  ```json
  {
    "campaignId": 1,
    "ruleName": "Zero Discount",
    "productVariationId": 1,
    "discountType": "PERCENTAGE",
    "discountValue": 0.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Discount value must be greater than 0"

#### TC_PROMOTION_008: Tạo promotion với discount value âm
- **Input:**
  ```json
  {
    "campaignId": 1,
    "ruleName": "Negative Discount",
    "productVariationId": 1,
    "discountType": "FIXED_AMOUNT",
    "discountValue": -5.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Discount value must be greater than 0"

#### TC_PROMOTION_009: Tạo promotion với minQuantity = 0
- **Input:**
  ```json
  {
    "campaignId": 1,
    "ruleName": "Zero Min Quantity",
    "productVariationId": 1,
    "discountType": "PERCENTAGE",
    "discountValue": 10.00,
    "minQuantity": 0
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Min quantity must be at least 1"

#### TC_PROMOTION_010: Tạo promotion với minOrderAmount âm
- **Input:**
  ```json
  {
    "campaignId": 1,
    "ruleName": "Negative Min Order",
    "productVariationId": 1,
    "discountType": "PERCENTAGE",
    "discountValue": 10.00,
    "minOrderAmount": -10.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Min order amount must be non-negative"

### 4.2 POST /api/promotions/calculate - Tính toán discount

#### TC_PROMOTION_011: Tính toán percentage promotion thành công
- **Điều kiện tiên quyết:** Product variation có promotion 20% off, min quantity = 2
- **Input:**
  ```json
  {
    "productVariationId": 1,
    "originalPrice": 100.00,
    "quantity": 2,
    "orderAmount": 200.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: PromotionCalculationResponse với:
    - originalPrice = 100.00
    - originalTotal = 200.00
    - totalDiscount = 40.00 (20% of 200)
    - finalPrice = 80.00
    - finalTotal = 160.00
    - appliedPromotions chứa promotion details

#### TC_PROMOTION_012: Tính toán fixed amount promotion thành công
- **Điều kiện tiên quyết:** Product variation có promotion $10 off
- **Input:**
  ```json
  {
    "productVariationId": 1,
    "originalPrice": 50.00,
    "quantity": 1,
    "orderAmount": 50.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: PromotionCalculationResponse với:
    - originalPrice = 50.00
    - originalTotal = 50.00
    - totalDiscount = 10.00
    - finalPrice = 40.00
    - finalTotal = 40.00

#### TC_PROMOTION_013: Tính toán với quantity không đủ điều kiện
- **Điều kiện tiên quyết:** Promotion yêu cầu min quantity = 3
- **Input:**
  ```json
  {
    "productVariationId": 1,
    "originalPrice": 100.00,
    "quantity": 2,
    "orderAmount": 200.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: PromotionCalculationResponse với:
    - totalDiscount = 0.00
    - finalPrice = originalPrice
    - appliedPromotions = []

#### TC_PROMOTION_014: Tính toán với order amount thấp hơn minimum
- **Điều kiện tiên quyết:** Promotion yêu cầu min order amount = 100
- **Input:**
  ```json
  {
    "productVariationId": 1,
    "originalPrice": 30.00,
    "quantity": 2,
    "orderAmount": 60.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: PromotionCalculationResponse với:
    - totalDiscount = 0.00
    - appliedPromotions = []

#### TC_PROMOTION_015: Tính toán với max discount limit
- **Điều kiện tiên quyết:** Promotion 50% off, max discount = $20
- **Input:**
  ```json
  {
    "productVariationId": 1,
    "originalPrice": 100.00,
    "quantity": 1,
    "orderAmount": 100.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: PromotionCalculationResponse với:
    - totalDiscount = 20.00 (limited by maxDiscountAmount)
    - finalPrice = 80.00

#### TC_PROMOTION_016: Tính toán multiple promotions applicable
- **Điều kiện tiên quyết:** Product variation có nhiều promotions applicable
- **Input:**
  ```json
  {
    "productVariationId": 1,
    "originalPrice": 100.00,
    "quantity": 3,
    "orderAmount": 300.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: PromotionCalculationResponse với:
    - appliedPromotions có multiple entries
    - totalDiscount = sum of all applicable discounts

#### TC_PROMOTION_017: Tính toán với product variation không có promotion
- **Input:**
  ```json
  {
    "productVariationId": 999,
    "originalPrice": 100.00,
    "quantity": 1,
    "orderAmount": 100.00
  }
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: PromotionCalculationResponse với:
    - totalDiscount = 0.00
    - appliedPromotions = []

#### TC_PROMOTION_018: Tính toán với input validation lỗi
- **Input:**
  ```json
  {
    "productVariationId": null,
    "originalPrice": -10.00,
    "quantity": 0,
    "orderAmount": -5.00
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Validation error messages

### 4.3 GET /api/promotions/product-variation/{productVariationId}/calculate

#### TC_PROMOTION_019: Tính toán via GET thành công
- **Input:** GET `/api/promotions/product-variation/1/calculate?originalPrice=100.00&quantity=2&orderAmount=200.00`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Same as POST calculate

#### TC_PROMOTION_020: Tính toán via GET với parameters thiếu
- **Input:** GET `/api/promotions/product-variation/1/calculate?originalPrice=100.00`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error về missing required parameters

#### TC_PROMOTION_021: Tính toán via GET với parameters không hợp lệ
- **Input:** GET `/api/promotions/product-variation/1/calculate?originalPrice=abc&quantity=-1&orderAmount=xyz`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Parameter validation errors

---

## 5. Detailed Test Cases - Campaigns

### 5.1 POST /api/campaigns - Tạo campaign mới

#### TC_CAMPAIGN_001: Tạo campaign thành công không có image
- **Input:** Multipart form data
  ```
  name: "Summer Sale 2025"
  description: "Big discounts for summer season"
  startDate: "2025-06-01T00:00:00"
  endDate: "2025-08-31T23:59:59"
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: CampaignResponse với thông tin đã tạo
  - Database: Campaign mới được tạo

#### TC_CAMPAIGN_002: Tạo campaign thành công có image
- **Input:** Multipart form data
  ```
  name: "Winter Sale 2025"
  description: "Cold season promotions"
  startDate: "2025-12-01T00:00:00"
  endDate: "2025-12-31T23:59:59"
  image: [valid image file]
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: CampaignResponse với image URL và imageID
  - Cloudinary: Image uploaded successfully

#### TC_CAMPAIGN_003: Tạo campaign với name trống
- **Input:**
  ```
  name: ""
  startDate: "2025-06-01T00:00:00"
  endDate: "2025-08-31T23:59:59"
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Campaign name is required"

#### TC_CAMPAIGN_004: Tạo campaign với name quá ngắn
- **Input:**
  ```
  name: "AB"
  startDate: "2025-06-01T00:00:00"
  endDate: "2025-08-31T23:59:59"
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Campaign name must be between 3 and 255 characters"

#### TC_CAMPAIGN_005: Tạo campaign với date format không hợp lệ
- **Input:**
  ```
  name: "Invalid Date Campaign"
  startDate: "invalid-date-format"
  endDate: "2025-08-31T23:59:59"
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về date parsing

#### TC_CAMPAIGN_006: Tạo campaign với endDate trước startDate
- **Input:**
  ```
  name: "Invalid Period Campaign"
  startDate: "2025-08-01T00:00:00"
  endDate: "2025-06-01T00:00:00"
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về invalid date range

#### TC_CAMPAIGN_007: Tạo campaign với image file không hợp lệ
- **Input:**
  ```
  name: "Image Test Campaign"
  startDate: "2025-06-01T00:00:00"
  endDate: "2025-08-31T23:59:59"
  image: [invalid file type, e.g., .txt]
  ```
- **Expected Output:**
  - Status Code: 500 Internal Server Error
  - Error message về image upload failure

#### TC_CAMPAIGN_008: Tạo campaign với image file quá lớn
- **Input:**
  ```
  name: "Large Image Campaign"
  startDate: "2025-06-01T00:00:00"
  endDate: "2025-08-31T23:59:59"
  image: [file > max size limit]
  ```
- **Expected Output:**
  - Status Code: 500 Internal Server Error
  - Error message về file size limit

### 5.2 PUT /api/campaigns/{id} - Cập nhật campaign

#### TC_CAMPAIGN_009: Cập nhật campaign thành công
- **Điều kiện tiên quyết:** Campaign với ID = 1 tồn tại
- **Input:** PUT `/api/campaigns/1`
  ```
  name: "Updated Summer Sale 2025"
  description: "Updated description"
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: CampaignResponse với thông tin updated
  - Database: Campaign được cập nhật

#### TC_CAMPAIGN_010: Cập nhật campaign với image mới
- **Điều kiện tiên quyết:** Campaign có image cũ
- **Input:** PUT `/api/campaigns/1`
  ```
  name: "Updated Campaign with New Image"
  image: [new image file]
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: CampaignResponse với image URL mới
  - Cloudinary: Old image deleted, new image uploaded

#### TC_CAMPAIGN_011: Cập nhật campaign không tồn tại
- **Input:** PUT `/api/campaigns/999999`
  ```
  name: "Non-existent Campaign"
  ```
- **Expected Output:**
  - Status Code: 404 Not Found
  - Error message: "Campaign not found"

#### TC_CAMPAIGN_012: Cập nhật campaign với partial data
- **Input:** PUT `/api/campaigns/1`
  ```
  description: "Only description updated"
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: CampaignResponse với chỉ description thay đổi

### 5.3 GET /api/campaigns/active - Lấy active campaigns

#### TC_CAMPAIGN_013: Lấy active campaigns thành công
- **Điều kiện tiên quyết:** Có campaigns đang active (current time trong khoảng startDate-endDate)
- **Input:** GET `/api/campaigns/active`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: List CampaignResponse với isActive = true

#### TC_CAMPAIGN_014: Lấy active campaigns khi không có active campaigns
- **Input:** GET `/api/campaigns/active`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

### 5.4 GET /api/campaigns/scheduled - Lấy scheduled campaigns

#### TC_CAMPAIGN_015: Lấy scheduled campaigns thành công
- **Điều kiện tiên quyết:** Có campaigns có startDate trong tương lai
- **Input:** GET `/api/campaigns/scheduled`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: List CampaignResponse với isScheduled = true

### 5.5 GET /api/campaigns/expired - Lấy expired campaigns

#### TC_CAMPAIGN_016: Lấy expired campaigns thành công
- **Điều kiện tiên quyết:** Có campaigns có endDate trong quá khứ
- **Input:** GET `/api/campaigns/expired`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: List CampaignResponse với isExpired = true

### 5.6 POST /api/campaigns/{id}/restore - Khôi phục campaign

#### TC_CAMPAIGN_017: Khôi phục campaign thành công
- **Điều kiện tiên quyết:** Campaign với ID = 1 đã bị soft delete
- **Input:** POST `/api/campaigns/1/restore`
- **Expected Output:**
  - Status Code: 200 OK
  - Database: Campaign restored (deletedAt = null)

#### TC_CAMPAIGN_018: Khôi phục campaign không tồn tại
- **Input:** POST `/api/campaigns/999999/restore`
- **Expected Output:**
  - Status Code: 404 Not Found

---

## 6. Integration & Business Logic Testing

### 6.1 Campaign-Promotion Relationship Tests

#### TC_INTEGRATION_001: Tạo promotion cho campaign active
- **Test:** Verify promotion có thể tạo cho active campaign
- **Expected:** Promotion tạo thành công và hoạt động

#### TC_INTEGRATION_002: Tạo promotion cho campaign expired
- **Test:** Verify promotion cho expired campaign
- **Expected:** Có thể tạo nhưng không hoạt động

#### TC_INTEGRATION_003: Xóa campaign có promotions
- **Test:** Xóa campaign khi có promotions
- **Expected:** Cascade deletion hoặc constraint error

#### TC_INTEGRATION_004: Promotion calculation cho inactive campaign
- **Test:** Calculate discount khi campaign inactive
- **Expected:** Không áp dụng discount

### 6.2 Complex Discount Calculation Tests

#### TC_CALCULATION_001: Multiple promotion stacking
- **Test:** Product có multiple promotions applicable
- **Expected:** Correct stacking logic (additive/max/etc.)

#### TC_CALCULATION_002: Percentage discount with max limit exceeded
- **Test:** 50% discount trên $1000 với max $100
- **Expected:** Discount = $100, not $500

#### TC_CALCULATION_003: Fixed amount discount > product price
- **Test:** $50 discount trên $30 product
- **Expected:** Discount = $30 (không âm)

#### TC_CALCULATION_004: Bulk quantity discount tiers
- **Test:** Quantity-based progressive discounts
- **Expected:** Correct tier calculation

### 6.3 File Upload & Cloudinary Integration

#### TC_UPLOAD_001: Upload supported image formats
- **Test:** Upload JPG, PNG, GIF files
- **Expected:** All upload successfully

#### TC_UPLOAD_002: Upload unsupported file format
- **Test:** Upload PDF, TXT files
- **Expected:** Error message về unsupported format

#### TC_UPLOAD_003: Image replacement workflow
- **Test:** Replace existing campaign image
- **Expected:** Old image deleted, new image uploaded

#### TC_UPLOAD_004: Concurrent image upload
- **Test:** Multiple simultaneous uploads
- **Expected:** All uploads handle correctly

---

## 7. Performance & Load Testing

### 7.1 Calculation Performance
- **TC_PERF_001:** Calculation với 1000+ applicable promotions
- **TC_PERF_002:** Concurrent calculation requests
- **TC_PERF_003:** Complex promotion rule evaluation

### 7.2 Campaign Management Performance
- **TC_PERF_004:** Pagination với 10,000+ campaigns
- **TC_PERF_005:** Image upload performance
- **TC_PERF_006:** Search functionality performance

---

## 8. Security Testing

### 8.1 Authorization & Access Control
- **TC_SEC_001:** Unauthorized access to admin endpoints
- **TC_SEC_002:** Role-based campaign management
- **TC_SEC_003:** Image upload security (malicious files)

### 8.2 Input Validation Security
- **TC_SEC_004:** SQL injection trong search parameters
- **TC_SEC_005:** XSS trong campaign descriptions
- **TC_SEC_006:** Path traversal trong image uploads

---

## 9. Edge Cases & Error Handling

### 9.1 Date/Time Edge Cases
- **TC_EDGE_001:** Campaign transition moments (start/end)
- **TC_EDGE_002:** Timezone handling
- **TC_EDGE_003:** Daylight saving time transitions
- **TC_EDGE_004:** Leap year handling

### 9.2 Calculation Edge Cases
- **TC_EDGE_005:** Rounding precision issues
- **TC_EDGE_006:** Currency conversion edge cases
- **TC_EDGE_007:** Extreme discount values (99.99%, $999999)
- **TC_EDGE_008:** Zero/negative prices edge cases

---

## 10. Test Data Setup

### 10.1 Campaign Test Data
```sql
-- Active campaigns
INSERT INTO campaigns (id, name, description, start_date, end_date, created_at, updated_at) VALUES
(1, 'Summer Sale 2025', 'Big summer discounts', '2025-06-01 00:00:00', '2025-08-31 23:59:59', NOW(), NOW()),
(2, 'Black Friday 2025', 'Biggest sale of the year', '2025-11-29 00:00:00', '2025-11-29 23:59:59', NOW(), NOW());

-- Scheduled campaigns
INSERT INTO campaigns (id, name, description, start_date, end_date, created_at, updated_at) VALUES
(3, 'Christmas Sale 2025', 'Holiday specials', '2025-12-20 00:00:00', '2025-12-26 23:59:59', NOW(), NOW());

-- Expired campaigns
INSERT INTO campaigns (id, name, description, start_date, end_date, created_at, updated_at) VALUES
(4, 'Spring Sale 2024', 'Past promotion', '2024-03-01 00:00:00', '2024-05-31 23:59:59', NOW(), NOW());
```

### 10.2 Promotion Test Data
```sql
-- Percentage promotions
INSERT INTO promotions (id, campaign_id, rule_name, product_variation_id, discount_type, discount_value, min_quantity, min_order_amount, max_discount_amount) VALUES
(1, 1, '20% Off Buy 2', 1, 'PERCENTAGE', 20.00, 2, 100.00, 50.00),
(2, 1, '15% Off Electronics', 2, 'PERCENTAGE', 15.00, 1, 0.00, 100.00);

-- Fixed amount promotions
INSERT INTO promotions (id, campaign_id, rule_name, product_variation_id, discount_type, discount_value, min_quantity, min_order_amount) VALUES
(3, 2, '$10 Off Any Item', 1, 'FIXED_AMOUNT', 10.00, 1, 50.00),
(4, 2, '$25 Off Premium', 3, 'FIXED_AMOUNT', 25.00, 1, 200.00);
```

---

## 11. Expected Outcomes

### 11.1 Success Criteria
- All CRUD operations work correctly for campaigns và promotions
- Campaign lifecycle management accurate (active/scheduled/expired)
- Promotion calculation engine precise và efficient
- File upload integration with Cloudinary functional
- Date/time handling correct across timezones
- Error handling comprehensive và user-friendly

### 11.2 Risk Assessment
- **High Risk:** Incorrect discount calculations, file upload vulnerabilities, campaign state inconsistencies
- **Medium Risk:** Performance degradation, timezone issues, data integrity problems
- **Low Risk:** UI display issues, minor edge cases

---

*Document Version: 1.0*  
*Last Updated: September 30, 2025*  
*Author: Test Team*