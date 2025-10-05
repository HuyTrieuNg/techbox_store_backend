# Attribute API Test Plan & Test Cases

## 1. API Overview

**Base URL:** `/api/attributes`

**Controller:** `AttributeController`

**Endpoints:**
- `GET /api/attributes` - Lấy tất cả attributes
- `GET /api/attributes/{id}` - Lấy attribute theo ID
- `POST /api/attributes` - Tạo attribute mới
- `PUT /api/attributes/{id}` - Cập nhật attribute
- `DELETE /api/attributes/{id}` - Xóa attribute
- `GET /api/attributes/search?keyword=<keyword>` - Tìm kiếm attributes theo tên
- `GET /api/attributes/exists?name=<name>` - Kiểm tra tồn tại attribute theo tên

---

## 2. Data Models

### AttributeCreateRequest
```json
{
  "name": "string" // @NotBlank, @Size(max=255)
}
```

### AttributeUpdateRequest
```json
{
  "name": "string" // @Size(max=255), optional
}
```

### AttributeResponse
```json
{
  "id": "integer",
  "name": "string"
}
```

---

## 3. Test Strategy

### 3.1 Test Categories
- **Functional Testing:** Kiểm tra tính năng cơ bản của từng endpoint
- **Validation Testing:** Kiểm tra ràng buộc dữ liệu đầu vào
- **Search Testing:** Kiểm tra tìm kiếm attributes theo tên
- **Error Handling:** Kiểm tra xử lý lỗi và exception
- **Edge Cases:** Kiểm tra các trường hợp biên
- **Data Integrity:** Kiểm tra tính toàn vẹn dữ liệu

### 3.2 Test Levels
- **Unit Test:** Test từng method trong controller
- **Integration Test:** Test tích hợp với service và database
- **API Test:** Test end-to-end qua HTTP requests

---

## 4. Detailed Test Cases

### 4.1 GET /api/attributes - Lấy tất cả attributes

#### TC_ATTRIBUTE_001: Lấy danh sách attributes thành công
- **Mô tả:** Kiểm tra lấy tất cả attributes khi có dữ liệu
- **Điều kiện tiên quyết:** Database có ít nhất 1 attribute
- **Input:** GET request đến `/api/attributes`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array of AttributeResponse objects
  - Content-Type: application/json
  - Verify attributes được sort theo name hoặc creation order

#### TC_ATTRIBUTE_002: Lấy danh sách attributes khi không có dữ liệu
- **Mô tả:** Kiểm tra khi database không có attribute nào
- **Điều kiện tiên quyết:** Database không có attribute
- **Input:** GET request đến `/api/attributes`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` (empty array)

#### TC_ATTRIBUTE_003: Lấy danh sách attributes với số lượng lớn
- **Mô tả:** Performance test với nhiều attributes
- **Điều kiện tiên quyết:** Database có 1000+ attributes
- **Input:** GET request đến `/api/attributes`
- **Expected Output:**
  - Status Code: 200 OK
  - Response time < 2 seconds
  - Complete list returned

### 4.2 GET /api/attributes/{id} - Lấy attribute theo ID

#### TC_ATTRIBUTE_004: Lấy attribute theo ID hợp lệ
- **Mô tả:** Kiểm tra lấy attribute với ID tồn tại
- **Điều kiện tiên quyết:** Attribute với ID cụ thể tồn tại trong database
- **Input:** GET request đến `/api/attributes/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: AttributeResponse object với ID = 1

#### TC_ATTRIBUTE_005: Lấy attribute với ID không tồn tại
- **Mô tả:** Kiểm tra khi ID attribute không tồn tại
- **Input:** GET request đến `/api/attributes/999999`
- **Expected Output:**
  - Status Code: 404 Not Found

#### TC_ATTRIBUTE_006: Lấy attribute với ID không hợp lệ
- **Mô tả:** Kiểm tra với ID không phải số nguyên
- **Input:** GET request đến `/api/attributes/abc`
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_ATTRIBUTE_007: Lấy attribute với ID = 0
- **Input:** GET request đến `/api/attributes/0`
- **Expected Output:**
  - Status Code: 404 Not Found (hoặc 400 tùy validation)

#### TC_ATTRIBUTE_008: Lấy attribute với ID âm
- **Input:** GET request đến `/api/attributes/-1`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.3 POST /api/attributes - Tạo attribute mới

#### TC_ATTRIBUTE_009: Tạo attribute thành công
- **Mô tả:** Tạo attribute với dữ liệu hợp lệ
- **Input:**
  ```json
  {
    "name": "Color"
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: AttributeResponse object với name = "Color"
  - Database: Attribute mới được tạo với ID auto-generated

#### TC_ATTRIBUTE_010: Tạo attribute với tên phức tạp
- **Input:**
  ```json
  {
    "name": "Screen Size (inches)"
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: AttributeResponse với name = "Screen Size (inches)"

#### TC_ATTRIBUTE_011: Tạo attribute với tên có ký tự đặc biệt
- **Input:**
  ```json
  {
    "name": "CPU Speed (GHz) - Max"
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created (nếu hệ thống cho phép)
  - hoặc 400 Bad Request (nếu có validation)

#### TC_ATTRIBUTE_012: Tạo attribute với tên trống
- **Mô tả:** Kiểm tra validation khi name rỗng
- **Input:**
  ```json
  {
    "name": ""
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Attribute name is required"

#### TC_ATTRIBUTE_013: Tạo attribute với tên null
- **Mô tả:** Kiểm tra validation khi name = null
- **Input:**
  ```json
  {
    "name": null
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Attribute name is required"

#### TC_ATTRIBUTE_014: Tạo attribute với tên quá dài
- **Mô tả:** Kiểm tra validation khi name > 255 ký tự
- **Input:**
  ```json
  {
    "name": "A very long attribute name that exceeds the maximum length of 255 characters. This string is intentionally created to be longer than the allowed limit to test the validation constraint that prevents attribute names from being too long and potentially causing database or display issues in the application interface and other systems that interact with this data structure."
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Attribute name must not exceed 255 characters"

#### TC_ATTRIBUTE_015: Tạo attribute với tên chỉ chứa khoảng trắng
- **Mô tả:** Kiểm tra validation với string chỉ có spaces
- **Input:**
  ```json
  {
    "name": "   "
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Attribute name is required"

#### TC_ATTRIBUTE_016: Tạo attribute với tên trùng lặp
- **Mô tả:** Kiểm tra ràng buộc unique constraint (nếu có)
- **Điều kiện tiên quyết:** Attribute "Color" đã tồn tại
- **Input:**
  ```json
  {
    "name": "Color"
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request (nếu có unique constraint)
  - hoặc 201 Created (nếu cho phép duplicate names)

#### TC_ATTRIBUTE_017: Tạo attribute với case khác nhau
- **Mô tả:** Kiểm tra case sensitivity
- **Điều kiện tiên quyết:** Attribute "Color" đã tồn tại
- **Input:**
  ```json
  {
    "name": "color"
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created (nếu case sensitive)
  - hoặc 400 Bad Request (nếu case insensitive unique constraint)

#### TC_ATTRIBUTE_018: Tạo attribute với request body rỗng
- **Input:** POST request với empty body `{}`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về missing required field

#### TC_ATTRIBUTE_019: Tạo attribute với invalid JSON
- **Input:** POST request với malformed JSON
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về JSON parsing

### 4.4 PUT /api/attributes/{id} - Cập nhật attribute

#### TC_ATTRIBUTE_020: Cập nhật attribute thành công
- **Mô tả:** Cập nhật attribute với dữ liệu hợp lệ
- **Điều kiện tiên quyết:** Attribute với ID = 1 tồn tại
- **Input:**
  - URL: `/api/attributes/1`
  - Body:
    ```json
    {
      "name": "Color Updated"
    }
    ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: AttributeResponse với name = "Color Updated"
  - Database: Attribute được cập nhật

#### TC_ATTRIBUTE_021: Cập nhật attribute với tên giống cũ
- **Mô tả:** Update với same name (no change)
- **Input:**
  - URL: `/api/attributes/1`
  - Body:
    ```json
    {
      "name": "Color"
    }
    ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: AttributeResponse không đổi

#### TC_ATTRIBUTE_022: Cập nhật attribute không tồn tại
- **Mô tả:** Cập nhật attribute với ID không tồn tại
- **Input:**
  - URL: `/api/attributes/999999`
  - Body:
    ```json
    {
      "name": "Non-existent Attribute"
    }
    ```
- **Expected Output:**
  - Status Code: 404 Not Found
  - hoặc 400 Bad Request (tùy implementation)

#### TC_ATTRIBUTE_023: Cập nhật attribute với tên trống
- **Mô tả:** Validation khi update với name rỗng
- **Input:**
  - URL: `/api/attributes/1`
  - Body:
    ```json
    {
      "name": ""
    }
    ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về validation (Note: Có thể khác với create vì @NotBlank chỉ có ở create)

#### TC_ATTRIBUTE_024: Cập nhật attribute với tên null
- **Input:**
  - URL: `/api/attributes/1`
  - Body:
    ```json
    {
      "name": null
    }
    ```
- **Expected Output:**
  - Status Code: 200 OK (nếu update request cho phép null để không thay đổi)
  - hoặc 400 Bad Request (nếu có validation)

#### TC_ATTRIBUTE_025: Cập nhật attribute với tên quá dài
- **Input:**
  - URL: `/api/attributes/1`
  - Body với name > 255 characters
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Attribute name must not exceed 255 characters"

#### TC_ATTRIBUTE_026: Cập nhật attribute với tên trùng lặp
- **Mô tả:** Cập nhật attribute thành tên đã tồn tại
- **Điều kiện tiên quyết:** 
  - Attribute ID = 1 có name = "Color"
  - Attribute ID = 2 có name = "Size"
- **Input:**
  - URL: `/api/attributes/1`
  - Body:
    ```json
    {
      "name": "Size"
    }
    ```
- **Expected Output:**
  - Status Code: 400 Bad Request (nếu có unique constraint)
  - hoặc 200 OK (nếu cho phép duplicate)

#### TC_ATTRIBUTE_027: Cập nhật với empty request body
- **Input:**
  - URL: `/api/attributes/1`
  - Body: `{}`
- **Expected Output:**
  - Status Code: 200 OK (no changes)
  - Response Body: AttributeResponse unchanged

### 4.5 DELETE /api/attributes/{id} - Xóa attribute

#### TC_ATTRIBUTE_028: Xóa attribute thành công
- **Mô tả:** Xóa attribute không được sử dụng
- **Điều kiện tiên quyết:** Attribute với ID = 1 tồn tại và không được sử dụng
- **Input:** DELETE request đến `/api/attributes/1`
- **Expected Output:**
  - Status Code: 204 No Content
  - Database: Attribute bị xóa hoặc đánh dấu deleted

#### TC_ATTRIBUTE_029: Xóa attribute không tồn tại
- **Mô tả:** Xóa attribute với ID không tồn tại
- **Input:** DELETE request đến `/api/attributes/999999`
- **Expected Output:**
  - Status Code: 404 Not Found
  - hoặc 400 Bad Request

#### TC_ATTRIBUTE_030: Xóa attribute đang được sử dụng
- **Mô tả:** Xóa attribute đang được tham chiếu bởi products/variations
- **Điều kiện tiên quyết:** Attribute có products hoặc variations đang sử dụng
- **Input:** DELETE request đến `/api/attributes/1`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về foreign key constraint
  - hoặc 409 Conflict

#### TC_ATTRIBUTE_031: Xóa attribute với ID không hợp lệ
- **Input:** DELETE request đến `/api/attributes/abc`
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_ATTRIBUTE_032: Xóa attribute với ID = 0
- **Input:** DELETE request đến `/api/attributes/0`
- **Expected Output:**
  - Status Code: 404 Not Found

### 4.6 GET /api/attributes/search - Tìm kiếm attributes

#### TC_ATTRIBUTE_033: Tìm kiếm có kết quả
- **Mô tả:** Tìm kiếm attributes với keyword matching
- **Điều kiện tiên quyết:** Database có attributes chứa keyword
- **Input:** GET request đến `/api/attributes/search?keyword=Color`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array attributes có tên chứa "Color"
  - Verify case-insensitive search

#### TC_ATTRIBUTE_034: Tìm kiếm không có kết quả
- **Mô tả:** Tìm kiếm với keyword không match
- **Input:** GET request đến `/api/attributes/search?keyword=NonExistentAttribute`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` (empty array)

#### TC_ATTRIBUTE_035: Tìm kiếm với keyword rỗng
- **Mô tả:** Search với empty keyword
- **Input:** GET request đến `/api/attributes/search?keyword=`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` hoặc tất cả attributes (tùy logic)

#### TC_ATTRIBUTE_036: Tìm kiếm không có parameter
- **Mô tả:** Missing keyword parameter
- **Input:** GET request đến `/api/attributes/search`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về missing parameter

#### TC_ATTRIBUTE_037: Tìm kiếm case-insensitive
- **Mô tả:** Verify search không phân biệt hoa thường
- **Điều kiện tiên quyết:** Attribute "Color" tồn tại
- **Input:** GET request đến `/api/attributes/search?keyword=color`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array chứa attribute "Color"

#### TC_ATTRIBUTE_038: Tìm kiếm partial match
- **Mô tả:** Search với substring
- **Điều kiện tiên quyết:** Attributes "Color", "Background Color", "Text Color" tồn tại
- **Input:** GET request đến `/api/attributes/search?keyword=Col`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array chứa tất cả attributes có "Col" trong tên

#### TC_ATTRIBUTE_039: Tìm kiếm với ký tự đặc biệt
- **Input:** GET request đến `/api/attributes/search?keyword=CPU%20(GHz)`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array attributes matching encoded keyword

#### TC_ATTRIBUTE_040: Tìm kiếm performance với large dataset
- **Điều kiện tiên quyết:** Database có 10,000+ attributes
- **Input:** GET request đến `/api/attributes/search?keyword=Test`
- **Expected Output:**
  - Status Code: 200 OK
  - Response time < 1 second
  - Accurate results returned

### 4.7 GET /api/attributes/exists - Kiểm tra tồn tại attribute

#### TC_ATTRIBUTE_041: Kiểm tra attribute tồn tại
- **Mô tả:** Kiểm tra tên attribute đã tồn tại
- **Điều kiện tiên quyết:** Attribute "Color" tồn tại
- **Input:** GET request đến `/api/attributes/exists?name=Color`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `true`

#### TC_ATTRIBUTE_042: Kiểm tra attribute không tồn tại
- **Mô tả:** Kiểm tra tên attribute chưa tồn tại
- **Input:** GET request đến `/api/attributes/exists?name=NonExistentAttribute`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_ATTRIBUTE_043: Kiểm tra với excludeId
- **Mô tả:** Kiểm tra duplicate khi update (loại trừ chính nó)
- **Điều kiện tiên quyết:** Attribute "Color" có id = 1
- **Input:** GET request đến `/api/attributes/exists?name=Color&excludeId=1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false` (loại trừ chính nó)

#### TC_ATTRIBUTE_044: Kiểm tra với excludeId khác
- **Điều kiện tiên quyết:** 
  - Attribute "Color" có id = 1
  - Attribute "Size" có id = 2
- **Input:** GET request đến `/api/attributes/exists?name=Color&excludeId=2`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `true` (không loại trừ)

#### TC_ATTRIBUTE_045: Kiểm tra với tên rỗng
- **Input:** GET request đến `/api/attributes/exists?name=`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_ATTRIBUTE_046: Kiểm tra không có parameter name
- **Input:** GET request đến `/api/attributes/exists`
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_ATTRIBUTE_047: Kiểm tra case sensitivity
- **Điều kiện tiên quyết:** Attribute "Color" tồn tại
- **Input:** GET request đến `/api/attributes/exists?name=color`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false` (nếu case sensitive) hoặc `true` (nếu case insensitive)

#### TC_ATTRIBUTE_048: Kiểm tra với excludeId không tồn tại
- **Input:** GET request đến `/api/attributes/exists?name=Color&excludeId=999999`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `true` (excludeId invalid, không ảnh hưởng kết quả)

---

## 5. Edge Cases & Special Scenarios

### 5.1 Performance Testing
- **TC_ATTRIBUTE_049:** Load test với 10,000+ attributes
- **TC_ATTRIBUTE_050:** Concurrent create operations với same name
- **TC_ATTRIBUTE_051:** Bulk delete operations
- **TC_ATTRIBUTE_052:** Search performance với complex keywords

### 5.2 Security Testing
- **TC_ATTRIBUTE_053:** SQL injection trong attribute name
- **TC_ATTRIBUTE_054:** XSS payload trong attribute name
- **TC_ATTRIBUTE_055:** SQL injection trong search keyword
- **TC_ATTRIBUTE_056:** Path traversal trong endpoints

### 5.3 Boundary Testing
- **TC_ATTRIBUTE_057:** Attribute name với đúng 255 ký tự
- **TC_ATTRIBUTE_058:** Attribute name với 256 ký tự
- **TC_ATTRIBUTE_059:** ID = Integer.MAX_VALUE
- **TC_ATTRIBUTE_060:** ID = Integer.MIN_VALUE

### 5.4 Unicode và Internationalization
- **TC_ATTRIBUTE_061:** Attribute names với Unicode characters (中文, العربية, Русский)
- **TC_ATTRIBUTE_062:** Emoji trong attribute names
- **TC_ATTRIBUTE_063:** Right-to-left language support
- **TC_ATTRIBUTE_064:** Mixed language attribute names

### 5.5 Integration Testing
- **TC_ATTRIBUTE_065:** Attribute deletion khi có product_attributes references
- **TC_ATTRIBUTE_066:** Attribute usage trong product variations
- **TC_ATTRIBUTE_067:** Attribute filtering trong product search
- **TC_ATTRIBUTE_068:** Database transaction rollback scenarios

---

## 6. Test Data Setup

### 6.1 Initial Data
```sql
-- Basic attributes
INSERT INTO attributes (id, name) VALUES
(1, 'Color'),
(2, 'Size'),
(3, 'Material'),
(4, 'Screen Size'),
(5, 'Storage Capacity'),
(6, 'RAM'),
(7, 'CPU Speed'),
(8, 'Brand'),
(9, 'Weight'),
(10, 'Dimensions');

-- Unicode test attributes
INSERT INTO attributes (id, name) VALUES
(11, '颜色'),
(12, 'لون'),
(13, 'Цвет'),
(14, 'Color 🎨'),
(15, 'Screen Size (inches)');

-- Attributes for testing constraints
INSERT INTO product_attributes (product_id, attribute_id, value) VALUES
(1, 1, 'Red'),
(1, 2, 'Large'),
(2, 1, 'Blue');
```

### 6.2 Cleanup Data
```sql
-- Clean test data
DELETE FROM product_attributes WHERE attribute_id IN (SELECT id FROM attributes WHERE name LIKE 'Test%');
DELETE FROM attributes WHERE name LIKE 'Test%';
DELETE FROM attributes WHERE name LIKE '%Updated%';
```

---

## 7. Test Execution Environment

### 7.1 Prerequisites
- Spring Boot application running
- Database với schema đầy đủ
- Test data được setup
- Foreign key constraints configured
- Proper indexing cho search operations

### 7.2 Tools
- **Manual Testing:** Postman/Insomnia
- **Automated Testing:** JUnit + MockMvc/TestRestTemplate
- **Performance Testing:** JMeter cho load testing
- **Unicode Testing:** Various language inputs

---

## 8. Expected Outcomes

### 8.1 Success Criteria
- All CRUD operations work correctly
- Search functionality accurate và performant
- Proper validation và error handling
- Unicode support working
- Foreign key constraints enforced
- Performance within acceptable limits

### 8.2 Risk Assessment
- **High Risk:** Data corruption, foreign key violations, security vulnerabilities
- **Medium Risk:** Performance issues với large datasets, validation bypass
- **Low Risk:** UI/UX issues, minor edge cases

### 8.3 Performance Benchmarks
- GET /attributes: < 500ms cho 10,000 records
- Search operations: < 1s cho complex queries
- Create/Update operations: < 200ms
- Delete operations: < 300ms (including constraint checks)

---

## 9. Special Considerations

### 9.1 Business Logic
- Attributes typically used để define product characteristics
- May be linked to product variations for filtering
- Could be used trong product search và faceted navigation
- Important for e-commerce categorization

### 9.2 Data Consistency
- Ensure attribute names are meaningful và consistent
- Consider normalization vs. denormalization for performance
- Plan for attribute value standardization
- Handle legacy data migration scenarios

---

*Document Version: 1.0*  
*Last Updated: September 30, 2025*  
*Author: Test Team*