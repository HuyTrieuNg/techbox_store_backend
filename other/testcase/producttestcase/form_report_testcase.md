Test Case ID	Test Case Description	Test Case Procedure	Expected Results	Pre-conditions	Round 1	Test date	Tester	Round 2	Test date	Tester	Round 3	Test date	Tester	Note
Function 1: Lấy danh sách sản phẩm (GET /api/products)														
TC_PROD_001	Kiểm tra lấy danh sách tất cả products thành công (Public access)	"Gửi GET request đến ""/api/products""."	"- HTTP Status: 200 OK
- Response body:
[
{
""id"": 1,
""name"": ""iPhone 15 Pro"",
""description"": ""Latest iPhone with Pro features"",
""imageUrl"": ""https://res.cloudinary.com/demo/image/upload/product_images/iphone.jpg"",
""imagePublicId"": ""product_images/iphone"",
""brandId"": 1,
""categoryId"": 1,
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T10:00:00Z"",
""deletedAt"": null
}
]"	"- Database có products: iPhone 15 Pro (ID=1)
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_002	Kiểm tra lấy danh sách products bao gồm deleted products	"Gửi GET request đến ""/api/products?includeDeleted=true""."	"- HTTP Status: 200 OK
- Response body bao gồm cả products có deletedAt không null"	"- Database có products bị soft delete
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_003	Kiểm tra lấy danh sách products khi database trống	"Gửi GET request đến ""/api/products""."	"- HTTP Status: 200 OK
- Response body: []"	"- Database không có product nào
- Không cần authentication"	Pending			Pending			Pending			
Function 2: Lấy danh sách sản phẩm active (GET /api/products/active)														
TC_PROD_004	Kiểm tra lấy danh sách active products thành công (Public access)	"Gửi GET request đến ""/api/products/active""."	"- HTTP Status: 200 OK
- Response body chỉ chứa products có deletedAt = null"	"- Database có active products
- Không cần authentication"	Pending			Pending			Pending			
Function 3: Lấy sản phẩm theo ID (GET /api/products/{id})														
TC_PROD_005	Kiểm tra lấy product theo ID hợp lệ (Public access)	"Gửi GET request đến ""/api/products/1""."	"- HTTP Status: 200 OK
- Response body:
{
""id"": 1,
""name"": ""iPhone 15 Pro"",
""description"": ""Latest iPhone with Pro features"",
""imageUrl"": ""https://res.cloudinary.com/demo/image/upload/product_images/iphone.jpg"",
""imagePublicId"": ""product_images/iphone"",
""brandId"": 1,
""categoryId"": 1,
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T10:00:00Z"",
""deletedAt"": null
}"	"- Product iPhone 15 Pro với ID=1 tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_006	Kiểm tra lấy product với ID không tồn tại	"Gửi GET request đến ""/api/products/999999""."	"- HTTP Status: 404 Not Found"	"- Product ID 999999 không tồn tại trong database
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_007	Kiểm tra lấy product với ID không hợp lệ (không phải số nguyên)	"Gửi GET request đến ""/api/products/abc""."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parameter"",
""message"": ""ID must be a valid integer""
}"	- Không cần authentication	Pending			Pending			Pending			
TC_PROD_008	Kiểm tra lấy deleted product với includeDeleted=true	"Gửi GET request đến ""/api/products/5?includeDeleted=true""."	"- HTTP Status: 200 OK
- Response body chứa product bị soft delete"	"- Product ID=5 bị soft delete (deletedAt không null)
- Không cần authentication"	Pending			Pending			Pending			
Function 4: Tạo sản phẩm mới (POST /api/products)														
TC_PROD_009	Kiểm tra tạo product thành công với role Admin (không có image)	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data: 
- name=""Samsung Galaxy S24""
- description=""Flagship Samsung smartphone""
- categoryId=1
- brandId=2"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 10,
""name"": ""Samsung Galaxy S24"",
""description"": ""Flagship Samsung smartphone"",
""imageUrl"": null,
""imagePublicId"": null,
""brandId"": 2,
""categoryId"": 1,
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z"",
""deletedAt"": null
}"	"- Admin đã đăng nhập với tài khoản (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Samsung (ID=2), Category Electronics (ID=1) tồn tại
- Product ""Samsung Galaxy S24"" chưa tồn tại"	Pending			Pending			Pending			
TC_PROD_010	Kiểm tra tạo product thành công với image upload	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""iPhone 16 Pro""
- description=""New iPhone""
- categoryId=1
- brandId=1
- image=<valid_image_file.jpg>"	"- HTTP Status: 201 Created
- Response body có imageUrl và imagePublicId từ Cloudinary"	"- Admin đã đăng nhập
- Valid image file (jpg/png, <5MB)
- Brand Apple (ID=1), Category Electronics (ID=1) tồn tại"	Pending			Pending			Pending			
TC_PROD_011	Kiểm tra tạo product với tên trống	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""""
- description=""Test product""
- categoryId=1"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Product name is required"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_PROD_012	Kiểm tra tạo product với tên null	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- description=""Test product""
- categoryId=1"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Product name is required"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_PROD_013	Kiểm tra tạo product với tên quá ngắn (<2 ký tự)	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""A""
- description=""Test product""
- categoryId=1"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Product name must be at least 2 characters long"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_PROD_014	Kiểm tra tạo product với tên quá dài (>255 ký tự)	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""A very long product name that exceeds maximum length of 255 characters dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd""
- description=""Test product""
- categoryId=1"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Product name must not exceed 255 characters"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_PROD_015	Kiểm tra tạo product với tên chỉ chứa khoảng trắng	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""     ""
- description=""Test product""
- categoryId=1"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Product name cannot contain only whitespace"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_PROD_016	Kiểm tra tạo product với tên có khoảng trắng đầu/cuối	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""  MacBook Pro M3  ""
- description=""Latest MacBook""
- categoryId=1
- brandId=1"	"- HTTP Status: 201 Created
- Response body có name=""MacBook Pro M3"" (auto-trim whitespace)"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role
- Auto-trim whitespace"	Pending			Pending			Pending			
TC_PROD_017	Kiểm tra tạo product với các ký tự bị cấm (XSS, SQL Injection)	"Gửi lần lượt POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data 1:
- name=""Product<script>alert('XSS')</script>""
- description=""Test""
Form data 2:
- name=""'; DROP TABLE products; --""
- description=""Test"""	"Cả 2 đều trả về:
- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Product name contains invalid characters""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_PROD_018	Kiểm tra tạo product với tên trùng lặp (case insensitive)	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""IPHONE 15 PRO""
- description=""Duplicate name test""
- categoryId=1
- brandId=1"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Duplicate entry"",
""message"": ""Product name 'IPHONE 15 PRO' already exists (case insensitive)"",
""field"": ""name"",
""existingProduct"": ""iPhone 15 Pro""
}"	"- Admin đã đăng nhập
- Product ""iPhone 15 Pro"" đã tồn tại (ID=1)"	Pending			Pending			Pending			
TC_PROD_019	Kiểm tra tạo product với categoryId không tồn tại	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""Invalid Category Product""
- description=""Test product""
- categoryId=999
- brandId=1"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid category"",
""message"": ""Category with ID 999 does not exist"",
""field"": ""categoryId""
}"	"- Admin đã đăng nhập
- Category ID 999 không tồn tại"	Pending			Pending			Pending			
TC_PROD_020	Kiểm tra tạo product với brandId không tồn tại	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""Invalid Brand Product""
- description=""Test product""
- categoryId=1
- brandId=999"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid brand"",
""message"": ""Brand with ID 999 does not exist"",
""field"": ""brandId""
}"	"- Admin đã đăng nhập
- Brand ID 999 không tồn tại"	Pending			Pending			Pending			
TC_PROD_021	Kiểm tra tạo product với file image không hợp lệ (format)	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""Image Format Test""
- description=""Test product""
- categoryId=1
- image=<invalid_file.txt>"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid file format"",
""message"": ""Only image files (jpg, jpeg, png, gif) are allowed""
}"	"- Admin đã đăng nhập
- File không phải image format"	Pending			Pending			Pending			
TC_PROD_022	Kiểm tra tạo product với file image quá lớn (>5MB)	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""Large Image Test""
- description=""Test product""
- categoryId=1
- image=<large_file_6MB.jpg>"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""File too large"",
""message"": ""Image file size must not exceed 5MB""
}"	"- Admin đã đăng nhập
- Image file >5MB"	Pending			Pending			Pending			
TC_PROD_023	Kiểm tra tạo product với Unicode trong name	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""Điện Thoại Thông Minh""
- description=""Smartphone in Vietnamese""
- categoryId=1
- brandId=2"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 11,
""name"": ""Điện Thoại Thông Minh"",
""description"": ""Smartphone in Vietnamese"",
""brandId"": 2,
""categoryId"": 1,
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}"	"- Admin đã đăng nhập
- Unicode characters được hỗ trợ"	Pending			Pending			Pending			
TC_PROD_024	Kiểm tra tạo product với role User (không có quyền)	"Gửi POST request đến ""/api/products"".
Header Authorization: Bearer <user_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""Unauthorized Product""
- description=""Test product""
- categoryId=1"	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can create products""
}"	"- User đã đăng nhập (user@techbox.com/user123)
- JWT token hợp lệ với USER role only"	Pending			Pending			Pending			
TC_PROD_025	Kiểm tra tạo product khi không đăng nhập	"Gửi POST request đến ""/api/products"".
Header Content-Type: multipart/form-data.
Form data:
- name=""Unauthorized Product""
- description=""Test product""
- categoryId=1"	"- HTTP Status: 401 Unauthorized
- Response body:
{
""error"": ""Unauthorized"",
""message"": ""Authentication required to access this resource""
}"	- Người dùng chưa đăng nhập	Pending			Pending			Pending			
Function 5: Cập nhật sản phẩm (PUT /api/products/{id})														
TC_PROD_026	Kiểm tra cập nhật product thành công với role Admin (không thay đổi image)	"Gửi PUT request đến ""/api/products/1"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""iPhone 15 Pro Max""
- description=""Updated iPhone description""
- categoryId=1
- brandId=1"	"- HTTP Status: 200 OK
- Response body:
{
""id"": 1,
""name"": ""iPhone 15 Pro Max"",
""description"": ""Updated iPhone description"",
""imageUrl"": ""https://existing-image-url.jpg"",
""brandId"": 1,
""categoryId"": 1,
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T14:30:00Z""
}"	"- Admin đã đăng nhập
- Product iPhone 15 Pro (ID=1) tồn tại
- Name ""iPhone 15 Pro Max"" chưa tồn tại"	Pending			Pending			Pending			
TC_PROD_027	Kiểm tra cập nhật product không tồn tại	"Gửi PUT request đến ""/api/products/999999"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""Non-existent Product""
- description=""Test"""	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Product not found"",
""message"": ""Product with ID 999999 does not exist""
}"	"- Admin đã đăng nhập
- ID 999999 không tồn tại"	Pending			Pending			Pending			
TC_PROD_028	Kiểm tra cập nhật product với tên trùng lặp	"Gửi PUT request đến ""/api/products/1"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""Samsung Galaxy S24""
- description=""Test update""
- categoryId=1
- brandId=1"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Duplicate entry"",
""message"": ""Product name 'Samsung Galaxy S24' already exists"",
""field"": ""name""
}"	"- Admin đã đăng nhập
- iPhone 15 Pro (ID=1), Samsung Galaxy S24 (ID=10) tồn tại"	Pending			Pending			Pending			
TC_PROD_029	Kiểm tra cập nhật product với image mới	"Gửi PUT request đến ""/api/products/1"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""iPhone 15 Pro""
- description=""Updated with new image""
- categoryId=1
- brandId=1
- image=<new_image.jpg>"	"- HTTP Status: 200 OK
- Response body có imageUrl và imagePublicId mới từ Cloudinary
- Image cũ bị xóa khỏi Cloudinary"	"- Admin đã đăng nhập
- Product iPhone 15 Pro (ID=1) tồn tại và có image cũ
- Valid new image file"	Pending			Pending			Pending			
TC_PROD_030	Kiểm tra cập nhật product với deleteImage=true	"Gửi PUT request đến ""/api/products/1"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""iPhone 15 Pro""
- description=""Image deleted""
- deleteImage=true"	"- HTTP Status: 200 OK
- Response body có imageUrl=null, imagePublicId=null
- Image cũ bị xóa khỏi Cloudinary"	"- Admin đã đăng nhập
- Product iPhone 15 Pro (ID=1) có image hiện tại"	Pending			Pending			Pending			
TC_PROD_031	Kiểm tra cập nhật product với role User (không có quyền)	"Gửi PUT request đến ""/api/products/1"".
Header Authorization: Bearer <user_token>.
Header Content-Type: multipart/form-data.
Form data:
- name=""iPhone 15 Pro Updated""
- description=""Test update"""	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can update products""
}"	"- User đã đăng nhập
- Product iPhone 15 Pro (ID=1) tồn tại"	Pending			Pending			Pending			
Function 6: Xóa sản phẩm (DELETE /api/products/{id})														
TC_PROD_032	Kiểm tra xóa product thành công với role Admin (soft delete)	"Gửi DELETE request đến ""/api/products/10"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 204 No Content
- Product bị soft delete (deletedAt được set)"	"- Admin đã đăng nhập
- Product Samsung Galaxy S24 (ID=10) tồn tại"	Pending			Pending			Pending			
TC_PROD_033	Kiểm tra xóa product không tồn tại	"Gửi DELETE request đến ""/api/products/999999"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Product not found"",
""message"": ""Product with ID 999999 does not exist""
}"	"- Admin đã đăng nhập
- ID 999999 không tồn tại"	Pending			Pending			Pending			
TC_PROD_034	Kiểm tra xóa product với role User (không có quyền)	"Gửi DELETE request đến ""/api/products/10"".
Header Authorization: Bearer <user_token>."	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can delete products""
}"	"- User đã đăng nhập
- Product Samsung Galaxy S24 (ID=10) tồn tại"	Pending			Pending			Pending			
Function 7: Khôi phục sản phẩm (PATCH /api/products/{id}/restore)														
TC_PROD_035	Kiểm tra khôi phục product đã bị soft delete với role Admin	"Gửi PATCH request đến ""/api/products/10/restore"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Product được khôi phục (deletedAt = null)"	"- Admin đã đăng nhập
- Product Samsung Galaxy S24 (ID=10) đã bị soft delete"	Pending			Pending			Pending			
TC_PROD_036	Kiểm tra khôi phục product chưa bị delete	"Gửi PATCH request đến ""/api/products/1/restore"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid operation"",
""message"": ""Product is not deleted""
}"	"- Admin đã đăng nhập
- Product iPhone 15 Pro (ID=1) chưa bị delete"	Pending			Pending			Pending			
TC_PROD_037	Kiểm tra khôi phục product với role User (không có quyền)	"Gửi PATCH request đến ""/api/products/10/restore"".
Header Authorization: Bearer <user_token>."	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can restore products""
}"	"- User đã đăng nhập
- Product Samsung Galaxy S24 (ID=10) đã bị soft delete"	Pending			Pending			Pending			
Function 8: Lấy sản phẩm theo category (GET /api/products/category/{categoryId})														
TC_PROD_038	Kiểm tra lấy products theo categoryId thành công (Public access)	"Gửi GET request đến ""/api/products/category/1""."	"- HTTP Status: 200 OK
- Response body:
[
{
""id"": 1,
""name"": ""iPhone 15 Pro"",
""categoryId"": 1,
""brandId"": 1,
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T10:00:00Z"",
""deletedAt"": null
}
]"	"- Category Electronics (ID=1) có products
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_039	Kiểm tra lấy products theo categoryId không có products	"Gửi GET request đến ""/api/products/category/5""."	"- HTTP Status: 200 OK
- Response body: []"	"- Category ID=5 tồn tại nhưng không có products
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_040	Kiểm tra lấy products theo categoryId không tồn tại	"Gửi GET request đến ""/api/products/category/999999""."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Category not found"",
""message"": ""Category with ID 999999 does not exist""
}"	"- Category ID 999999 không tồn tại
- Không cần authentication"	Pending			Pending			Pending			
Function 9: Lấy sản phẩm theo brand (GET /api/products/brand/{brandId})														
TC_PROD_041	Kiểm tra lấy products theo brandId thành công (Public access)	"Gửi GET request đến ""/api/products/brand/1""."	"- HTTP Status: 200 OK
- Response body chứa tất cả products có brandId=1"	"- Brand Apple (ID=1) có products
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_042	Kiểm tra lấy products theo brandId không có products	"Gửi GET request đến ""/api/products/brand/5""."	"- HTTP Status: 200 OK
- Response body: []"	"- Brand ID=5 tồn tại nhưng không có products
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_043	Kiểm tra lấy products theo brandId không tồn tại	"Gửi GET request đến ""/api/products/brand/999999""."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Brand not found"",
""message"": ""Brand with ID 999999 does not exist""
}"	"- Brand ID 999999 không tồn tại
- Không cần authentication"	Pending			Pending			Pending			
Function 10: Tìm kiếm sản phẩm (GET /api/products/search)														
TC_PROD_044	Kiểm tra tìm kiếm products theo keyword thành công (Public access)	"Gửi GET request đến ""/api/products/search?keyword=iPhone""."	"- HTTP Status: 200 OK
- Response body chứa products có name chứa ""iPhone"""	"- Database có products với name chứa ""iPhone""
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_045	Kiểm tra tìm kiếm products với keyword không tìm thấy	"Gửi GET request đến ""/api/products/search?keyword=NonExistentProduct""."	"- HTTP Status: 200 OK
- Response body: []"	"- Database không có products với keyword ""NonExistentProduct""
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_046	Kiểm tra tìm kiếm products với keyword rỗng	"Gửi GET request đến ""/api/products/search?keyword=""."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parameter"",
""message"": ""Search keyword is required"",
""field"": ""keyword""
}"	- Không cần authentication	Pending			Pending			Pending			
TC_PROD_047	Kiểm tra tìm kiếm products với keyword case insensitive	"Gửi GET request đến ""/api/products/search?keyword=iphone""."	"- HTTP Status: 200 OK
- Response body chứa products có name chứa ""iPhone"" (case insensitive)"	"- Database có products với name chứa ""iPhone""
- Search case insensitive
- Không cần authentication"	Pending			Pending			Pending			
Function 11: Kiểm tra tồn tại tên sản phẩm (GET /api/products/exists)														
TC_PROD_048	Kiểm tra product name tồn tại (Public access)	"Gửi GET request đến ""/api/products/exists?name=iPhone 15 Pro""."	"- HTTP Status: 200 OK
- Response body: true"	"- Product ""iPhone 15 Pro"" tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_049	Kiểm tra product name không tồn tại	"Gửi GET request đến ""/api/products/exists?name=NonExistentProduct""."	"- HTTP Status: 200 OK
- Response body: false"	"- Product ""NonExistentProduct"" không tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_050	Kiểm tra product name tồn tại với excludeId	"Gửi GET request đến ""/api/products/exists?name=iPhone 15 Pro&excludeId=1""."	"- HTTP Status: 200 OK
- Response body: false"	"- Product ""iPhone 15 Pro"" tồn tại với ID=1 nhưng bị exclude
- Không cần authentication"	Pending			Pending			Pending			
TC_PROD_051	Kiểm tra với parameter name rỗng	"Gửi GET request đến ""/api/products/exists?name=""."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parameter"",
""message"": ""Product name parameter is required"",
""field"": ""name""
}"	- Không cần authentication	Pending			Pending			Pending			
Function 12: Xóa image của sản phẩm (DELETE /api/products/{id}/image)														
TC_PROD_052	Kiểm tra xóa image của product thành công với role Admin	"Gửi DELETE request đến ""/api/products/1/image"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Response body:
{
""id"": 1,
""name"": ""iPhone 15 Pro"",
""imageUrl"": null,
""imagePublicId"": null,
""updatedAt"": ""2025-09-30T15:00:00Z""
}
- Image bị xóa khỏi Cloudinary"	"- Admin đã đăng nhập
- Product iPhone 15 Pro (ID=1) có image"	Pending			Pending			Pending			
TC_PROD_053	Kiểm tra xóa image của product không có image	"Gửi DELETE request đến ""/api/products/2/image"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Response body:
{
""message"": ""Product has no image to delete""
}"	"- Admin đã đăng nhập
- Product (ID=2) không có image"	Pending			Pending			Pending			
TC_PROD_054	Kiểm tra xóa image của product không tồn tại	"Gửi DELETE request đến ""/api/products/999999/image"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Product not found"",
""message"": ""Product with ID 999999 does not exist""
}"	"- Admin đã đăng nhập
- Product ID 999999 không tồn tại"	Pending			Pending			Pending			
TC_PROD_055	Kiểm tra xóa image với role User (không có quyền)	"Gửi DELETE request đến ""/api/products/1/image"".
Header Authorization: Bearer <user_token>."	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can delete product images""
}"	"- User đã đăng nhập
- Product iPhone 15 Pro (ID=1) có image"	Pending			Pending			Pending			
Function 13: Upload image cho sản phẩm (POST /api/products/upload-image)														
TC_PROD_056	Kiểm tra upload image thành công với role Admin	"Gửi POST request đến ""/api/products/upload-image"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- file=<valid_image.jpg>"	"- HTTP Status: 200 OK
- Response body:
{
""secure_url"": ""https://res.cloudinary.com/demo/image/upload/product_images/abc123.jpg"",
""public_id"": ""product_images/abc123"",
""message"": ""Image uploaded successfully""
}"	"- Admin đã đăng nhập
- Valid image file (jpg/png/gif, <5MB)"	Pending			Pending			Pending			
TC_PROD_057	Kiểm tra upload image với file không hợp lệ	"Gửi POST request đến ""/api/products/upload-image"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: multipart/form-data.
Form data:
- file=<invalid_file.txt>"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid file format"",
""message"": ""Only image files (jpg, jpeg, png, gif) are allowed""
}"	"- Admin đã đăng nhập
- File không phải image format"	Pending			Pending			Pending			
TC_PROD_058	Kiểm tra upload image với role User (không có quyền)	"Gửi POST request đến ""/api/products/upload-image"".
Header Authorization: Bearer <user_token>.
Header Content-Type: multipart/form-data.
Form data:
- file=<valid_image.jpg>"	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can upload product images""
}"	"- User đã đăng nhập
- Valid image file"	Pending			Pending			Pending			
Function 14: Xóa image theo publicId (DELETE /api/products/delete-image)														
TC_PROD_059	Kiểm tra xóa image theo publicId thành công với role Admin	"Gửi DELETE request đến ""/api/products/delete-image?publicId=product_images/abc123"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Response body:
{
""result"": ""ok"",
""message"": ""Image deleted successfully""
}
- Image bị xóa khỏi Cloudinary"	"- Admin đã đăng nhập
- PublicId ""product_images/abc123"" tồn tại trên Cloudinary"	Pending			Pending			Pending			
TC_PROD_060	Kiểm tra xóa image với publicId không tồn tại	"Gửi DELETE request đến ""/api/products/delete-image?publicId=product_images/nonexistent"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Image not found"",
""message"": ""Image with publicId 'product_images/nonexistent' does not exist""
}"	"- Admin đã đăng nhập
- PublicId không tồn tại trên Cloudinary"	Pending			Pending			Pending			
TC_PROD_061	Kiểm tra xóa image theo publicId với role User (không có quyền)	"Gửi DELETE request đến ""/api/products/delete-image?publicId=product_images/abc123"".
Header Authorization: Bearer <user_token>."	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can delete images""
}"	"- User đã đăng nhập
- PublicId tồn tại"	Pending			Pending			Pending