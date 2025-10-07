# TechBox Store Backend - Tài Liệu Test Case

## Mục lục
1. [Đăng ký tài khoản](#1-đăng-ký-tài-khoản)
2. [Đăng nhập](#2-đăng-nhập)
3. [Refresh Token](#3-refresh-token)
4. [Xác nhận Token](#4-xác-nhận-token)
5. [Xem danh sách tài khoản](#5-xem-danh-sách-tài-khoản)
6. [Phân quyền tài khoản](#6-phân-quyền-tài-khoản)
7. [Xóa tài khoản](#7-xóa-tài-khoản)
8. [Xem thông tin cá nhân](#8-xem-thông-tin-cá-nhân)
9. [Chỉnh sửa thông tin cá nhân](#9-chỉnh-sửa-thông-tin-cá-nhân)
10. [Quản lý giỏ hàng](#10-quản-lý-giỏ-hàng)

---

## 1. Đăng ký tài khoản

### Test Case 1.1: Đăng ký thành công với dữ liệu hợp lệ
**Tiền điều kiện:** 
- Server đang chạy
- Database đã được khởi tạo
- Username chưa tồn tại trong hệ thống

**Quy trình thực hiện:**
1. Gửi POST request đến `/register`
2. Trong body request, gửi JSON:
```json
{
    "username": "testuser001",
    "email": "testuser001@example.com",
    "password": "Password123!",
    "role": "customer"
}
```

**Kết quả mong muốn:**
- HTTP Status: 201 Created
- Response body:
```json
{
    "id": 1,
    "username": "testuser001",
    "email": "testuser001@example.com",
    "role": "customer"
}
```
- Location header: `/api/users/1`

### Test Case 1.2: Đăng ký thất bại - Username đã tồn tại
**Tiền điều kiện:**
- Username "testuser001" đã tồn tại trong database

**Quy trình thực hiện:**
1. Gửi POST request đến `/register`
2. Sử dụng username đã tồn tại:
```json
{
    "username": "testuser001",
    "email": "newemail@example.com",
    "password": "Password123!",
    "role": "customer"
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body chứa thông báo lỗi

### Test Case 1.3: Đăng ký thất bại - Dữ liệu không hợp lệ
**Tiền điều kiện:**
- Server đang chạy

**Quy trình thực hiện:**
1. Gửi POST request đến `/register`
2. Sử dụng dữ liệu không hợp lệ:
```json
{
    "username": "",
    "email": "invalid-email",
    "password": "123",
    "role": "invalid_role"
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body chứa thông báo lỗi validation

---

## 2. Đăng nhập

### Test Case 2.1: Đăng nhập thành công với thông tin đúng
**Tiền điều kiện:**
- Tài khoản "testuser001" với password "Password123!" đã tồn tại

**Quy trình thực hiện:**
1. Gửi POST request đến `/login`
2. Body request:
```json
{
    "username": "testuser001",
    "password": "Password123!"
}
```

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_string_here",
    "expiresIn": 3600
}
```

### Test Case 2.2: Đăng nhập thất bại - Sai mật khẩu
**Tiền điều kiện:**
- Tài khoản "testuser001" đã tồn tại

**Quy trình thực hiện:**
1. Gửi POST request đến `/login`
2. Body request với sai password:
```json
{
    "username": "testuser001",
    "password": "WrongPassword"
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request

### Test Case 2.3: Đăng nhập thất bại - Username không tồn tại
**Tiền điều kiện:**
- Username "nonexistentuser" không tồn tại

**Quy trình thực hiện:**
1. Gửi POST request đến `/login`
2. Body request:
```json
{
    "username": "nonexistentuser",
    "password": "Password123!"
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request

---

## 3. Refresh Token

### Test Case 3.1: Refresh token thành công
**Tiền điều kiện:**
- Có refresh token hợp lệ từ quá trình đăng nhập trước đó

**Quy trình thực hiện:**
1. Gửi POST request đến `/refresh-token`
2. Body request:
```json
{
    "refreshToken": "valid_refresh_token_here"
}
```

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "token": "new_jwt_token_here",
    "refreshToken": "new_refresh_token_here",
    "expiresIn": 3600
}
```

### Test Case 3.2: Refresh token thất bại - Token không hợp lệ
**Tiền điều kiện:**
- Server đang chạy

**Quy trình thực hiện:**
1. Gửi POST request đến `/refresh-token`
2. Body request với token không hợp lệ:
```json
{
    "refreshToken": "invalid_refresh_token"
}
```

**Kết quả mong muốn:**
- HTTP Status: 401 Unauthorized
- Response body:
```json
{
    "code": "REFRESH_FAILED",
    "message": "Failed to refresh token: Token không hợp lệ",
    "success": false
}
```

### Test Case 3.3: Refresh token thất bại - Token đã hết hạn
**Tiền điều kiện:**
- Có refresh token đã hết hạn

**Quy trình thực hiện:**
1. Gửi POST request đến `/refresh-token`
2. Body request với token hết hạn:
```json
{
    "refreshToken": "expired_refresh_token"
}
```

**Kết quả mong muốn:**
- HTTP Status: 401 Unauthorized
- Response body chứa thông báo token hết hạn

---

## 4. Xác nhận Token

### Test Case 4.1: Xác nhận token hợp lệ qua protected endpoint
**Tiền điều kiện:**
- Có JWT token hợp lệ

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/users` (protected endpoint)
2. Header Authorization: `Bearer valid_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body chứa danh sách users

### Test Case 4.2: Xác nhận token không hợp lệ
**Tiền điều kiện:**
- Server đang chạy

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/users`
2. Header Authorization: `Bearer invalid_token`

**Kết quả mong muốn:**
- HTTP Status: 401 Unauthorized

### Test Case 4.3: Xác nhận không có token
**Tiền điều kiện:**
- Server đang chạy

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/users`
2. Không có header Authorization

**Kết quả mong muốn:**
- HTTP Status: 401 Unauthorized

---

## 5. Xem danh sách tài khoản

### Test Case 5.1: Xem danh sách tài khoản với quyền admin
**Tiền điều kiện:**
- Đăng nhập với tài khoản có role "admin"
- Có ít nhất 3 tài khoản trong database

**Quy trình thực hiện:**
1. Đăng nhập với admin account để lấy token
2. Gửi GET request đến `/api/users`
3. Header Authorization: `Bearer admin_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
[
    {
        "id": 1,
        "username": "admin001",
        "email": "admin@example.com",
        "role": "admin"
    },
    {
        "id": 2,
        "username": "staff001",
        "email": "staff@example.com",
        "role": "staff"
    },
    {
        "id": 3,
        "username": "customer001",
        "email": "customer@example.com",
        "role": "customer"
    }
]
```

### Test Case 5.2: Xem danh sách tài khoản với quyền customer (không được phép)
**Tiền điều kiện:**
- Đăng nhập với tài khoản có role "customer"

**Quy trình thực hiện:**
1. Đăng nhập với customer account
2. Gửi GET request đến `/api/users`
3. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 403 Forbidden

---

## 6. Phân quyền tài khoản

### Test Case 6.1: Cập nhật role tài khoản thành công
**Tiền điều kiện:**
- Đăng nhập với tài khoản admin
- Tài khoản có id=3 tồn tại với role "customer"

**Quy trình thực hiện:**
1. Gửi PUT request đến `/api/users/3`
2. Header Authorization: `Bearer admin_jwt_token`
3. Body request:
```json
{
    "username": "customer001",
    "email": "customer@example.com",
    "role": "staff"
}
```

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "id": 3,
    "username": "customer001",
    "email": "customer@example.com",
    "role": "staff"
}
```

### Test Case 6.2: Cập nhật role thất bại - Không có quyền
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer

**Quy trình thực hiện:**
1. Gửi PUT request đến `/api/users/2`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request thay đổi role

**Kết quả mong muốn:**
- HTTP Status: 403 Forbidden

---

## 7. Xóa tài khoản (Soft Delete)

### Test Case 7.1: Xóa tài khoản thành công (Soft Delete)
**Tiền điều kiện:**
- Đăng nhập với tài khoản admin
- Tài khoản có id=5 tồn tại và đang active (status = true)

**Quy trình thực hiện:**
1. Gửi DELETE request đến `/api/users/5`
2. Header Authorization: `Bearer admin_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "id": 5,
    "username": "user_to_delete",
    "email": "delete@example.com",
    "role": "customer",
    "status": false,
    "message": "Tài khoản đã được xóa thành công"
}
```

### Test Case 7.2: Xóa tài khoản không tồn tại
**Tiền điều kiện:**
- Đăng nhập với tài khoản admin
- Tài khoản có id=999 không tồn tại

**Quy trình thực hiện:**
1. Gửi DELETE request đến `/api/users/999`
2. Header Authorization: `Bearer admin_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 404 Not Found
- Response body:
```json
{
    "code": "USER_NOT_FOUND",
    "message": "Không tìm thấy tài khoản với ID: 999",
    "success": false
}
```

### Test Case 7.3: Xóa tài khoản đã bị xóa trước đó
**Tiền điều kiện:**
- Đăng nhập với tài khoản admin
- Tài khoản có id=6 đã bị soft delete (status = false)

**Quy trình thực hiện:**
1. Gửi DELETE request đến `/api/users/6`
2. Header Authorization: `Bearer admin_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body:
```json
{
    "code": "ALREADY_DELETED",
    "message": "Tài khoản đã được xóa trước đó",
    "success": false
}
```

### Test Case 7.4: Xóa tài khoản thất bại - Không có quyền
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer

**Quy trình thực hiện:**
1. Gửi DELETE request đến `/api/users/2`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 403 Forbidden
- Response body:
```json
{
    "code": "INSUFFICIENT_PERMISSIONS",
    "message": "Bạn không có quyền xóa tài khoản này",
    "success": false
}
```

### Test Case 7.5: Khôi phục tài khoản đã xóa
**Tiền điều kiện:**
- Đăng nhập với tài khoản admin
- Tài khoản có id=7 đã bị soft delete (status = false)

**Quy trình thực hiện:**
1. Gửi PUT request đến `/api/users/7/restore`
2. Header Authorization: `Bearer admin_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "id": 7,
    "username": "restored_user",
    "email": "restored@example.com",
    "role": "customer",
    "status": true,
    "message": "Tài khoản đã được khôi phục thành công"
}
```

---

## 8. Xem thông tin cá nhân

### Test Case 8.1: Xem thông tin cá nhân thành công
**Tiền điều kiện:**
- Đăng nhập với tài khoản có id=1

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/users/1`
2. Header Authorization: `Bearer user_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "id": 1,
    "username": "testuser001",
    "email": "testuser001@example.com",
    "role": "customer"
}
```

### Test Case 8.2: Xem thông tin tài khoản không tồn tại
**Tiền điều kiện:**
- Đăng nhập thành công

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/users/999`
2. Header Authorization: `Bearer valid_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 404 Not Found

---

## 9. Chỉnh sửa thông tin cá nhân

### Test Case 9.1: Cập nhật thông tin cá nhân thành công
**Tiền điều kiện:**
- Đăng nhập với tài khoản có id=1

**Quy trình thực hiện:**
1. Gửi PUT request đến `/api/users/1`
2. Header Authorization: `Bearer user_jwt_token`
3. Body request:
```json
{
    "username": "updateduser001",
    "email": "updated@example.com",
    "role": "customer"
}
```

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "id": 1,
    "username": "updateduser001",
    "email": "updated@example.com",
    "role": "customer"
}
```

### Test Case 9.2: Cập nhật thông tin thất bại - Email không hợp lệ
**Tiền điều kiện:**
- Đăng nhập thành công

**Quy trình thực hiện:**
1. Gửi PUT request đến `/api/users/1`
2. Header Authorization: `Bearer user_jwt_token`
3. Body request với email không hợp lệ:
```json
{
    "username": "testuser001",
    "email": "invalid-email-format",
    "role": "customer"
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body chứa thông báo lỗi validation

---

## 10. Quản lý giỏ hàng

### Test Case 10.1: Thêm sản phẩm vào giỏ hàng lần đầu
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Sản phẩm có id=101 tồn tại trong hệ thống với stock > 0
- Giỏ hàng của user đang rỗng

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/items`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "productId": 101,
    "quantity": 2
}
```

**Kết quả mong muốn:**
- HTTP Status: 201 Created
- Response body:
```json
{
    "id": 1,
    "productId": 101,
    "productName": "Laptop Gaming",
    "quantity": 2,
    "unitPrice": 15000000,
    "totalPrice": 30000000,
    "addedAt": "2024-01-15T10:30:00Z"
}
```

### Test Case 10.2: Thêm sản phẩm đã có trong giỏ hàng (tăng số lượng)
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Sản phẩm có id=101 đã có trong giỏ hàng với số lượng 2

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/items`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "productId": 101,
    "quantity": 3
}
```

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "id": 1,
    "productId": 101,
    "productName": "Laptop Gaming",
    "quantity": 5,
    "unitPrice": 15000000,
    "totalPrice": 75000000,
    "updatedAt": "2024-01-15T11:00:00Z"
}
```

### Test Case 10.3: Thêm sản phẩm thất bại - Vượt quá tồn kho
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Sản phẩm có id=102 có stock = 5

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/items`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "productId": 102,
    "quantity": 10
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body:
```json
{
    "code": "INSUFFICIENT_STOCK",
    "message": "Số lượng yêu cầu (10) vượt quá tồn kho hiện có (5)",
    "success": false,
    "availableStock": 5
}
```

### Test Case 10.4: Thêm sản phẩm thất bại - Số lượng không hợp lệ
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/items`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "productId": 101,
    "quantity": 0
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body:
```json
{
    "code": "INVALID_QUANTITY",
    "message": "Số lượng phải lớn hơn 0",
    "success": false
}
```

### Test Case 10.5: Thêm sản phẩm thất bại - Sản phẩm không tồn tại
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Sản phẩm có id=999 không tồn tại

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/items`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "productId": 999,
    "quantity": 1
}
```

**Kết quả mong muốn:**
- HTTP Status: 404 Not Found
- Response body:
```json
{
    "code": "PRODUCT_NOT_FOUND",
    "message": "Không tìm thấy sản phẩm với ID: 999",
    "success": false
}
```

### Test Case 10.6: Thêm sản phẩm thất bại - Sản phẩm không còn bán
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Sản phẩm có id=105 có status = "DISCONTINUED"

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/items`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "productId": 105,
    "quantity": 1
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body:
```json
{
    "code": "PRODUCT_UNAVAILABLE",
    "message": "Sản phẩm này hiện không còn được bán",
    "success": false
}
```

### Test Case 10.7: Xem danh sách sản phẩm trong giỏ hàng (có sản phẩm)
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng có ít nhất 2 sản phẩm khác nhau

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/cart`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "userId": 1,
    "items": [
        {
            "id": 1,
            "productId": 101,
            "productName": "Laptop Gaming",
            "productImage": "https://example.com/laptop.jpg",
            "quantity": 2,
            "unitPrice": 15000000,
            "totalPrice": 30000000,
            "addedAt": "2024-01-15T10:30:00Z"
        },
        {
            "id": 2,
            "productId": 102,
            "productName": "Mouse Gaming",
            "productImage": "https://example.com/mouse.jpg",
            "quantity": 1,
            "unitPrice": 500000,
            "totalPrice": 500000,
            "addedAt": "2024-01-15T10:35:00Z"
        }
    ],
    "totalItems": 2,
    "totalQuantity": 3,
    "totalAmount": 30500000,
    "lastUpdated": "2024-01-15T10:35:00Z"
}
```

### Test Case 10.8: Xem giỏ hàng rỗng
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng không có sản phẩm nào

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/cart`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "userId": 1,
    "items": [],
    "totalItems": 0,
    "totalQuantity": 0,
    "totalAmount": 0,
    "message": "Giỏ hàng của bạn đang trống"
}
```

### Test Case 10.9: Cập nhật số lượng sản phẩm thành công
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Cart item có id=1 đã có trong giỏ hàng với quantity=2
- Sản phẩm có đủ tồn kho

**Quy trình thực hiện:**
1. Gửi PUT request đến `/api/cart/items/1`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "quantity": 5
}
```

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "id": 1,
    "productId": 101,
    "productName": "Laptop Gaming",
    "quantity": 5,
    "unitPrice": 15000000,
    "totalPrice": 75000000,
    "updatedAt": "2024-01-15T11:15:00Z"
}
```

### Test Case 10.10: Cập nhật số lượng thất bại - Vượt quá tồn kho
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Cart item có id=1 trong giỏ hàng
- Sản phẩm có stock = 3

**Quy trình thực hiện:**
1. Gửi PUT request đến `/api/cart/items/1`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "quantity": 10
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body:
```json
{
    "code": "INSUFFICIENT_STOCK",
    "message": "Số lượng yêu cầu (10) vượt quá tồn kho hiện có (3)",
    "success": false,
    "currentQuantity": 2,
    "availableStock": 3
}
```

### Test Case 10.11: Cập nhật số lượng thất bại - Cart item không tồn tại
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Cart item có id=999 không tồn tại

**Quy trình thực hiện:**
1. Gửi PUT request đến `/api/cart/items/999`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "quantity": 3
}
```

**Kết quả mong muốn:**
- HTTP Status: 404 Not Found
- Response body:
```json
{
    "code": "CART_ITEM_NOT_FOUND",
    "message": "Không tìm thấy sản phẩm trong giỏ hàng",
    "success": false
}
```

### Test Case 10.12: Cập nhật số lượng thất bại - Không có quyền
**Tiền điều kiện:**
- Đăng nhập với customer A
- Cart item có id=5 thuộc về customer B

**Quy trình thực hiện:**
1. Gửi PUT request đến `/api/cart/items/5`
2. Header Authorization: `Bearer customerA_jwt_token`
3. Body request:
```json
{
    "quantity": 3
}
```

**Kết quả mong muốn:**
- HTTP Status: 403 Forbidden
- Response body:
```json
{
    "code": "ACCESS_DENIED",
    "message": "Bạn không có quyền truy cập sản phẩm này trong giỏ hàng",
    "success": false
}
```

### Test Case 10.13: Xóa một sản phẩm khỏi giỏ hàng thành công
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Cart item có id=2 đã có trong giỏ hàng

**Quy trình thực hiện:**
1. Gửi DELETE request đến `/api/cart/items/2`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "message": "Đã xóa sản phẩm khỏi giỏ hàng thành công",
    "deletedItem": {
        "id": 2,
        "productId": 102,
        "productName": "Mouse Gaming",
        "quantity": 1
    },
    "success": true
}
```

### Test Case 10.14: Xóa sản phẩm thất bại - Item không tồn tại
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer

**Quy trình thực hiện:**
1. Gửi DELETE request đến `/api/cart/items/999`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 404 Not Found
- Response body:
```json
{
    "code": "CART_ITEM_NOT_FOUND",
    "message": "Không tìm thấy sản phẩm trong giỏ hàng",
    "success": false
}
```

### Test Case 10.15: Xóa toàn bộ giỏ hàng thành công
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng có ít nhất 2 sản phẩm

**Quy trình thực hiện:**
1. Gửi DELETE request đến `/api/cart`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "message": "Đã xóa toàn bộ giỏ hàng thành công",
    "deletedItemsCount": 2,
    "success": true
}
```

### Test Case 10.16: Xóa giỏ hàng khi giỏ hàng đã rỗng
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng không có sản phẩm nào

**Quy trình thực hiện:**
1. Gửi DELETE request đến `/api/cart`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "message": "Giỏ hàng đã rỗng",
    "deletedItemsCount": 0,
    "success": true
}
```

### Test Case 10.17: Đếm số lượng sản phẩm trong giỏ hàng
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng có 3 sản phẩm khác nhau với tổng số lượng 7

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/cart/count`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "totalItems": 3,
    "totalQuantity": 7,
    "totalAmount": 45000000
}
```

### Test Case 10.18: Kiểm tra giỏ hàng sau khi sản phẩm hết hàng
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng có sản phẩm id=103
- Admin cập nhật sản phẩm id=103 thành hết hàng (stock=0)

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/cart`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "userId": 1,
    "items": [
        {
            "id": 3,
            "productId": 103,
            "productName": "Keyboard Mechanical",
            "quantity": 1,
            "unitPrice": 1200000,
            "totalPrice": 1200000,
            "status": "OUT_OF_STOCK",
            "message": "Sản phẩm này hiện đã hết hàng"
        }
    ],
    "totalItems": 1,
    "totalQuantity": 1,
    "totalAmount": 1200000,
    "hasUnavailableItems": true,
    "warnings": ["Một số sản phẩm trong giỏ hàng hiện không còn sẵn có"]
}
```

### Test Case 10.19: Đồng bộ giỏ hàng khi giá sản phẩm thay đổi
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng có sản phẩm id=101 với giá 15000000
- Admin cập nhật giá sản phẩm id=101 thành 16000000

**Quy trình thực hiện:**
1. Gửi GET request đến `/api/cart/sync`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "message": "Giỏ hàng đã được đồng bộ",
    "updatedItems": [
        {
            "id": 1,
            "productId": 101,
            "productName": "Laptop Gaming",
            "quantity": 2,
            "oldPrice": 15000000,
            "newPrice": 16000000,
            "totalPrice": 32000000,
            "priceChanged": true
        }
    ],
    "priceChangesDetected": true,
    "newTotalAmount": 32000000
}
```

### Test Case 10.20: Áp dụng mã giảm giá cho giỏ hàng
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng có tổng giá trị 30000000
- Mã giảm giá "DISCOUNT10" có hiệu lực (10% discount)

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/apply-discount`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "discountCode": "DISCOUNT10"
}
```

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "discountCode": "DISCOUNT10",
    "discountType": "PERCENTAGE",
    "discountValue": 10,
    "originalAmount": 30000000,
    "discountAmount": 3000000,
    "finalAmount": 27000000,
    "message": "Áp dụng mã giảm giá thành công",
    "success": true
}
```

### Test Case 10.21: Áp dụng mã giảm giá thất bại - Mã không hợp lệ
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Mã giảm giá "INVALID_CODE" không tồn tại

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/apply-discount`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "discountCode": "INVALID_CODE"
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body:
```json
{
    "code": "INVALID_DISCOUNT_CODE",
    "message": "Mã giảm giá không hợp lệ hoặc đã hết hạn",
    "success": false
}
```

### Test Case 10.22: Áp dụng mã giảm giá thất bại - Không đủ điều kiện
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng có tổng giá trị 500000
- Mã giảm giá "BIGORDER" yêu cầu đơn hàng tối thiểu 1000000

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/apply-discount`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "discountCode": "BIGORDER"
}
```

**Kết quả mong muốn:**
- HTTP Status: 400 Bad Request
- Response body:
```json
{
    "code": "MINIMUM_ORDER_NOT_MET",
    "message": "Đơn hàng phải có giá trị tối thiểu 1,000,000 VND để sử dụng mã này",
    "currentAmount": 500000,
    "minimumRequired": 1000000,
    "success": false
}
```

### Test Case 10.23: Lưu giỏ hàng để mua sau
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng có ít nhất 1 sản phẩm

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/save-for-later`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "name": "Giỏ hàng gaming setup"
}
```

**Kết quả mong muốn:**
- HTTP Status: 201 Created
- Response body:
```json
{
    "id": 1,
    "name": "Giỏ hàng gaming setup",
    "itemCount": 2,
    "totalAmount": 30500000,
    "savedAt": "2024-01-15T12:00:00Z",
    "message": "Đã lưu giỏ hàng thành công"
}
```

### Test Case 10.24: Khôi phục giỏ hàng đã lưu
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Có giỏ hàng đã lưu với id=1

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/restore/1`
2. Header Authorization: `Bearer customer_jwt_token`

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "message": "Đã khôi phục giỏ hàng thành công",
    "restoredItems": 2,
    "conflictItems": [],
    "totalAmount": 30500000,
    "success": true
}
```

### Test Case 10.25: Ước tính phí vận chuyển
**Tiền điều kiện:**
- Đăng nhập với tài khoản customer
- Giỏ hàng có sản phẩm với tổng trọng lượng và địa chỉ giao hàng

**Quy trình thực hiện:**
1. Gửi POST request đến `/api/cart/estimate-shipping`
2. Header Authorization: `Bearer customer_jwt_token`
3. Body request:
```json
{
    "deliveryAddress": {
        "city": "Ho Chi Minh City",
        "district": "District 1",
        "ward": "Ben Nghe Ward"
    },
    "shippingMethod": "STANDARD"
}
```

**Kết quả mong muốn:**
- HTTP Status: 200 OK
- Response body:
```json
{
    "shippingFee": 50000,
    "estimatedDays": "2-3",
    "cartTotal": 30500000,
    "finalTotal": 30550000,
    "freeShippingThreshold": 50000000,
    "amountNeededForFreeShipping": 19450000
}
```
````
