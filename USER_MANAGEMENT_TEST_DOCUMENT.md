# TÀI LIỆU TEST QUẢN LÝ NGƯỜI DÙNG - TECHBOX STORE (ENUM-BASED PERMISSIONS)

## MỤC LỤC
1. [Đăng ký tài khoản](#1-đăng-ký-tài-khoản)
2. [Đăng nhập](#2-đăng-nhập)
3. [Refresh token](#3-refresh-token)
4. [Xác thực token](#4-xác-thực-token)
5. [Xem danh sách tài khoản](#5-xem-danh-sách-tài-khoản)
6. [Phân quyền tài khoản theo UserPermission Enum](#6-phân-quyền-tài-khoản-theo-userpermission-enum)
7. [Xóa tài khoản (Soft Delete)](#7-xóa-tài-khoản-soft-delete)
8. [Xem thông tin cá nhân](#8-xem-thông-tin-cá-nhân)
9. [Chỉnh sửa thông tin cá nhân](#9-chỉnh-sửa-thông-tin-cá-nhân)
10. [Quản lý giỏ hàng](#10-quản-lý-giỏ-hàng)

---

## HỆ THỐNG PERMISSION THEO ENUM

### UserPermission Enum Structure:
Hệ thống sử dụng enum `UserPermission` với cấu trúc:
```java
USER_READ, USER_WRITE, USER_UPDATE, USER_DELETE
PRODUCT_READ, PRODUCT_WRITE, PRODUCT_UPDATE, PRODUCT_DELETE
ORDER_READ, ORDER_WRITE, ORDER_UPDATE, ORDER_DELETE
// ... và các module khác
```

### Phân quyền theo Role (đã seed):
- **ROLE_ADMIN**: Có TẤT CẢ permissions của tất cả module
- **ROLE_STAFF**: Có permissions PRODUCT, ORDER, PROMOTION, VOUCHER, CAMPAIGN (trừ DELETE)
- **ROLE_CUSTOMER**: Chỉ có READ permissions cho PRODUCT và ORDER

### User Test Accounts (đã seed):
- **admin/admin123**: ROLE_ADMIN - có tất cả quyền
- **staff/staff123**: ROLE_STAFF - có quyền quản lý (trừ xóa)
- **customer/customer123**: ROLE_CUSTOMER - chỉ có quyền xem

---

## 1. ĐĂNG KÝ TÀI KHOẢN

### 1.1 Test Case: Đăng ký tài khoản thành công
**Mục đích:** Kiểm tra chức năng đăng ký tài khoản mới với thông tin hợp lệ

**Tiền điều kiện:**
- Hệ thống đang hoạt động bình thường
- Database đã seed với roles và permissions
- Username và email chưa tồn tại trong hệ thống

**Dữ liệu test:**
```json
{
  "username": "testuser001",
  "email": "testuser001@example.com",
  "password": "SecurePass123!",
  "firstName": "Nguyễn",
  "lastName": "Văn A",
  "phone": "0987654321",
  "address": "123 Đường ABC, Quận 1, TP.HCM",
  "dateOfBirth": "1995-05-15T00:00:00"
}
```

**Quy trình thực hiện:**
1. Gửi POST request đến `/register`
2. Kiểm tra response status code
3. Kiểm tra dữ liệu trả về
4. Kiểm tra dữ liệu trong database

**Kết quả mong đợi:**
- Status code: 201 Created
- Response body chứa thông tin user mới tạo
- User được lưu vào database với role mặc định ROLE_CUSTOMER
- Password được mã hóa BCrypt

### 1.2 Test Case: Đăng ký với username đã tồn tại
**Mục đích:** Kiểm tra xử lý lỗi khi đăng ký với username đã tồn tại

**Tiền điều kiện:**
- Username "admin" đã tồn tại trong hệ thống (từ seed data)

**Dữ liệu test:**
```json
{
  "username": "admin",
  "email": "newemail@example.com",
  "password": "SecurePass123!",
  "firstName": "Trần",
  "lastName": "Văn B"
}
```

**Quy trình thực hiện:**
1. Gửi POST request đến `/register`
2. Kiểm tra response status code và message

**Kết quả mong đợi:**
- Status code: 400 Bad Request
- Error message: "Username already exists: admin"

### 1.3 Test Case: Đăng ký với email đã tồn tại
**Mục đích:** Kiểm tra xử lý lỗi khi đăng ký với email đã tồn tại

**Tiền điều kiện:**
- Email "testuser001@example.com" đã tồn tại trong hệ thống

**Dữ liệu test:**
```json
{
  "username": "newuser123",
  "email": "testuser001@example.com",
  "password": "SecurePass123!",
  "firstName": "Lê",
  "lastName": "Thị C"
}
```

**Quy trình thực hiện:**
1. Gửi POST request đến `/register`
2. Kiểm tra response status code và message

**Kết quả mong đợi:**
- Status code: 400 Bad Request
- Error message: "Email already exists: testuser001@example.com"

---

## 2. ĐĂNG NHẬP

### 2.1 Test Case: Đăng nhập với tài khoản ADMIN
**Mục đích:** Kiểm tra đăng nhập với tài khoản admin đã seed

**Tiền điều kiện:**
- Tài khoản admin đã được seed

**Dữ liệu test:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Quy trình thực hiện:**
1. Gửi POST request đến `/login`
2. Kiểm tra response status code
3. Kiểm tra access token và refresh token trong response
4. Decode token để verify permissions

**Kết quả mong đợi:**
- Status code: 200 OK
- Response body chứa accessToken, refreshToken, và expiryTime
- Token chứa thông tin ROLE_ADMIN với tất cả permissions

### 2.2 Test Case: Đăng nhập với tài khoản STAFF
**Mục đích:** Kiểm tra đăng nhập với tài khoản staff đã seed

**Dữ liệu test:**
```json
{
  "username": "staff",
  "password": "staff123"
}
```

**Quy trình thực hiện:**
1. Gửi POST request đến `/login`
2. Kiểm tra response và decode token

**Kết quả mong đợi:**
- Status code: 200 OK
- Token chứa ROLE_STAFF với permissions: PRODUCT, ORDER, PROMOTION, VOUCHER, CAMPAIGN (trừ DELETE)

### 2.3 Test Case: Đăng nhập với tài khoản CUSTOMER
**Mục đích:** Kiểm tra đăng nhập với tài khoản customer đã seed

**Dữ liệu test:**
```json
{
  "username": "customer",
  "password": "customer123"
}
```

**Quy trình thực hiện:**
1. Gửi POST request đến `/login`
2. Kiểm tra response và decode token

**Kết quả mong đợi:**
- Status code: 200 OK
- Token chứa ROLE_CUSTOMER với permissions: PRODUCT_READ, ORDER_READ

---

## 5. XEM DANH SÁCH TÀI KHOẢN

### 5.1 Test Case: ADMIN xem danh sách tài khoản
**Mục đích:** Kiểm tra ADMIN có USER_READ permission có thể xem tất cả tài khoản

**Tiền điều kiện:**
- Đăng nhập với tài khoản admin (có USER_READ permission)

**Quy trình thực hiện:**
1. Đăng nhập với admin/admin123
2. Gửi GET request đến `/api/users`
3. Thêm Authorization header với token của admin
4. Kiểm tra response

**Kết quả mong đợi:**
- Status code: 200 OK
- Response chứa danh sách tất cả user (admin, staff, customer + users khác)
- Mỗi user có đầy đủ thông tin và roles

### 5.2 Test Case: STAFF không thể xem danh sách tài khoản
**Mục đích:** Kiểm tra STAFF không có USER_READ permission không thể xem danh sách

**Tiền điều kiện:**
- Đăng nhập với tài khoản staff (không có USER_READ permission)

**Quy trình thực hiện:**
1. Đăng nhập với staff/staff123
2. Gửi GET request đến `/api/users`
3. Thêm Authorization header với token của staff

**Kết quả mong đợi:**
- Status code: 403 Forbidden
- Error message về thiếu quyền truy cập

### 5.3 Test Case: CUSTOMER không thể xem danh sách tài khoản
**Mục đích:** Kiểm tra CUSTOMER không có USER_READ permission

**Tiền điều kiện:**
- Đăng nhập với tài khoản customer (không có USER_READ permission)

**Quy trình thực hiện:**
1. Đăng nhập với customer/customer123
2. Gửi GET request đến `/api/users`
3. Thêm Authorization header với token của customer

**Kết quả mong đợi:**
- Status code: 403 Forbidden

---

## 6. PHÂN QUYỀN TÀI KHOẢN THEO USERPERMISSION ENUM

### 6.1 Test Case: ADMIN tạo tài khoản mới
**Mục đích:** Kiểm tra ADMIN có USER_WRITE permission có thể tạo tài khoản

**Tiền điều kiện:**
- Đăng nhập với tài khoản admin (có USER_WRITE permission)

**Dữ liệu test:**
```json
{
  "username": "newstaff001",
  "email": "newstaff@techbox.com",
  "password": "NewStaff123!",
  "firstName": "Nhân",
  "lastName": "Viên Mới",
  "roleNames": ["ROLE_STAFF"]
}
```

**Quy trình thực hiện:**
1. Đăng nhập với admin/admin123
2. Gửi POST request đến `/api/users`
3. Thêm Authorization header với token admin
4. Kiểm tra response và database

**Kết quả mong đợi:**
- Status code: 201 Created
- User được tạo với role ROLE_STAFF
- Location header chứa URL của user mới

### 6.2 Test Case: STAFF không thể tạo tài khoản
**Mục đích:** Kiểm tra STAFF không có USER_WRITE permission

**Tiền điều kiện:**
- Đăng nhập với tài khoản staff (không có USER_WRITE permission)

**Quy trình thực hiện:**
1. Đăng nhập với staff/staff123
2. Gửi POST request đến `/api/users`
3. Thêm Authorization header với token staff

**Kết quả mong đợi:**
- Status code: 403 Forbidden

### 6.3 Test Case: ADMIN cập nhật thông tin user khác
**Mục đích:** Kiểm tra ADMIN có USER_WRITE/USER_UPDATE permission có thể sửa user khác

**Tiền điều kiện:**
- Đăng nhập với tài khoản admin
- Biết ID của user staff (thường là ID = 2)

**Dữ liệu test:**
```json
{
  "firstName": "Staff Updated",
  "lastName": "By Admin",
  "phone": "0999888777"
}
```

**Quy trình thực hiện:**
1. Đăng nhập với admin/admin123
2. Gửi PUT request đến `/api/users/2` (ID của staff user)
3. Thêm Authorization header với token admin
4. Kiểm tra response và database

**Kết quả mong đợi:**
- Status code: 200 OK
- User staff được cập nhật thông tin
- Database phản ánh thay đổi

### 6.4 Test Case: STAFF không thể cập nhật user khác
**Mục đích:** Kiểm tra STAFF không có USER_WRITE/USER_UPDATE permission không thể sửa user khác

**Tiền điều kiện:**
- Đăng nhập với tài khoản staff
- Biết ID của user admin (thường là ID = 1)

**Quy trình thực hiện:**
1. Đăng nhập với staff/staff123
2. Gửi PUT request đến `/api/users/1` (ID của admin user)
3. Thêm Authorization header với token staff

**Kết quả mong đợi:**
- Status code: 403 Forbidden

### 6.5 Test Case: User tự cập nhật thông tin của mình
**Mục đích:** Kiểm tra logic canModifyUser cho phép user sửa thông tin chính mình

**Tiền điều kiện:**
- Đăng nhập với tài khoản staff

**Dữ liệu test:**
```json
{
  "firstName": "Staff Self",
  "lastName": "Updated",
  "phone": "0888777666"
}
```

**Quy trình thực hiện:**
1. Đăng nhập với staff/staff123
2. Gửi PUT request đến `/api/users/2` (ID của chính staff user)
3. Thêm Authorization header với token staff

**Kết quả mong đợi:**
- Status code: 200 OK
- Staff user được cập nhật thông tin của chính mình

---

## 7. XÓA TÀI KHOẢN (SOFT DELETE)

### 7.1 Test Case: ADMIN xóa tài khoản
**Mục đích:** Kiểm tra ADMIN có USER_DELETE permission có thể xóa tài khoản

**Tiền điều kiện:**
- Đăng nhập với tài khoản admin (có USER_DELETE permission)
- Tạo user test để xóa

**Quy trình thực hiện:**
1. Đăng nhập với admin/admin123
2. Tạo user test trước
3. Gửi DELETE request đến `/api/users/{test_user_id}`
4. Thêm Authorization header với token admin
5. Kiểm tra response và database

**Kết quả mong đợi:**
- Status code: 204 No Content
- User.deletedAt được set thành thời gian hiện tại
- User vẫn tồn tại trong database nhưng được đánh dấu đã xóa

### 7.2 Test Case: STAFF không thể xóa tài khoản
**Mục đích:** Kiểm tra STAFF không có USER_DELETE permission

**Tiền điều kiện:**
- Đăng nhập với tài khoản staff (không có USER_DELETE permission)

**Quy trình thực hiện:**
1. Đăng nhập với staff/staff123
2. Gửi DELETE request đến `/api/users/3` (customer user)
3. Thêm Authorization header với token staff

**Kết quả mong đợi:**
- Status code: 403 Forbidden

---

## 8. XEM THÔNG TIN CÁ NHÂN

### 8.1 Test Case: Tất cả user có thể xem profile của mình
**Mục đích:** Kiểm tra canReadSelfProfile cho phép tất cả authenticated user xem profile

**Test Case 8.1a: ADMIN xem profile**
- Đăng nhập: admin/admin123
- Request: GET `/api/users/profile`
- Kết quả: 200 OK với thông tin admin user

**Test Case 8.1b: STAFF xem profile**
- Đăng nhập: staff/staff123
- Request: GET `/api/users/profile`
- Kết quả: 200 OK với thông tin staff user

**Test Case 8.1c: CUSTOMER xem profile**
- Đăng nhập: customer/customer123
- Request: GET `/api/users/profile`
- Kết quả: 200 OK với thông tin customer user

### 8.2 Test Case: User không đăng nhập không thể xem profile
**Mục đích:** Kiểm tra yêu cầu authentication

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/users/profile` không có Authorization header
2. Kiểm tra response

**Kết quả mong đợi:**
- Status code: 401 Unauthorized

### 8.3 Test Case: Kiểm tra logic canAccessUser
**Mục đích:** Kiểm tra logic phân quyền xem user cụ thể

**Test Case 8.3a: ADMIN xem user khác**
- Đăng nhập: admin/admin123
- Request: GET `/api/users/2` (staff user)
- Kết quả: 200 OK (có USER_READ permission)

**Test Case 8.3b: STAFF xem chính mình**
- Đăng nhập: staff/staff123
- Request: GET `/api/users/2` (chính staff user)
- Kết quả: 200 OK (logic cho phép xem chính mình)

**Test Case 8.3c: STAFF xem user khác**
- Đăng nhập: staff/staff123
- Request: GET `/api/users/1` (admin user)
- Kết quả: 403 Forbidden (không có USER_READ và không phải chính mình)

**Test Case 8.3d: CUSTOMER xem user khác**
- Đăng nhập: customer/customer123
- Request: GET `/api/users/1` (admin user)
- Kết quả: 403 Forbidden

---

## 9. CHỈNH SỬA THÔNG TIN CÁ NHÂN

### 9.1 Test Case: Tất cả user có thể cập nhật profile của mình
**Mục đích:** Kiểm tra canUpdateSelfProfile cho phép tất cả authenticated user cập nhật profile

**Dữ liệu test:**
```json
{
  "firstName": "Updated",
  "lastName": "Profile",
  "phone": "0999000111",
  "address": "New Address"
}
```

**Test Case 9.1a: ADMIN cập nhật profile**
- Đăng nhập: admin/admin123
- Request: PUT `/api/users/profile`
- Kết quả: 200 OK

**Test Case 9.1b: STAFF cập nhật profile**
- Đăng nhập: staff/staff123
- Request: PUT `/api/users/profile`
- Kết quả: 200 OK

**Test Case 9.1c: CUSTOMER cập nhật profile**
- Đăng nhập: customer/customer123
- Request: PUT `/api/users/profile`
- Kết quả: 200 OK

---

## NOTES

### Permission System Summary:
- **Enum-based**: Sử dụng UserPermission enum thay vì string permissions
- **Role-based assignment**: Permissions được gán theo roles trong UserDataSeeder
- **Method-level security**: Sử dụng @PreAuthorize với custom methods
- **Self-access logic**: Cho phép user truy cập/sửa thông tin của chính mình

### Test Users (Seeded):
```
admin/admin123     - ROLE_ADMIN    - All permissions
staff/staff123     - ROLE_STAFF    - PRODUCT,ORDER,PROMOTION,VOUCHER,CAMPAIGN (no DELETE)
customer/customer123 - ROLE_CUSTOMER - PRODUCT_READ,ORDER_READ only
```

### Permission Logic:
- **canListUsers()**: Requires USER_READ permission
- **canAccessUser(id)**: USER_READ permission OR own user
- **canCreateUser()**: Requires USER_WRITE permission
- **canModifyUser(id)**: USER_WRITE/USER_UPDATE permission OR own user
- **canDeleteUser()**: Requires USER_DELETE permission
- **canReadSelfProfile()**: Any authenticated user
- **canUpdateSelfProfile()**: Any authenticated user

### API Endpoints Permission Matrix:
| Endpoint | ADMIN | STAFF | CUSTOMER |
|----------|-------|-------|----------|
| GET /api/users | ✅ | ❌ | ❌ |
| GET /api/users/{id} | ✅ | ✅(self) | ✅(self) |
| POST /api/users | ✅ | ❌ | ❌ |
| PUT /api/users/{id} | ✅ | ✅(self) | ✅(self) |
| DELETE /api/users/{id} | ✅ | ❌ | ❌ |
| GET /api/users/profile | ✅ | ✅ | ✅ |
| PUT /api/users/profile | ✅ | ✅ | ✅ |
