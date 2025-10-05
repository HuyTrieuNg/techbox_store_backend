# Category Management API - Test Report

## Overview

### **Project Information**
- **API Base URL**: `http://localhost:8080/api/categories`  
- **Authentication**: JWT Bearer Token (planned for Admin operations)
- **Database**: MySQL với hierarchical categories (parent-child relationships)
- **Framework**: Spring Boot REST API

### **API Functions Covered**
| Function | Endpoint | Method | Description |
|----------|----------|--------|-------------|
| **List Categories** | `/api/categories` | GET | Lấy tất cả categories |
| **Get Category by ID** | `/api/categories/{id}` | GET | Lấy category theo ID |
| **Get Root Categories** | `/api/categories/root` | GET | Lấy categories gốc (parent = null) |
| **Get Child Categories** | `/api/categories/{parentId}/children` | GET | Lấy categories con |
| **Create Category** | `/api/categories` | POST | Tạo category mới |
| **Update Category** | `/api/categories/{id}` | PUT | Cập nhật category |
| **Delete Category** | `/api/categories/{id}` | DELETE | Xóa category |
| **Check Exists** | `/api/categories/exists` | GET | Kiểm tra tên category tồn tại |

## Validation Rules cho Category Name

### **🔒 Security & Data Integrity Rules**

| Rule | Description | Implementation | Test Cases |
|------|-------------|----------------|-------------|
| **Required** | Tên category bắt buộc nhập | `@NotBlank` | TC_CAT_008, 009, 009A |
| **Length** | Độ dài: 2-255 ký tự | `@Size(min=2, max=255)` | TC_CAT_010, 010A |
| **Null/Undefined** | Không được null hoặc undefined | `@NotNull` validation | TC_CAT_009, 009A |
| **Whitespace** | Không được chỉ toàn khoảng trắng | Custom validation | TC_CAT_011, 011A |
| **Special Characters** | Cho phép: a-z, A-Z, 0-9, space, &, -, ', Unicode | Pattern validation | TC_CAT_011B, 011C |
| **Uniqueness** | Tên category phải duy nhất | Database constraint | TC_CAT_012 |
| **Case Insensitive** | Không phân biệt hoa thường | Custom uniqueness check | TC_CAT_012A, 012B |
| **SQL Injection** | Ngăn chặn SQL injection | Input sanitization | TC_CAT_012C |
| **XSS Protection** | Ngăn chặn XSS attacks | HTML/Script filtering | TC_CAT_012D, 011C |
| **Hierarchical Rules** | Parent-child relationship validation | Business logic | TC_CAT_013A-013D |

### **✅ Allowed Characters**
- **Letters**: a-z, A-Z (Latin alphabet)
- **Unicode**: à, á, ả, ã, ạ, ă, ằ, ắ, ẳ, ẵ, ặ, â, ầ, ấ, ẩ, ẫ, ậ, đ, è, é, ẻ, ẽ, ẹ, ê, ề, ế, ể, ễ, ệ, ì, í, ỉ, ĩ, ị, ò, ó, ỏ, õ, ọ, ô, ồ, ố, ổ, ỗ, ộ, ơ, ờ, ớ, ở, ỡ, ợ, ù, ú, ủ, ũ, ụ, ư, ừ, ứ, ử, ữ, ự, ỳ, ý, ỷ, ỹ, ỵ và các ký tự Unicode khác
- **Numbers**: 0-9  
- **Spaces**: Single spaces (auto-trimmed)
- **Special**: & (ampersand), - (hyphen), ' (apostrophe)
- **Examples**: "Electronics", "Thời Trang", "Home & Garden", "Men's Fashion"

### **❌ Blocked Characters**
- **HTML Tags**: `<`, `>`, `<script>`, `<img>`
- **SQL Characters**: `'`, `"`, `;`, `--`, `/*`, `*/`  
- **Script**: `javascript:`, `vbscript:`, `onload=`, `onerror=`
- **Control**: `\n`, `\r`, `\t` (except normal space)

## Test Cases Detail

### **Function 1: Lấy danh sách categories (GET /api/categories)**

| Test Case ID | Test Case Description | Request Format | Expected Results | Pre-conditions |
|--------------|----------------------|----------------|-----------------|--------------| 
| TC_CAT_001 | Kiểm tra lấy danh sách tất cả categories thành công (Public access) | Gửi GET request đến "/api/categories". | - HTTP Status: 200 OK<br>- Response body:<br>[<br>  {<br>    "id": 1,<br>    "name": "Electronics",<br>    "parentCategoryId": null,<br>    "createdAt": "2025-09-30T10:00:00Z",<br>    "updatedAt": "2025-09-30T10:00:00Z"<br>  },<br>  {<br>    "id": 2,<br>    "name": "Smartphones",<br>    "parentCategoryId": 1,<br>    "createdAt": "2025-09-30T10:00:00Z",<br>    "updatedAt": "2025-09-30T10:00:00Z"<br>  }<br>] | Database có categories: Electronics (ID=1, parent=null), Smartphones (ID=2, parent=1)<br>**Không cần authentication** |
| TC_CAT_002 | Kiểm tra lấy danh sách categories khi database trống | Gửi GET request đến "/api/categories". | - HTTP Status: 200 OK<br>- Response body: [] | Database không có category nào<br>**Không cần authentication** |

### **Function 2: Lấy category theo ID (GET /api/categories/{id})**

| Test Case ID | Test Case Description | Request Format | Expected Results | Pre-conditions |
|--------------|----------------------|----------------|-----------------|--------------| 
| TC_CAT_003 | Kiểm tra lấy category theo ID hợp lệ (Public access) | Gửi GET request đến "/api/categories/1". | - HTTP Status: 200 OK<br>- Response body:<br>{<br>  "id": 1,<br>  "name": "Electronics",<br>  "parentCategoryId": null,<br>  "createdAt": "2025-09-30T10:00:00Z",<br>  "updatedAt": "2025-09-30T10:00:00Z"<br>} | Category Electronics với ID=1 tồn tại<br>**Không cần authentication** |
| TC_CAT_004 | Kiểm tra lấy category với ID không tồn tại | Gửi GET request đến "/api/categories/999999". | - HTTP Status: 404 Not Found<br>- Response body:<br>{<br>  "error": "Category not found",<br>  "message": "Category with ID 999999 does not exist"<br>} | ID 999999 không tồn tại trong database<br>**Không cần authentication** |
| TC_CAT_005 | Kiểm tra lấy category với ID không hợp lệ (string) | Gửi GET request đến "/api/categories/abc". | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Invalid parameter",<br>  "message": "ID must be a valid integer"<br>} | **Không cần authentication** |

### **Function 3: Lấy root categories (GET /api/categories/root)**

| Test Case ID | Test Case Description | Request Format | Expected Results | Pre-conditions |
|--------------|----------------------|----------------|-----------------|--------------| 
| TC_CAT_006 | Kiểm tra lấy root categories thành công | Gửi GET request đến "/api/categories/root". | - HTTP Status: 200 OK<br>- Response body:<br>[<br>  {<br>    "id": 1,<br>    "name": "Electronics",<br>    "parentCategoryId": null<br>  },<br>  {<br>    "id": 3,<br>    "name": "Fashion",<br>    "parentCategoryId": null<br>  }<br>] | Database có root categories: Electronics (ID=1), Fashion (ID=3)<br>**Không cần authentication** |
| TC_CAT_007 | Kiểm tra lấy root categories khi không có | Gửi GET request đến "/api/categories/root". | - HTTP Status: 200 OK<br>- Response body: [] | Database chỉ có child categories<br>**Không cần authentication** |

### **Function 4: Lấy child categories (GET /api/categories/{parentId}/children)**

| Test Case ID | Test Case Description | Request Format | Expected Results | Pre-conditions |
|--------------|----------------------|----------------|-----------------|--------------| 
| TC_CAT_007A | Kiểm tra lấy child categories thành công | Gửi GET request đến "/api/categories/1/children". | - HTTP Status: 200 OK<br>- Response body:<br>[<br>  {<br>    "id": 2,<br>    "name": "Smartphones",<br>    "parentCategoryId": 1<br>  },<br>  {<br>    "id": 4,<br>    "name": "Laptops",<br>    "parentCategoryId": 1<br>  }<br>] | Electronics (ID=1) có child categories: Smartphones (ID=2), Laptops (ID=4)<br>**Không cần authentication** |
| TC_CAT_007B | Kiểm tra lấy child categories khi parent không có con | Gửi GET request đến "/api/categories/2/children". | - HTTP Status: 200 OK<br>- Response body: [] | Smartphones (ID=2) không có child categories<br>**Không cần authentication** |
| TC_CAT_007C | Kiểm tra lấy child categories với parent không tồn tại | Gửi GET request đến "/api/categories/999/children". | - HTTP Status: 404 Not Found<br>- Response body:<br>{<br>  "error": "Parent category not found",<br>  "message": "Parent category with ID 999 does not exist"<br>} | ID 999 không tồn tại<br>**Không cần authentication** |

### **Function 5: Tạo category mới (POST /api/categories)**

| Test Case ID | Test Case Description | Request Format | Expected Results | Pre-conditions |
|--------------|----------------------|----------------|-----------------|--------------| 
| TC_CAT_008 | Kiểm tra tạo category mới thành công với role Admin | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Gaming" } | - HTTP Status: 201 Created<br>- Response body:<br>{<br>  "id": 5,<br>  "name": "Gaming",<br>  "parentCategoryId": null,<br>  "createdAt": "2025-09-30T14:00:00Z",<br>  "updatedAt": "2025-09-30T14:00:00Z"<br>} | Admin đã đăng nhập (admin@techbox.com/admin123)<br>JWT token hợp lệ với ADMIN role<br>Category name "Gaming" chưa tồn tại |
| TC_CAT_009 | Kiểm tra tạo category với tên trống | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "" } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Validation failed",<br>  "message": "Category name is required",<br>  "field": "name"<br>} | Admin đã đăng nhập<br>JWT token hợp lệ |
| TC_CAT_009A | Kiểm tra tạo category với tên undefined | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Validation failed",<br>  "message": "Category name is required",<br>  "field": "name"<br>} | Admin đã đăng nhập<br>JWT token hợp lệ |
| TC_CAT_010 | Kiểm tra tạo category với tên quá dài (>255 ký tự) | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "A very long category name that exceeds..." } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Validation failed",<br>  "message": "Category name must not exceed 255 characters",<br>  "field": "name"<br>} | Admin đã đăng nhập<br>JWT token hợp lệ |
| TC_CAT_010A | Kiểm tra tạo category với tên quá ngắn (<2 ký tự) | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "A" } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Validation failed",<br>  "message": "Category name must be at least 2 characters long",<br>  "field": "name"<br>} | Admin đã đăng nhập<br>JWT token hợp lệ |
| TC_CAT_011 | Kiểm tra tạo category với tên chỉ chứa khoảng trắng | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "   " } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Validation failed",<br>  "message": "Category name cannot contain only whitespace",<br>  "field": "name"<br>} | Admin đã đăng nhập<br>JWT token hợp lệ |
| TC_CAT_011A | Kiểm tra tạo category với tên có khoảng trắng đầu/cuối | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "  Books  " } | - HTTP Status: 201 Created<br>- Response body:<br>{<br>  "id": 6,<br>  "name": "Books",<br>  "parentCategoryId": null,<br>  "createdAt": "2025-09-30T14:00:00Z",<br>  "updatedAt": "2025-09-30T14:00:00Z"<br>} | Admin đã đăng nhập<br>Hệ thống tự động trim khoảng trắng |
| TC_CAT_011B | Kiểm tra tạo category với ký tự đặc biệt hợp lệ | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Home & Garden" } | - HTTP Status: 201 Created<br>- Response body:<br>{<br>  "id": 7,<br>  "name": "Home & Garden",<br>  "parentCategoryId": null,<br>  "createdAt": "2025-09-30T14:00:00Z",<br>  "updatedAt": "2025-09-30T14:00:00Z"<br>} | Admin đã đăng nhập<br>Ký tự &, space hợp lệ |
| TC_CAT_011C | Kiểm tra tạo category với ký tự đặc biệt không hợp lệ | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Category<script>alert('xss')</script>" } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Validation failed",<br>  "message": "Category name contains invalid characters",<br>  "field": "name",<br>  "invalidChars": ["<", ">"]<br>} | Admin đã đăng nhập<br>Ngăn chặn XSS attack |
| TC_CAT_012 | Kiểm tra tạo category với tên trùng lặp | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Electronics" } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Duplicate entry",<br>  "message": "Category name 'Electronics' already exists",<br>  "field": "name"<br>} | Admin đã đăng nhập<br>Category "Electronics" đã tồn tại (ID=1) |
| TC_CAT_012A | Kiểm tra tạo category với tên trùng lặp (case insensitive) | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "ELECTRONICS" } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Duplicate entry",<br>  "message": "Category name 'ELECTRONICS' already exists (case insensitive)",<br>  "field": "name",<br>  "existingCategory": "Electronics"<br>} | Admin đã đăng nhập<br>Category "Electronics" đã tồn tại<br>Case insensitive check |
| TC_CAT_012B | Kiểm tra SQL Injection trong tên category | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "'; DROP TABLE categories; --" } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Validation failed",<br>  "message": "Category name contains potentially dangerous characters",<br>  "field": "name",<br>  "securityViolation": "SQL_INJECTION_ATTEMPT"<br>} | Admin đã đăng nhập<br>Ngăn chặn SQL injection |
| TC_CAT_012C | Kiểm tra XSS trong tên category | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "<img src='x' onerror='alert(1)'>" } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Validation failed",<br>  "message": "Category name contains potentially dangerous HTML/Script content",<br>  "field": "name",<br>  "securityViolation": "XSS_ATTEMPT"<br>} | Admin đã đăng nhập<br>Ngăn chặn XSS attack |
| TC_CAT_012D | Kiểm tra Unicode trong tên category | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Thời Trang" } | - HTTP Status: 201 Created<br>- Response body:<br>{<br>  "id": 8,<br>  "name": "Thời Trang",<br>  "parentCategoryId": null,<br>  "createdAt": "2025-09-30T14:00:00Z",<br>  "updatedAt": "2025-09-30T14:00:00Z"<br>} | Admin đã đăng nhập<br>Unicode characters được hỗ trợ |
| TC_CAT_013 | Kiểm tra tạo child category thành công | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Smartphones", "parentCategoryId": 1 } | - HTTP Status: 201 Created<br>- Response body:<br>{<br>  "id": 9,<br>  "name": "Smartphones",<br>  "parentCategoryId": 1,<br>  "createdAt": "2025-09-30T14:00:00Z",<br>  "updatedAt": "2025-09-30T14:00:00Z"<br>} | Admin đã đăng nhập<br>Parent category Electronics (ID=1) tồn tại |
| TC_CAT_013A | Kiểm tra tạo category với parent không tồn tại | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Invalid Child", "parentCategoryId": 999 } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Invalid parent category",<br>  "message": "Parent category with ID 999 does not exist",<br>  "field": "parentCategoryId"<br>} | Admin đã đăng nhập<br>Parent ID 999 không tồn tại |
| TC_CAT_013B | Kiểm tra tạo category với parent = chính nó (circular reference) | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Self Parent", "parentCategoryId": 10 } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Invalid hierarchy",<br>  "message": "Category cannot be its own parent",<br>  "field": "parentCategoryId"<br>} | Admin đã đăng nhập<br>Business logic validation |
| TC_CAT_013C | Kiểm tra tạo category với parentCategoryId = 0 | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Zero Parent", "parentCategoryId": 0 } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Invalid parent category",<br>  "message": "Parent category ID must be a positive integer or null",<br>  "field": "parentCategoryId"<br>} | Admin đã đăng nhập<br>ID validation |
| TC_CAT_014 | Kiểm tra tạo category với role User (không có quyền) | Gửi POST request đến "/api/categories".<br>Header Authorization: Bearer &lt;user_token&gt;.<br>Body: { "name": "Unauthorized Category" } | - HTTP Status: 403 Forbidden<br>- Response body:<br>{<br>  "error": "Access denied",<br>  "message": "Only administrators can create categories"<br>} | User đã đăng nhập (user@techbox.com/user123)<br>JWT token hợp lệ với USER role only |

### **Function 6: Cập nhật category (PUT /api/categories/{id})**

| Test Case ID | Test Case Description | Request Format | Expected Results | Pre-conditions |
|--------------|----------------------|----------------|-----------------|--------------| 
| TC_CAT_015 | Kiểm tra cập nhật category thành công với role Admin | Gửi PUT request đến "/api/categories/1".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Consumer Electronics" } | - HTTP Status: 200 OK<br>- Response body:<br>{<br>  "id": 1,<br>  "name": "Consumer Electronics",<br>  "parentCategoryId": null,<br>  "createdAt": "2025-09-30T10:00:00Z",<br>  "updatedAt": "2025-09-30T14:30:00Z"<br>} | Admin đã đăng nhập<br>Category Electronics (ID=1) tồn tại<br>New name "Consumer Electronics" chưa tồn tại |
| TC_CAT_016 | Kiểm tra cập nhật category không tồn tại | Gửi PUT request đến "/api/categories/999999".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Non-existent Category" } | - HTTP Status: 404 Not Found<br>- Response body:<br>{<br>  "error": "Category not found",<br>  "message": "Category with ID 999999 does not exist"<br>} | Admin đã đăng nhập<br>ID 999999 không tồn tại |
| TC_CAT_017 | Kiểm tra cập nhật category với tên trùng lặp | Gửi PUT request đến "/api/categories/1".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Fashion" } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Duplicate entry",<br>  "message": "Category name 'Fashion' already exists",<br>  "field": "name"<br>} | Admin đã đăng nhập<br>Category Electronics (ID=1), Fashion (ID=3) tồn tại |
| TC_CAT_018 | Kiểm tra cập nhật category với role User (không có quyền) | Gửi PUT request đến "/api/categories/1".<br>Header Authorization: Bearer &lt;user_token&gt;.<br>Body: { "name": "Electronics Updated" } | - HTTP Status: 403 Forbidden<br>- Response body:<br>{<br>  "error": "Access denied",<br>  "message": "Only administrators can update categories"<br>} | User đã đăng nhập<br>Category Electronics (ID=1) tồn tại |
| TC_CAT_019 | Kiểm tra cập nhật parent category thành công | Gửi PUT request đến "/api/categories/2".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Mobile Phones", "parentCategoryId": 3 } | - HTTP Status: 200 OK<br>- Response body:<br>{<br>  "id": 2,<br>  "name": "Mobile Phones",<br>  "parentCategoryId": 3,<br>  "createdAt": "2025-09-30T10:00:00Z",<br>  "updatedAt": "2025-09-30T14:45:00Z"<br>} | Admin đã đăng nhập<br>Category Smartphones (ID=2) và Fashion (ID=3) tồn tại |
| TC_CAT_020 | Kiểm tra cập nhật category với parent tạo circular reference | Gửi PUT request đến "/api/categories/1".<br>Header Authorization: Bearer &lt;admin_token&gt;.<br>Body: { "name": "Electronics", "parentCategoryId": 2 } | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Invalid hierarchy",<br>  "message": "Cannot create circular reference: Electronics is already parent of Smartphones",<br>  "field": "parentCategoryId"<br>} | Admin đã đăng nhập<br>Electronics (ID=1) is parent of Smartphones (ID=2) |

### **Function 7: Xóa category (DELETE /api/categories/{id})**

| Test Case ID | Test Case Description | Request Format | Expected Results | Pre-conditions |
|--------------|----------------------|----------------|-----------------|--------------| 
| TC_CAT_021 | Kiểm tra xóa category thành công (soft delete) với role Admin | Gửi DELETE request đến "/api/categories/4".<br>Header Authorization: Bearer &lt;admin_token&gt;. | - HTTP Status: 204 No Content | Admin đã đăng nhập<br>Category Books (ID=4) tồn tại và không có child categories hoặc products |
| TC_CAT_022 | Kiểm tra xóa category không tồn tại | Gửi DELETE request đến "/api/categories/999999".<br>Header Authorization: Bearer &lt;admin_token&gt;. | - HTTP Status: 404 Not Found<br>- Response body:<br>{<br>  "error": "Category not found",<br>  "message": "Category with ID 999999 does not exist"<br>} | Admin đã đăng nhập<br>ID 999999 không tồn tại |
| TC_CAT_023 | Kiểm tra xóa category có child categories | Gửi DELETE request đến "/api/categories/1".<br>Header Authorization: Bearer &lt;admin_token&gt;. | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Cannot delete category",<br>  "message": "Category has 2 child categories. Please reassign or delete child categories first.",<br>  "childCategories": [2, 4]<br>} | Admin đã đăng nhập<br>Electronics (ID=1) có child categories: Smartphones (ID=2), Laptops (ID=4) |
| TC_CAT_024 | Kiểm tra xóa category đang được sử dụng bởi products | Gửi DELETE request đến "/api/categories/2".<br>Header Authorization: Bearer &lt;admin_token&gt;. | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Cannot delete category",<br>  "message": "Category is being used by 5 products. Please reassign or delete products first.",<br>  "relatedProducts": [101, 102, 103, 104, 105]<br>} | Admin đã đăng nhập<br>Smartphones (ID=2) có products |
| TC_CAT_025 | Kiểm tra xóa category với role User (không có quyền) | Gửi DELETE request đến "/api/categories/4".<br>Header Authorization: Bearer &lt;user_token&gt;. | - HTTP Status: 403 Forbidden<br>- Response body:<br>{<br>  "error": "Access denied",<br>  "message": "Only administrators can delete categories"<br>} | User đã đăng nhập<br>Category Books (ID=4) tồn tại |

### **Function 8: Kiểm tra tồn tại category (GET /api/categories/exists)**

| Test Case ID | Test Case Description | Request Format | Expected Results | Pre-conditions |
|--------------|----------------------|----------------|-----------------|--------------| 
| TC_CAT_026 | Kiểm tra category tồn tại với tên hợp lệ (Public access) | Gửi GET request đến "/api/categories/exists?name=Electronics". | - HTTP Status: 200 OK<br>- Response body:<br>{<br>  "exists": true,<br>  "categoryId": 1,<br>  "name": "Electronics"<br>} | Category "Electronics" tồn tại (ID=1)<br>**Không cần authentication** |
| TC_CAT_027 | Kiểm tra category không tồn tại | Gửi GET request đến "/api/categories/exists?name=NonExistentCategory". | - HTTP Status: 200 OK<br>- Response body:<br>{<br>  "exists": false,<br>  "name": "NonExistentCategory"<br>} | Category "NonExistentCategory" không tồn tại<br>**Không cần authentication** |
| TC_CAT_028 | Kiểm tra với parameter name rỗng | Gửi GET request đến "/api/categories/exists?name=". | - HTTP Status: 400 Bad Request<br>- Response body:<br>{<br>  "error": "Invalid parameter",<br>  "message": "Category name parameter is required",<br>  "field": "name"<br>} | **Không cần authentication** |
| TC_CAT_029 | Kiểm tra case sensitivity trong tên category | Gửi GET request đến "/api/categories/exists?name=electronics". | - HTTP Status: 200 OK<br>- Response body:<br>{<br>  "exists": true,<br>  "categoryId": 1,<br>  "name": "Electronics",<br>  "searchedName": "electronics"<br>} | Category "Electronics" tồn tại (case insensitive match)<br>**Không cần authentication** |

---

## Summary

### **📊 Test Coverage Overview**

| Function | Test Cases | Coverage |
|----------|------------|----------|
| **GET /api/categories** | TC_CAT_001-002 | ✅ List, Empty, Public Access |
| **GET /api/categories/{id}** | TC_CAT_003-005 | ✅ Valid ID, Invalid ID, Not Found |
| **GET /api/categories/root** | TC_CAT_006-007 | ✅ Root categories, Empty |
| **GET /api/categories/{parentId}/children** | TC_CAT_007A-007C | ✅ Child categories, Empty, Invalid parent |
| **POST /api/categories** | TC_CAT_008-014 | ✅ Success, Validation, Hierarchy, Security |
| **PUT /api/categories/{id}** | TC_CAT_015-020 | ✅ Update, Validation, Hierarchy, Security |
| **DELETE /api/categories/{id}** | TC_CAT_021-025 | ✅ Delete, Constraints, Hierarchy, Permissions |
| **GET /api/categories/exists** | TC_CAT_026-029 | ✅ Exists Check, Parameters |

**Total Test Cases: 39**

### **🔐 Security Test Coverage**

| Security Aspect | Test Cases | Status |
|------------------|------------|--------|
| **Authentication** | TC_CAT_014, 018, 025 | ✅ Role-based access |
| **Input Validation** | TC_CAT_009-012D | ✅ Comprehensive |
| **SQL Injection** | TC_CAT_012B | ✅ Protected |
| **XSS Protection** | TC_CAT_012C, 011C | ✅ Filtered |
| **Data Integrity** | TC_CAT_012, 012A | ✅ Uniqueness |
| **Hierarchical Logic** | TC_CAT_013A-013C, 020, 023 | ✅ Business rules |
| **Length Validation** | TC_CAT_010, 010A | ✅ Min/Max limits |

### **🏗️ Hierarchical Features Coverage**

| Hierarchy Aspect | Test Cases | Status |
|------------------|------------|--------|
| **Parent-Child Creation** | TC_CAT_013, 013A | ✅ Valid/Invalid parent |
| **Circular Reference** | TC_CAT_013B, 020 | ✅ Prevention |
| **Root Categories** | TC_CAT_006-007 | ✅ Listing |
| **Child Categories** | TC_CAT_007A-007C | ✅ Listing by parent |
| **Cascade Constraints** | TC_CAT_023, 024 | ✅ Delete restrictions |
| **Parent Updates** | TC_CAT_019, 020 | ✅ Hierarchy validation |

### **💡 Implementation Notes**

1. **Hierarchical Validation**: Must prevent circular references and validate parent existence
2. **Cascade Rules**: Categories with children or products cannot be deleted
3. **Case Sensitivity**: Database queries for uniqueness check should be case-insensitive
4. **Input Sanitization**: Auto-trim whitespace, encode HTML entities
5. **Business Logic**: Root categories (parentCategoryId = null) vs child categories
6. **Error Messages**: Provide clear, security-conscious error messages
7. **Logging**: Log all security validation failures and hierarchy violations

---

## Authentication & Authorization Details

### **Login để lấy JWT Token**

Gửi POST request đến "/api/auth/login".
Body: 
```json
{
  "username": "admin@techbox.com",
  "password": "admin123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "admin@techbox.com", 
    "role": "ADMIN"
  },
  "expiresIn": 3600
}
```

### **Role-based Access Control - Logic hợp lý cho E-commerce**

| Role | Permissions | API Access | Business Logic |
|------|-------------|-------------|----------------|
| **ADMIN** | Full management access | ✅ GET, POST, PUT, DELETE /api/categories/* | Quản lý toàn bộ danh mục |
| **MANAGER** | View only (for reporting) | ✅ GET /api/categories/* <br> ❌ POST, PUT, DELETE | Xem để báo cáo, không thay đổi |
| **USER** | View for shopping | ✅ GET /api/categories/* <br> ❌ POST, PUT, DELETE | Xem categories để mua sắm |
| **ANONYMOUS** | Public view | ✅ GET /api/categories/* <br> ❌ POST, PUT, DELETE | Khách vãng lai xem sản phẩm |

**💡 Logic nghiệp vụ hợp lý:**
- 🛍️ **Xem categories**: Public access - khách hàng cần thấy categories để navigate
- 🔐 **Quản lý categories**: Chỉ Admin - vì ảnh hưởng trực tiếp đến cấu trúc site
- 📊 **Manager**: Chỉ xem để báo cáo, không được thay đổi dữ liệu
- 🏗️ **Hierarchical structure**: Critical cho navigation và SEO

**⚠️ Cần cập nhật SecurityConfig:**
```java
.requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
.requestMatchers("/api/categories/**").hasRole("ADMIN")
```

### **Test Accounts**
```
Admin: admin@techbox.com / admin123
Manager: manager@techbox.com / manager123  
User: user@techbox.com / user123
```

### **Test Data Setup**
```sql
-- Root Categories
INSERT INTO categories (id, name, parent_category_id) VALUES 
(1, 'Electronics', NULL),
(3, 'Fashion', NULL);

-- Child Categories  
INSERT INTO categories (id, name, parent_category_id) VALUES
(2, 'Smartphones', 1),
(4, 'Laptops', 1);
```