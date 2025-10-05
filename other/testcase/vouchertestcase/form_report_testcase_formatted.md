# Voucher API Test Case Form Report

## Tổng quan
- **API Module**: Voucher Management
- **Base URL**: `/api/vouchers`
- **Authentication**: Public access for validation, Admin required for CRUD operations
- **Test Environment**: Development/Staging
- **Features**: Discount management, Usage tracking, Validation, Expiration handling

---

## Function 1: Lấy danh sách voucher (GET /api/vouchers)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_001 | Kiểm tra lấy danh sách tất cả vouchers thành công với role Admin | GET /api/vouchers với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: Array of vouchers with complete details | - Admin đã đăng nhập<br>- Database có vouchers: SAVE20 (ID=1), FREESHIP (ID=2) | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_002 | Kiểm tra lấy danh sách vouchers với pagination | GET /api/vouchers?page=0&size=5 với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: Paginated voucher list | - Admin đã đăng nhập<br>- Database có > 5 vouchers | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_003 | Kiểm tra lấy danh sách vouchers khi database trống | GET /api/vouchers với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: [] | - Admin đã đăng nhập<br>- Database không có voucher nào | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_004 | Kiểm tra access với role User (không có quyền) | GET /api/vouchers với Authorization: Bearer \<user_token\> | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can view all vouchers" | - User đã đăng nhập với USER role | Pending | | | Pending | | | Pending | | | |

---

## Function 2: Lấy voucher theo ID (GET /api/vouchers/{id})

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_005 | Kiểm tra lấy voucher theo ID hợp lệ với role Admin | GET /api/vouchers/1 với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: Complete voucher details | - Admin đã đăng nhập<br>- Voucher SAVE20 với ID=1 tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_006 | Kiểm tra lấy voucher với ID không tồn tại | GET /api/vouchers/999999 với Authorization: Bearer \<admin_token\> | - HTTP Status: 404 Not Found<br>- Error message: "Voucher not found" | - Admin đã đăng nhập<br>- Voucher ID 999999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_007 | Kiểm tra lấy voucher với ID không hợp lệ | GET /api/vouchers/abc với Authorization: Bearer \<admin_token\> | - HTTP Status: 400 Bad Request<br>- Error message: "ID must be a valid integer" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |

---

## Function 3: Tạo voucher mới (POST /api/vouchers)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_008 | Kiểm tra tạo voucher PERCENTAGE thành công | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "SAVE30", "discountType": "PERCENTAGE", "discountValue": 30, "minOrderAmount": 100, "maxUses": 100, "expirationDate": "2025-12-31T23:59:59"} | - HTTP Status: 201 Created<br>- Response body: Created voucher với auto-generated ID | - Admin đã đăng nhập<br>- Voucher code "SAVE30" chưa tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_009 | Kiểm tra tạo voucher FIXED_AMOUNT thành công | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "DISCOUNT50", "discountType": "FIXED_AMOUNT", "discountValue": 50, "minOrderAmount": 200, "maxUses": 50} | - HTTP Status: 201 Created<br>- Response body: Created voucher | - Admin đã đăng nhập<br>- Voucher code "DISCOUNT50" chưa tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_010 | Kiểm tra tạo voucher FREE_SHIPPING thành công | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "FREESHIP2025", "discountType": "FREE_SHIPPING", "minOrderAmount": 0, "maxUses": 1000} | - HTTP Status: 201 Created<br>- Response body: Created voucher với discountValue = 0 | - Admin đã đăng nhập<br>- Voucher code "FREESHIP2025" chưa tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_011 | Kiểm tra tạo voucher với code trống | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "", "discountType": "PERCENTAGE", "discountValue": 20} | - HTTP Status: 400 Bad Request<br>- Error message: "Voucher code is required" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_012 | Kiểm tra tạo voucher với code quá ngắn (<3 ký tự) | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "AB", "discountType": "PERCENTAGE", "discountValue": 20} | - HTTP Status: 400 Bad Request<br>- Error message: "Voucher code must be at least 3 characters long" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_013 | Kiểm tra tạo voucher với code quá dài (>20 ký tự) | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "VERYLONGVOUCHERCODE123", "discountType": "PERCENTAGE", "discountValue": 20} | - HTTP Status: 400 Bad Request<br>- Error message: "Voucher code must not exceed 20 characters" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_014 | Kiểm tra tạo voucher với code chứa ký tự đặc biệt | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "SAVE@20%", "discountType": "PERCENTAGE", "discountValue": 20} | - HTTP Status: 400 Bad Request<br>- Error message: "Voucher code can only contain letters and numbers" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_015 | Kiểm tra tạo voucher với code trùng lặp | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "SAVE20", "discountType": "PERCENTAGE", "discountValue": 25} | - HTTP Status: 400 Bad Request<br>- Error message: "Voucher code 'SAVE20' already exists" | - Admin đã đăng nhập<br>- Voucher "SAVE20" đã tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_016 | Kiểm tra tạo voucher với discountType không hợp lệ | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "TEST", "discountType": "INVALID_TYPE", "discountValue": 20} | - HTTP Status: 400 Bad Request<br>- Error message: "Invalid discount type" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_017 | Kiểm tra tạo voucher PERCENTAGE với discountValue > 100 | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "OVER100", "discountType": "PERCENTAGE", "discountValue": 150} | - HTTP Status: 400 Bad Request<br>- Error message: "Percentage discount cannot exceed 100%" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_018 | Kiểm tra tạo voucher với discountValue âm | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "NEGATIVE", "discountType": "FIXED_AMOUNT", "discountValue": -10} | - HTTP Status: 400 Bad Request<br>- Error message: "Discount value must be positive" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_019 | Kiểm tra tạo voucher với minOrderAmount âm | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "MINNEGA", "discountType": "PERCENTAGE", "discountValue": 20, "minOrderAmount": -50} | - HTTP Status: 400 Bad Request<br>- Error message: "Minimum order amount must be non-negative" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_020 | Kiểm tra tạo voucher với maxUses = 0 | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "ZEROLIMIT", "discountType": "PERCENTAGE", "discountValue": 20, "maxUses": 0} | - HTTP Status: 400 Bad Request<br>- Error message: "Maximum uses must be greater than 0" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_021 | Kiểm tra tạo voucher với expirationDate trong quá khứ | POST /api/vouchers với Authorization: Bearer \<admin_token\><br>Body: {"code": "EXPIRED", "discountType": "PERCENTAGE", "discountValue": 20, "expirationDate": "2023-01-01T00:00:00"} | - HTTP Status: 400 Bad Request<br>- Error message: "Expiration date must be in the future" | - Admin đã đăng nhập | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_022 | Kiểm tra tạo voucher với role User (không có quyền) | POST /api/vouchers với Authorization: Bearer \<user_token\><br>Body: {"code": "UNAUTHORIZED", "discountType": "PERCENTAGE", "discountValue": 20} | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can create vouchers" | - User đã đăng nhập với USER role | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_023 | Kiểm tra tạo voucher khi không đăng nhập | POST /api/vouchers (không có Authorization header)<br>Body: {"code": "NOAUTH", "discountType": "PERCENTAGE", "discountValue": 20} | - HTTP Status: 401 Unauthorized<br>- Error message: "Authentication required" | - Không có authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 4: Cập nhật voucher (PUT /api/vouchers/{id})

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_024 | Kiểm tra cập nhật voucher thành công | PUT /api/vouchers/1 với Authorization: Bearer \<admin_token\><br>Body: {"code": "SAVE25", "discountValue": 25, "maxUses": 200} | - HTTP Status: 200 OK<br>- Response body: Updated voucher với timestamps mới | - Admin đã đăng nhập<br>- Voucher SAVE20 (ID=1) tồn tại<br>- Code "SAVE25" chưa tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_025 | Kiểm tra cập nhật voucher không tồn tại | PUT /api/vouchers/999999 với Authorization: Bearer \<admin_token\><br>Body: {"code": "NONEXIST", "discountValue": 20} | - HTTP Status: 404 Not Found<br>- Error message: "Voucher with ID 999999 does not exist" | - Admin đã đăng nhập<br>- Voucher ID 999999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_026 | Kiểm tra cập nhật voucher với code trùng lặp | PUT /api/vouchers/1 với Authorization: Bearer \<admin_token\><br>Body: {"code": "FREESHIP"} | - HTTP Status: 400 Bad Request<br>- Error message: "Voucher code 'FREESHIP' already exists" | - Admin đã đăng nhập<br>- Vouchers SAVE20 (ID=1), FREESHIP (ID=2) tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_027 | Kiểm tra cập nhật voucher đã được sử dụng | PUT /api/vouchers/3 với Authorization: Bearer \<admin_token\><br>Body: {"discountValue": 15} | - HTTP Status: 400 Bad Request<br>- Error message: "Cannot modify voucher that has been used" | - Admin đã đăng nhập<br>- Voucher ID=3 đã có usage history | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_028 | Kiểm tra cập nhật voucher với role User (không có quyền) | PUT /api/vouchers/1 với Authorization: Bearer \<user_token\><br>Body: {"discountValue": 30} | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can update vouchers" | - User đã đăng nhập<br>- Voucher ID=1 tồn tại | Pending | | | Pending | | | Pending | | | |

---

## Function 5: Xóa voucher (DELETE /api/vouchers/{id})

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_029 | Kiểm tra xóa voucher thành công | DELETE /api/vouchers/5 với Authorization: Bearer \<admin_token\> | - HTTP Status: 204 No Content | - Admin đã đăng nhập<br>- Voucher ID=5 tồn tại và chưa được sử dụng | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_030 | Kiểm tra xóa voucher không tồn tại | DELETE /api/vouchers/999999 với Authorization: Bearer \<admin_token\> | - HTTP Status: 404 Not Found<br>- Error message: "Voucher with ID 999999 does not exist" | - Admin đã đăng nhập<br>- Voucher ID 999999 không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_031 | Kiểm tra xóa voucher đã được sử dụng | DELETE /api/vouchers/3 với Authorization: Bearer \<admin_token\> | - HTTP Status: 400 Bad Request<br>- Error message: "Cannot delete voucher that has been used" | - Admin đã đăng nhập<br>- Voucher ID=3 đã có usage history | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_032 | Kiểm tra xóa voucher với role User (không có quyền) | DELETE /api/vouchers/5 với Authorization: Bearer \<user_token\> | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can delete vouchers" | - User đã đăng nhập<br>- Voucher ID=5 tồn tại | Pending | | | Pending | | | Pending | | | |

---

## Function 6: Validate voucher (POST /api/vouchers/validate)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_033 | Kiểm tra validate voucher hợp lệ (Public access) | POST /api/vouchers/validate<br>Body: {"code": "SAVE20", "orderAmount": 200, "userId": 1} | - HTTP Status: 200 OK<br>- Response body: {"valid": true, "discountAmount": 40, "finalAmount": 160} | - Voucher SAVE20 (20% discount, min order 100) tồn tại và active<br>- Order amount = 200<br>- User chưa sử dụng voucher này | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_034 | Kiểm tra validate voucher với code không tồn tại | POST /api/vouchers/validate<br>Body: {"code": "NOTEXIST", "orderAmount": 200, "userId": 1} | - HTTP Status: 200 OK<br>- Response body: {"valid": false, "message": "Voucher code does not exist"} | - Code "NOTEXIST" không tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_035 | Kiểm tra validate voucher đã hết hạn | POST /api/vouchers/validate<br>Body: {"code": "EXPIRED2024", "orderAmount": 200, "userId": 1} | - HTTP Status: 200 OK<br>- Response body: {"valid": false, "message": "Voucher has expired"} | - Voucher "EXPIRED2024" có expirationDate trong quá khứ | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_036 | Kiểm tra validate voucher với order amount thấp hơn minimum | POST /api/vouchers/validate<br>Body: {"code": "SAVE20", "orderAmount": 50, "userId": 1} | - HTTP Status: 200 OK<br>- Response body: {"valid": false, "message": "Order amount below minimum required"} | - Voucher SAVE20 có minOrderAmount = 100<br>- Order amount = 50 | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_037 | Kiểm tra validate voucher đã đạt max uses | POST /api/vouchers/validate<br>Body: {"code": "MAXED", "orderAmount": 200, "userId": 1} | - HTTP Status: 200 OK<br>- Response body: {"valid": false, "message": "Voucher usage limit reached"} | - Voucher "MAXED" đã đạt maxUses | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_038 | Kiểm tra validate voucher user đã sử dụng trước đó | POST /api/vouchers/validate<br>Body: {"code": "SAVE20", "orderAmount": 200, "userId": 5} | - HTTP Status: 200 OK<br>- Response body: {"valid": false, "message": "User has already used this voucher"} | - User ID=5 đã sử dụng voucher SAVE20 trước đó | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_039 | Kiểm tra validate voucher FIXED_AMOUNT | POST /api/vouchers/validate<br>Body: {"code": "DISCOUNT50", "orderAmount": 300, "userId": 2} | - HTTP Status: 200 OK<br>- Response body: {"valid": true, "discountAmount": 50, "finalAmount": 250} | - Voucher DISCOUNT50 (fixed 50 discount) tồn tại và valid<br>- Order amount = 300 | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_040 | Kiểm tra validate voucher FREE_SHIPPING | POST /api/vouchers/validate<br>Body: {"code": "FREESHIP", "orderAmount": 100, "userId": 3} | - HTTP Status: 200 OK<br>- Response body: {"valid": true, "discountAmount": 0, "freeShipping": true} | - Voucher FREESHIP (free shipping) tồn tại và valid<br>- Order amount = 100 | Pending | | | Pending | | | Pending | | | |

---

## Function 7: Use voucher (POST /api/vouchers/use)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_041 | Kiểm tra use voucher thành công | POST /api/vouchers/use với Authorization: Bearer \<user_token\><br>Body: {"code": "SAVE20", "orderAmount": 200, "orderId": 101} | - HTTP Status: 200 OK<br>- Response body: {"success": true, "discountAmount": 40}<br>- Usage tracked in database | - User đã đăng nhập<br>- Voucher SAVE20 valid cho user này<br>- Order ID=101 tồn tại | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_042 | Kiểm tra use voucher không hợp lệ | POST /api/vouchers/use với Authorization: Bearer \<user_token\><br>Body: {"code": "EXPIRED2024", "orderAmount": 200, "orderId": 102} | - HTTP Status: 400 Bad Request<br>- Error message: "Voucher validation failed: Voucher has expired" | - User đã đăng nhập<br>- Voucher EXPIRED2024 đã hết hạn | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_043 | Kiểm tra use voucher đã được user sử dụng trước đó | POST /api/vouchers/use với Authorization: Bearer \<user_token\><br>Body: {"code": "SAVE20", "orderAmount": 200, "orderId": 103} | - HTTP Status: 400 Bad Request<br>- Error message: "User has already used this voucher" | - User đã đăng nhập<br>- User đã sử dụng voucher SAVE20 trước đó | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_044 | Kiểm tra use voucher khi không đăng nhập | POST /api/vouchers/use (không có Authorization header)<br>Body: {"code": "SAVE20", "orderAmount": 200, "orderId": 104} | - HTTP Status: 401 Unauthorized<br>- Error message: "Authentication required" | - Không có authentication | Pending | | | Pending | | | Pending | | | |

---

## Function 8: Get voucher usage history (GET /api/vouchers/{id}/usage)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_045 | Kiểm tra lấy usage history của voucher | GET /api/vouchers/1/usage với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: Array of usage records với user info, order info, timestamps | - Admin đã đăng nhập<br>- Voucher ID=1 có usage history | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_046 | Kiểm tra lấy usage history của voucher chưa được sử dụng | GET /api/vouchers/6/usage với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: [] | - Admin đã đăng nhập<br>- Voucher ID=6 chưa được sử dụng | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_047 | Kiểm tra lấy usage history với role User (không có quyền) | GET /api/vouchers/1/usage với Authorization: Bearer \<user_token\> | - HTTP Status: 403 Forbidden<br>- Error message: "Only administrators can view usage history" | - User đã đăng nhập với USER role | Pending | | | Pending | | | Pending | | | |

---

## Function 9: Get user voucher usage (GET /api/vouchers/user/{userId}/usage)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_048 | Kiểm tra lấy voucher usage của user cụ thể | GET /api/vouchers/user/5/usage với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: Array of vouchers used by user ID=5 | - Admin đã đăng nhập<br>- User ID=5 đã sử dụng một số vouchers | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_049 | Kiểm tra lấy voucher usage của user chưa sử dụng voucher nào | GET /api/vouchers/user/10/usage với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: [] | - Admin đã đăng nhập<br>- User ID=10 chưa sử dụng voucher nào | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_050 | Kiểm tra user lấy voucher usage của chính mình | GET /api/vouchers/user/5/usage với Authorization: Bearer \<user_token_id5\> | - HTTP Status: 200 OK<br>- Response body: User's own voucher usage history | - User ID=5 đã đăng nhập<br>- User request own data | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_051 | Kiểm tra user lấy voucher usage của user khác | GET /api/vouchers/user/3/usage với Authorization: Bearer \<user_token_id5\> | - HTTP Status: 403 Forbidden<br>- Error message: "Cannot access other user's voucher usage" | - User ID=5 đã đăng nhập<br>- Request data of user ID=3 | Pending | | | Pending | | | Pending | | | |

---

## Function 10: Get expired vouchers (GET /api/vouchers/expired)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_052 | Kiểm tra lấy danh sách expired vouchers | GET /api/vouchers/expired với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: Array of vouchers có expirationDate trong quá khứ | - Admin đã đăng nhập<br>- Database có expired vouchers | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_053 | Kiểm tra lấy expired vouchers khi không có voucher nào hết hạn | GET /api/vouchers/expired với Authorization: Bearer \<admin_token\> | - HTTP Status: 200 OK<br>- Response body: [] | - Admin đã đăng nhập<br>- Tất cả vouchers đều còn hạn | Pending | | | Pending | | | Pending | | | |

---

## Additional Test Cases

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| TC_VOUCH_054 | Kiểm tra case sensitivity của voucher code | POST /api/vouchers/validate<br>Body: {"code": "save20", "orderAmount": 200, "userId": 1} | - HTTP Status: 200 OK<br>- Case insensitive matching works | - Voucher "SAVE20" tồn tại<br>- Test với lowercase "save20" | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_055 | Kiểm tra concurrent voucher usage | 2 users đồng thời use cùng voucher có maxUses = 1 | - Chỉ 1 request thành công, 1 request failed | - Voucher có maxUses = 1<br>- 2 concurrent requests | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_056 | Kiểm tra performance với large usage history | GET /api/vouchers/1/usage cho voucher có >1000 usage records | - Response time trong acceptable range<br>- Pagination có thể cần thiết | - Voucher với large usage history<br>- Performance testing | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_057 | Kiểm tra timezone handling cho expiration date | Validate voucher ở multiple timezones | - Expiration correctly handled across timezones | - Voucher với specific expiration time<br>- Multiple timezone testing | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_058 | Kiểm tra transaction rollback khi voucher use fails | Use voucher nhưng order creation fails sau đó | - Voucher usage được rollback<br>- Consistency maintained | - Voucher valid<br>- Order creation failure scenario | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_059 | Kiểm tra bulk voucher operations | Create/update multiple vouchers in batch | - All operations succeed hoặc fail atomically | - Bulk operation scenario<br>- Admin authentication | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_060 | Kiểm tra voucher analytics và reporting | Generate voucher usage reports | - Accurate statistics và analytics | - Voucher với usage history<br>- Reporting functionality | Pending | | | Pending | | | Pending | | | |
| TC_VOUCH_061 | Kiểm tra voucher integration với order system | Complete order flow với voucher discount | - Discount correctly applied trong order total | - Order system integration<br>- Valid voucher | Pending | | | Pending | | | Pending | | | |

---

## Execution Instructions

### Pre-test Setup
1. **Environment Setup**: Configure test environment with clean database
2. **Authentication**: Prepare valid admin và user JWT tokens
3. **Test Data**: Create sample vouchers với different types và expiration dates
4. **Database**: Ensure proper indexes và constraints for voucher operations
5. **Time Settings**: Configure timezone settings for expiration testing

### Test Execution Guidelines
1. **Sequential Testing**: Execute test cases in order for dependencies
2. **Data Cleanup**: Clean test data between rounds, especially usage history
3. **Error Logging**: Document all error responses và status codes
4. **Usage Tracking**: Verify usage counts are accurate after operations
5. **Business Logic**: Validate discount calculations manually

### Validation Criteria
- **HTTP Status Codes**: Verify correct status codes for all scenarios
- **Response Format**: Validate JSON structure và required fields
- **Business Rules**: Confirm discount calculations follow business logic
- **Usage Tracking**: Ensure usage counts và history are accurate
- **Security**: Ensure proper authentication và authorization
- **Data Integrity**: Verify database consistency after operations

### Test Data Requirements
- **Vouchers**: SAVE20 (20% discount), FREESHIP (free shipping), DISCOUNT50 (fixed amount)
- **Users**: Multiple user accounts for usage testing
- **Orders**: Sample orders với different amounts
- **Expiration**: Vouchers với past, present, và future expiration dates

---

## Notes
- Test cases marked with * require manual verification
- Usage tracking must be accurate for business reporting
- Concurrent access scenarios should be tested thoroughly
- Timezone handling is critical for expiration validation
- Security tests must validate proper access controls