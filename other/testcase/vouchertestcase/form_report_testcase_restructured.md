# Voucher API Test Case Form Report

## Tổng quan
- **API Module**: Voucher Management
- **Base URL**: `/api/vouchers`
- **Authentication**: Public access for GET/POST validate, Admin required for POST/PUT/DELETE
- **Test Environment**: Development/Staging
- **Features**: CRUD operations, Discount validation, Usage tracking, Expiration management

---

## Function 1: Tạo voucher mới (POST /api/vouchers)

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions |
|--------------|-------------|-----------|------------------|----------------|
| TC_VOUCH_001 | Kiểm tra tạo voucher thành công với PERCENTAGE discount type | Gửi POST request đến "/api/vouchers".<br>Header Authorization: Bearer \<admin_token\>.<br>Header Content-Type: application/json.<br>Body: {<br>"code": "SPRING2024",<br>"name": "Spring Sale",<br>"description": "25% discount for spring season",<br>"discountType": "PERCENTAGE",<br>"discountValue": 25.00,<br>"minOrderAmount": 100.00,<br>"maxDiscountAmount": 50.00,<br>"usageLimit": 1000,<br>"usageLimitPerUser": 2,<br>"startDate": "2024-03-01T00:00:00",<br>"endDate": "2024-05-31T23:59:59",<br>"isActive": true<br>} | - HTTP Status: 201 Created<br>- Response body:<br>```json<br>{<br>  "id": 10,<br>  "code": "SPRING2024",<br>  "name": "Spring Sale",<br>  "description": "25% discount for spring season",<br>  "discountType": "PERCENTAGE",<br>  "discountValue": 25.00,<br>  "minOrderAmount": 100.00,<br>  "maxDiscountAmount": 50.00,<br>  "usageLimit": 1000,<br>  "usageLimitPerUser": 2,<br>  "usedCount": 0,<br>  "isActive": true,<br>  "validFrom": "2024-03-01T00:00:00Z",<br>  "validUntil": "2024-05-31T23:59:59Z",<br>  "createdAt": "2025-09-30T14:00:00Z",<br>  "updatedAt": "2025-09-30T14:00:00Z"<br>}<br>``` | - Admin đã đăng nhập (admin@techbox.com/admin123)<br>- JWT token hợp lệ với ADMIN role<br>- Voucher code "SPRING2024" chưa tồn tại |
| TC_VOUCH_002 | Kiểm tra tạo voucher thành công với FIXED_AMOUNT discount type | Gửi POST request đến "/api/vouchers".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"code": "WELCOME50",<br>"name": "Welcome Discount",<br>"description": "Fixed 50k discount for new customers",<br>"discountType": "FIXED_AMOUNT",<br>"discountValue": 50000.00,<br>"minOrderAmount": 200000.00,<br>"usageLimit": 500,<br>"usageLimitPerUser": 1,<br>"startDate": "2024-01-01T00:00:00",<br>"endDate": "2024-12-31T23:59:59",<br>"isActive": true<br>} | - HTTP Status: 201 Created<br>- Response body có discountType="FIXED_AMOUNT"<br>- discountValue=50000.00<br>- maxDiscountAmount=null (không áp dụng cho FIXED_AMOUNT) | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role<br>- Voucher code "WELCOME50" chưa tồn tại |
| TC_VOUCH_003 | Kiểm tra tạo voucher với code trống | Gửi POST request đến "/api/vouchers".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"code": "",<br>"name": "Test Voucher",<br>"description": "Test description",<br>"discountType": "PERCENTAGE",<br>"discountValue": 10.00,<br>"minOrderAmount": 50.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Validation failed",<br>  "message": "Voucher code is required",<br>  "field": "code"<br>}<br>``` | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role |
| TC_VOUCH_004 | Kiểm tra tạo voucher với code trùng lặp (case insensitive) | Gửi POST request đến "/api/vouchers".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"code": "spring2024",<br>"name": "Duplicate Code Test",<br>"description": "Test duplicate code",<br>"discountType": "PERCENTAGE",<br>"discountValue": 15.00,<br>"minOrderAmount": 100.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Duplicate entry",<br>  "message": "Voucher code 'spring2024' already exists (case insensitive)",<br>  "field": "code",<br>  "existingVoucher": "SPRING2024"<br>}<br>``` | - Admin đã đăng nhập<br>- Voucher "SPRING2024" đã tồn tại |
| TC_VOUCH_005 | Kiểm tra tạo voucher với PERCENTAGE discount > 100% | Gửi POST request đến "/api/vouchers".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"code": "OVER100",<br>"discountType": "PERCENTAGE",<br>"discountValue": 150.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Validation failed",<br>  "message": "Percentage discount cannot exceed 100%",<br>  "field": "discountValue"<br>}<br>``` | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role |
| TC_VOUCH_006 | Kiểm tra tạo voucher với startDate > endDate | Gửi POST request đến "/api/vouchers".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"code": "INVALIDDATE",<br>"discountType": "PERCENTAGE",<br>"discountValue": 10.00,<br>"startDate": "2024-12-31T23:59:59",<br>"endDate": "2024-01-01T00:00:00"<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Validation failed",<br>  "message": "End date must be after start date",<br>  "field": "endDate"<br>}<br>``` | - Admin đã đăng nhập<br>- JWT token hợp lệ với ADMIN role |
| TC_VOUCH_007 | Kiểm tra tạo voucher với role User (không có quyền) | Gửi POST request đến "/api/vouchers".<br>Header Authorization: Bearer \<user_token\>.<br>Body: {<br>"code": "UNAUTHORIZED",<br>"name": "Unauthorized Test",<br>"discountType": "PERCENTAGE",<br>"discountValue": 10.00<br>} | - HTTP Status: 403 Forbidden<br>- Response body:<br>```json<br>{<br>  "error": "Access denied",<br>  "message": "Only administrators can create vouchers"<br>}<br>``` | - User đã đăng nhập (user@techbox.com/user123)<br>- JWT token hợp lệ với USER role only |

---

## Function 2: Cập nhật voucher theo code (PUT /api/vouchers/code/{code})

| TC_VOUCH_008 | Kiểm tra cập nhật voucher thành công với role Admin | Gửi PUT request đến "/api/vouchers/code/SPRING2024".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"name": "Spring Sale Updated",<br>"description": "Updated spring season discount",<br>"discountValue": 30.00,<br>"minOrderAmount": 120.00,<br>"maxDiscountAmount": 60.00,<br>"usageLimit": 1500,<br>"usageLimitPerUser": 3,<br>"isActive": true<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "id": 10,<br>  "code": "SPRING2024",<br>  "name": "Spring Sale Updated",<br>  "description": "Updated spring season discount",<br>  "discountValue": 30.00,<br>  "minOrderAmount": 120.00,<br>  "maxDiscountAmount": 60.00,<br>  "usageLimit": 1500,<br>  "usageLimitPerUser": 3,<br>  "updatedAt": "2025-09-30T15:00:00Z"<br>}<br>``` | - Admin đã đăng nhập<br>- Voucher "SPRING2024" tồn tại |
| TC_VOUCH_009 | Kiểm tra cập nhật voucher không tồn tại | Gửi PUT request đến "/api/vouchers/code/NONEXISTENT".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"name": "Non-existent Test",<br>"discountValue": 15.00<br>} | - HTTP Status: 404 Not Found<br>- Response body:<br>```json<br>{<br>  "error": "Voucher not found",<br>  "message": "Voucher with code 'NONEXISTENT' does not exist"<br>}<br>``` | - Admin đã đăng nhập<br>- Code "NONEXISTENT" không tồn tại |
| TC_VOUCH_010 | Kiểm tra cập nhật voucher với role User (không có quyền) | Gửi PUT request đến "/api/vouchers/code/SPRING2024".<br>Header Authorization: Bearer \<user_token\>.<br>Body: {<br>"name": "User Update Test"<br>} | - HTTP Status: 403 Forbidden<br>- Response body:<br>```json<br>{<br>  "error": "Access denied",<br>  "message": "Only administrators can update vouchers"<br>}<br>``` | - User đã đăng nhập<br>- Voucher "SPRING2024" tồn tại |

---

## Function 3: Lấy voucher theo code (GET /api/vouchers/code/{code})

| TC_VOUCH_011 | Kiểm tra lấy voucher theo code hợp lệ (Public access) | Gửi GET request đến "/api/vouchers/code/SPRING2024". | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "id": 10,<br>  "code": "SPRING2024",<br>  "name": "Spring Sale",<br>  "description": "25% discount for spring season",<br>  "discountType": "PERCENTAGE",<br>  "discountValue": 25.00,<br>  "minOrderAmount": 100.00,<br>  "maxDiscountAmount": 50.00,<br>  "usageLimit": 1000,<br>  "usedCount": 0,<br>  "isActive": true,<br>  "validFrom": "2024-03-01T00:00:00Z",<br>  "validUntil": "2024-05-31T23:59:59Z"<br>}<br>``` | - Voucher "SPRING2024" tồn tại<br>- Không cần authentication |
| TC_VOUCH_012 | Kiểm tra lấy voucher với code không tồn tại | Gửi GET request đến "/api/vouchers/code/NONEXISTENT". | - HTTP Status: 404 Not Found<br>- Response body:<br>```json<br>{<br>  "error": "Voucher not found",<br>  "message": "Voucher with code 'NONEXISTENT' does not exist"<br>}<br>``` | - Voucher code "NONEXISTENT" không tồn tại<br>- Không cần authentication |
| TC_VOUCH_013 | Kiểm tra lấy voucher với code case sensitivity | Gửi GET request đến "/api/vouchers/code/spring2024". | - HTTP Status: 200 OK<br>- Response body chứa voucher với code="SPRING2024"<br>- Case insensitive search | - Voucher "SPRING2024" tồn tại<br>- Search case insensitive<br>- Không cần authentication |

---

## Function 4: Xóa voucher theo code (DELETE /api/vouchers/code/{code})

| TC_VOUCH_014 | Kiểm tra xóa voucher thành công với role Admin (soft delete) | Gửi DELETE request đến "/api/vouchers/code/WELCOME50".<br>Header Authorization: Bearer \<admin_token\>. | - HTTP Status: 204 No Content<br>- Voucher bị soft delete (deletedAt được set) | - Admin đã đăng nhập<br>- Voucher "WELCOME50" tồn tại và chưa được sử dụng |
| TC_VOUCH_015 | Kiểm tra xóa voucher không tồn tại | Gửi DELETE request đến "/api/vouchers/code/NONEXISTENT".<br>Header Authorization: Bearer \<admin_token\>. | - HTTP Status: 404 Not Found<br>- Response body:<br>```json<br>{<br>  "error": "Voucher not found",<br>  "message": "Voucher with code 'NONEXISTENT' does not exist"<br>}<br>``` | - Admin đã đăng nhập<br>- Code "NONEXISTENT" không tồn tại |
| TC_VOUCH_016 | Kiểm tra xóa voucher đã được sử dụng | Gửi DELETE request đến "/api/vouchers/code/SPRING2024".<br>Header Authorization: Bearer \<admin_token\>. | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Cannot delete voucher",<br>  "message": "Voucher has been used 5 times. Cannot delete used voucher.",<br>  "usedCount": 5<br>}<br>``` | - Admin đã đăng nhập<br>- Voucher "SPRING2024" đã được sử dụng |
| TC_VOUCH_017 | Kiểm tra xóa voucher với role User (không có quyền) | Gửi DELETE request đến "/api/vouchers/code/WELCOME50".<br>Header Authorization: Bearer \<user_token\>. | - HTTP Status: 403 Forbidden<br>- Response body:<br>```json<br>{<br>  "error": "Access denied",<br>  "message": "Only administrators can delete vouchers"<br>}<br>``` | - User đã đăng nhập<br>- Voucher "WELCOME50" tồn tại |

---

## Function 5: Validate voucher (POST /api/vouchers/validate) - Comprehensive Coverage

| TC_VOUCH_018 | Kiểm tra validate voucher hợp lệ với PERCENTAGE discount | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "SPRING2024",<br>"orderAmount": 150.00,<br>"userId": 1<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": true,<br>  "voucherId": 10,<br>  "discountType": "PERCENTAGE",<br>  "discountValue": 25.00,<br>  "calculatedDiscount": 37.50,<br>  "finalAmount": 112.50,<br>  "message": "Voucher is valid"<br>}<br>``` | - Voucher "SPRING2024" active và trong thời gian valid<br>- Order amount (150) >= minOrderAmount (100)<br>- User chưa đạt usage limit<br>- Không cần authentication |
| TC_VOUCH_019 | Kiểm tra validate voucher hợp lệ với FIXED_AMOUNT discount | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "WELCOME50",<br>"orderAmount": 300000.00,<br>"userId": 2<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": true,<br>  "voucherId": 11,<br>  "discountType": "FIXED_AMOUNT",<br>  "discountValue": 50000.00,<br>  "calculatedDiscount": 50000.00,<br>  "finalAmount": 250000.00,<br>  "message": "Voucher is valid"<br>}<br>``` | - Voucher "WELCOME50" active<br>- Order amount >= minOrderAmount<br>- User chưa đạt usage limit |
| TC_VOUCH_020 | Kiểm tra validate voucher với PERCENTAGE có maxDiscountAmount cap | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "SPRING2024",<br>"orderAmount": 1000.00,<br>"userId": 3<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": true,<br>  "voucherId": 10,<br>  "discountType": "PERCENTAGE",<br>  "discountValue": 25.00,<br>  "calculatedDiscount": 50.00,<br>  "finalAmount": 950.00,<br>  "message": "Voucher is valid (discount capped at maximum)"<br>}<br>``` | - Voucher "SPRING2024" có maxDiscountAmount = 50<br>- 25% của 1000 = 250, nhưng bị cap ở 50 |
| TC_VOUCH_021 | Kiểm tra validate voucher với code không tồn tại | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "NOTFOUND",<br>"orderAmount": 150.00,<br>"userId": 1<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": false,<br>  "message": "Voucher code 'NOTFOUND' does not exist"<br>}<br>``` | - Voucher "NOTFOUND" không tồn tại<br>- Không cần authentication |
| TC_VOUCH_022 | Kiểm tra validate voucher với order amount < minOrderAmount | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "SPRING2024",<br>"orderAmount": 50.00,<br>"userId": 1<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": false,<br>  "message": "Order amount (50.00) is below minimum required amount (100.00)"<br>}<br>``` | - Voucher "SPRING2024" có minOrderAmount = 100<br>- Order amount = 50 < 100 |
| TC_VOUCH_023 | Kiểm tra validate voucher đã hết hạn | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "EXPIRED2023",<br>"orderAmount": 150.00,<br>"userId": 1<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": false,<br>  "message": "Voucher has expired on 2023-12-31T23:59:59Z"<br>}<br>``` | - Voucher "EXPIRED2023" đã hết hạn |
| TC_VOUCH_024 | Kiểm tra validate voucher chưa đến thời gian bắt đầu | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "FUTURE2025",<br>"orderAmount": 150.00,<br>"userId": 1<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": false,<br>  "message": "Voucher is not yet active. Valid from 2025-12-01T00:00:00Z"<br>}<br>``` | - Voucher "FUTURE2025" startDate > current time |
| TC_VOUCH_025 | Kiểm tra validate voucher đã đạt usage limit | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "LIMITREACHED",<br>"orderAmount": 150.00,<br>"userId": 1<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": false,<br>  "message": "Voucher usage limit reached (1000/1000)"<br>}<br>``` | - Voucher "LIMITREACHED" đã đạt usage limit |
| TC_VOUCH_026 | Kiểm tra validate voucher với user đã đạt personal usage limit | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "SPRING2024",<br>"orderAmount": 150.00,<br>"userId": 5<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": false,<br>  "message": "User has reached personal usage limit for this voucher (2/2)"<br>}<br>``` | - User ID=5 đã sử dụng voucher "SPRING2024" 2 lần<br>- Voucher có usageLimitPerUser = 2 |
| TC_VOUCH_027 | Kiểm tra validate voucher bị deactivate | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "INACTIVE001",<br>"orderAmount": 150.00,<br>"userId": 1<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "isValid": false,<br>  "message": "Voucher is currently inactive"<br>}<br>``` | - Voucher "INACTIVE001" có isActive = false |
| TC_VOUCH_028 | Kiểm tra validate với orderAmount = 0 | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "SPRING2024",<br>"orderAmount": 0.00,<br>"userId": 1<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Validation failed",<br>  "message": "Order amount must be greater than 0",<br>  "field": "orderAmount"<br>}<br>``` | - Order amount không hợp lệ |
| TC_VOUCH_029 | Kiểm tra validate với userId null | Gửi POST request đến "/api/vouchers/validate".<br>Body: {<br>"voucherCode": "SPRING2024",<br>"orderAmount": 150.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Validation failed",<br>  "message": "User ID is required",<br>  "field": "userId"<br>}<br>``` | - User ID bắt buộc để check personal usage limit |

---

## Function 6: Sử dụng voucher (POST /api/vouchers/use) - Comprehensive Coverage

| TC_VOUCH_030 | Kiểm tra sử dụng voucher thành công với PERCENTAGE discount | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"voucherCode": "SPRING2024",<br>"userId": 10,<br>"orderId": 12345,<br>"orderAmount": 200.00,<br>"discountAmount": 50.00<br>} | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>{<br>  "success": true,<br>  "message": "Voucher used successfully",<br>  "voucherUsage": {<br>    "id": 1,<br>    "voucherId": 10,<br>    "userId": 10,<br>    "orderId": 12345,<br>    "discountAmount": 50.00,<br>    "usedAt": "2025-10-01T10:00:00Z"<br>  }<br>}<br>``` | - Admin đã đăng nhập<br>- Voucher "SPRING2024" valid<br>- User ID=10 chưa đạt personal limit<br>- Order amount >= minOrderAmount |
| TC_VOUCH_031 | Kiểm tra sử dụng voucher thành công với FIXED_AMOUNT discount | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"voucherCode": "WELCOME50",<br>"userId": 11,<br>"orderId": 12346,<br>"orderAmount": 300000.00,<br>"discountAmount": 50000.00<br>} | - HTTP Status: 200 OK<br>- usedCount tăng lên 1<br>- UserVoucher record được tạo<br>- Discount amount = 50000.00 (fixed amount) | - Admin đã đăng nhập<br>- Voucher "WELCOME50" valid<br>- User ID=11 chưa đạt personal limit |
| TC_VOUCH_032 | Kiểm tra sử dụng voucher với discountAmount không khớp validation | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"voucherCode": "SPRING2024",<br>"userId": 12,<br>"orderId": 12347,<br>"orderAmount": 200.00,<br>"discountAmount": 100.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Discount amount mismatch",<br>  "message": "Provided discount amount (100.00) does not match calculated discount (50.00)",<br>  "expectedDiscount": 50.00,<br>  "providedDiscount": 100.00<br>}<br>``` | - Admin đã đăng nhập<br>- 25% của 200 = 50, không phải 100 |
| TC_VOUCH_033 | Kiểm tra sử dụng voucher không hợp lệ (expired) | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"voucherCode": "EXPIRED2023",<br>"userId": 13,<br>"orderId": 12348,<br>"orderAmount": 200.00,<br>"discountAmount": 50.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Invalid voucher",<br>  "message": "Voucher has expired on 2023-12-31T23:59:59Z"<br>}<br>``` | - Admin đã đăng nhập<br>- Voucher "EXPIRED2023" đã hết hạn |
| TC_VOUCH_034 | Kiểm tra sử dụng voucher với orderAmount < minOrderAmount | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"voucherCode": "SPRING2024",<br>"userId": 14,<br>"orderId": 12349,<br>"orderAmount": 50.00,<br>"discountAmount": 12.50<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Order amount too low",<br>  "message": "Order amount (50.00) is below minimum required amount (100.00)"<br>}<br>``` | - Admin đã đăng nhập<br>- Order amount không đủ điều kiện |
| TC_VOUCH_035 | Kiểm tra sử dụng voucher khi user đã đạt personal usage limit | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"voucherCode": "SPRING2024",<br>"userId": 5,<br>"orderId": 12350,<br>"orderAmount": 200.00,<br>"discountAmount": 50.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Usage limit exceeded",<br>  "message": "User has reached personal usage limit for this voucher (2/2)"<br>}<br>``` | - Admin đã đăng nhập<br>- User ID=5 đã sử dụng voucher 2 lần<br>- usageLimitPerUser = 2 |
| TC_VOUCH_036 | Kiểm tra sử dụng voucher khi đã đạt global usage limit | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"voucherCode": "LIMITREACHED",<br>"userId": 15,<br>"orderId": 12351,<br>"orderAmount": 200.00,<br>"discountAmount": 50.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Usage limit exceeded",<br>  "message": "Voucher usage limit reached (1000/1000)"<br>}<br>``` | - Admin đã đăng nhập<br>- Voucher đã đạt global usage limit |
| TC_VOUCH_037 | Kiểm tra sử dụng voucher với orderId trùng lặp (prevent double usage) | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"voucherCode": "SPRING2024",<br>"userId": 16,<br>"orderId": 12345,<br>"orderAmount": 200.00,<br>"discountAmount": 50.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Duplicate order",<br>  "message": "Voucher has already been used for order ID 12345"<br>}<br>``` | - Admin đã đăng nhập<br>- Order ID 12345 đã được sử dụng trước đó |
| TC_VOUCH_038 | Kiểm tra sử dụng voucher với role User (không có quyền) | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<user_token\>.<br>Body: {<br>"voucherCode": "SPRING2024",<br>"userId": 17,<br>"orderId": 12352,<br>"orderAmount": 200.00,<br>"discountAmount": 50.00<br>} | - HTTP Status: 403 Forbidden<br>- Response body:<br>```json<br>{<br>  "error": "Access denied",<br>  "message": "Only administrators or system can use vouchers"<br>}<br>``` | - User đã đăng nhập<br>- Chỉ admin hoặc system có thể mark voucher as used |
| TC_VOUCH_039 | Kiểm tra sử dụng voucher khi không đăng nhập | Gửi POST request đến "/api/vouchers/use".<br>Body: {<br>"voucherCode": "SPRING2024",<br>"userId": 18,<br>"orderId": 12353,<br>"orderAmount": 200.00,<br>"discountAmount": 50.00<br>} | - HTTP Status: 401 Unauthorized<br>- Response body:<br>```json<br>{<br>  "error": "Unauthorized",<br>  "message": "Authentication required to access this resource"<br>}<br>``` | - Người dùng chưa đăng nhập |
| TC_VOUCH_040 | Kiểm tra sử dụng voucher với userId không tồn tại | Gửi POST request đến "/api/vouchers/use".<br>Header Authorization: Bearer \<admin_token\>.<br>Body: {<br>"voucherCode": "SPRING2024",<br>"userId": 999999,<br>"orderId": 12354,<br>"orderAmount": 200.00,<br>"discountAmount": 50.00<br>} | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "User not found",<br>  "message": "User with ID 999999 does not exist"<br>}<br>``` | - Admin đã đăng nhập<br>- User ID 999999 không tồn tại trong hệ thống |

---

## Function 7: Lấy danh sách vouchers (GET /api/vouchers)

| TC_VOUCH_041 | Kiểm tra lấy danh sách tất cả vouchers với pagination (Public access) | Gửi GET request đến "/api/vouchers?page=0&size=10&sortBy=createdAt&sortDir=DESC". | - HTTP Status: 200 OK<br>- Response body có pagination và sorting<br>- Sorted by createdAt DESC | - Database có vouchers<br>- Không cần authentication |
| TC_VOUCH_042 | Kiểm tra lấy danh sách valid vouchers (Public access) | Gửi GET request đến "/api/vouchers/valid?page=0&size=10". | - HTTP Status: 200 OK<br>- Response body chỉ chứa vouchers valid | - Database có active vouchers trong thời gian valid<br>- Không cần authentication |

---

## Function 8: Lấy expired vouchers (GET /api/vouchers/expired) - Admin Only

| TC_VOUCH_043 | Kiểm tra lấy danh sách expired vouchers (Admin only) | Gửi GET request đến "/api/vouchers/expired".<br>Header Authorization: Bearer \<admin_token\>. | - HTTP Status: 200 OK<br>- Response body chứa list vouchers có validUntil < current time | - Admin đã đăng nhập<br>- Database có expired vouchers |
| TC_VOUCH_044 | Kiểm tra lấy expired vouchers với role User (không có quyền) | Gửi GET request đến "/api/vouchers/expired".<br>Header Authorization: Bearer \<user_token\>. | - HTTP Status: 403 Forbidden<br>- Response body:<br>```json<br>{<br>  "error": "Access denied",<br>  "message": "Only administrators can view expired vouchers"<br>}<br>``` | - User đã đăng nhập<br>- Reports chỉ dành cho admin |

---

## Function 9: Lấy user voucher usage (GET /api/vouchers/usage/user/{userId}) - Admin Only

| TC_VOUCH_045 | Kiểm tra lấy voucher usage của user (Admin only) | Gửi GET request đến "/api/vouchers/usage/user/1".<br>Header Authorization: Bearer \<admin_token\>. | - HTTP Status: 200 OK<br>- Response body:<br>```json<br>[<br>  {<br>    "id": 1,<br>    "userId": 1,<br>    "voucherId": 10,<br>    "orderId": 12345,<br>    "discountAmount": 50.00,<br>    "usedAt": "2025-09-30T16:00:00Z"<br>  }<br>]<br>``` | - Admin đã đăng nhập<br>- User ID=1 đã sử dụng vouchers |
| TC_VOUCH_046 | Kiểm tra lấy usage của user chưa sử dụng voucher nào | Gửi GET request đến "/api/vouchers/usage/user/999".<br>Header Authorization: Bearer \<admin_token\>. | - HTTP Status: 200 OK<br>- Response body: [] | - Admin đã đăng nhập<br>- User ID=999 chưa sử dụng voucher nào |

---

## Function 10: Khôi phục voucher (POST /api/vouchers/code/{code}/restore)

| TC_VOUCH_047 | Kiểm tra khôi phục voucher đã bị soft delete với role Admin | Gửi POST request đến "/api/vouchers/code/WELCOME50/restore".<br>Header Authorization: Bearer \<admin_token\>. | - HTTP Status: 200 OK<br>- Voucher được khôi phục (deletedAt = null) | - Admin đã đăng nhập<br>- Voucher "WELCOME50" đã bị soft delete |
| TC_VOUCH_048 | Kiểm tra khôi phục voucher chưa bị delete | Gửi POST request đến "/api/vouchers/code/SPRING2024/restore".<br>Header Authorization: Bearer \<admin_token\>. | - HTTP Status: 400 Bad Request<br>- Response body:<br>```json<br>{<br>  "error": "Invalid operation",<br>  "message": "Voucher is not deleted"<br>}<br>``` | - Admin đã đăng nhập<br>- Voucher "SPRING2024" chưa bị delete |

---

## Notes và Validation Criteria

### Testing Guidelines
1. **Security Testing**: Verify proper authentication and authorization for each endpoint
2. **Validation Testing**: Test all input validation rules and edge cases  
3. **Business Logic**: Verify discount calculations, usage limits, and expiration logic
4. **Error Handling**: Document all error responses and status codes
5. **Performance**: Test pagination and response times for large datasets

### Validation Criteria
- **HTTP Status Codes**: Verify correct status codes for all scenarios
- **Response Format**: Validate JSON structure and required fields  
- **Discount Calculations**: Verify percentage and fixed amount calculations
- **Usage Tracking**: Confirm proper tracking of global and per-user limits
- **Security**: Ensure proper authentication and authorization
- **Data Integrity**: Verify database consistency after operations

### Test Data Requirements
- **Vouchers**: SPRING2024 (25% off, min 100), WELCOME50 (50k off, min 200k)
- **Users**: Admin (admin@techbox.com) and User (user@techbox.com) accounts
- **Usage History**: Various usage scenarios for limit testing
- **Time-based**: Valid, expired, and future vouchers for time validation

---

## Summary
- **Total Test Cases**: 48
- **Security Test Cases**: 12 (25%)
- **Validation Test Cases**: 15 (31%) 
- **Business Logic Test Cases**: 21 (44%)
- **Code-based Operations**: All CRUD operations use voucher code instead of ID
- **Comprehensive Usage Testing**: 11 test cases covering all voucher usage scenarios