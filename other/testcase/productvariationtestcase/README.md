# Product Variation API Test Plan & Test Cases

## 1. API Overview

**Base URL:** `/api/product-variations`

**Controller:** `ProductVariationController`

**Endpoints:**
- `GET /api/product-variations` - Lấy tất cả product variations (có/không includeDeleted)
- `GET /api/product-variations/{id}` - Lấy product variation theo ID
- `POST /api/product-variations` - Tạo product variation mới (multipart/form-data)
- `PUT /api/product-variations/{id}` - Cập nhật product variation (multipart/form-data)
- `DELETE /api/product-variations/{id}` - Xóa product variation (soft delete)
- `PATCH /api/product-variations/{id}/restore` - Khôi phục product variation
- `GET /api/product-variations/product/{productId}` - Lấy variations theo productId
- `GET /api/product-variations/in-stock` - Lấy variations còn hàng
- `GET /api/product-variations/low-stock?threshold=<n>` - Lấy variations sắp hết hàng
- `GET /api/product-variations/sku/{sku}` - Lấy variation theo SKU
- `PATCH /api/product-variations/{id}/stock?quantity=<n>` - Cập nhật tồn kho
- `GET /api/product-variations/exists?sku=<sku>` - Kiểm tra SKU tồn tại

---

## 2. Data Models

### ProductVariationCreateRequest
```json
{
  "variationName": "string", // @Size(max=255), optional
  "productId": "integer", // @NotNull
  "price": "decimal", // @NotNull, @DecimalMin(0.0, exclusive)
  "sku": "string", // @Size(max=255), optional
  "imageUrls": ["string"], // List, optional
  "imagePublicIds": ["string"], // List, optional
  "quantity": "integer" // @NotNull, @Min(0)
}
```

### ProductVariationUpdateRequest
```json
{
  "variationName": "string", // @Size(max=255), optional
  "price": "decimal", // @DecimalMin(0.0, exclusive), optional
  "sku": "string", // @Size(max=255), optional
  "imageUrls": ["string"], // List of new images, optional
  "imagePublicIds": ["string"], // List of new image public IDs, optional
  "deleteImageIds": ["string"], // List of image IDs to delete, optional
  "quantity": "integer" // @Min(0), optional
}
```

### ProductVariationResponse
```json
{
  "id": "integer",
  "variationName": "string",
  "productId": "integer",
  "price": "decimal",
  "sku": "string",
  "imageUrls": ["string"],
  "quantity": "integer",
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "deletedAt": "datetime",
  "productName": "string",
  "isDeleted": "boolean",
  "isInStock": "boolean"
}
```

---

## 3. Test Strategy

### 3.1 Test Categories
- **Functional Testing:** CRUD operations cho product variations
- **Stock Management Testing:** Quantity tracking, in-stock, low-stock operations
- **SKU Management Testing:** Unique SKU constraints và validation
- **Multi-Image Testing:** Multiple image upload/delete operations
- **Soft Delete Testing:** Delete và restore operations
- **Validation Testing:** Input validation và business rules
- **Integration Testing:** Product relationship, Cloudinary integration

### 3.2 Test Levels
- **Unit Test:** Controller methods testing
- **Integration Test:** Service + Repository + Database + Cloudinary
- **API Test:** End-to-end HTTP requests
- **Business Logic Test:** Stock calculations, SKU uniqueness

---

## 4. Detailed Test Cases

### 4.1 GET /api/product-variations - Lấy tất cả variations

#### TC_VARIATION_001: Lấy variations không bao gồm deleted
- **Input:** GET `/api/product-variations` hoặc `?includeDeleted=false`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array variations có deletedAt = null
  - Verify productName được populate
  - Verify isInStock và isDeleted flags

#### TC_VARIATION_002: Lấy variations bao gồm deleted
- **Input:** GET `/api/product-variations?includeDeleted=true`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array tất cả variations kể cả deleted

#### TC_VARIATION_003: Lấy variations khi không có dữ liệu
- **Input:** GET `/api/product-variations`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

### 4.2 GET /api/product-variations/{id} - Lấy variation theo ID

#### TC_VARIATION_004: Lấy variation active theo ID
- **Input:** GET `/api/product-variations/1?includeDeleted=false`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductVariationResponse với ID = 1

#### TC_VARIATION_005: Lấy variation deleted theo ID với includeDeleted=true
- **Input:** GET `/api/product-variations/1?includeDeleted=true`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductVariationResponse kể cả deleted

#### TC_VARIATION_006: Lấy variation không tồn tại
- **Input:** GET `/api/product-variations/999999`
- **Expected Output:**
  - Status Code: 404 Not Found

#### TC_VARIATION_007: ID không hợp lệ
- **Input:** GET `/api/product-variations/abc`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.3 POST /api/product-variations - Tạo variation (multipart/form-data)

#### TC_VARIATION_008: Tạo variation thành công với multiple images
- **Input:**
  - Content-Type: multipart/form-data
  ```
  variationName: "iPhone 15 Pro 256GB Space Black"
  productId: 1
  price: 999.99
  sku: "IPH15PRO256SB"
  quantity: 50
  images: [image1.jpg, image2.jpg, image3.jpg]
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: ProductVariationResponse với imageUrls array
  - Database: Variation mới được tạo
  - Cloudinary: Multiple images uploaded

#### TC_VARIATION_009: Tạo variation không có ảnh
- **Input:**
  ```
  variationName: "Basic Variation"
  productId: 1
  price: 99.99
  quantity: 10
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: ProductVariationResponse với imageUrls = []

#### TC_VARIATION_010: Tạo variation thiếu productId
- **Input:**
  ```
  price: 99.99
  quantity: 10
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Product ID is required"

#### TC_VARIATION_011: Tạo variation thiếu price
- **Input:**
  ```
  productId: 1
  quantity: 10
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Price is required"

#### TC_VARIATION_012: Tạo variation thiếu quantity
- **Input:**
  ```
  productId: 1
  price: 99.99
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Quantity is required"

#### TC_VARIATION_013: Tạo variation với price = 0
- **Input:**
  ```
  productId: 1
  price: 0.0
  quantity: 10
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Price must be greater than 0"

#### TC_VARIATION_014: Tạo variation với price âm
- **Input:**
  ```
  productId: 1
  price: -10.5
  quantity: 10
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Price must be greater than 0"

#### TC_VARIATION_015: Tạo variation với quantity âm
- **Input:**
  ```
  productId: 1
  price: 99.99
  quantity: -5
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Quantity must be non-negative"

#### TC_VARIATION_016: Tạo variation với productId không tồn tại
- **Input:**
  ```
  productId: 999999
  price: 99.99
  quantity: 10
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về invalid product

#### TC_VARIATION_017: Tạo variation với SKU trùng lặp
- **Điều kiện tiên quyết:** SKU "DUPLICATE001" đã tồn tại
- **Input:**
  ```
  productId: 1
  price: 99.99
  sku: "DUPLICATE001"
  quantity: 10
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về duplicate SKU

#### TC_VARIATION_018: Tạo variation với variationName quá dài
- **Input:**
  ```
  variationName: "A very long variation name..." (> 255 chars)
  productId: 1
  price: 99.99
  quantity: 10
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Variation name must not exceed 255 characters"

#### TC_VARIATION_019: Tạo variation với SKU quá dài
- **Input:**
  ```
  sku: "VERY_LONG_SKU_CODE..." (> 255 chars)
  productId: 1
  price: 99.99
  quantity: 10
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "SKU must not exceed 255 characters"

#### TC_VARIATION_020: Tạo variation với invalid image files
- **Input:**
  ```
  productId: 1
  price: 99.99
  quantity: 10
  images: [invalid_file.txt, document.pdf]
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về invalid file types

### 4.4 PUT /api/product-variations/{id} - Cập nhật variation

#### TC_VARIATION_021: Cập nhật variation thành công
- **Input:**
  - URL: `/api/product-variations/1`
  ```
  variationName: "Updated Variation"
  price: 149.99
  quantity: 25
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductVariationResponse với thông tin updated

#### TC_VARIATION_022: Cập nhật variation với images mới
- **Input:**
  ```
  variationName: "Updated with Images"
  newImages: [new_image1.jpg, new_image2.jpg]
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductVariationResponse với imageUrls mới
  - Cloudinary: New images uploaded

#### TC_VARIATION_023: Cập nhật variation xóa images cũ
- **Input:**
  ```
  variationName: "Updated"
  deleteImageIds: ["old_image_id1", "old_image_id2"]
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductVariationResponse với imageUrls reduced
  - Cloudinary: Specified images deleted

#### TC_VARIATION_024: Cập nhật variation thêm và xóa images
- **Input:**
  ```
  variationName: "Mixed Update"
  newImages: [new_image.jpg]
  deleteImageIds: ["old_image_id"]
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Updated imageUrls reflecting changes
  - Cloudinary: Old images deleted, new images uploaded

#### TC_VARIATION_025: Cập nhật variation không tồn tại
- **Input:** PUT `/api/product-variations/999999`
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_VARIATION_026: Cập nhật với SKU trùng lặp
- **Điều kiện tiên quyết:** 
  - Variation ID = 1 có SKU = "SKU001"
  - Variation ID = 2 có SKU = "SKU002"
- **Input:**
  - URL: `/api/product-variations/1`
  ```
  sku: "SKU002"
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về duplicate SKU

#### TC_VARIATION_027: Cập nhật với validation errors
- **Input:**
  ```
  price: -10.0
  quantity: -5
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Multiple validation error messages

### 4.5 DELETE /api/product-variations/{id} - Soft delete

#### TC_VARIATION_028: Xóa variation thành công
- **Input:** DELETE `/api/product-variations/1`
- **Expected Output:**
  - Status Code: 204 No Content
  - Database: Variation có deletedAt != null
  - Cloudinary: Images KHÔNG bị xóa (soft delete)

#### TC_VARIATION_029: Xóa variation không tồn tại
- **Input:** DELETE `/api/product-variations/999999`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.6 PATCH /api/product-variations/{id}/restore - Khôi phục variation

#### TC_VARIATION_030: Khôi phục variation thành công
- **Điều kiện tiên quyết:** Variation đã bị soft deleted
- **Input:** PATCH `/api/product-variations/1/restore`
- **Expected Output:**
  - Status Code: 200 OK
  - Database: deletedAt = null

#### TC_VARIATION_031: Khôi phục variation chưa bị xóa
- **Input:** PATCH `/api/product-variations/1/restore`
- **Expected Output:**
  - Status Code: 400 Bad Request (hoặc success tùy logic)

### 4.7 GET /api/product-variations/product/{productId} - Lấy variations theo product

#### TC_VARIATION_032: Lấy variations của product có variations
- **Điều kiện tiên quyết:** Product ID = 1 có multiple variations
- **Input:** GET `/api/product-variations/product/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array variations có productId = 1
  - Verify variations sorted by creation date or name

#### TC_VARIATION_033: Lấy variations của product không có variation
- **Input:** GET `/api/product-variations/product/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

#### TC_VARIATION_034: Lấy variations của product không tồn tại
- **Input:** GET `/api/product-variations/product/999999`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

### 4.8 GET /api/product-variations/in-stock - Lấy variations còn hàng

#### TC_VARIATION_035: Lấy tất cả variations in-stock
- **Điều kiện tiên quyết:** Có variations với quantity > 0
- **Input:** GET `/api/product-variations/in-stock`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array variations có quantity > 0
  - Verify isInStock = true

#### TC_VARIATION_036: Lấy in-stock variations theo productId
- **Input:** GET `/api/product-variations/in-stock?productId=1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array variations có productId = 1 và quantity > 0

#### TC_VARIATION_037: Lấy in-stock khi tất cả hết hàng
- **Điều kiện tiên quyết:** Tất cả variations có quantity = 0
- **Input:** GET `/api/product-variations/in-stock`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

### 4.9 GET /api/product-variations/low-stock - Lấy variations sắp hết hàng

#### TC_VARIATION_038: Lấy low-stock với threshold mặc định
- **Điều kiện tiên quyết:** Có variations với quantity <= 10
- **Input:** GET `/api/product-variations/low-stock`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array variations có quantity <= 10

#### TC_VARIATION_039: Lấy low-stock với threshold tùy chỉnh
- **Input:** GET `/api/product-variations/low-stock?threshold=5`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array variations có quantity <= 5

#### TC_VARIATION_040: Lấy low-stock với threshold = 0
- **Input:** GET `/api/product-variations/low-stock?threshold=0`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array variations có quantity = 0

#### TC_VARIATION_041: Lấy low-stock khi không có variation nào thỏa mãn
- **Input:** GET `/api/product-variations/low-stock?threshold=1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

### 4.10 GET /api/product-variations/sku/{sku} - Lấy variation theo SKU

#### TC_VARIATION_042: Lấy variation theo SKU tồn tại
- **Điều kiện tiên quyết:** SKU "IPH15PRO256" tồn tại
- **Input:** GET `/api/product-variations/sku/IPH15PRO256`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductVariationResponse với SKU = "IPH15PRO256"

#### TC_VARIATION_043: Lấy variation theo SKU không tồn tại
- **Input:** GET `/api/product-variations/sku/NONEXISTENT`
- **Expected Output:**
  - Status Code: 404 Not Found

#### TC_VARIATION_044: Lấy variation theo SKU của variation đã deleted
- **Điều kiện tiên quyết:** SKU tồn tại nhưng variation bị soft deleted
- **Input:** GET `/api/product-variations/sku/DELETED_SKU`
- **Expected Output:**
  - Status Code: 404 Not Found (chỉ trả về active variations)

### 4.11 PATCH /api/product-variations/{id}/stock - Cập nhật tồn kho

#### TC_VARIATION_045: Cập nhật stock thành công
- **Input:** PATCH `/api/product-variations/1/stock?quantity=100`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductVariationResponse với quantity = 100
  - Database: Quantity updated

#### TC_VARIATION_046: Cập nhật stock = 0 (hết hàng)
- **Input:** PATCH `/api/product-variations/1/stock?quantity=0`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductVariationResponse với quantity = 0, isInStock = false

#### TC_VARIATION_047: Cập nhật stock với quantity âm
- **Input:** PATCH `/api/product-variations/1/stock?quantity=-10`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về invalid quantity

#### TC_VARIATION_048: Cập nhật stock của variation không tồn tại
- **Input:** PATCH `/api/product-variations/999999/stock?quantity=50`
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_VARIATION_049: Cập nhật stock không có parameter quantity
- **Input:** PATCH `/api/product-variations/1/stock`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.12 GET /api/product-variations/exists - Kiểm tra SKU tồn tại

#### TC_VARIATION_050: Kiểm tra SKU tồn tại
- **Input:** GET `/api/product-variations/exists?sku=IPH15PRO256`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `true`

#### TC_VARIATION_051: Kiểm tra SKU không tồn tại
- **Input:** GET `/api/product-variations/exists?sku=NONEXISTENT`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_VARIATION_052: Kiểm tra SKU với excludeId
- **Mô tả:** Kiểm tra duplicate khi update (loại trừ chính nó)
- **Input:** GET `/api/product-variations/exists?sku=IPH15PRO256&excludeId=1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false` (nếu SKU thuộc về variation id=1)

#### TC_VARIATION_053: Kiểm tra SKU rỗng
- **Input:** GET `/api/product-variations/exists?sku=`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_VARIATION_054: Kiểm tra không có parameter SKU
- **Input:** GET `/api/product-variations/exists`
- **Expected Output:**
  - Status Code: 400 Bad Request

---

## 5. Business Logic Testing

### 5.1 Stock Management Logic
- **TC_VARIATION_055:** Stock reduction khi có order
- **TC_VARIATION_056:** Stock increment khi cancel order
- **TC_VARIATION_057:** Prevent order khi out of stock
- **TC_VARIATION_058:** Low stock notification triggers

### 5.2 SKU Management Logic
- **TC_VARIATION_059:** Auto-generate SKU nếu không provided
- **TC_VARIATION_060:** SKU case sensitivity
- **TC_VARIATION_061:** SKU format validation
- **TC_VARIATION_062:** SKU uniqueness across soft deleted items

### 5.3 Pricing Logic
- **TC_VARIATION_063:** Price comparison giữa variations cùng product
- **TC_VARIATION_064:** Currency precision handling
- **TC_VARIATION_065:** Price history tracking
- **TC_VARIATION_066:** Discount calculations

---

## 6. Edge Cases & Special Scenarios

### 6.1 Multi-Image Management
- **TC_VARIATION_067:** Upload maximum số lượng images
- **TC_VARIATION_068:** Delete tất cả images
- **TC_VARIATION_069:** Replace tất cả images cùng lúc
- **TC_VARIATION_070:** Mixed image operations (add + delete) với errors

### 6.2 Concurrent Operations
- **TC_VARIATION_071:** Multiple users update cùng variation stock
- **TC_VARIATION_072:** Create variations với cùng SKU đồng thời
- **TC_VARIATION_073:** Delete variation đang được order
- **TC_VARIATION_074:** Update price khi có active shopping carts

### 6.3 Performance Testing
- **TC_VARIATION_075:** Load test với 10,000+ variations
- **TC_VARIATION_076:** Search performance với complex filters
- **TC_VARIATION_077:** Image upload performance với large files
- **TC_VARIATION_078:** Bulk stock update operations

### 6.4 Integration Edge Cases
- **TC_VARIATION_079:** Product deletion khi có variations
- **TC_VARIATION_080:** Category change impact on variations
- **TC_VARIATION_081:** Brand change impact on variations
- **TC_VARIATION_082:** Cloudinary service interruption recovery

---

## 7. Security Testing

### 7.1 Input Validation Security
- **TC_VARIATION_083:** SQL injection trong SKU field
- **TC_VARIATION_084:** XSS payload trong variation name
- **TC_VARIATION_085:** Path traversal trong image uploads
- **TC_VARIATION_086:** File upload với malicious files

### 7.2 Business Logic Security
- **TC_VARIATION_087:** Price manipulation attempts
- **TC_VARIATION_088:** Stock manipulation attacks
- **TC_VARIATION_089:** SKU collision attacks
- **TC_VARIATION_090:** Unauthorized variation access

---

## 8. Test Data Setup

### 8.1 Initial Data
```sql
-- Products
INSERT INTO products (id, name, category_id, brand_id) VALUES
(1, 'iPhone 15 Pro', 1, 1),
(2, 'Galaxy S24', 1, 2),
(3, 'Deleted Product', 1, 1);

-- Product Variations
INSERT INTO product_variations (id, variation_name, product_id, price, sku, quantity, created_at, updated_at) VALUES
(1, 'iPhone 15 Pro 256GB Space Black', 1, 999.99, 'IPH15PRO256SB', 50, NOW(), NOW()),
(2, 'iPhone 15 Pro 512GB Natural Titanium', 1, 1199.99, 'IPH15PRO512NT', 30, NOW(), NOW()),
(3, 'Galaxy S24 128GB Phantom Black', 2, 799.99, 'GAL24128PB', 0, NOW(), NOW()),
(4, 'Deleted Variation', 1, 99.99, 'DELETED001', 10, NOW(), NOW());

-- Soft delete one variation
UPDATE product_variations SET deleted_at = NOW() WHERE id = 4;

-- Low stock variations
INSERT INTO product_variations (id, variation_name, product_id, price, sku, quantity) VALUES
(5, 'Low Stock Item', 1, 599.99, 'LOWSTOCK001', 5),
(6, 'Out of Stock Item', 1, 699.99, 'OUTSTOCK001', 0);
```

### 8.2 Test Image Files
```
/test-files/variation-images/
  ├── variation1_image1.jpg (valid image)
  ├── variation1_image2.jpg (valid image)
  ├── variation1_image3.jpg (valid image)
  ├── large_variation_image.jpg (> size limit)
  ├── invalid_variation_file.txt (not an image)
  └── malicious_variation_file.exe (security test)
```

---

## 9. Expected Outcomes

### 9.1 Success Criteria
- All CRUD operations work correctly với multiple images
- Stock management accuracy và consistency
- SKU uniqueness enforcement
- Soft delete/restore functionality
- Search và filtering accuracy
- Performance within acceptable limits
- Proper validation và error handling

### 9.2 Risk Assessment
- **High Risk:** Stock inconsistency, SKU conflicts, image upload failures
- **Medium Risk:** Performance degradation, validation bypass
- **Low Risk:** UI/UX issues, minor business logic edge cases

---

*Document Version: 1.0*  
*Last Updated: September 30, 2025*  
*Author: Test Team*