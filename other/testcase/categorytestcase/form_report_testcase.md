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

## Function 2: Lấy danh mục theo ID (GET /api/categories/{id})| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
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

## Function 4: Lấy danh mục con (GET /api/categories/{parentId}/children)| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_007A | Kiểm tra lấy child categories thành công (Public access) | Gửi GET request đến "/api/categories/1/children" | - HTTP Status: 200 OK<br>- Response body: Array of child categories with parentCategoryId=1 | - Electronics (ID=1) có child categories: Smartphones (ID=2), Laptops (ID=4)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_007B | Kiểm tra lấy child categories khi parent không có con | Gửi GET request đến "/api/categories/2/children" | - HTTP Status: 200 OK<br>- Response body: [] | - Smartphones (ID=2) không có child categories<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_CAT_007C | Kiểm tra lấy child categories với parent không tồn tại | Gửi GET request đến "/api/categories/999999/children" | - HTTP Status: 404 Not Found<br>- Error message: "Parent category not found" | - Parent ID 999999 không tồn tại<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 5: Tạo danh mục mới (POST /api/categories)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_CAT_008 | Kiểm tra tạo root category thành công với role Admin | POST /api/categories với Authorization: Bearer \<admin_token\><br>Body: { "name": "Books & Media" } | - HTTP Status: 201 Created<br>- Response body: Created category object with auto-generated ID | - Admin đã đăng nhập với tài khoản (admin@techbox.com/admin123)<br>- JWT token hợp lệ với ADMIN role<br>- Category "Books & Media" chưa tồn tại | Pending | | | Pending | | | Pending | | | |			
TC_CAT_009	Kiểm tra tạo category với tên trống	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": """" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Category name is required"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_CAT_010	Kiểm tra tạo category với tên quá ngắn (<2 ký tự)	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""A"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Category name must be at least 2 characters long"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_CAT_010A	Kiểm tra tạo category với tên quá dài (>255 ký tự)	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""A very long category name that exceeds maximum length of 255 characters dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Category name must not exceed 255 characters"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_CAT_011	Kiểm tra tạo category với tên null	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": null }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Category name is required"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_CAT_011A	Kiểm tra tạo category với tên chỉ chứa khoảng trắng	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""     "" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Category name cannot contain only whitespace"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_CAT_011B	Kiểm tra tạo category với tên có khoảng trắng đầu/cuối	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""  Sports & Outdoors  "" }"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 5,
""name"": ""Sports & Outdoors"",
""parentCategoryId"": null,
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role
- Auto-trim whitespace"	Pending			Pending			Pending			
TC_CAT_011C	Kiểm tra tạo category với các ký tự bị cấm (XSS, SQL Injection)	"Gửi lần lượt POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body 1: { ""name"": ""Category<script>alert('XSS')</script>"" }
Body 2: { ""name"": ""'; DROP TABLE categories; --"" }"	"Cả 2 đều trả về:
- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Category name contains invalid characters""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_CAT_012	Kiểm tra tạo category với tên trùng lặp (case insensitive)	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""ELECTRONICS"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Duplicate entry"",
""message"": ""Category name 'ELECTRONICS' already exists (case insensitive)"",
""field"": ""name"",
""existingCategory"": ""Electronics""
}"	"- Admin đã đăng nhập
- Category ""Electronics"" đã tồn tại (ID=1)"	Pending			Pending			Pending			
TC_CAT_012A	Kiểm tra tạo category với tên trùng lặp (exact match)	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Electronics"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Duplicate entry"",
""message"": ""Category name 'Electronics' already exists"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- Category ""Electronics"" đã tồn tại (ID=1)"	Pending			Pending			Pending			
TC_CAT_012B	Kiểm tra tạo category với SQL injection trong name	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Electronics'; DROP TABLE categories; --"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Category name contains invalid characters""
}"	"- Admin đã đăng nhập
- SQL injection protection"	Pending			Pending			Pending			
TC_CAT_012C	Kiểm tra tạo category với XSS script trong name	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Electronics<script>alert('hack')</script>"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Category name contains invalid characters""
}"	"- Admin đã đăng nhập
- XSS protection"	Pending			Pending			Pending			
TC_CAT_012D	Kiểm tra Unicode trong tên category	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Thời Trang"" }"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 8,
""name"": ""Thời Trang"",
""parentCategoryId"": null,
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}"	"- Admin đã đăng nhập
- Unicode characters được hỗ trợ"	Pending			Pending			Pending			
TC_CAT_013	Kiểm tra tạo child category thành công	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Gaming Laptops"", ""parentCategoryId"": 4 }"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 9,
""name"": ""Gaming Laptops"",
""parentCategoryId"": 4,
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}"	"- Admin đã đăng nhập
- Parent category Laptops (ID=4) tồn tại"	Pending			Pending			Pending			
TC_CAT_013A	Kiểm tra tạo category với parent không tồn tại	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Invalid Child"", ""parentCategoryId"": 999 }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parent category"",
""message"": ""Parent category with ID 999 does not exist"",
""field"": ""parentCategoryId""
}"	"- Admin đã đăng nhập
- Parent ID 999 không tồn tại"	Pending			Pending			Pending			
TC_CAT_013B	Kiểm tra tạo category với parentCategoryId = 0	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Zero Parent"", ""parentCategoryId"": 0 }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parent category"",
""message"": ""Parent category ID must be a positive integer or null"",
""field"": ""parentCategoryId""
}"	"- Admin đã đăng nhập
- ID validation"	Pending			Pending			Pending			
TC_CAT_013C	Kiểm tra tạo category với parentCategoryId âm	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Negative Parent"", ""parentCategoryId"": -1 }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parent category"",
""message"": ""Parent category ID must be a positive integer or null"",
""field"": ""parentCategoryId""
}"	"- Admin đã đăng nhập
- ID validation"	Pending			Pending			Pending			
TC_CAT_014	Kiểm tra tạo category với role User (không có quyền)	"Gửi POST request đến ""/api/categories"".
Header Authorization: Bearer <user_token>.
Body: { ""name"": ""Unauthorized Category"" }"	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can create categories""
}"	"- User đã đăng nhập (user@techbox.com/user123)
- JWT token hợp lệ với USER role only"	Pending			Pending			Pending			
TC_CAT_015	Kiểm tra tạo category khi không đăng nhập	"Gửi POST request đến ""/api/categories"".
Body: { ""name"": ""Unauthorized Category"" }"	"- HTTP Status: 401 Unauthorized
- Response body:
{
""error"": ""Unauthorized"",
""message"": ""Authentication required to access this resource""
}"	- Người dùng chưa đăng nhập	Pending			Pending			Pending			
Function 6: Cập nhật danh mục (PUT /api/categories/{id})														
TC_CAT_016	Kiểm tra cập nhật category thành công với role Admin	"Gửi PUT request đến ""/api/categories/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Consumer Electronics"" }"	"- HTTP Status: 200 OK
- Response body:
{
""id"": 1,
""name"": ""Consumer Electronics"",
""parentCategoryId"": null,
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T14:30:00Z""
}"	"- Admin đã đăng nhập
- Category Electronics (ID=1) tồn tại
- Name ""Consumer Electronics"" chưa tồn tại"	Pending			Pending			Pending			
TC_CAT_017	Kiểm tra cập nhật category không tồn tại	"Gửi PUT request đến ""/api/categories/999999"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Non-existent Category"" }"	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Category not found"",
""message"": ""Category with ID 999999 does not exist""
}"	"- Admin đã đăng nhập
- ID 999999 không tồn tại"	Pending			Pending			Pending			
TC_CAT_018	Kiểm tra cập nhật category với tên trùng lặp	"Gửi PUT request đến ""/api/categories/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Fashion"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Duplicate entry"",
""message"": ""Category name 'Fashion' already exists"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- Electronics (ID=1), Fashion (ID=3) tồn tại"	Pending			Pending			Pending			
TC_CAT_019	Kiểm tra cập nhật category với role User (không có quyền)	"Gửi PUT request đến ""/api/categories/1"".
Header Authorization: Bearer <user_token>.
Body: { ""name"": ""Electronics Updated"" }"	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can update categories""
}"	"- User đã đăng nhập
- Category Electronics (ID=1) tồn tại"	Pending			Pending			Pending			
TC_CAT_020	Kiểm tra cập nhật parent category thành công	"Gửi PUT request đến ""/api/categories/2"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Mobile Phones"", ""parentCategoryId"": 3 }"	"- HTTP Status: 200 OK
- Response body:
{
""id"": 2,
""name"": ""Mobile Phones"",
""parentCategoryId"": 3,
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T14:45:00Z""
}"	"- Admin đã đăng nhập
- Smartphones (ID=2) và Fashion (ID=3) tồn tại"	Pending			Pending			Pending			
TC_CAT_021	Kiểm tra cập nhật category với parent tạo circular reference	"Gửi PUT request đến ""/api/categories/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Electronics"", ""parentCategoryId"": 2 }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid hierarchy"",
""message"": ""Cannot create circular reference: Electronics is already parent of Smartphones"",
""field"": ""parentCategoryId""
}"	"- Admin đã đăng nhập
- Electronics (ID=1) is parent of Smartphones (ID=2)"	Pending			Pending			Pending			
Function 7: Xóa danh mục (DELETE /api/categories/{id})														
TC_CAT_022	Kiểm tra xóa category thành công với role Admin	"Gửi DELETE request đến ""/api/categories/5"".
Header Authorization: Bearer <admin_token>."	- HTTP Status: 204 No Content	"- Admin đã đăng nhập
- Category Books (ID=5) tồn tại và không có child/products"	Pending			Pending			Pending			
TC_CAT_023	Kiểm tra xóa category không tồn tại	"Gửi DELETE request đến ""/api/categories/999999"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Category not found"",
""message"": ""Category with ID 999999 does not exist""
}"	"- Admin đã đăng nhập
- ID 999999 không tồn tại"	Pending			Pending			Pending			
TC_CAT_024	Kiểm tra xóa category có child categories	"Gửi DELETE request đến ""/api/categories/1"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Cannot delete category"",
""message"": ""Category has 2 child categories. Please reassign or delete child categories first"",
""childCategories"": [2, 4]
}"	"- Admin đã đăng nhập
- Electronics (ID=1) có children: Smartphones (ID=2), Laptops (ID=4)"	Pending			Pending			Pending			
TC_CAT_025	Kiểm tra xóa category đang được sử dụng bởi products	"Gửi DELETE request đến ""/api/categories/2"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Cannot delete category"",
""message"": ""Category is being used by 5 products. Please reassign or delete products first"",
""relatedProducts"": [101, 102, 103, 104, 105]
}"	"- Admin đã đăng nhập
- Smartphones (ID=2) có products"	Pending			Pending			Pending			
TC_CAT_026	Kiểm tra xóa category với role User (không có quyền)	"Gửi DELETE request đến ""/api/categories/5"".
Header Authorization: Bearer <user_token>."	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can delete categories""
}"	"- User đã đăng nhập
- Category Books (ID=5) tồn tại"	Pending			Pending			Pending			
Function 8: Kiểm tra tồn tại danh mục (GET /api/categories/exists)														
TC_CAT_027	Kiểm tra category tồn tại với tên hợp lệ (Public access)	"Gửi GET request đến ""/api/categories/exists?name=Electronics""."	"- HTTP Status: 200 OK
- Response body:
{
""exists"": true,
""categoryId"": 1,
""name"": ""Electronics""
}"	"- Category ""Electronics"" tồn tại (ID=1)
- Không cần authentication"	Pending			Pending			Pending			
TC_CAT_028	Kiểm tra category không tồn tại	"Gửi GET request đến ""/api/categories/exists?name=NonExistentCategory""."	"- HTTP Status: 200 OK
- Response body:
{
""exists"": false,
""name"": ""NonExistentCategory""
}"	"- Category ""NonExistentCategory"" không tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_CAT_029	Kiểm tra với parameter name rỗng	"Gửi GET request đến ""/api/categories/exists?name=""."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parameter"",
""message"": ""Category name parameter is required"",
""field"": ""name""
}"	- Không cần authentication	Pending			Pending			Pending			
TC_CAT_030	Kiểm tra case sensitivity trong tên category	"Gửi GET request đến ""/api/categories/exists?name=electronics""."	"- HTTP Status: 200 OK
- Response body:
{
""exists"": true,
""categoryId"": 1,
""name"": ""Electronics"",
""searchedName"": ""electronics""
}"	"- Category ""Electronics"" tồn tại (case insensitive)
- Không cần authentication"	Pending			Pending			Pending