# Brand API Test Plan & Test Cases

## 1. API Overview

**Base URL:** `/api/brands`

**Controller:** `BrandController`

**Endpoints:**
- `GET /api/brands` - Lấy tất cả brands
- `GET /api/brands/{id}` - Lấy brand theo ID
- `POST /api/brands` - Tạo brand mới
- `PUT /api/brands/{id}` - Cập nhật brand
- `DELETE /api/brands/{id}` - Xóa brand
- `GET /api/brands/exists?name=<name>` - Kiểm tra tồn tại brand theo tên

---

## 2. Data Models

### BrandCreateRequest
```json
{
  "name": "string" // @NotBlank, @Size(max=255)
}
```

### BrandUpdateRequest
```json
{
  "name": "string" // @NotBlank, @Size(max=255)
}
```

### BrandResponse
```json
{
  "id": "integer",
  "name": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

---

## 3. Test Strategy

### 3.1 Test Categories
- **Functional Testing:** Kiểm tra tính năng cơ bản của từng endpoint
- **Validation Testing:** Kiểm tra ràng buộc dữ liệu đầu vào
- **Error Handling:** Kiểm tra xử lý lỗi và exception
- **Edge Cases:** Kiểm tra các trường hợp biên
- **Data Integrity:** Kiểm tra tính toàn vẹn dữ liệu

### 3.2 Test Levels
- **Unit Test:** Test từng method trong controller
- **Integration Test:** Test tích hợp với service và database
- **API Test:** Test end-to-end qua HTTP requests

---

## 4. Detailed Test Cases

### 4.1 GET /api/brands - Lấy tất cả brands

#### TC_BRAND_001: Lấy danh sách brands thành công
- **Mô tả:** Kiểm tra lấy tất cả brands khi có dữ liệu
- **Điều kiện tiên quyết:** Database có ít nhất 1 brand
- **Input:** GET request đến `/api/brands`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array of BrandResponse objects
  - Content-Type: application/json

#### TC_BRAND_002: Lấy danh sách brands khi không có dữ liệu
- **Mô tả:** Kiểm tra khi database không có brand nào
- **Điều kiện tiên quyết:** Database không có brand
- **Input:** GET request đến `/api/brands`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` (empty array)

### 4.2 GET /api/brands/{id} - Lấy brand theo ID

#### TC_BRAND_003: Lấy brand theo ID hợp lệ
- **Mô tả:** Kiểm tra lấy brand với ID tồn tại
- **Điều kiện tiên quyết:** Brand với ID cụ thể tồn tại trong database
- **Input:** GET request đến `/api/brands/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: BrandResponse object với ID = 1

#### TC_BRAND_004: Lấy brand với ID không tồn tại
- **Mô tả:** Kiểm tra khi ID brand không tồn tại
- **Input:** GET request đến `/api/brands/999999`
- **Expected Output:**
  - Status Code: 404 Not Found

#### TC_BRAND_005: Lấy brand với ID không hợp lệ
- **Mô tả:** Kiểm tra với ID không phải số nguyên
- **Input:** GET request đến `/api/brands/abc`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.3 POST /api/brands - Tạo brand mới

#### TC_BRAND_006: Tạo brand thành công
- **Mô tả:** Tạo brand với dữ liệu hợp lệ
- **Input:**
  ```json
  {
    "name": "Apple"
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: BrandResponse object với name = "Apple"
  - Database: Brand mới được tạo

#### TC_BRAND_007: Tạo brand với tên trống
- **Mô tả:** Kiểm tra validation khi name rỗng
- **Input:**
  ```json
  {
    "name": ""
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Brand name is required"

#### TC_BRAND_008: Tạo brand với tên null
- **Mô tả:** Kiểm tra validation khi name = null
- **Input:**
  ```json
  {
    "name": null
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Brand name is required"

#### TC_BRAND_009: Tạo brand với tên quá dài
- **Mô tả:** Kiểm tra validation khi name > 255 ký tự
- **Input:**
  ```json
  {
    "name": "A very long brand name that exceeds the maximum length of 255 characters. This string is intentionally created to be longer than the allowed limit to test the validation constraint that prevents brand names from being too long and potentially causing database or display issues in the application interface and other systems that interact with this data."
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Brand name must not exceed 255 characters"

#### TC_BRAND_010: Tạo brand với tên chỉ chứa khoảng trắng
- **Mô tả:** Kiểm tra validation với string chỉ có spaces
- **Input:**
  ```json
  {
    "name": "   "
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Brand name is required"

#### TC_BRAND_011: Tạo brand với tên trùng lặp
- **Mô tả:** Kiểm tra ràng buộc unique constraint
- **Điều kiện tiên quyết:** Brand "Samsung" đã tồn tại
- **Input:**
  ```json
  {
    "name": "Samsung"
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message liên quan đến duplicate name

#### TC_BRAND_012: Tạo brand với ký tự đặc biệt
- **Mô tả:** Kiểm tra tên chứa ký tự đặc biệt
- **Input:**
  ```json
  {
    "name": "Brand@#$%"
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created (nếu hệ thống cho phép)
  - hoặc 400 Bad Request (nếu có validation)

### 4.4 PUT /api/brands/{id} - Cập nhật brand

#### TC_BRAND_013: Cập nhật brand thành công
- **Mô tả:** Cập nhật brand với dữ liệu hợp lệ
- **Điều kiện tiên quyết:** Brand với ID = 1 tồn tại
- **Input:**
  - URL: `/api/brands/1`
  - Body:
    ```json
    {
      "name": "Apple Updated"
    }
    ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: BrandResponse với name = "Apple Updated"
  - Database: Brand được cập nhật

#### TC_BRAND_014: Cập nhật brand không tồn tại
- **Mô tả:** Cập nhật brand với ID không tồn tại
- **Input:**
  - URL: `/api/brands/999999`
  - Body:
    ```json
    {
      "name": "Non-existent Brand"
    }
    ```
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_BRAND_015: Cập nhật brand với tên trống
- **Mô tả:** Validation khi update với name rỗng
- **Input:**
  - URL: `/api/brands/1`
  - Body:
    ```json
    {
      "name": ""
    }
    ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Brand name is required"

#### TC_BRAND_016: Cập nhật brand với tên trùng lặp
- **Mô tả:** Cập nhật brand thành tên đã tồn tại
- **Điều kiện tiên quyết:** 
  - Brand ID = 1 tồn tại với name = "Apple"
  - Brand ID = 2 tồn tại với name = "Samsung"
- **Input:**
  - URL: `/api/brands/1`
  - Body:
    ```json
    {
      "name": "Samsung"
    }
    ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message liên quan đến duplicate name

### 4.5 DELETE /api/brands/{id} - Xóa brand

#### TC_BRAND_017: Xóa brand thành công
- **Mô tả:** Xóa brand tồn tại
- **Điều kiện tiên quyết:** Brand với ID = 1 tồn tại và không được sử dụng
- **Input:** DELETE request đến `/api/brands/1`
- **Expected Output:**
  - Status Code: 204 No Content
  - Database: Brand bị xóa hoặc đánh dấu deleted

#### TC_BRAND_018: Xóa brand không tồn tại
- **Mô tả:** Xóa brand với ID không tồn tại
- **Input:** DELETE request đến `/api/brands/999999`
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_BRAND_019: Xóa brand đang được sử dụng
- **Mô tả:** Xóa brand đang được tham chiếu bởi products
- **Điều kiện tiên quyết:** Brand có products đang sử dụng
- **Input:** DELETE request đến `/api/brands/1`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về foreign key constraint

### 4.6 GET /api/brands/exists - Kiểm tra tồn tại brand

#### TC_BRAND_020: Kiểm tra brand tồn tại
- **Mô tả:** Kiểm tra tên brand đã tồn tại
- **Điều kiện tiên quyết:** Brand "Apple" tồn tại
- **Input:** GET request đến `/api/brands/exists?name=Apple`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `true`

#### TC_BRAND_021: Kiểm tra brand không tồn tại
- **Mô tả:** Kiểm tra tên brand chưa tồn tại
- **Input:** GET request đến `/api/brands/exists?name=NonExistentBrand`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_BRAND_022: Kiểm tra với tên rỗng
- **Mô tả:** Kiểm tra với parameter name rỗng
- **Input:** GET request đến `/api/brands/exists?name=`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_BRAND_023: Kiểm tra không có parameter
- **Mô tả:** Không truyền parameter name
- **Input:** GET request đến `/api/brands/exists`
- **Expected Output:**
  - Status Code: 400 Bad Request

---

## 5. Edge Cases & Special Scenarios

### 5.1 Performance Testing
- **TC_BRAND_024:** Test với số lượng lớn brands (10,000+ records)
- **TC_BRAND_025:** Test concurrent requests (multiple users cùng lúc)

### 5.2 Security Testing
- **TC_BRAND_026:** SQL Injection trong tên brand
- **TC_BRAND_027:** XSS payload trong tên brand
- **TC_BRAND_028:** Authentication/Authorization (nếu có)

### 5.3 Boundary Testing
- **TC_BRAND_029:** Tên brand với đúng 255 ký tự
- **TC_BRAND_030:** Tên brand với 256 ký tự
- **TC_BRAND_031:** ID = 0, ID = -1, ID = MAX_INTEGER

---

## 6. Test Data Setup

### 6.1 Initial Data
```sql
INSERT INTO brands (id, name, created_at, updated_at) VALUES
(1, 'Apple', NOW(), NOW()),
(2, 'Samsung', NOW(), NOW()),
(3, 'Sony', NOW(), NOW());
```

### 6.2 Cleanup Data
```sql
DELETE FROM brands WHERE name LIKE 'Test%';
```

---

## 7. Test Execution Environment

### 7.1 Prerequisites
- Spring Boot application running
- Database với schema đầy đủ
- Test data được setup
- API documentation available

### 7.2 Tools
- **Manual Testing:** Postman/Insomnia
- **Automated Testing:** JUnit + MockMvc/TestRestTemplate
- **Load Testing:** JMeter (nếu cần)

---

## 8. Expected Outcomes

### 8.1 Success Criteria
- Tất cả positive test cases pass
- Negative test cases trả về đúng error codes
- Validation messages rõ ràng và hữu ích
- Performance trong giới hạn chấp nhận được

### 8.2 Risk Assessment
- **High Risk:** Data corruption, security vulnerabilities
- **Medium Risk:** Performance issues, validation bypass
- **Low Risk:** UI/UX issues trong error messages

---

## 9. Test Reporting

### 9.1 Metrics
- Test cases passed/failed
- Code coverage
- Response time metrics
- Error rate

### 9.2 Bug Tracking
- Priority levels: Critical, High, Medium, Low
- Categories: Functional, Performance, Security, Usability

---

*Document Version: 1.0*  
*Last Updated: September 30, 2025*  
*Author: Test Team*