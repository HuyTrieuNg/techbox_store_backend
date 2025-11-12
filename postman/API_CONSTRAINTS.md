# Tài liệu Ràng buộc API và Dữ liệu Seed

Tài liệu này mô tả các ràng buộc validation và dữ liệu test đã seed cho các API của Techbox Store Backend.

## 1. Xác thực (Login)

### Ràng buộc
- **Email**: Bắt buộc, định dạng email hợp lệ, tối đa 254 ký tự
- **Password**: Bắt buộc, tối thiểu 6 ký tự, tối đa 128 ký tự
- **Lỗi Validation**: Trả về 400 với thông báo cụ thể cho từng trường
- **Lỗi Xác thực**: Trả về 401 với INVALID_CREDENTIALS hoặc ACCOUNT_LOCKED

### Dữ liệu User Test Đã Seed
| Email | Password | Role | Trạng thái | Ghi chú |
|-------|----------|------|------------|---------|
| admin@techbox.vn | admin123 | ADMIN | Hoạt động | Tài khoản admin |
| staff1@techbox.vn | staff123 | STAFF | Hoạt động | Tài khoản staff 1 |
| staff2@techbox.vn | staff123 | STAFF | Hoạt động | Tài khoản staff 2 |
| testCustomer@techbox.vn | customer123 | CUSTOMER | Hoạt động | Khách hàng test |
| testCustomer123@techbox.vn | @Customer123 | CUSTOMER | Hoạt động | Khách hàng test với ký tự đặc biệt |
| locked@techbox.vn | locked123 | CUSTOMER | Bị khóa | Tài khoản bị khóa để test |
| disabled@techbox.vn | disabled123 | CUSTOMER | Vô hiệu hóa | Tài khoản vô hiệu hóa để test |
| invalidemail | somepassword | CUSTOMER | Hoạt động | Để test email không hợp lệ |
| test@ | somepassword | CUSTOMER | Hoạt động | Để test domain email không hợp lệ |
|  test@techbox.vn  | somepassword | CUSTOMER | Hoạt động | Để test email có khoảng trắng |
| TESTCUSTOMER@TECHBOX.VN | somepassword | CUSTOMER | Hoạt động | Để test email viết hoa |
| user+test@techbox.vn | somepassword | CUSTOMER | Hoạt động | Để test email có ký tự đặc biệt |
| 测试@techbox.vn | somepassword | CUSTOMER | Hoạt động | Để test email có unicode |
| user@sub.techbox.vn | somepassword | CUSTOMER | Hoạt động | Để test email có subdomain |

## 2. Tạo Category

### Ràng buộc
- **Name**: Bắt buộc, tối thiểu 2 ký tự, tối đa 50 ký tự, duy nhất (phân biệt hoa thường)
- **Parent Category ID**: Tùy chọn, phải tồn tại nếu cung cấp, không được âm
- **Phân quyền**: Yêu cầu quyền PRODUCT:WRITE (chỉ ADMIN)
- **Lỗi Validation**: Trả về 400 với thông báo cụ thể cho từng trường
- **Lỗi Phân quyền**: Trả về 403 Forbidden

### Dữ liệu Category Test Đã Seed
| Name | Parent ID | Ghi chú |
|------|-----------|---------|
| Electronics | null | Category gốc |
| Smartphones | 1 | Category con của Electronics |

## 3. Tạo Voucher

### Ràng buộc
- **Code**: Bắt buộc, 3-50 ký tự, chỉ chữ hoa, số, gạch dưới, gạch ngang, duy nhất
- **Voucher Type**: Bắt buộc, phải là FIXED_AMOUNT hoặc PERCENTAGE
- **Value**: Bắt buộc, >0, <10,000,000 cho FIXED_AMOUNT (chỉ số nguyên), 1-100 cho PERCENTAGE
- **Min Order Amount**: Bắt buộc, >=0
- **Usage Limit**: Bắt buộc, 1-1,000,000
- **Valid From/Until**: Bắt buộc, khoảng thời gian hợp lệ
- **Phân quyền**: Yêu cầu quyền VOUCHER:WRITE (chỉ ADMIN)
- **Lỗi Validation**: Trả về 400 với thông báo cụ thể cho từng trường
- **Lỗi Xung đột**: Trả về 409 cho code trùng lặp

### Dữ liệu Voucher Test Đã Seed
| Code | Type | Value | Min Order | Usage Limit | Valid From | Valid Until | Ghi chú |
|------|------|-------|-----------|-------------|------------|-------------|---------|
| EXISTINGCODE | FIXED_AMOUNT | 50000.00 | 0.00 | 100 | 2025-12-01T00:00:00Z | 2025-12-31T23:59:59Z | Để test code trùng lặp |

