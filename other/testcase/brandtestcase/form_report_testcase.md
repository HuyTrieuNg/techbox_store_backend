Test Case ID	Test Case Description	Test Case Procedure	Expected Results	Pre-conditions	Round 1	Test date	Tester	Round 2	Test date	Tester	Round 3	Test date	Tester	Note
Function 1: Lấy danh sách thương hiệu (GET /api/brands)														
TC_BRAND_001	Kiểm tra lấy danh sách tất cả brands thành công (Public access)	Gửi GET request đến "/api/brands".	"- HTTP Status: 200 OK
- Response body:
[
{
""id"": 1,
""name"": ""Apple"",
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T10:00:00Z""
},
{
""id"": 2,
""name"": ""Samsung"",
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T10:00:00Z""
}
]"	"- Database có brands: Apple (ID=1), Samsung (ID=2)
- Không cần authentication"	Pending			Pending			Pending			
TC_BRAND_002	Kiểm tra lấy danh sách brands khi database trống	Gửi GET request đến "/api/brands".	"- HTTP Status: 200 OK
- Response body: []"	"- Database không có brand nào
- Không cần authentication"	Pending			Pending			Pending			
Function 2: Lấy thương hiệu theo ID (GET /api/brands/{id})														
TC_BRAND_003	Kiểm tra lấy brand theo ID hợp lệ (là số nguyên và tồn tại bản ghi trong database)	Gửi GET request đến "/api/brands/1".	"- HTTP Status: 200 OK
- Response body:
{
""id"": 1,
""name"": ""Apple"",
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T10:00:00Z""
}"	"- Brand Apple với ID=1 tồn tại
- Không cần authentication"	Pending									
TC_BRAND_004	Kiểm tra lấy brand với ID không tồn tại	Gửi GET request đến "/api/brands/999999".	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Brand not found"",
""message"": ""Brand with ID 999999 does not exist""
}"	"- Brand ID 999999 không tồn tại trong database
- Không cần authentication"	Pending			Pending			Pending			
TC_BRAND_005	Kiểm tra lấy brand với ID không hợp lệ (không phải số nguyên: số thập phân) 	Gửi GET request đến "/api/brands/5.5"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parameter"",
""message"": ""ID must be a valid integer""
}"	- Không cần authentication	Pending			Pending			Pending			
TC_BRAND_006	Kiểm tra lấy brand với ID không hợp lệ (không phải số nguyên: chữ cái) 	Gửi GET request đến "/api/brands/abc"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parameter"",
""message"": ""ID must be a valid integer""
}"	- Không cần authentication	Pending			Pending			Pending			
Function 3: Tạo thương hiệu mới (POST /api/brands)														
TC_BRAND_007	Kiểm tra tạo brand thành công bao gồm ký tự ký tự hợp lệ (Unicode, số, ký tự đặc biệt) bằng role admin	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Công Nghệ & AI 360"" }"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 6,
""name"": ""Công Nghệ & AI 360"",
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}"	"""- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role.
-  Có sẵn 5 brand từ ID 1 đến 5.
- Brand ""Công nghệ & AI 360"" chưa tồn tại."	Pending			Pending			Pending			
TC_BRAND_008	Kiểm tra tạo brand với tên trống	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": """" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name is required"",
""field"": ""name"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_BRAND_009	Kiểm tra tạo brand với tên null	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": null }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name is required"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_BRAND_010	Kiểm tra tạo brand với tên undefined	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name is required"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_BRAND_011	Kiểm tra tạo brand với tên quá dài (trên cận trên: >255 ký tự) 	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""A very long brand name that exceeds the maximum length of 255 characters extendddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name must not exceed 255 characters"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role.
- Tên brand với 256 kí tự (trên cận trên): ""A very long brand name that exceeds the maximum length of 255 characters extendddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"""	Pending			Pending			Pending			
TC_BRAND_012	Kiểm tra tạo brand với tên quá ngắn (dưới cận dưới: <2 ký tự)	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""A"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name must be at least 2 characters long"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role.
- Tên brand với 1 kí tự (dưới cận dưới): ""A"""	Pending			Pending			Pending			
TC_BRAND_013	Kiểm tra tạo brand với tên ngắn tối thiểu (cận dưới: 2 ký tự)	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""AB"" }"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 6,
""name"": AB
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}
"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role.
- Tên brand với 2 kí tự (cận dưới): ""AB""
- Có sẵn 5 brand từ ID 1 đến 5."	Pending			Pending			Pending			
TC_BRAND_014	Kiểm tra tạo brand với tên dài tối đa (cận trên: 255 ký tự)	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""255 characters dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"" }"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 6,
""name"": 255 characters dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}
"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role.
- Tên brand với 255 kí tự (cận trên): ""255 characters dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd""
- Có sẵn 5 brand từ ID 1 đến 5."	Pending			Pending			Pending			
TC_BRAND_015	Kiểm tra tạo brand với tên chỉ chứa khoảng trắng	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""             "" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name cannot contain only whitespace"",
""field"": ""name""
}
"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role.
"	Pending			Pending			Pending			
TC_BRAND_016	Kiểm tra tạo brand với tên có khoảng trắng đầu/cuối	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""         Sony       "" }"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 6,
""name"": ""Sony"",
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}"	"""- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role.
- Có sẵn 5 brand từ ID 1 đến 5."	Pending			Pending			Pending			
TC_BRAND_017	Kiểm tra tạo brand với các chuỗi ký tự bị cấm (XSS, SQLI, Ký tự điều khiển)	"Gửi lần lượt 3 POST request đến ""/api/brands"".
với chung Header Authorization: Bearer <admin_token>. và 3 payload khác nhau:
1. XSS: Body: { ""name"": ""Brand<script>alert('XSS')</script>"" } 
2. SQLI: Body: { ""name"": ""'; DROP TABLE brands; --"" } 
3. Ký tự điều khiển: Body: { ""name"": ""Brand Name \n New Line"" }"	"Cả 3 đều trả về:
- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name contains invalid characters.""
}
"	"""- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role."	Pending			Pending			Pending			
TC_BRAND_018	Kiểm tra tạo brand với tên trùng lặp (không phân biệt hoa thường)	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""aPpLe"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Duplicate entry"",
""message"": ""Brand name 'APPLE' already exists (case insensitive)"",
""field"": ""name"",
""existingBrand"": ""Apple""
}
"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role.
- Brand ""Apple"" đã tồn tại (ID=1)"	Pending			Pending			Pending			
TC_BRAND_019	Kiểm tra tạo brand với role Staff (không có quyền)	"Gửi POST request đến ""/api/brands"".
Header Authorization: Bearer <staff_token>.
Body: { ""name"": ""Unauthorized Brand"" }"	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""You do not have the required permissions to perform this action.""
}"	"- Người dùng đã đăng nhập với tài khoản customer có sẵn (staff@techbox.com/staff123)
- JWT token hợp lệ với STAFF role."	Pending			Pending			Pending			
TC_BRAND_020	Kiểm tra tạo brand khi không đăng nhập 	"Gửi POST request đến ""/api/brands"".

Body: { ""name"": ""Unauthorized Brand"" }"	"- HTTP Status: 401 Unauthorized
- Response body:
{
  ""error"": ""Unauthorized"",
  ""message"": ""Authentication required to access this resource.""
}"	- Người dùng chưa đăng nhập	Pending			Pending			Pending			
Function 4: Cập nhật thương hiệu (PUT /api/brands/{id})														
TC_BRAND_021	Kiểm tra cập nhật brand thành công bao gồm ký tự ký tự hợp lệ (Unicode, số, ký tự đặc biệt) bằng role admin	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Công nghệ & AI 360"" }"	"- HTTP Status: 200 OK
- Response body:
{
""id"": 1,
""name"""": ""Công nghệ & AI 360"",
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-31T14:00:00Z""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) tồn tại.
- Brand ""Công nghệ & AI 360"" chưa tồn tại."	Pending			Pending			Pending			
TC_BRAND_022	Kiểm tra cập nhật brand không tồn tại	"Gửi PUT request đến ""/api/brands/999999"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""Non-existent Brand"" }"	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Brand not found"",
""message"": ""Brand with ID 999999 does not exist""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- ID 999999 không tồn tại"	Pending			Pending			Pending			
TC_BRAND_023	Kiểm tra cập nhật brand với tên trùng lặp (không phân biệt hoa thường)	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""sAmsSung"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Duplicate entry"",
""message"": ""Brand name 'Samsung' already exists"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1), Samsung (ID=2) tồn tại"	Pending			Pending			Pending			
TC_BRAND_024	Kiểm tra cập nhật brand với tên trống	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": """" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name is required"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) tồn tại"	Pending			Pending			Pending			
TC_BRAND_025	Kiểm tra cập nhật brand với tên null	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": null }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name is required"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) tồn tại"	Pending			Pending			Pending			
TC_BRAND_026	Kiểm tra cập nhật brand với tên quá dài (trên cận trên: >255 ký tự) 	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""A very long brand name that exceeds the maximum length of 255 characters extendddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"" }
"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name must not exceed 255 characters"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) tồn tại
- E49Tên brand với 256 kí tự (trên cận trên): ""A very long brand name that exceeds the maximum length of 255 characters extendddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"""	Pending			Pending			Pending			
TC_BRAND_027	Kiểm tra tạo brand với tên quá ngắn (dưới cận dưới: <2 ký tự)	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""A"" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name must be at least 2 characters long"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role.
- Brand Apple (ID=1) tồn tại
- Tên brand với 1 kí tự (dưới cận dưới): ""A"""	Pending			Pending			Pending			
TC_BRAND_028	Kiểm tra cập nhật brand với tên ngắn tối thiểu (cận dưới: 2 ký tự)	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""AB"" }"	"- HTTP Status: 200 OK
- Response body:
{
""id"": 1,
""name"": ""AB"",
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T14:30:00Z""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) tồn tại
- Tên brand với 2 kí tự (cận dưới): ""AB"""	Pending			Pending			Pending			
TC_BRAND_029	Kiểm tra cập nhật brand với tên dài tối đa (cận trên: 255 ký tự) 	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { """"name"""": ""255 characters dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"" }
"	"- HTTP Status: 200 OK
- Response body:
{
""id"": 1,
""name"": ""255 characters dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"",
""createdAt"": ""2025-09-30T10:00:00Z"",
""updatedAt"": ""2025-09-30T14:30:00Z""
}
"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) tồn tại
- Tên brand với 255 kí tự (cận trên): ""255 characters dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"""	Pending			Pending			Pending			
TC_BRAND_030	Kiểm tra cập nhật brand với tên chỉ chứa khoảng trắng	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""             "" }"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name cannot contain only whitespace"",
""field"": ""name""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) tồn tại"	Pending			Pending			Pending			
TC_BRAND_031	Kiểm tra cập nhật brand với tên có khoảng trắng đầu/cuối	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>.
Body: { ""name"": ""         Sony       "" }"	"- HTTP Status: 200 OK
- Response body:
{
""id"": 1,
""name"""": ""Sony"",
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) tồn tại
"	Pending			Pending			Pending			
TC_BRAND_032	Kiểm tra cập nhật brand với các chuỗi ký tự bị cấm (XSS, SQLI, Ký tự điều khiển)	"Gửi lần lượt PUT request đến ""/api/brands/1"".
với chung Header Authorization: Bearer <admin_token>. và 3 payload khác nhau:
1. XSS: Body: { ""name"": ""Brand<script>alert('XSS')</script>"" } 
2. SQLI: Body: { ""name"": ""'; DROP TABLE brands; --"" } 
3. Ký tự điều khiển: Body: { ""name"": ""Brand Name \n New Line"" }"	"Cả 3 đều trả về:
- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Brand name contains invalid characters.""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) tồn tại"	Pending			Pending			Pending			
TC_BRAND_033	Kiểm tra tạo brand với role Staff (không có quyền)	"Gửi PUT request đến ""/api/brands/1"".
Header Authorization: Bearer <staff_token>.Body: { ""name"": ""Unauthorized Brand"" }"	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""You do not have the required permissions to perform this action.""
}"	"- Người dùng đã đăng nhập với tài khoản customer có sẵn (staff@techbox.com/staff123)
- JWT token hợp lệ với STAFF role.
- Brand Apple (ID=1) tồn tại"	Pending			Pending			Pending			
TC_BRAND_034	Kiểm tra cập nhật brand khi không đăng nhập 	"Gửi PUT request đến ""/api/brands/1"".

Body: { ""name"": ""Unauthorized Brand"" }"	"- HTTP Status: 401 Unauthorized
- Response body:
{
  ""error"": ""Unauthorized"",
  ""message"": ""Authentication required to access this resource.""
}"	"- người dùng chưa đăng nhập
- Brand Apple (ID=1) tồn tại"	Pending			Pending			Pending			
Function 5: Xóa thương hiệu (DELETE /api/brands/{id})														
TC_BRAND_035	Kiểm tra xóa brand thành công với role Admin	"Gửi DELETE request đến ""/api/brands/3"".
Header Authorization: Bearer <admin_token>."	- HTTP Status: 204 No Content	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand SamSung (ID=3) tồn tại và không sử dụng"	Pending			Pending			Pending			
TC_BRAND_036	Kiểm tra xóa brand không tồn tại	"Gửi DELETE request đến ""/api/brands/999999"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Brand not found"",
""message"": ""Brand with ID 999999 does not exist""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- ID 999999 không tồn tại"	Pending			Pending			Pending			
TC_BRAND_037	Kiểm tra xóa brand đang được sử dụng bởi products	"Gửi DELETE request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Cannot delete brand"",
""message"": ""Brand is being used by 3 products. Please reassign or delete products first."",
""relatedProducts"": [101, 102, 103]
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) có products: iPhone 15 (ID=101), iPad (ID=102), MacBook (ID=103)"	Pending			Pending			Pending			
TC_BRAND_038	Kiểm tra xóa brand đang được sử dụng bởi products	"Gửi DELETE request đến ""/api/brands/1"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Cannot delete brand"",
""message"": ""Brand is being used by 3 products. Please reassign or delete products first."",
""relatedProducts"": [101, 102, 103]
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Brand Apple (ID=1) có products: iPhone 15 (ID=101), iPad (ID=102), MacBook (ID=103)"	Pending			Pending			Pending			
TC_BRAND_039	Kiểm tra xóa brand với role Staff (không có quyền)	"Gửi DELETE request đến ""/api/brands/3"".
Header Authorization: Bearer <staff_token>."	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can delete brands""
}"	"- Người dùng đã đăng nhập với tài khoản admin có sẵn (staff@techbox.com/staff123)
- JWT token hợp lệ với STAFF role
- Brand SamSung (ID=3) tồn tại và không sử dụng"	Pending			Pending			Pending			
TC_BRAND_040	Kiểm tra xóa brand chưa đăng nhập	"Gửi DELETE request đến ""/api/brands/3"".
Header Authorization: Bearer <staff_token>."	"- HTTP Status: 401 Unauthorized
- Response body:
{
  ""error"": ""Unauthorized"",
  ""message"": ""Authentication required to access this resource.""
}"	"- người dùng chưa đăng nhập
- Brand SamSung (ID=3) tồn tại và không sử dụng"	Pending			Pending			Pending			
