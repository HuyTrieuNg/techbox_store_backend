# Category API Test Case Form Report

## Tổng quan
- **API Module**: Category Management
- **Base URL**: `/api/categories`
- **Authentication**: Public access for GET, Admin required for POST/PUT/DELETE
- **Test Environment**: Development/Staging

---

## Function 1: Lấy danh sách danh mục (GET /api/categories)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_001 | Kiểm tra lấy danh sách tất cả categories thành công (Public access) | Gửi GET request đến "/api/categories" | - HTTP Status: 200 OK<br>- Response body: Array of categories with id, name, parentCategoryId, createdAt, updatedAt | - Database có categories: Electronics (ID=1), Smartphones (ID=2)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_002 | Kiểm tra lấy danh sách categories khi database trống | Gửi GET request đến "/api/categories" | - HTTP Status: 200 OK<br>- Response body: [] | - Database không có category nào<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 2: Lấy danh mục theo ID (GET /api/categories/{id})

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_003 | Kiểm tra lấy category theo ID hợp lệ (Public access) | Gửi GET request đến "/api/categories/1" | - HTTP Status: 200 OK<br>- Response body: Category object with id=1, name="Electronics" | - Category Electronics với ID=1 tồn tại<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_004 | Kiểm tra lấy category với ID không tồn tại | Gửi GET request đến "/api/categories/999999" | - HTTP Status: 404 Not Found<br>- Error message: "Category not found" | - Category ID 999999 không tồn tại trong database<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_005 | Kiểm tra lấy category với ID không hợp lệ (không phải số nguyên) | Gửi GET request đến "/api/categories/abc" | - HTTP Status: 400 Bad Request<br>- Error message: "ID must be a valid integer" | - Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 3: Lấy danh mục gốc (GET /api/categories/root)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_006 | Kiểm tra lấy root categories thành công (Public access) | Gửi GET request đến "/api/categories/root" | - HTTP Status: 200 OK<br>- Response body: Array of root categories (parentCategoryId=null) | - Database có root categories: Electronics (ID=1), Fashion (ID=3)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_007 | Kiểm tra lấy root categories khi không có root category nào | Gửi GET request đến "/api/categories/root" | - HTTP Status: 200 OK<br>- Response body: [] | - Database không có root category nào (tất cả đều có parent)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 4: Lấy danh mục con (GET /api/categories/{parentId}/children)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_007A | Kiểm tra lấy child categories thành công (Public access) | Gửi GET request đến "/api/categories/1/children" | - HTTP Status: 200 OK<br>- Response body: Array of child categories with parentCategoryId=1 | - Electronics (ID=1) có child categories: Smartphones (ID=2), Laptops (ID=4)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_007B | Kiểm tra lấy child categories khi parent không có con | Gửi GET request đến "/api/categories/2/children" | - HTTP Status: 200 OK<br>- Response body: [] | - Smartphones (ID=2) không có child categories<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_007C | Kiểm tra lấy child categories với parent không tồn tại | Gửi GET request đến "/api/categories/999999/children" | - HTTP Status: 404 Not Found<br>- Error message: "Parent category not found" | - Parent ID 999999 không tồn tại<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 5: Tạo danh mục mới (POST /api/categories)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_008 | Kiểm tra tạo root category thành công với role Admin | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Books & Media" } | - HTTP Status: 201 Created<br>- Response body: Created category object with auto-generated ID | - Admin đã đăng nhập với tài khoản (admin@techbox.com/admin123)<br>- JWT token hợp lệ với ADMIN role<br>- Category "Books & Media" chưa tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_CAT_009 | Kiểm tra tạo category với tên trống | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "" } | - HTTP Status: 400 Bad Request<br>- Error message: "Category name is required" | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role | Pending | | | Pending | | | Pending | | | |
| TC_CAT_010 | Kiểm tra tạo category với tên quá ngắn (<2 ký tự) | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "A" } | - HTTP Status: 400 Bad Request<br>- Error message: "Category name must be at least 2 characters long" | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role | Pending | | | Pending | | | Pending | | | |
| TC_CAT_010A | Kiểm tra tạo category với tên quá dài (>255 ký tự) | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Very long name..." } | - HTTP Status: 400 Bad Request<br>- Error message: "Category name must not exceed 255 characters" | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role | Pending | | | Pending | | | Pending | | | |
| TC_CAT_011 | Kiểm tra tạo category với tên null | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": null } | - HTTP Status: 400 Bad Request<br>- Error message: "Category name is required" | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role | Pending | | | Pending | | | Pending | | | |
| TC_CAT_011A | Kiểm tra tạo category với tên chỉ chứa khoảng trắng | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "     " } | - HTTP Status: 400 Bad Request<br>- Error message: "Category name cannot contain only whitespace" | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role | Pending | | | Pending | | | Pending | | | |
| TC_CAT_011B | Kiểm tra tạo category với tên có khoảng trắng đầu/cuối | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "  Sports & Outdoors  " } | - HTTP Status: 201 Created<br>- Response body: Category with trimmed name "Sports & Outdoors" | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role<br>- Auto-trim whitespace | Pending | | | Pending | | | Pending | | | |
| TC_CAT_011C | Kiểm tra tạo category với các ký tự bị cấm (XSS, SQL Injection) | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Category\<script\>alert('XSS')\</script\>" } | - HTTP Status: 400 Bad Request<br>- Error message: "Category name contains invalid characters" | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role | Pending | | | Pending | | | Pending | | | |
| TC_CAT_012 | Kiểm tra tạo category với tên trùng lặp (case insensitive) | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "ELECTRONICS" } | - HTTP Status: 400 Bad Request<br>- Error message: "Category name 'ELECTRONICS' already exists (case insensitive)" | - Admin đã đăng nhập<br>- Category "Electronics" đã tồn tại (ID=1) | Pending | | | Pending | | | Pending | | | |
| TC_CAT_012A | Kiểm tra tạo category với tên trùng lặp (exact match) | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Electronics" } | - HTTP Status: 400 Bad Request<br>- Error message: "Category name 'Electronics' already exists" | - Admin đã đăng nhập<br>- Category "Electronics" đã tồn tại (ID=1) | Pending | | | Pending | | | Pending | | | |
| TC_CAT_012D | Kiểm tra Unicode trong tên category | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Thời Trang" } | - HTTP Status: 201 Created<br>- Response body: Category with Unicode name | - Admin đã đăng nhập<br>- Unicode characters được hỗ trợ | Pending | | | Pending | | | Pending | | | |
| TC_CAT_013 | Kiểm tra tạo child category thành công | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Gaming Laptops", "parentCategoryId": 4 } | - HTTP Status: 201 Created<br>- Response body: Child category with parentCategoryId=4 | - Admin đã đăng nhập<br>- Parent category Laptops (ID=4) tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_CAT_013A | Kiểm tra tạo category với parent không tồn tại | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Invalid Child", "parentCategoryId": 999 } | - HTTP Status: 400 Bad Request<br>- Error message: "Parent category with ID 999 does not exist" | - Admin đã đăng nhập<br>- Parent ID 999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_CAT_013B | Kiểm tra tạo category với parentCategoryId = 0 | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Zero Parent", "parentCategoryId": 0 } | - HTTP Status: 400 Bad Request<br>- Error message: "Parent category ID must be a positive integer or null" | - Admin đã đăng nhập<br>- ID validation | Pending | | | Pending | | | Pending | | | |
| TC_CAT_013C | Kiểm tra tạo category với parentCategoryId âm | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Negative Parent", "parentCategoryId": -1 } | - HTTP Status: 400 Bad Request<br>- Error message: "Parent category ID must be a positive integer or null" | - Admin đã đăng nhập<br>- ID validation | Pending | | | Pending | | | Pending | | | |
| TC_CAT_014 | Kiểm tra tạo category với role User (không có quyền) | POST /api/categories với Authorization: Bearer \<user_token\><br>Body: { "name": "Unauthorized Category" } | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can create categories" | - User đã đăng nhập (user@techbox.com/user123)<br>- JWT token hợp lệ với USER role only | Pending | | | Pending | | | Pending | | | |
| TC_CAT_015 | Kiểm tra tạo category khi không đăng nhập | POST /api/categories (không có Authorization header)<br>Body: { "name": "Unauthorized Category" } | - HTTP Status: 401 Unauthorized<br>- Error message: "Authentication required to access this resource" | - Người dùng chưa đăng nhập | Pending | | | Pending | | | Pending | | | |

---

## Function 6: Cập nhật danh mục (PUT /api/categories/{id})

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_016 | Kiểm tra cập nhật category thành công với role Admin | PUT /api/categories/1 với Authorization: Bearer \<admin_token\><br>Body: { "name": "Consumer Electronics" } | - HTTP Status: 200 OK<br>- Response body: Updated category with new name and updated timestamp | - Admin đã đăng nhập<br>- Category Electronics (ID=1) tồn tại<br>- Name "Consumer Electronics" chưa tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_CAT_017 | Kiểm tra cập nhật category không tồn tại | PUT /api/categories/999999 với Authorization: Bearer \<admin_token\><br>Body: { "name": "Non-existent Category" } | - HTTP Status: 404 Not Found<br>- Error message: "Category with ID 999999 does not exist" | - Admin đã đăng nhập<br>- ID 999999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_CAT_018 | Kiểm tra cập nhật category với tên trùng lặp | PUT /api/categories/1 với Authorization: Bearer \<admin_token\><br>Body: { "name": "Fashion" } | - HTTP Status: 400 Bad Request<br>- Error message: "Category name 'Fashion' already exists" | - Admin đã đăng nhập<br>- Electronics (ID=1), Fashion (ID=3) tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_CAT_019 | Kiểm tra cập nhật category với role User (không có quyền) | PUT /api/categories/1 với Authorization: Bearer \<user_token\><br>Body: { "name": "Electronics Updated" } | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can update categories" | - User đã đăng nhập<br>- Category Electronics (ID=1) tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_CAT_020 | Kiểm tra cập nhật parent category thành công | PUT /api/categories/2 với Authorization: Bearer \<admin_token\><br>Body: { "name": "Mobile Phones", "parentCategoryId": 3 } | - HTTP Status: 200 OK<br>- Response body: Updated category with new parent | - Admin đã đăng nhập<br>- Smartphones (ID=2) và Fashion (ID=3) tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_CAT_021 | Kiểm tra cập nhật category với parent tạo circular reference | PUT /api/categories/1 với Authorization: Bearer \<admin_token\><br>Body: { "name": "Electronics", "parentCategoryId": 2 } | - HTTP Status: 400 Bad Request<br>- Error message: "Cannot create circular reference: Electronics is already parent of Smartphones" | - Admin đã đăng nhập<br>- Electronics (ID=1) is parent of Smartphones (ID=2) | Pending | | | Pending | | | Pending | | | |

---

## Function 7: Xóa danh mục (DELETE /api/categories/{id})

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_022 | Kiểm tra xóa category thành công với role Admin | DELETE /api/categories/5 với Authorization: Bearer \<admin_token\> | - HTTP Status: 204 No Content | - Admin đã đăng nhập<br>- Category Books (ID=5) tồn tại và không có child/products | Pending | | | Pending | | | Pending | | | |
| TC_CAT_023 | Kiểm tra xóa category không tồn tại | DELETE /api/categories/999999 với Authorization: Bearer \<admin_token\> | - HTTP Status: 404 Not Found<br>- Error message: "Category with ID 999999 does not exist" | - Admin đã đăng nhập<br>- ID 999999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_CAT_024 | Kiểm tra xóa category có child categories | DELETE /api/categories/1 với Authorization: Bearer \<admin_token\> | - HTTP Status: 400 Bad Request<br>- Error message: "Category has 2 child categories. Please reassign or delete child categories first" | - Admin đã đăng nhập<br>- Electronics (ID=1) có children: Smartphones (ID=2), Laptops (ID=4) | Pending | | | Pending | | | Pending | | | |
| TC_CAT_025 | Kiểm tra xóa category đang được sử dụng bởi products | DELETE /api/categories/2 với Authorization: Bearer \<admin_token\> | - HTTP Status: 400 Bad Request<br>- Error message: "Category is being used by 5 products. Please reassign or delete products first" | - Admin đã đăng nhập<br>- Smartphones (ID=2) có products | Pending | | | Pending | | | Pending | | | |
| TC_CAT_026 | Kiểm tra xóa category với role User (không có quyền) | DELETE /api/categories/5 với Authorization: Bearer \<user_token\> | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can delete categories" | - User đã đăng nhập<br>- Category Books (ID=5) tồn tại | Pending | | | Pending | | | Pending | | | |

---

## Function 8: Kiểm tra tồn tại danh mục (GET /api/categories/exists)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_027 | Kiểm tra category tồn tại với tên hợp lệ (Public access) | GET /api/categories/exists?name=Electronics | - HTTP Status: 200 OK<br>- Response body: { "exists": true, "categoryId": 1, "name": "Electronics" } | - Category "Electronics" tồn tại (ID=1)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_028 | Kiểm tra category không tồn tại | GET /api/categories/exists?name=NonExistentCategory | - HTTP Status: 200 OK<br>- Response body: { "exists": false, "name": "NonExistentCategory" } | - Category "NonExistentCategory" không tồn tại<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_029 | Kiểm tra với parameter name rỗng | GET /api/categories/exists?name= | - HTTP Status: 400 Bad Request<br>- Error message: "Category name parameter is required" | - Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_030 | Kiểm tra case sensitivity trong tên category | GET /api/categories/exists?name=electronics | - HTTP Status: 200 OK<br>- Response body: { "exists": true, "categoryId": 1, "name": "Electronics", "searchedName": "electronics" } | - Category "Electronics" tồn tại (case insensitive)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Execution Instructions

### Pre-test Setup
1. **Environment Setup**: Configure test environment with clean database
2. **Authentication**: Prepare valid admin and user JWT tokens
3. **Test Data**: Create required categories with hierarchical structure
4. **Database**: Ensure proper indexes and foreign key constraints

### Test Execution Guidelines
1. **Sequential Testing**: Execute test cases in order for dependencies
2. **Data Cleanup**: Clean test data between rounds
3. **Error Logging**: Document all error responses and status codes
4. **Hierarchy Validation**: Verify parent-child relationships work correctly

### Validation Criteria
- **HTTP Status Codes**: Verify correct status codes for all scenarios
- **Response Format**: Validate JSON structure and required fields
- **Hierarchy Rules**: Confirm parent-child relationships and circular reference prevention
- **Security**: Ensure proper authentication and authorization
- **Data Integrity**: Verify database consistency after operations

### Test Data Requirements
- **Categories**: Electronics (ID=1), Smartphones (ID=2), Fashion (ID=3), Laptops (ID=4)
- **Hierarchy**: Electronics → Smartphones, Electronics → Laptops
- **Users**: Admin (admin@techbox.com) and User (user@techbox.com) accounts

---

## Notes
- Test cases marked with * require manual verification
- All parent-child relationships must be validated for circular references
- Security tests must validate proper access controls
- Database referential integrity should be maintained throughout testing