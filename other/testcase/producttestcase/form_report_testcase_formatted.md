# Product API Test Case Form Report

## Tổng quan
- **API Module**: Product Management
- **Base URL**: `/api/products`
- **Authentication**: Public access for GET, Admin required for POST/PUT/DELETE
- **Test Environment**: Development/Staging
- **Features**: CRUD operations, Image upload/delete via Cloudinary, Soft delete, Restore functionality

---

## Function 1: Lấy danh sách sản phẩm (GET /api/products)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_001 | Kiểm tra lấy danh sách tất cả products thành công (Public access) | Gửi GET request đến "/api/products" | - HTTP Status: 200 OK<br>- Response body: Array of products with id, name, description, imageUrl, brandId, categoryId, timestamps | - Database có products: iPhone 15 Pro (ID=1)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_002 | Kiểm tra lấy danh sách products bao gồm deleted products | Gửi GET request đến "/api/products?includeDeleted=true" | - HTTP Status: 200 OK<br>- Response body bao gồm cả products có deletedAt không null | - Database có products bị soft delete<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_003 | Kiểm tra lấy danh sách products khi database trống | Gửi GET request đến "/api/products" | - HTTP Status: 200 OK<br>- Response body: [] | - Database không có product nào<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 2: Lấy danh sách sản phẩm active (GET /api/products/active)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_004 | Kiểm tra lấy danh sách active products thành công (Public access) | Gửi GET request đến "/api/products/active" | - HTTP Status: 200 OK<br>- Response body chỉ chứa products có deletedAt = null | - Database có active products<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 3: Lấy sản phẩm theo ID (GET /api/products/{id})

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_005 | Kiểm tra lấy product theo ID hợp lệ (Public access) | Gửi GET request đến "/api/products/1" | - HTTP Status: 200 OK<br>- Response body: Product object with complete details | - Product iPhone 15 Pro với ID=1 tồn tại<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_006 | Kiểm tra lấy product với ID không tồn tại | Gửi GET request đến "/api/products/999999" | - HTTP Status: 404 Not Found | - Product ID 999999 không tồn tại trong database<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_007 | Kiểm tra lấy product với ID không hợp lệ (không phải số nguyên) | Gửi GET request đến "/api/products/abc" | - HTTP Status: 400 Bad Request<br>- Error message: "ID must be a valid integer" | - Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_008 | Kiểm tra lấy deleted product với includeDeleted=true | Gửi GET request đến "/api/products/5?includeDeleted=true" | - HTTP Status: 200 OK<br>- Response body chứa product bị soft delete | - Product ID=5 bị soft delete (deletedAt không null)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 4: Tạo sản phẩm mới (POST /api/products)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_009 | Kiểm tra tạo product thành công với role Admin (không có image) | POST /api/products với Authorization: Bearer \<admin_token\><br>Content-Type: multipart/form-data<br>Form data: name="Samsung Galaxy S24", description="Flagship smartphone", categoryId=1, brandId=2 | - HTTP Status: 201 Created<br>- Response body: Created product with imageUrl=null, imagePublicId=null | - Admin đã đăng nhập<br>- Brand Samsung (ID=2), Category Electronics (ID=1) tồn tại<br>- Product "Samsung Galaxy S24" chưa tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_010 | Kiểm tra tạo product thành công với image upload | POST /api/products với Authorization: Bearer \<admin_token\><br>Content-Type: multipart/form-data<br>Form data: name="iPhone 16 Pro", description="New iPhone", categoryId=1, brandId=1, image=\<valid_image_file.jpg\> | - HTTP Status: 201 Created<br>- Response body có imageUrl và imagePublicId từ Cloudinary | - Admin đã đăng nhập<br>- Valid image file (jpg/png, <5MB)<br>- Brand Apple (ID=1), Category Electronics (ID=1) tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_011 | Kiểm tra tạo product với tên trống | POST /api/products với Authorization: Bearer \<admin_token\><br>Form data: name="", description="Test product", categoryId=1 | - HTTP Status: 400 Bad Request<br>- Error message: "Product name is required" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_PROD_012 | Kiểm tra tạo product với tên quá ngắn (<2 ký tự) | POST /api/products với Authorization: Bearer \<admin_token\><br>Form data: name="A", description="Test product", categoryId=1 | - HTTP Status: 400 Bad Request<br>- Error message: "Product name must be at least 2 characters long" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_PROD_013 | Kiểm tra tạo product với tên quá dài (>255 ký tự) | POST /api/products với Authorization: Bearer \<admin_token\><br>Form data: name="Very long product name...", categoryId=1 | - HTTP Status: 400 Bad Request<br>- Error message: "Product name must not exceed 255 characters" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_PROD_014 | Kiểm tra tạo product với description quá dài | POST /api/products với Authorization: Bearer \<admin_token\><br>Form data: name="Test Product", description="Very long description...", categoryId=1 | - HTTP Status: 400 Bad Request<br>- Error message: "Description must not exceed 1000 characters" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_PROD_015 | Kiểm tra tạo product với tên trùng lặp | POST /api/products với Authorization: Bearer \<admin_token\><br>Form data: name="iPhone 15 Pro", description="Test", categoryId=1 | - HTTP Status: 400 Bad Request<br>- Error message: "Product name 'iPhone 15 Pro' already exists" | - Admin đã đăng nhập<br>- Product "iPhone 15 Pro" đã tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_016 | Kiểm tra tạo product với categoryId không tồn tại | POST /api/products với Authorization: Bearer \<admin_token\><br>Form data: name="Test Product", categoryId=999 | - HTTP Status: 400 Bad Request<br>- Error message: "Category with ID 999 does not exist" | - Admin đã đăng nhập<br>- Category ID 999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_017 | Kiểm tra tạo product với brandId không tồn tại | POST /api/products với Authorization: Bearer \<admin_token\><br>Form data: name="Test Product", categoryId=1, brandId=999 | - HTTP Status: 400 Bad Request<br>- Error message: "Brand with ID 999 does not exist" | - Admin đã đăng nhập<br>- Brand ID 999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_018 | Kiểm tra tạo product với image file quá lớn (>5MB) | POST /api/products với Authorization: Bearer \<admin_token\><br>Form data: name="Test Product", categoryId=1, image=\<large_file.jpg\> | - HTTP Status: 400 Bad Request<br>- Error message: "Image file size must not exceed 5MB" | - Admin đã đăng nhập<br>- Image file >5MB | Pending | | | Pending | | | Pending | | | |
| TC_PROD_019 | Kiểm tra tạo product với image format không hợp lệ | POST /api/products với Authorization: Bearer \<admin_token\><br>Form data: name="Test Product", categoryId=1, image=\<file.txt\> | - HTTP Status: 400 Bad Request<br>- Error message: "Only JPEG, PNG image formats are allowed" | - Admin đã đăng nhập<br>- Invalid image format | Pending | | | Pending | | | Pending | | | |
| TC_PROD_020 | Kiểm tra tạo product với role User (không có quyền) | POST /api/products với Authorization: Bearer \<user_token\><br>Form data: name="Unauthorized Product", categoryId=1 | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can create products" | - User đã đăng nhập với USER role | Pending | | | Pending | | | Pending | | | |
| TC_PROD_021 | Kiểm tra tạo product khi không đăng nhập | POST /api/products (không có Authorization header)<br>Form data: name="Unauthorized Product", categoryId=1 | - HTTP Status: 401 Unauthorized<br>- Error message: "Authentication required" | - Không có authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 5: Cập nhật sản phẩm (PUT /api/products/{id})

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_022 | Kiểm tra cập nhật product thành công (không thay đổi image) | PUT /api/products/1 với Authorization: Bearer \<admin_token\><br>Form data: name="iPhone 15 Pro Max", description="Updated description" | - HTTP Status: 200 OK<br>- Response body: Updated product với timestamps mới | - Admin đã đăng nhập<br>- Product iPhone 15 Pro (ID=1) tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_023 | Kiểm tra cập nhật product với image mới | PUT /api/products/1 với Authorization: Bearer \<admin_token\><br>Form data: name="iPhone 15 Pro", image=\<new_image.jpg\> | - HTTP Status: 200 OK<br>- Old image deleted from Cloudinary<br>- New image uploaded and URL updated | - Admin đã đăng nhập<br>- Product có existing image<br>- Valid new image file | Pending | | | Pending | | | Pending | | | |
| TC_PROD_024 | Kiểm tra cập nhật product không tồn tại | PUT /api/products/999999 với Authorization: Bearer \<admin_token\><br>Form data: name="Non-existent" | - HTTP Status: 404 Not Found<br>- Error message: "Product with ID 999999 does not exist" | - Admin đã đăng nhập<br>- Product ID 999999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_025 | Kiểm tra cập nhật product với tên trùng lặp | PUT /api/products/1 với Authorization: Bearer \<admin_token\><br>Form data: name="Samsung Galaxy S24" | - HTTP Status: 400 Bad Request<br>- Error message: "Product name 'Samsung Galaxy S24' already exists" | - Admin đã đăng nhập<br>- Products iPhone 15 Pro (ID=1), Samsung Galaxy S24 tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_026 | Kiểm tra cập nhật product với role User (không có quyền) | PUT /api/products/1 với Authorization: Bearer \<user_token\><br>Form data: name="Updated Name" | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can update products" | - User đã đăng nhập<br>- Product ID=1 tồn tại | Pending | | | Pending | | | Pending | | | |

---

## Function 6: Xóa sản phẩm (DELETE /api/products/{id})

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_027 | Kiểm tra soft delete product thành công | DELETE /api/products/1 với Authorization: Bearer \<admin_token\> | - HTTP Status: 204 No Content<br>- Product được soft delete (deletedAt được set)<br>- Image vẫn được giữ trên Cloudinary | - Admin đã đăng nhập<br>- Product ID=1 tồn tại và chưa bị delete | Pending | | | Pending | | | Pending | | | |
| TC_PROD_028 | Kiểm tra xóa product không tồn tại | DELETE /api/products/999999 với Authorization: Bearer \<admin_token\> | - HTTP Status: 404 Not Found<br>- Error message: "Product with ID 999999 does not exist" | - Admin đã đăng nhập<br>- Product ID 999999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_029 | Kiểm tra xóa product đã bị soft delete | DELETE /api/products/5 với Authorization: Bearer \<admin_token\> | - HTTP Status: 400 Bad Request<br>- Error message: "Product is already deleted" | - Admin đã đăng nhập<br>- Product ID=5 đã bị soft delete | Pending | | | Pending | | | Pending | | | |
| TC_PROD_030 | Kiểm tra xóa product với role User (không có quyền) | DELETE /api/products/1 với Authorization: Bearer \<user_token\> | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can delete products" | - User đã đăng nhập<br>- Product ID=1 tồn tại | Pending | | | Pending | | | Pending | | | |

---

## Function 7: Khôi phục sản phẩm (POST /api/products/{id}/restore)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_031 | Kiểm tra restore product thành công | POST /api/products/5/restore với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Product được restore (deletedAt = null)<br>- Response body: Restored product object | - Admin đã đăng nhập<br>- Product ID=5 bị soft delete | Pending | | | Pending | | | Pending | | | |
| TC_PROD_032 | Kiểm tra restore product không tồn tại | POST /api/products/999999/restore với Authorization: Bearer \<admin_token\> | - HTTP Status: 404 Not Found<br>- Error message: "Product with ID 999999 does not exist" | - Admin đã đăng nhập<br>- Product ID 999999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_PROD_033 | Kiểm tra restore product chưa bị delete | POST /api/products/1/restore với Authorization: Bearer \<admin_token\> | - HTTP Status: 400 Bad Request<br>- Error message: "Product is not deleted" | - Admin đã đăng nhập<br>- Product ID=1 chưa bị delete (deletedAt = null) | Pending | | | Pending | | | Pending | | | |
| TC_PROD_034 | Kiểm tra restore product với role User (không có quyền) | POST /api/products/5/restore với Authorization: Bearer \<user_token\> | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can restore products" | - User đã đăng nhập<br>- Product ID=5 bị soft delete | Pending | | | Pending | | | Pending | | | |

---

## Function 8: Permanent delete sản phẩm (DELETE /api/products/{id}/permanent)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_035 | Kiểm tra permanent delete product thành công | DELETE /api/products/5/permanent với Authorization: Bearer \<admin_token\> | - HTTP Status: 204 No Content<br>- Product được xóa vĩnh viễn khỏi database<br>- Image được xóa khỏi Cloudinary | - Admin đã đăng nhập<br>- Product ID=5 bị soft delete<br>- Product có image trên Cloudinary | Pending | | | Pending | | | Pending | | | |
| TC_PROD_036 | Kiểm tra permanent delete product chưa bị soft delete | DELETE /api/products/1/permanent với Authorization: Bearer \<admin_token\> | - HTTP Status: 400 Bad Request<br>- Error message: "Product must be soft deleted before permanent deletion" | - Admin đã đăng nhập<br>- Product ID=1 chưa bị soft delete | Pending | | | Pending | | | Pending | | | |
| TC_PROD_037 | Kiểm tra permanent delete product không tồn tại | DELETE /api/products/999999/permanent với Authorization: Bearer \<admin_token\> | - HTTP Status: 404 Not Found<br>- Error message: "Product with ID 999999 does not exist" | - Admin đã đăng nhập<br>- Product ID 999999 không tồn tại | Pending | | | Pending | | | Pending | | | |

---

## Function 9: Tìm kiếm sản phẩm (GET /api/products/search)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_038 | Kiểm tra search products theo keyword thành công | GET /api/products/search?keyword=iPhone | - HTTP Status: 200 OK<br>- Response body: Array of products matching "iPhone" | - Database có products chứa "iPhone" trong name/description<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_039 | Kiểm tra search products với keyword trống | GET /api/products/search?keyword= | - HTTP Status: 200 OK<br>- Response body: All active products | - Database có products<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_040 | Kiểm tra search products với keyword không có kết quả | GET /api/products/search?keyword=NonExistentProduct | - HTTP Status: 200 OK<br>- Response body: [] | - Keyword không match với product nào<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_041 | Kiểm tra search products với pagination | GET /api/products/search?keyword=phone&page=0&size=5 | - HTTP Status: 200 OK<br>- Response body: Paginated results với đúng page và size | - Database có nhiều products matching "phone"<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 10: Lọc sản phẩm theo category và brand (GET /api/products/filter)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_042 | Kiểm tra filter products theo categoryId | GET /api/products/filter?categoryId=1 | - HTTP Status: 200 OK<br>- Response body: Products thuộc category ID=1 | - Database có products thuộc category Electronics (ID=1)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_043 | Kiểm tra filter products theo brandId | GET /api/products/filter?brandId=1 | - HTTP Status: 200 OK<br>- Response body: Products thuộc brand ID=1 | - Database có products thuộc brand Apple (ID=1)<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_044 | Kiểm tra filter products theo cả categoryId và brandId | GET /api/products/filter?categoryId=1&brandId=1 | - HTTP Status: 200 OK<br>- Response body: Products thuộc cả category=1 và brand=1 | - Database có products thuộc cả category và brand<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_045 | Kiểm tra filter với categoryId không tồn tại | GET /api/products/filter?categoryId=999 | - HTTP Status: 200 OK<br>- Response body: [] | - Category ID 999 không tồn tại<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |
| TC_PROD_046 | Kiểm tra filter với brandId không tồn tại | GET /api/products/filter?brandId=999 | - HTTP Status: 200 OK<br>- Response body: [] | - Brand ID 999 không tồn tại<br>- Không cần authentication | Pending | | | Pending | | | Pending | | | |

---

## Additional Test Cases

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_PROD_047 | Kiểm tra upload image với special characters trong filename | POST /api/products với image filename có special characters | - HTTP Status: 201 Created<br>- Image uploaded successfully với sanitized filename | - Admin đã đăng nhập<br>- Valid image với special chars filename | Pending | | | Pending | | | Pending | | | |
| TC_PROD_048 | Kiểm tra Cloudinary integration khi service unavailable | POST/PUT /api/products khi Cloudinary down | - HTTP Status: 500 Internal Server Error<br>- Error message: "Image upload service unavailable" | - Admin đã đăng nhập<br>- Cloudinary service unavailable | Pending | | | Pending | | | Pending | | | |
| TC_PROD_049 | Kiểm tra concurrent access khi update cùng product | 2 requests đồng thời PUT /api/products/1 | - Một request thành công, một request failed với conflict error | - Admin đã đăng nhập<br>- Product ID=1 tồn tại<br>- Concurrent scenario | Pending | | | Pending | | | Pending | | | |
| TC_PROD_050 | Kiểm tra product với Unicode characters trong name/description | POST /api/products với Unicode name "Điện thoại thông minh" | - HTTP Status: 201 Created<br>- Unicode được handle correctly | - Admin đã đăng nhập<br>- Unicode support enabled | Pending | | | Pending | | | Pending | | | |
| TC_PROD_051 | Kiểm tra validation với các loại SQL injection attacks | POST /api/products với malicious SQL trong name/description | - HTTP Status: 400 Bad Request<br>- SQL injection prevented | - Admin đã đăng nhập<br>- SQL injection protection | Pending | | | Pending | | | Pending | | | |
| TC_PROD_052 | Kiểm tra XSS protection trong product fields | POST /api/products với XSS script trong name/description | - HTTP Status: 400 Bad Request<br>- XSS script rejected hoặc escaped | - Admin đã đăng nhập<br>- XSS protection enabled | Pending | | | Pending | | | Pending | | | |
| TC_PROD_053 | Kiểm tra rate limiting cho product creation | Multiple rapid POST requests đến /api/products | - HTTP Status: 429 Too Many Requests sau limit threshold | - Admin đã đăng nhập<br>- Rate limiting configured | Pending | | | Pending | | | Pending | | | |
| TC_PROD_054 | Kiểm tra database transaction rollback khi image upload fails | POST /api/products với valid data nhưng image upload fails | - HTTP Status: 500 Internal Server Error<br>- Product không được tạo trong database | - Admin đã đăng nhập<br>- Image upload failure scenario | Pending | | | Pending | | | Pending | | | |
| TC_PROD_055 | Kiểm tra soft delete với product có relationships | DELETE product đang được reference bởi orders/carts | - HTTP Status: 400 Bad Request<br>- Error về relationship constraints | - Admin đã đăng nhập<br>- Product có active relationships | Pending | | | Pending | | | Pending | | | |
| TC_PROD_056 | Kiểm tra performance với large image files | POST /api/products với image file gần limit (4.9MB) | - HTTP Status: 201 Created<br>- Response time trong acceptable range | - Admin đã đăng nhập<br>- Large valid image file | Pending | | | Pending | | | Pending | | | |
| TC_PROD_057 | Kiểm tra backup và recovery của image trên Cloudinary | Simulate Cloudinary backup restore scenario | - Images restored correctly<br>- URLs still accessible | - Product với images on Cloudinary<br>- Backup/restore scenario | Pending | | | Pending | | | Pending | | | |
| TC_PROD_058 | Kiểm tra audit trail cho product operations | Thực hiện các operations CREATE/UPDATE/DELETE | - All operations được log với timestamp và user info | - Audit logging enabled<br>- Admin operations | Pending | | | Pending | | | Pending | | | |
| TC_PROD_059 | Kiểm tra cache invalidation sau khi update product | GET product sau khi UPDATE | - Response trả về updated data, không phải cached data | - Caching enabled<br>- Product update scenario | Pending | | | Pending | | | Pending | | | |
| TC_PROD_060 | Kiểm tra data integrity khi database connection lost | Simulate database connection loss during operations | - Proper error handling<br>- No data corruption | - Database connection scenarios<br>- Error handling testing | Pending | | | Pending | | | Pending | | | |
| TC_PROD_061 | Kiểm tra migration và compatibility với old data format | Access products created với old API version | - Backward compatibility maintained<br>- All products accessible | - Products from different API versions<br>- Migration scenario | Pending | | | Pending | | | Pending | | | |

---

## Execution Instructions

### Pre-test Setup
1. **Environment Setup**: Configure test environment với clean database
2. **Authentication**: Prepare valid admin và user JWT tokens
3. **Test Data**: Create required brands, categories, và sample products
4. **Cloudinary**: Configure image upload service với test account
5. **Database**: Ensure proper indexes và constraints

### Test Execution Guidelines
1. **Sequential Testing**: Execute test cases in order for dependencies
2. **Data Cleanup**: Clean test data between rounds, especially images
3. **Error Logging**: Document all error responses và status codes
4. **Image Validation**: Verify image upload/delete operations on Cloudinary
5. **Performance**: Monitor response times for image operations

### Validation Criteria
- **HTTP Status Codes**: Verify correct status codes for all scenarios
- **Response Format**: Validate JSON structure và required fields
- **Image Operations**: Confirm Cloudinary integration works correctly
- **Soft Delete**: Verify soft delete/restore functionality
- **Security**: Ensure proper authentication và authorization
- **Data Integrity**: Verify database consistency after operations

### Test Data Requirements
- **Categories**: Electronics (ID=1), Fashion (ID=2)
- **Brands**: Apple (ID=1), Samsung (ID=2)
- **Products**: iPhone 15 Pro (ID=1), Samsung Galaxy (ID=2)
- **Users**: Admin (admin@techbox.com) và User (user@techbox.com) accounts
- **Images**: Valid JPEG/PNG files of various sizes for testing

---

## Notes
- Test cases marked with * require manual verification
- Image upload tests require valid Cloudinary configuration
- Soft delete functionality must preserve image URLs
- Performance tests should be conducted under realistic load
- Security tests must validate proper access controls và input sanitization