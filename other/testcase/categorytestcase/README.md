# Category API Test Plan & Test Cases

## 1. API Overview

**Base URL:** `/api/categories`

**Controller:** `CategoryController`

**Endpoints:**
- `GET /api/categories` - Lấy tất cả categories
- `GET /api/categories/{id}` - Lấy category theo ID
- `GET /api/categories/root` - Lấy tất cả root categories (không có parent)
- `GET /api/categories/{parentId}/children` - Lấy categories con theo parentId
- `POST /api/categories` - Tạo category mới
- `PUT /api/categories/{id}` - Cập nhật category
- `DELETE /api/categories/{id}` - Xóa category
- `GET /api/categories/exists?name=<name>` - Kiểm tra tồn tại category theo tên

---

## 2. Data Models

### CategoryCreateRequest
```json
{
  "name": "string", // @NotBlank, @Size(max=255)
  "parentCategoryId": "integer" // optional
}
```

### CategoryUpdateRequest
```json
{
  "name": "string", // @NotBlank, @Size(max=255)
  "parentCategoryId": "integer" // optional
}
```

### CategoryResponse
```json
{
  "id": "integer",
  "name": "string",
  "parentCategoryId": "integer",
  "parentCategoryName": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "childCategories": [CategoryResponse] // nested structure
}
```

---

## 3. Test Strategy

### 3.1 Test Categories
- **Functional Testing:** Kiểm tra tính năng cơ bản của từng endpoint
- **Hierarchical Testing:** Kiểm tra quan hệ parent-child categories
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

### 4.1 GET /api/categories - Lấy tất cả categories

#### TC_CATEGORY_001: Lấy danh sách categories thành công
- **Mô tả:** Kiểm tra lấy tất cả categories khi có dữ liệu
- **Điều kiện tiên quyết:** Database có ít nhất 1 category
- **Input:** GET request đến `/api/categories`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array of CategoryResponse objects
  - Content-Type: application/json
  - Verify nested childCategories structure

#### TC_CATEGORY_002: Lấy danh sách categories khi không có dữ liệu
- **Mô tả:** Kiểm tra khi database không có category nào
- **Điều kiện tiên quyết:** Database không có category
- **Input:** GET request đến `/api/categories`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` (empty array)

### 4.2 GET /api/categories/{id} - Lấy category theo ID

#### TC_CATEGORY_003: Lấy category theo ID hợp lệ
- **Mô tả:** Kiểm tra lấy category với ID tồn tại
- **Điều kiện tiên quyết:** Category với ID cụ thể tồn tại trong database
- **Input:** GET request đến `/api/categories/1`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: CategoryResponse object với ID = 1
  - Verify parentCategoryName và childCategories nếu có

#### TC_CATEGORY_004: Lấy category với ID không tồn tại
- **Mô tả:** Kiểm tra khi ID category không tồn tại
- **Input:** GET request đến `/api/categories/999999`
- **Expected Output:**
  - Status Code: 404 Not Found

#### TC_CATEGORY_005: Lấy category với ID không hợp lệ
- **Mô tả:** Kiểm tra với ID không phải số nguyên
- **Input:** GET request đến `/api/categories/abc`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.3 GET /api/categories/root - Lấy root categories

#### TC_CATEGORY_006: Lấy root categories thành công
- **Mô tả:** Lấy tất cả categories không có parent
- **Điều kiện tiên quyết:** Database có root categories (parentCategoryId = null)
- **Input:** GET request đến `/api/categories/root`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array of CategoryResponse với parentCategoryId = null
  - Verify childCategories được populate

#### TC_CATEGORY_007: Lấy root categories khi không có
- **Mô tả:** Kiểm tra khi không có root category nào
- **Điều kiện tiên quyết:** Tất cả categories đều có parent
- **Input:** GET request đến `/api/categories/root`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` (empty array)

### 4.4 GET /api/categories/{parentId}/children - Lấy categories con

#### TC_CATEGORY_008: Lấy categories con thành công
- **Mô tả:** Lấy tất cả categories con của một parent
- **Điều kiện tiên quyết:** Parent category ID = 1 có categories con
- **Input:** GET request đến `/api/categories/1/children`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: Array of CategoryResponse với parentCategoryId = 1

#### TC_CATEGORY_009: Lấy categories con của parent không có con
- **Mô tả:** Parent tồn tại nhưng không có category con
- **Input:** GET request đến `/api/categories/1/children`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` (empty array)

#### TC_CATEGORY_010: Lấy categories con của parent không tồn tại
- **Mô tả:** ParentId không tồn tại trong database
- **Input:** GET request đến `/api/categories/999999/children`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `[]` (empty array)

### 4.5 POST /api/categories - Tạo category mới

#### TC_CATEGORY_011: Tạo root category thành công
- **Mô tả:** Tạo category không có parent
- **Input:**
  ```json
  {
    "name": "Electronics"
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: CategoryResponse với name = "Electronics", parentCategoryId = null

#### TC_CATEGORY_012: Tạo child category thành công
- **Mô tả:** Tạo category có parent hợp lệ
- **Điều kiện tiên quyết:** Parent category ID = 1 tồn tại
- **Input:**
  ```json
  {
    "name": "Smartphones",
    "parentCategoryId": 1
  }
  ```
- **Expected Output:**
  - Status Code: 201 Created
  - Response Body: CategoryResponse với parentCategoryId = 1, parentCategoryName được populate

#### TC_CATEGORY_013: Tạo category với tên trống
- **Mô tả:** Kiểm tra validation khi name rỗng
- **Input:**
  ```json
  {
    "name": ""
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Category name is required"

#### TC_CATEGORY_014: Tạo category với tên null
- **Mô tả:** Kiểm tra validation khi name = null
- **Input:**
  ```json
  {
    "name": null
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Category name is required"

#### TC_CATEGORY_015: Tạo category với tên quá dài
- **Mô tả:** Kiểm tra validation khi name > 255 ký tự
- **Input:**
  ```json
  {
    "name": "A very long category name that exceeds the maximum length of 255 characters. This string is intentionally created to be longer than the allowed limit to test the validation constraint that prevents category names from being too long and potentially causing database or display issues in the application interface and other systems that interact with this data."
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message: "Category name must not exceed 255 characters"

#### TC_CATEGORY_016: Tạo category với parentId không tồn tại
- **Mô tả:** Parent category không tồn tại trong database
- **Input:**
  ```json
  {
    "name": "Invalid Parent Category",
    "parentCategoryId": 999999
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về invalid parent category

#### TC_CATEGORY_017: Tạo category với tên trùng lặp
- **Mô tả:** Kiểm tra ràng buộc unique constraint (nếu có)
- **Điều kiện tiên quyết:** Category "Electronics" đã tồn tại
- **Input:**
  ```json
  {
    "name": "Electronics"
  }
  ```
- **Expected Output:**
  - Status Code: 400 Bad Request (nếu có unique constraint)
  - hoặc 201 Created (nếu cho phép duplicate names)

#### TC_CATEGORY_018: Tạo category với circular reference
- **Mô tả:** Thử tạo category mà parent của nó lại là con của chính nó
- **Điều kiện tiên quyết:** Setup hierarchy A -> B
- **Input:** Tạo category C với parent = A, sau đó update A có parent = C
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về circular reference

### 4.6 PUT /api/categories/{id} - Cập nhật category

#### TC_CATEGORY_019: Cập nhật category thành công
- **Mô tả:** Cập nhật tên category
- **Điều kiện tiên quyết:** Category với ID = 1 tồn tại
- **Input:**
  - URL: `/api/categories/1`
  - Body:
    ```json
    {
      "name": "Electronics Updated"
    }
    ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: CategoryResponse với name = "Electronics Updated"

#### TC_CATEGORY_020: Cập nhật parent category
- **Mô tả:** Thay đổi parent của category
- **Điều kiện tiên quyết:** Category ID = 1, 2 tồn tại
- **Input:**
  - URL: `/api/categories/1`
  - Body:
    ```json
    {
      "name": "Smartphones",
      "parentCategoryId": 2
    }
    ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: CategoryResponse với parentCategoryId = 2

#### TC_CATEGORY_021: Cập nhật category thành root
- **Mô tả:** Chuyển category từ có parent thành root
- **Input:**
  - URL: `/api/categories/1`
  - Body:
    ```json
    {
      "name": "New Root Category",
      "parentCategoryId": null
    }
    ```
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: CategoryResponse với parentCategoryId = null

#### TC_CATEGORY_022: Cập nhật category không tồn tại
- **Mô tả:** Cập nhật category với ID không tồn tại
- **Input:**
  - URL: `/api/categories/999999`
  - Body:
    ```json
    {
      "name": "Non-existent Category"
    }
    ```
- **Expected Output:**
  - Status Code: 400 Bad Request

#### TC_CATEGORY_023: Cập nhật với circular reference
- **Mô tả:** Tạo vòng lặp trong hierarchy
- **Điều kiện tiên quyết:** A (id=1) -> B (id=2) -> C (id=3)
- **Input:** Update A có parentCategoryId = 3
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về circular reference

### 4.7 DELETE /api/categories/{id} - Xóa category

#### TC_CATEGORY_024: Xóa leaf category thành công
- **Mô tả:** Xóa category không có con và không được sử dụng
- **Điều kiện tiên quyết:** Category không có children và không có products
- **Input:** DELETE request đến `/api/categories/1`
- **Expected Output:**
  - Status Code: 204 No Content
  - Database: Category bị xóa hoặc đánh dấu deleted

#### TC_CATEGORY_025: Xóa category có children
- **Mô tả:** Thử xóa category đang có categories con
- **Điều kiện tiên quyết:** Category có child categories
- **Input:** DELETE request đến `/api/categories/1`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về constraint violation

#### TC_CATEGORY_026: Xóa category đang được sử dụng
- **Mô tả:** Xóa category đang được tham chiếu bởi products
- **Điều kiện tiên quyết:** Category có products đang sử dụng
- **Input:** DELETE request đến `/api/categories/1`
- **Expected Output:**
  - Status Code: 400 Bad Request
  - Error message về foreign key constraint

#### TC_CATEGORY_027: Xóa category không tồn tại
- **Mô tả:** Xóa category với ID không tồn tại
- **Input:** DELETE request đến `/api/categories/999999`
- **Expected Output:**
  - Status Code: 400 Bad Request

### 4.8 GET /api/categories/exists - Kiểm tra tồn tại category

#### TC_CATEGORY_028: Kiểm tra category tồn tại
- **Mô tả:** Kiểm tra tên category đã tồn tại
- **Điều kiện tiên quyết:** Category "Electronics" tồn tại
- **Input:** GET request đến `/api/categories/exists?name=Electronics`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `true`

#### TC_CATEGORY_029: Kiểm tra category không tồn tại
- **Mô tả:** Kiểm tra tên category chưa tồn tại
- **Input:** GET request đến `/api/categories/exists?name=NonExistentCategory`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_CATEGORY_030: Kiểm tra với tên rỗng
- **Mô tả:** Kiểm tra với parameter name rỗng
- **Input:** GET request đến `/api/categories/exists?name=`
- **Expected Output:**
  - Status Code: 200 OK
  - Response Body: `false`

#### TC_CATEGORY_031: Kiểm tra không có parameter
- **Mô tả:** Không truyền parameter name
- **Input:** GET request đến `/api/categories/exists`
- **Expected Output:**
  - Status Code: 400 Bad Request

---

## 5. Hierarchical Structure Testing

### 5.1 Deep Hierarchy Testing
- **TC_CATEGORY_032:** Test với hierarchy sâu (5+ levels)
- **TC_CATEGORY_033:** Test move category giữa các branches khác nhau
- **TC_CATEGORY_034:** Test orphan categories (parent bị xóa)

### 5.2 Performance Testing
- **TC_CATEGORY_035:** Test với số lượng lớn categories (10,000+ records)
- **TC_CATEGORY_036:** Test với deep nesting và nhiều children
- **TC_CATEGORY_037:** Test concurrent hierarchy modifications

---

## 6. Edge Cases & Special Scenarios

### 6.1 Security Testing
- **TC_CATEGORY_038:** SQL Injection trong tên category
- **TC_CATEGORY_039:** XSS payload trong tên category
- **TC_CATEGORY_040:** Path traversal trong hierarchy

### 6.2 Boundary Testing
- **TC_CATEGORY_041:** Tên category với đúng 255 ký tự
- **TC_CATEGORY_042:** Maximum depth hierarchy
- **TC_CATEGORY_043:** Maximum children per parent

---

## 7. Test Data Setup

### 7.1 Initial Hierarchy Data
```sql
-- Root categories
INSERT INTO categories (id, name, parent_category_id, created_at, updated_at) VALUES
(1, 'Electronics', NULL, NOW(), NOW()),
(2, 'Clothing', NULL, NOW(), NOW()),
(3, 'Books', NULL, NOW(), NOW());

-- Child categories
INSERT INTO categories (id, name, parent_category_id, created_at, updated_at) VALUES
(4, 'Smartphones', 1, NOW(), NOW()),
(5, 'Laptops', 1, NOW(), NOW()),
(6, 'Men Clothing', 2, NOW(), NOW()),
(7, 'Women Clothing', 2, NOW(), NOW());

-- Grandchild categories
INSERT INTO categories (id, name, parent_category_id, created_at, updated_at) VALUES
(8, 'iPhone', 4, NOW(), NOW()),
(9, 'Samsung', 4, NOW(), NOW()),
(10, 'Gaming Laptops', 5, NOW(), NOW());
```

### 7.2 Cleanup Data
```sql
-- Delete test categories (in reverse dependency order)
DELETE FROM categories WHERE name LIKE 'Test%';
DELETE FROM categories WHERE parent_category_id IN 
  (SELECT id FROM categories WHERE name LIKE 'Test%');
```

---

## 8. Test Execution Environment

### 8.1 Prerequisites
- Spring Boot application running
- Database với schema đầy đủ và foreign key constraints
- Test data hierarchy được setup
- Proper indexing cho parent-child queries

### 8.2 Tools
- **Manual Testing:** Postman/Insomnia
- **Automated Testing:** JUnit + MockMvc/TestRestTemplate
- **Hierarchy Visualization:** Database tools để verify structure

---

## 9. Expected Outcomes

### 9.1 Success Criteria
- Tất cả CRUD operations work correctly
- Hierarchy integrity được maintain
- Circular reference được prevent
- Performance acceptable cho deep hierarchies
- Proper error handling cho constraint violations

### 9.2 Risk Assessment
- **High Risk:** Data corruption trong hierarchy, orphan categories
- **Medium Risk:** Performance issues với deep nesting, circular references
- **Low Risk:** UI/UX issues trong navigation

---

## 10. Special Considerations

### 10.1 Hierarchy Integrity
- Parent-child relationships must be consistent
- No circular references allowed
- Orphan detection và handling
- Cascade delete behavior

### 10.2 Performance Considerations
- Efficient queries cho tree traversal
- Pagination cho large category lists
- Caching strategies cho frequently accessed hierarchies

---

*Document Version: 1.0*  
*Last Updated: September 30, 2025*  
*Author: Test Team*