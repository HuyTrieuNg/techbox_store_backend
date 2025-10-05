# Product API Test Plan & Test Cases

## 1. API Overview

**Base URL:** `/api/products`

**Controller:** `ProductController`

**Endpoints:**
- `GET /api/products` - Lấy tất cả products (có/không includeDeleted)
- `GET /api/products/active` - Lấy tất cả products active
- `GET /api/products/{id}` - Lấy product theo ID
- `POST /api/products` - Tạo product mới (multipart/form-data)
- `PUT /api/products/{id}` - Cập nhật product (multipart/form-data)
- `DELETE /api/products/{id}` - Xóa product (soft delete)
- `PATCH /api/products/{id}/restore` - Khôi phục product
- `GET /api/products/category/{categoryId}` - Lấy products theo category
- `GET /api/products/brand/{brandId}` - Lấy products theo brand
- `GET /api/products/search?keyword=<keyword>` - Tìm kiếm products theo tên
- `GET /api/products/exists?name=<name>` - Kiểm tra tồn tại product theo tên
- `DELETE /api/products/{id}/image` - Xóa ảnh product
- `POST /api/products/upload-image` - Upload ảnh riêng
- `POST /api/products/create-with-image` - Tạo product với ảnh
- `PUT /api/products/{id}/update-with-image` - Cập nhật product với ảnh
- `DELETE /api/products/delete-image?publicId=<id>` - Xóa ảnh theo publicId

---

## 2. Data Models

### ProductCreateRequest
```json
{
  "name": "string", // @NotBlank, @Size(max=255)
  "description": "string", // @Size(max=5000), optional
  "categoryId": "integer", // optional
  "brandId": "integer", // optional
  "imageUrl": "string", // @Size(max=255), optional
  "imagePublicId": "string" // @Size(max=255), optional
}
```

### ProductUpdateRequest
```json
{
  "name": "string", // @Size(max=255), optional
  "description": "string", // @Size(max=5000), optional
  "categoryId": "integer", // optional
  "brandId": "integer", // optional
  "imageUrl": "string", // @Size(max=255), optional
  "imagePublicId": "string" // @Size(max=255), optional
}
```

### ProductResponse
```json
{
  "id": "integer",
  "name": "string",
  "description": "string",
  "categoryId": "integer",
  "brandId": "integer",
  "imageUrl": "string",
  "imagePublicId": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "deletedAt": "datetime",
  "categoryName": "string",
  "brandName": "string",
  "isDeleted": "boolean"
}
```

---

## 3. Test Strategy

### 3.1 Test Categories
- **Functional Testing:** CRUD operations cơ bản
- **File Upload Testing:** Multipart/form-data với image files
- **Soft Delete Testing:** Delete và restore operations
- **Search & Filter Testing:** Category, brand, keyword search
- **Validation Testing:** Input validation và constraints
- **Error Handling:** Exception handling và error responses
- **Integration Testing:** Cloudinary integration, database relationships

### 3.2 Test Levels
- **Unit Test:** Controller methods testing
- **Integration Test:** Service + Repository + Database
- **API Test:** End-to-end HTTP requests
- **File Upload Test:** Multipart file handling

---

## 4. Detailed Test Cases

### 4.1 GET /api/products - Lấy tất cả products

#### TC_PRODUCT_001: Lấy products không bao gồm deleted
- **Mô tả:** Lấy tất cả products active (includeDeleted=false)
- **Điều kiện tiên quyết:** Database có products active và deleted
- **Input:** GET `/api/products` hoặc `/api/products?includeDeleted=false`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array chỉ chứa products có deletedAt = null
  - Verify categoryName và brandName được populate

#### TC_PRODUCT_002: Lấy products bao gồm deleted
- **Mô tả:** Lấy tất cả products kể cả deleted (includeDeleted=true)
- **Input:** GET `/api/products?includeDeleted=true`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array chứa tất cả products kể cả deleted
  - Verify isDeleted flag

#### TC_PRODUCT_003: Lấy products khi không có dữ liệu
- **Mô tả:** Database không có product nào
- **Input:** GET `/api/products`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` (empty array)

### 4.2 GET /api/products/active - Lấy products active

#### TC_PRODUCT_004: Lấy products active thành công
- **Mô tả:** Chỉ lấy products chưa bị deleted
- **Input:** GET `/api/products/active`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array products với deletedAt = null

### 4.3 GET /api/products/{id} - Lấy product theo ID

#### TC_PRODUCT_005: Lấy product active theo ID
- **Mô tả:** Lấy product chưa deleted với ID hợp lệ
- **Input:** GET `/api/products/1?includeDeleted=false`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductResponse với ID = 1

#### TC_PRODUCT_006: Lấy product deleted theo ID
- **Mô tả:** Lấy product đã deleted khi includeDeleted=true
- **Input:** GET `/api/products/1?includeDeleted=true`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductResponse kể cả deleted

#### TC_PRODUCT_007: Lấy product không tồn tại
- **Input:** GET `/api/products/999999`
- **Expected Output:**
  - Status Code: 404 Not Found

#### TC_PRODUCT_008: ID không hợp lệ
- **Input:** GET `/api/products/abc`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.4 POST /api/products - Tạo product (multipart/form-data)

#### TC_PRODUCT_009: Tạo product thành công với ảnh
- **Mô tả:** Tạo product với đầy đủ thông tin và file ảnh
- **Input:**
  - Content-Type: multipart/form-data
  - Form Data:
    ```
    name: "iPhone 15 Pro"
    description: "Latest iPhone model"
    categoryId: 1
    brandId: 2
    image: [valid_image_file.jpg]
    ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: ProductResponse với imageUrl và imagePublicId từ Cloudinary
  - Database: Product mới được tạo
  - Cloudinary: Image được upload

#### TC_PRODUCT_010: Tạo product không có ảnh
- **Mô tả:** Tạo product chỉ với thông tin text
- **Input:**
  ```
  name: "Product Without Image"
  description: "Test product"
  categoryId: 1
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: ProductResponse với imageUrl = null

#### TC_PRODUCT_011: Tạo product với tên trống
- **Input:**
  ```
  name: ""
  description: "Test"
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Product name is required"

#### TC_PRODUCT_012: Tạo product với tên quá dài
- **Input:**
  ```
  name: "A very long product name that exceeds..." (> 255 chars)
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Product name must not exceed 255 characters"

#### TC_PRODUCT_013: Tạo product với description quá dài
- **Input:**
  ```
  description: "A very long description..." (> 5000 chars)
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Description must not exceed 5000 characters"

#### TC_PRODUCT_014: Tạo product với categoryId không tồn tại
- **Input:**
  ```
  name: "Test Product"
  categoryId: 999999
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về invalid category

#### TC_PRODUCT_015: Tạo product với brandId không tồn tại
- **Input:**
  ```
  name: "Test Product"
  brandId: 999999
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về invalid brand

#### TC_PRODUCT_016: Tạo product với file không phải ảnh
- **Input:**
  ```
  name: "Test Product"
  image: [text_file.txt]
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "File must be an image"

#### TC_PRODUCT_017: Tạo product với file ảnh quá lớn
- **Input:**
  ```
  name: "Test Product"
  image: [large_image.jpg] (> size limit)
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về file size

#### TC_PRODUCT_018: Tạo product khi Cloudinary error
- **Mô tả:** Cloudinary service không available
- **Input:** Valid product data với image
- **Expected Output:**
  - Status Code: 500 Internal Server Error
  - Error message: "Failed to upload image"

### 4.5 PUT /api/products/{id} - Cập nhật product

#### TC_PRODUCT_019: Cập nhật product thành công
- **Input:**
  - URL: `/api/products/1`
  - Form Data:
    ```
    name: "Updated Product Name"
    description: "Updated description"
    ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductResponse với thông tin updated

#### TC_PRODUCT_020: Cập nhật product với ảnh mới
- **Mô tả:** Thay thế ảnh cũ bằng ảnh mới
- **Điều kiện tiên quyết:** Product có ảnh cũ
- **Input:**
  ```
  name: "Updated Product"
  image: [new_image.jpg]
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductResponse với imageUrl mới
  - Cloudinary: Ảnh cũ bị xóa, ảnh mới được upload

#### TC_PRODUCT_021: Xóa ảnh product khi update
- **Input:**
  ```
  name: "Product"
  deleteImage: true
  ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductResponse với imageUrl = null
  - Cloudinary: Ảnh cũ bị xóa

#### TC_PRODUCT_022: Cập nhật product không tồn tại
- **Input:** PUT `/api/products/999999`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Product not found"

#### TC_PRODUCT_023: Cập nhật với validation lỗi
- **Input:**
  ```
  name: "" (empty)
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Validation error messages

### 4.6 DELETE /api/products/{id} - Soft delete

#### TC_PRODUCT_024: Xóa product thành công
- **Mô tả:** Soft delete product (set deletedAt)
- **Input:** DELETE `/api/products/1`
- **Expected Output:**
  - Status Code: 204 No Content
  - Database: Product có deletedAt != null
  - Cloudinary: Ảnh KHÔNG bị xóa (soft delete)

#### TC_PRODUCT_025: Xóa product không tồn tại
- **Input:** DELETE `/api/products/999999`
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_PRODUCT_026: Xóa product đã bị xóa
- **Điều kiện tiên quyết:** Product đã có deletedAt
- **Input:** DELETE `/api/products/1`
- **Expected Output:**
  - Status Code: 400 Bad Request (hoặc success tùy business logic)

### 4.7 PATCH /api/products/{id}/restore - Khôi phục product

#### TC_PRODUCT_027: Khôi phục product thành công
- **Điều kiện tiên quyết:** Product đã bị soft deleted
- **Input:** PATCH `/api/products/1/restore`
- **Expected Output:**
  - Status Code: 200 OK
  - Database: deletedAt = null

#### TC_PRODUCT_028: Khôi phục product chưa bị xóa
- **Input:** PATCH `/api/products/1/restore`
- **Expected Output:**
  - Status Code: 400 Bad Request (hoặc success)

### 4.8 GET /api/products/category/{categoryId} - Lấy products theo category

#### TC_PRODUCT_029: Lấy products theo category hợp lệ
- **Điều kiện tiên quyết:** Category có products
- **Input:** GET `/api/products/category/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array products có categoryId = 1

#### TC_PRODUCT_030: Lấy products của category không có product
- **Input:** GET `/api/products/category/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

#### TC_PRODUCT_031: Category không tồn tại
- **Input:** GET `/api/products/category/999999`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

### 4.9 GET /api/products/brand/{brandId} - Lấy products theo brand

#### TC_PRODUCT_032: Lấy products theo brand hợp lệ
- **Input:** GET `/api/products/brand/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array products có brandId = 1

#### TC_PRODUCT_033: Brand không có product
- **Input:** GET `/api/products/brand/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

### 4.10 GET /api/products/search - Tìm kiếm products

#### TC_PRODUCT_034: Tìm kiếm có kết quả
- **Input:** GET `/api/products/search?keyword=iPhone`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array products có tên chứa "iPhone"

#### TC_PRODUCT_035: Tìm kiếm không có kết quả
- **Input:** GET `/api/products/search?keyword=NonExistentProduct`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]`

#### TC_PRODUCT_036: Tìm kiếm với keyword rỗng
- **Input:** GET `/api/products/search?keyword=`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` hoặc tất cả products

#### TC_PRODUCT_037: Tìm kiếm không có parameter
- **Input:** GET `/api/products/search`
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_PRODUCT_038: Tìm kiếm case-insensitive
- **Input:** GET `/api/products/search?keyword=iphone` (lowercase)
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Products có tên chứa "iPhone" (bất kể case)

### 4.11 GET /api/products/exists - Kiểm tra tồn tại product

#### TC_PRODUCT_039: Kiểm tra product tồn tại
- **Input:** GET `/api/products/exists?name=iPhone 15`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `true`

#### TC_PRODUCT_040: Kiểm tra product không tồn tại
- **Input:** GET `/api/products/exists?name=NonExistentProduct`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_PRODUCT_041: Kiểm tra với excludeId
- **Mô tả:** Kiểm tra duplicate khi update (loại trừ chính nó)
- **Input:** GET `/api/products/exists?name=iPhone 15&excludeId=1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false` (nếu iPhone 15 có id=1)

### 4.12 DELETE /api/products/{id}/image - Xóa ảnh product

#### TC_PRODUCT_042: Xóa ảnh thành công
- **Điều kiện tiên quyết:** Product có ảnh
- **Input:** DELETE `/api/products/1/image`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductResponse với imageUrl = null
  - Cloudinary: Ảnh bị xóa

#### TC_PRODUCT_043: Xóa ảnh khi product không có ảnh
- **Input:** DELETE `/api/products/1/image`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Message "Product has no image to delete"

#### TC_PRODUCT_044: Xóa ảnh của product không tồn tại
- **Input:** DELETE `/api/products/999999/image`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.13 POST /api/products/upload-image - Upload ảnh riêng

#### TC_PRODUCT_045: Upload ảnh thành công
- **Input:**
  - Content-Type: multipart/form-data
  - file: [valid_image.jpg]
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body:
    ```json
    {
      "imageUrl": "https://res.cloudinary.com/...",
      "publicId": "product_images/...",
      "message": "Image uploaded successfully"
    }
    ```

#### TC_PRODUCT_046: Upload file rỗng
- **Input:** file: [empty_file]
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "File is empty"

#### TC_PRODUCT_047: Upload file không phải ảnh
- **Input:** file: [document.pdf]
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "File must be an image"

### 4.14 POST /api/products/create-with-image - Tạo product với ảnh

#### TC_PRODUCT_048: Tạo product với ảnh thành công
- **Input:**
  ```
  name: "Product With Image"
  description: "Test"
  categoryId: 1
  brandId: 1
  file: [image.jpg]
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: ProductResponse với imageUrl từ Cloudinary

#### TC_PRODUCT_049: Tạo product với file upload lỗi
- **Input:** Valid data với invalid image file
- **Expected Output:**
  - Status Code: 500 Internal Server Error
  - Error message: "Failed to upload image"

### 4.15 PUT /api/products/{id}/update-with-image - Cập nhật với ảnh

#### TC_PRODUCT_050: Cập nhật product với ảnh mới
- **Input:**
  - URL: `/api/products/1/update-with-image`
  - Form data với file ảnh mới
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductResponse với imageUrl mới

#### TC_PRODUCT_051: Cập nhật không có file ảnh
- **Input:** Chỉ có text fields, không có file
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: ProductResponse với imageUrl không đổi

### 4.16 DELETE /api/products/delete-image - Xóa ảnh theo publicId

#### TC_PRODUCT_052: Xóa ảnh theo publicId thành công
- **Input:** DELETE `/api/products/delete-image?publicId=product_images/abc123`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body:
    ```json
    {
      "result": "ok",
      "message": "Image deleted successfully"
    }
    ```

#### TC_PRODUCT_053: Xóa ảnh với publicId không tồn tại
- **Input:** publicId không có trong Cloudinary
- **Expected Output:**
  - Status Code: 500 Internal Server Error
  - Error message về failed deletion

---

## 5. Edge Cases & Special Scenarios

### 5.1 File Upload Edge Cases
- **TC_PRODUCT_054:** Upload ảnh với size limit boundary (exactly max size)
- **TC_PRODUCT_055:** Upload multiple images đồng thời
- **TC_PRODUCT_056:** Upload với special characters trong filename
- **TC_PRODUCT_057:** Upload với các format khác nhau (jpg, png, gif, webp)

### 5.2 Concurrent Operations
- **TC_PRODUCT_058:** Multiple users tạo product cùng tên đồng thời
- **TC_PRODUCT_059:** Update và delete product đồng thời
- **TC_PRODUCT_060:** Upload image và delete product đồng thời

### 5.3 Performance Testing
- **TC_PRODUCT_061:** Load test với 1000+ products
- **TC_PRODUCT_062:** Search performance với large dataset
- **TC_PRODUCT_063:** Image upload performance với large files

### 5.4 Security Testing
- **TC_PRODUCT_064:** SQL injection trong search keyword
- **TC_PRODUCT_065:** XSS payload trong product name/description
- **TC_PRODUCT_066:** Path traversal trong image upload
- **TC_PRODUCT_067:** File upload với malicious files (exe, script)

---

## 6. Integration Testing

### 6.1 Database Integration
- **TC_PRODUCT_068:** Foreign key constraints với category/brand
- **TC_PRODUCT_069:** Soft delete cascade behavior
- **TC_PRODUCT_070:** Transaction rollback scenarios

### 6.2 Cloudinary Integration
- **TC_PRODUCT_071:** Cloudinary service unavailable
- **TC_PRODUCT_072:** Invalid Cloudinary credentials
- **TC_PRODUCT_073:** Network timeout during upload
- **TC_PRODUCT_074:** Cloudinary quota exceeded

---

## 7. Test Data Setup

### 7.1 Initial Data
```sql
-- Categories
INSERT INTO categories (id, name) VALUES 
(1, 'Electronics'), (2, 'Clothing'), (3, 'Books');

-- Brands  
INSERT INTO brands (id, name) VALUES
(1, 'Apple'), (2, 'Samsung'), (3, 'Sony');

-- Products
INSERT INTO products (id, name, description, category_id, brand_id, created_at, updated_at) VALUES
(1, 'iPhone 15 Pro', 'Latest iPhone', 1, 1, NOW(), NOW()),
(2, 'Galaxy S24', 'Samsung flagship', 1, 2, NOW(), NOW()),
(3, 'Deleted Product', 'Test deleted', 1, 1, NOW(), NOW());

-- Soft delete one product
UPDATE products SET deleted_at = NOW() WHERE id = 3;
```

### 7.2 Test Files
```
/test-files/
  ├── valid_image.jpg (< 5MB, valid image)
  ├── large_image.jpg (> size limit)
  ├── invalid_file.txt (not an image)
  ├── empty_file.jpg (0 bytes)
  └── malicious_file.exe (security test)
```

---

## 8. Expected Outcomes

### 8.1 Success Criteria
- All CRUD operations work correctly
- File upload/delete integration với Cloudinary
- Soft delete/restore functionality
- Search và filtering accuracy
- Proper validation và error handling
- Performance within acceptable limits

### 8.2 Risk Assessment
- **High Risk:** Data loss, image upload failures, security vulnerabilities
- **Medium Risk:** Performance issues, validation bypass
- **Low Risk:** UI/UX issues, minor validation messages

---

*Document Version: 1.0*  
*Last Updated: September 30, 2025*  
*Author: Test Team*