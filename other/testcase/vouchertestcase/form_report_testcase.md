Test Case ID	Test Case Description	Test Case Procedure	Expected Results	Pre-conditions	Round 1	Test date	Tester	Round 2	Test date	Tester	Round 3	Test date	Tester	Note
Function 1: Tạo voucher mới (POST /api/vouchers)														
TC_VOUCH_001	Kiểm tra tạo voucher thành công với PERCENTAGE discount type	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""SPRING2024"",
""name"": ""Spring Sale"",
""description"": ""25% discount for spring season"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 25.00,
""minOrderAmount"": 100.00,
""maxDiscountAmount"": 50.00,
""usageLimit"": 1000,
""usageLimitPerUser"": 2,
""startDate"": ""2024-03-01T00:00:00"",
""endDate"": ""2024-05-31T23:59:59"",
""isActive"": true
}"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 10,
""code"": ""SPRING2024"",
""name"": ""Spring Sale"",
""description"": ""25% discount for spring season"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 25.00,
""minOrderAmount"": 100.00,
""maxDiscountAmount"": 50.00,
""usageLimit"": 1000,
""usageLimitPerUser"": 2,
""usedCount"": 0,
""isActive"": true,
""validFrom"": ""2024-03-01T00:00:00Z"",
""validUntil"": ""2024-05-31T23:59:59Z"",
""createdAt"": ""2025-09-30T14:00:00Z"",
""updatedAt"": ""2025-09-30T14:00:00Z""
}"	"- Admin đã đăng nhập (admin@techbox.com/admin123)
- JWT token hợp lệ với ADMIN role
- Voucher code ""SPRING2024"" chưa tồn tại"	Pending			Pending			Pending			
TC_VOUCH_002	Kiểm tra tạo voucher thành công với FIXED_AMOUNT discount type	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""WELCOME50"",
""name"": ""Welcome Discount"",
""description"": ""Fixed 50k discount for new customers"",
""discountType"": ""FIXED_AMOUNT"",
""discountValue"": 50000.00,
""minOrderAmount"": 200000.00,
""usageLimit"": 500,
""usageLimitPerUser"": 1,
""startDate"": ""2024-01-01T00:00:00"",
""endDate"": ""2024-12-31T23:59:59"",
""isActive"": true
}"	"- HTTP Status: 201 Created
- Response body có discountType=""FIXED_AMOUNT""
- discountValue=50000.00
- maxDiscountAmount=null (không áp dụng cho FIXED_AMOUNT)"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role
- Voucher code ""WELCOME50"" chưa tồn tại"	Pending			Pending			Pending			
TC_VOUCH_003	Kiểm tra tạo voucher với code trống	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": """",
""name"": ""Test Voucher"",
""description"": ""Test description"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": 50.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Voucher code is required"",
""field"": ""code""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_004	Kiểm tra tạo voucher với code null	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""name"": ""Test Voucher"",
""description"": ""Test description"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": 50.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Voucher code is required"",
""field"": ""code""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_005	Kiểm tra tạo voucher với code quá ngắn (<3 ký tự)	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""AB"",
""name"": ""Test Voucher"",
""description"": ""Test description"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": 50.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Voucher code must be at least 3 characters long"",
""field"": ""code""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_006	Kiểm tra tạo voucher với code quá dài (>50 ký tự)	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""VERYLONGVOUCHERCODEEXCEEDINGMAXIMUMLENGTHALLOWED123456"",
""name"": ""Test Voucher"",
""description"": ""Test description"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": 50.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Voucher code must not exceed 50 characters"",
""field"": ""code""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_007	Kiểm tra tạo voucher với code chứa ký tự đặc biệt không hợp lệ	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""SPRING@2024#"",
""name"": ""Test Voucher"",
""description"": ""Test description"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": 50.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Voucher code can only contain alphanumeric characters, hyphens and underscores"",
""field"": ""code""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_008	Kiểm tra tạo voucher với code trùng lặp (case insensitive)	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""spring2024"",
""name"": ""Duplicate Code Test"",
""description"": ""Test duplicate code"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 15.00,
""minOrderAmount"": 100.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Duplicate entry"",
""message"": ""Voucher code 'spring2024' already exists (case insensitive)"",
""field"": ""code"",
""existingVoucher"": ""SPRING2024""
}"	"- Admin đã đăng nhập
- Voucher ""SPRING2024"" đã tồn tại (ID=10)"	Pending			Pending			Pending			
TC_VOUCH_009	Kiểm tra tạo voucher với discountValue âm	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""NEGATIVE001"",
""name"": ""Negative Test"",
""description"": ""Test negative discount"",
""discountType"": ""PERCENTAGE"",
""discountValue"": -10.00,
""minOrderAmount"": 100.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Discount value must be greater than 0"",
""field"": ""discountValue""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_010	Kiểm tra tạo voucher với PERCENTAGE discount > 100%	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""OVER100"",
""name"": ""Over 100% Test"",
""description"": ""Test over 100% discount"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 150.00,
""minOrderAmount"": 100.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Percentage discount cannot exceed 100%"",
""field"": ""discountValue""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_011	Kiểm tra tạo voucher với minOrderAmount âm	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""NEGMIN001"",
""name"": ""Negative Min Test"",
""description"": ""Test negative min order amount"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": -50.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Minimum order amount must be greater than or equal to 0"",
""field"": ""minOrderAmount""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_012	Kiểm tra tạo voucher với usageLimit = 0	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""ZEROLIMIT"",
""name"": ""Zero Limit Test"",
""description"": ""Test zero usage limit"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": 100.00,
""usageLimit"": 0
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""Usage limit must be greater than 0 or null for unlimited"",
""field"": ""usageLimit""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_013	Kiểm tra tạo voucher với startDate > endDate	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""INVALIDDATE"",
""name"": ""Invalid Date Test"",
""description"": ""Test invalid date range"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": 100.00,
""startDate"": ""2024-12-31T23:59:59"",
""endDate"": ""2024-01-01T00:00:00""
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Validation failed"",
""message"": ""End date must be after start date"",
""field"": ""endDate""
}"	"- Admin đã đăng nhập
- JWT token hợp lệ với ADMIN role"	Pending			Pending			Pending			
TC_VOUCH_014	Kiểm tra tạo voucher với Unicode trong name và description	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""UNICODE2024"",
""name"": ""Khuyến Mãi Mùa Xuân"",
""description"": ""Giảm giá đặc biệt cho mùa xuân năm 2024"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 20.00,
""minOrderAmount"": 150.00
}"	"- HTTP Status: 201 Created
- Response body:
{
""id"": 11,
""code"": ""UNICODE2024"",
""name"": ""Khuyến Mãi Mùa Xuân"",
""description"": ""Giảm giá đặc biệt cho mùa xuân năm 2024"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 20.00,
""minOrderAmount"": 150.00,
""createdAt"": ""2025-09-30T14:00:00Z""
}"	"- Admin đã đăng nhập
- Unicode characters được hỗ trợ"	Pending			Pending			Pending			
TC_VOUCH_015	Kiểm tra tạo voucher với role User (không có quyền)	"Gửi POST request đến ""/api/vouchers"".
Header Authorization: Bearer <user_token>.
Header Content-Type: application/json.
Body: {
""code"": ""UNAUTHORIZED"",
""name"": ""Unauthorized Test"",
""description"": ""Test unauthorized access"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": 100.00
}"	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can create vouchers""
}"	"- User đã đăng nhập (user@techbox.com/user123)
- JWT token hợp lệ với USER role only"	Pending			Pending			Pending			
TC_VOUCH_016	Kiểm tra tạo voucher khi không đăng nhập	"Gửi POST request đến ""/api/vouchers"".
Header Content-Type: application/json.
Body: {
""code"": ""NOAUTH"",
""name"": ""No Auth Test"",
""description"": ""Test no authentication"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 10.00,
""minOrderAmount"": 100.00
}"	"- HTTP Status: 401 Unauthorized
- Response body:
{
""error"": ""Unauthorized"",
""message"": ""Authentication required to access this resource""
}"	- Người dùng chưa đăng nhập	Pending			Pending			Pending			
Function 2: Cập nhật voucher (PUT /api/vouchers/{id})														
TC_VOUCH_017	Kiểm tra cập nhật voucher thành công với role Admin	"Gửi PUT request đến ""/api/vouchers/10"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""SPRING2024_UPD"",
""name"": ""Spring Sale Updated"",
""description"": ""Updated spring season discount"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 30.00,
""minOrderAmount"": 120.00,
""maxDiscountAmount"": 60.00,
""usageLimit"": 1500,
""usageLimitPerUser"": 3,
""isActive"": true
}"	"- HTTP Status: 200 OK
- Response body:
{
""id"": 10,
""code"": ""SPRING2024_UPD"",
""name"": ""Spring Sale Updated"",
""description"": ""Updated spring season discount"",
""discountValue"": 30.00,
""minOrderAmount"": 120.00,
""maxDiscountAmount"": 60.00,
""usageLimit"": 1500,
""usageLimitPerUser"": 3,
""updatedAt"": ""2025-09-30T15:00:00Z""
}"	"- Admin đã đăng nhập
- Voucher SPRING2024 (ID=10) tồn tại
- New code ""SPRING2024_UPD"" chưa tồn tại"	Pending			Pending			Pending			
TC_VOUCH_018	Kiểm tra cập nhật voucher không tồn tại	"Gửi PUT request đến ""/api/vouchers/999999"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""NONEXISTENT"",
""name"": ""Non-existent Test"",
""description"": ""Test update non-existent voucher"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 15.00,
""minOrderAmount"": 100.00
}"	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Voucher not found"",
""message"": ""Voucher with ID 999999 does not exist""
}"	"- Admin đã đăng nhập
- ID 999999 không tồn tại"	Pending			Pending			Pending			
TC_VOUCH_019	Kiểm tra cập nhật voucher với code trùng lặp	"Gửi PUT request đến ""/api/vouchers/10"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""code"": ""WELCOME50"",
""name"": ""Spring Sale Updated"",
""description"": ""Updated description"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 25.00,
""minOrderAmount"": 100.00
}"	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Duplicate entry"",
""message"": ""Voucher code 'WELCOME50' already exists"",
""field"": ""code""
}"	"- Admin đã đăng nhập
- SPRING2024 (ID=10), WELCOME50 (ID=11) tồn tại"	Pending			Pending			Pending			
TC_VOUCH_020	Kiểm tra cập nhật voucher với role User (không có quyền)	"Gửi PUT request đến ""/api/vouchers/10"".
Header Authorization: Bearer <user_token>.
Header Content-Type: application/json.
Body: {
""code"": ""SPRING2024_USER"",
""name"": ""User Update Test"",
""description"": ""Test user update""
}"	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can update vouchers""
}"	"- User đã đăng nhập
- Voucher SPRING2024 (ID=10) tồn tại"	Pending			Pending			Pending			
Function 3: Lấy voucher theo ID (GET /api/vouchers/{id})														
TC_VOUCH_021	Kiểm tra lấy voucher theo ID hợp lệ (Public access)	"Gửi GET request đến ""/api/vouchers/10""."	"- HTTP Status: 200 OK
- Response body:
{
""id"": 10,
""code"": ""SPRING2024"",
""name"": ""Spring Sale"",
""description"": ""25% discount for spring season"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 25.00,
""minOrderAmount"": 100.00,
""maxDiscountAmount"": 50.00,
""usageLimit"": 1000,
""usedCount"": 0,
""isActive"": true,
""validFrom"": ""2024-03-01T00:00:00Z"",
""validUntil"": ""2024-05-31T23:59:59Z""
}"	"- Voucher SPRING2024 với ID=10 tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_022	Kiểm tra lấy voucher với ID không tồn tại	"Gửi GET request đến ""/api/vouchers/999999""."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Voucher not found"",
""message"": ""Voucher with ID 999999 does not exist""
}"	"- Voucher ID 999999 không tồn tại trong database
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_023	Kiểm tra lấy voucher với ID không hợp lệ (không phải số nguyên)	"Gửi GET request đến ""/api/vouchers/abc""."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parameter"",
""message"": ""ID must be a valid integer""
}"	- Không cần authentication	Pending			Pending			Pending			
Function 4: Lấy voucher theo code (GET /api/vouchers/code/{code})														
TC_VOUCH_024	Kiểm tra lấy voucher theo code hợp lệ (Public access)	"Gửi GET request đến ""/api/vouchers/code/SPRING2024""."	"- HTTP Status: 200 OK
- Response body chứa voucher details với code=""SPRING2024"""	"- Voucher ""SPRING2024"" tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_025	Kiểm tra lấy voucher với code không tồn tại	"Gửi GET request đến ""/api/vouchers/code/NONEXISTENT""."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Voucher not found"",
""message"": ""Voucher with code 'NONEXISTENT' does not exist""
}"	"- Voucher code ""NONEXISTENT"" không tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_026	Kiểm tra lấy voucher với code case sensitivity	"Gửi GET request đến ""/api/vouchers/code/spring2024""."	"- HTTP Status: 200 OK
- Response body chứa voucher với code=""SPRING2024""
- Case insensitive search"	"- Voucher ""SPRING2024"" tồn tại
- Search case insensitive
- Không cần authentication"	Pending			Pending			Pending			
Function 5: Kiểm tra voucher code tồn tại (GET /api/vouchers/code/exists)														
TC_VOUCH_027	Kiểm tra voucher code tồn tại (Public access)	"Gửi GET request đến ""/api/vouchers/code/exists?code=SPRING2024""."	"- HTTP Status: 200 OK
- Response body: true"	"- Voucher ""SPRING2024"" tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_028	Kiểm tra voucher code không tồn tại	"Gửi GET request đến ""/api/vouchers/code/exists?code=NONEXISTENT""."	"- HTTP Status: 200 OK
- Response body: false"	"- Voucher ""NONEXISTENT"" không tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_029	Kiểm tra với parameter code rỗng	"Gửi GET request đến ""/api/vouchers/code/exists?code=""."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parameter"",
""message"": ""Voucher code parameter is required"",
""field"": ""code""
}"	- Không cần authentication	Pending			Pending			Pending			
Function 6: Lấy danh sách vouchers (GET /api/vouchers)														
TC_VOUCH_030	Kiểm tra lấy danh sách tất cả vouchers với pagination (Public access)	"Gửi GET request đến ""/api/vouchers?page=0&size=10&sortBy=createdAt&sortDir=DESC""."	"- HTTP Status: 200 OK
- Response body:
{
""content"": [
{
""id"": 11,
""code"": ""UNICODE2024"",
""name"": ""Khuyến Mãi Mùa Xuân"",
""discountType"": ""PERCENTAGE"",
""discountValue"": 20.00,
""isActive"": true,
""createdAt"": ""2025-09-30T14:00:00Z""
}
],
""pageable"": {
""pageNumber"": 0,
""pageSize"": 10
},
""totalElements"": 2,
""totalPages"": 1
}"	"- Database có vouchers
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_031	Kiểm tra lấy danh sách vouchers khi database trống	"Gửi GET request đến ""/api/vouchers?page=0&size=10""."	"- HTTP Status: 200 OK
- Response body:
{
""content"": [],
""pageable"": {
""pageNumber"": 0,
""pageSize"": 10
},
""totalElements"": 0,
""totalPages"": 0
}"	"- Database không có voucher nào
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_032	Kiểm tra pagination với page size và sort options	"Gửi GET request đến ""/api/vouchers?page=1&size=5&sortBy=discountValue&sortDir=ASC""."	"- HTTP Status: 200 OK
- Response body có đúng pagination và sorting
- Sorted by discountValue ASC"	"- Database có nhiều hơn 5 vouchers
- Không cần authentication"	Pending			Pending			Pending			
Function 7: Lấy danh sách valid vouchers (GET /api/vouchers/valid)														
TC_VOUCH_033	Kiểm tra lấy danh sách valid vouchers (Public access)	"Gửi GET request đên ""/api/vouchers/valid?page=0&size=10""."	"- HTTP Status: 200 OK
- Response body chỉ chứa vouchers:
  + isActive = true
  + validFrom <= current time
  + validUntil >= current time
  + usedCount < usageLimit (nếu có)"	"- Database có active vouchers trong thời gian valid
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_034	Kiểm tra lấy valid vouchers khi không có voucher nào valid	"Gửi GET request đến ""/api/vouchers/valid?page=0&size=10""."	"- HTTP Status: 200 OK
- Response body:
{
""content"": [],
""totalElements"": 0
}"	"- Tất cả vouchers đều expired hoặc inactive
- Không cần authentication"	Pending			Pending			Pending			
Function 8: Tìm kiếm vouchers (GET /api/vouchers/search)														
TC_VOUCH_035	Kiểm tra tìm kiếm vouchers theo code pattern (Public access)	"Gửi GET request đến ""/api/vouchers/search?searchTerm=SPRING&page=0&size=10""."	"- HTTP Status: 200 OK
- Response body chứa vouchers có code chứa ""SPRING""
- Case insensitive search"	"- Database có vouchers với code chứa ""SPRING""
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_036	Kiểm tra tìm kiếm vouchers với keyword không tìm thấy	"Gửi GET request đến ""/api/vouchers/search?searchTerm=NOTFOUND&page=0&size=10""."	"- HTTP Status: 200 OK
- Response body:
{
""content"": [],
""totalElements"": 0
}"	"- Database không có vouchers với keyword ""NOTFOUND""
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_037	Kiểm tra tìm kiếm vouchers với searchTerm rỗng	"Gửi GET request đến ""/api/vouchers/search?searchTerm=&page=0&size=10""."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid parameter"",
""message"": ""Search term is required"",
""field"": ""searchTerm""
}"	- Không cần authentication	Pending			Pending			Pending			
Function 9: Xóa voucher (DELETE /api/vouchers/{id})														
TC_VOUCH_038	Kiểm tra xóa voucher thành công với role Admin (soft delete)	"Gửi DELETE request đến ""/api/vouchers/11"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 204 No Content
- Voucher bị soft delete (deletedAt được set)"	"- Admin đã đăng nhập
- Voucher UNICODE2024 (ID=11) tồn tại và chưa được sử dụng"	Pending			Pending			Pending			
TC_VOUCH_039	Kiểm tra xóa voucher không tồn tại	"Gửi DELETE request đến ""/api/vouchers/999999"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 404 Not Found
- Response body:
{
""error"": ""Voucher not found"",
""message"": ""Voucher with ID 999999 does not exist""
}"	"- Admin đã đăng nhập
- ID 999999 không tồn tại"	Pending			Pending			Pending			
TC_VOUCH_040	Kiểm tra xóa voucher đã được sử dụng	"Gửi DELETE request đến ""/api/vouchers/10"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Cannot delete voucher"",
""message"": ""Voucher has been used 5 times. Cannot delete used voucher."",
""usedCount"": 5
}"	"- Admin đã đăng nhập
- Voucher SPRING2024 (ID=10) đã được sử dụng"	Pending			Pending			Pending			
TC_VOUCH_041	Kiểm tra xóa voucher với role User (không có quyền)	"Gửi DELETE request đến ""/api/vouchers/11"".
Header Authorization: Bearer <user_token>."	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can delete vouchers""
}"	"- User đã đăng nhập
- Voucher UNICODE2024 (ID=11) tồn tại"	Pending			Pending			Pending			
Function 10: Khôi phục voucher (POST /api/vouchers/{id}/restore)														
TC_VOUCH_042	Kiểm tra khôi phục voucher đã bị soft delete với role Admin	"Gửi POST request đến ""/api/vouchers/11/restore"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Voucher được khôi phục (deletedAt = null)"	"- Admin đã đăng nhập
- Voucher UNICODE2024 (ID=11) đã bị soft delete"	Pending			Pending			Pending			
TC_VOUCH_043	Kiểm tra khôi phục voucher chưa bị delete	"Gửi POST request đến ""/api/vouchers/10/restore"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 400 Bad Request
- Response body:
{
""error"": ""Invalid operation"",
""message"": ""Voucher is not deleted""
}"	"- Admin đã đăng nhập
- Voucher SPRING2024 (ID=10) chưa bị delete"	Pending			Pending			Pending			
TC_VOUCH_044	Kiểm tra khôi phục voucher với role User (không có quyền)	"Gửi POST request đến ""/api/vouchers/11/restore"".
Header Authorization: Bearer <user_token>."	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can restore vouchers""
}"	"- User đã đăng nhập
- Voucher UNICODE2024 (ID=11) đã bị soft delete"	Pending			Pending			Pending			
Function 11: Validate voucher (POST /api/vouchers/validate)														
TC_VOUCH_045	Kiểm tra validate voucher hợp lệ (Public access)	"Gửi POST request đến ""/api/vouchers/validate"".
Header Content-Type: application/json.
Body: {
""voucherCode"": ""SPRING2024"",
""orderAmount"": 150.00,
""userId"": 1
}"	"- HTTP Status: 200 OK
- Response body:
{
""isValid"": true,
""voucherId"": 10,
""discountType"": ""PERCENTAGE"",
""discountValue"": 25.00,
""calculatedDiscount"": 37.50,
""finalAmount"": 112.50,
""message"": ""Voucher is valid""
}"	"- Voucher ""SPRING2024"" active và trong thời gian valid
- Order amount (150) >= minOrderAmount (100)
- User chưa đạt usage limit
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_046	Kiểm tra validate voucher với code không tồn tại	"Gửi POST request đến ""/api/vouchers/validate"".
Header Content-Type: application/json.
Body: {
""voucherCode"": ""NOTFOUND"",
""orderAmount"": 150.00,
""userId"": 1
}"	"- HTTP Status: 200 OK
- Response body:
{
""isValid"": false,
""message"": ""Voucher code 'NOTFOUND' does not exist""
}"	"- Voucher ""NOTFOUND"" không tồn tại
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_047	Kiểm tra validate voucher với order amount < minOrderAmount	"Gửi POST request đến ""/api/vouchers/validate"".
Header Content-Type: application/json.
Body: {
""voucherCode"": ""SPRING2024"",
""orderAmount"": 50.00,
""userId"": 1
}"	"- HTTP Status: 200 OK
- Response body:
{
""isValid"": false,
""message"": ""Order amount (50.00) is below minimum required amount (100.00)""
}"	"- Voucher ""SPRING2024"" có minOrderAmount = 100
- Order amount = 50 < 100
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_048	Kiểm tra validate voucher đã hết hạn	"Gửi POST request đến ""/api/vouchers/validate"".
Header Content-Type: application/json.
Body: {
""voucherCode"": ""EXPIRED2023"",
""orderAmount"": 150.00,
""userId"": 1
}"	"- HTTP Status: 200 OK
- Response body:
{
""isValid"": false,
""message"": ""Voucher has expired on 2023-12-31T23:59:59Z""
}"	"- Voucher ""EXPIRED2023"" đã hết hạn
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_049	Kiểm tra validate voucher đã đạt usage limit	"Gửi POST request đến ""/api/vouchers/validate"".
Header Content-Type: application/json.
Body: {
""voucherCode"": ""LIMITREACHED"",
""orderAmount"": 150.00,
""userId"": 1
}"	"- HTTP Status: 200 OK
- Response body:
{
""isValid"": false,
""message"": ""Voucher usage limit reached (1000/1000)""
}"	"- Voucher ""LIMITREACHED"" đã đạt usage limit
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_050	Kiểm tra validate voucher với user đã đạt personal usage limit	"Gửi POST request đến ""/api/vouchers/validate"".
Header Content-Type: application/json.
Body: {
""voucherCode"": ""SPRING2024"",
""orderAmount"": 150.00,
""userId"": 2
}"	"- HTTP Status: 200 OK
- Response body:
{
""isValid"": false,
""message"": ""User has reached personal usage limit for this voucher (2/2)""
}"	"- User ID=2 đã sử dụng voucher ""SPRING2024"" 2 lần
- Voucher có usageLimitPerUser = 2
- Không cần authentication"	Pending			Pending			Pending			
Function 12: Sử dụng voucher (POST /api/vouchers/use)														
TC_VOUCH_051	Kiểm tra sử dụng voucher thành công (Admin hoặc System)	"Gửi POST request đến ""/api/vouchers/use"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""voucherCode"": ""SPRING2024"",
""userId"": 3,
""orderId"": 12345,
""orderAmount"": 200.00,
""discountAmount"": 50.00
}"	"- HTTP Status: 200 OK
- Response body: ""Voucher used successfully""
- usedCount tăng lên 1
- UserVoucher record được tạo"	"- Admin đã đăng nhập
- Voucher ""SPRING2024"" valid
- User ID=3 chưa đạt personal limit
- Order amount >= minOrderAmount"	Pending			Pending			Pending			
TC_VOUCH_052	Kiểm tra sử dụng voucher không hợp lệ	"Gửi POST request đến ""/api/vouchers/use"".
Header Authorization: Bearer <admin_token>.
Header Content-Type: application/json.
Body: {
""voucherCode"": ""EXPIRED2023"",
""userId"": 3,
""orderId"": 12346,
""orderAmount"": 200.00,
""discountAmount"": 50.00
}"	"- HTTP Status: 400 Bad Request
- Response body: ""Voucher has expired on 2023-12-31T23:59:59Z"""	"- Admin đã đăng nhập
- Voucher ""EXPIRED2023"" đã hết hạn"	Pending			Pending			Pending			
TC_VOUCH_053	Kiểm tra sử dụng voucher với role User (không có quyền)	"Gửi POST request đến ""/api/vouchers/use"".
Header Authorization: Bearer <user_token>.
Header Content-Type: application/json.
Body: {
""voucherCode"": ""SPRING2024"",
""userId"": 3,
""orderId"": 12347,
""orderAmount"": 200.00,
""discountAmount"": 50.00
}"	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators or system can use vouchers""
}"	"- User đã đăng nhập
- Chỉ admin hoặc system có thể mark voucher as used"	Pending			Pending			Pending			
Function 13: Lấy expired vouchers (GET /api/vouchers/expired)														
TC_VOUCH_054	Kiểm tra lấy danh sách expired vouchers (Admin only)	"Gửi GET request đến ""/api/vouchers/expired"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Response body chứa list vouchers có validUntil < current time"	"- Admin đã đăng nhập
- Database có expired vouchers"	Pending			Pending			Pending			
TC_VOUCH_055	Kiểm tra lấy expired vouchers với role User (không có quyền)	"Gửi GET request đến ""/api/vouchers/expired"".
Header Authorization: Bearer <user_token>."	"- HTTP Status: 403 Forbidden
- Response body:
{
""error"": ""Access denied"",
""message"": ""Only administrators can view expired vouchers""
}"	"- User đã đăng nhập
- Reports chỉ dành cho admin"	Pending			Pending			Pending			
Function 14: Lấy vouchers expiring soon (GET /api/vouchers/expiring-soon)														
TC_VOUCH_056	Kiểm tra lấy vouchers expiring soon với default 7 days (Admin only)	"Gửi GET request đến ""/api/vouchers/expiring-soon"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Response body chứa vouchers có validUntil trong vòng 7 ngày tới"	"- Admin đã đăng nhập
- Database có vouchers expiring trong 7 ngày"	Pending			Pending			Pending			
TC_VOUCH_057	Kiểm tra lấy vouchers expiring soon với custom days	"Gửi GET request đến ""/api/vouchers/expiring-soon?days=30"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Response body chứa vouchers có validUntil trong vòng 30 ngày tới"	"- Admin đã đăng nhập
- Database có vouchers expiring trong 30 ngày"	Pending			Pending			Pending			
Function 15: Lấy user voucher usage (GET /api/vouchers/usage/user/{userId})														
TC_VOUCH_058	Kiểm tra lấy voucher usage của user (Admin only)	"Gửi GET request đến ""/api/vouchers/usage/user/1"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Response body:
[
{
""id"": 1,
""userId"": 1,
""voucherId"": 10,
""orderId"": 12345,
""usedAt"": ""2025-09-30T16:00:00Z""
}
]"	"- Admin đã đăng nhập
- User ID=1 đã sử dụng vouchers"	Pending			Pending			Pending			
TC_VOUCH_059	Kiểm tra lấy usage của user chưa sử dụng voucher nào	"Gửi GET request đến ""/api/vouchers/usage/user/999"".
Header Authorization: Bearer <admin_token>."	"- HTTP Status: 200 OK
- Response body: []"	"- Admin đã đăng nhập
- User ID=999 chưa sử dụng voucher nào"	Pending			Pending			Pending			
Function 16: Lấy voucher usage count (GET /api/vouchers/{voucherId}/usage-count)														
TC_VOUCH_060	Kiểm tra lấy usage count của voucher (Public access)	"Gửi GET request đến ""/api/vouchers/10/usage-count""."	"- HTTP Status: 200 OK
- Response body: 1"	"- Voucher ID=10 đã được sử dụng 1 lần
- Không cần authentication"	Pending			Pending			Pending			
TC_VOUCH_061	Kiểm tra lấy usage count của voucher chưa được sử dụng	"Gửi GET request đến ""/api/vouchers/11/usage-count""."	"- HTTP Status: 200 OK
- Response body: 0"	"- Voucher ID=11 chưa được sử dụng
- Không cần authentication"	Pending			Pending			Pending