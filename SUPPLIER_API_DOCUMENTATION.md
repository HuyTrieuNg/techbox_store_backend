# Tài liệu API: Nhà cung cấp (Supplier)

## Tổng quan
Tài liệu này mô tả các API endpoint để quản lý nhà cung cấp trong hệ thống Techbox Store. Các API này cho phép tạo, đọc, cập nhật và xóa thông tin nhà cung cấp.

## Base URL
```
/api/suppliers
```

## Authentication & Authorization
- **Authentication**: JWT Bearer token
- **Authorities**:
  - `INVENTORY:READ` - Đọc thông tin nhà cung cấp
  - `INVENTORY:WRITE` - Tạo nhà cung cấp mới
  - `INVENTORY:UPDATE` - Cập nhật nhà cung cấp
  - `INVENTORY:DELETE` - Xóa nhà cung cấp

## 1. Lấy danh sách nhà cung cấp

### Endpoint
```
GET /api/suppliers
```

### Authorization
- **Required**: `INVENTORY:READ`

### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | Integer | No | 0 | Số trang (bắt đầu từ 0) |
| `size` | Integer | No | 20 | Số bản ghi mỗi trang |
| `keyword` | String | No | - | Từ khóa tìm kiếm (tên, email, phone) |
| `includeDeleted` | Boolean | No | false | Bao gồm nhà cung cấp đã xóa mềm |

### Response

#### Success (200 OK)
```json
{
  "content": [
    {
      "supplierId": 1,
      "name": "TechBox Supplier",
      "phone": "0123456789",
      "email": "supplier@techbox.com",
      "address": "123 Supplier Street, City",
      "taxCode": "1234567890",
      "createdAt": "2025-11-15T10:00:00",
      "updatedAt": "2025-11-15T10:00:00",
      "deletedAt": null,
      "deleted": false
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "empty": false
}
```

## 2. Lấy thông tin nhà cung cấp theo ID

### Endpoint
```
GET /api/suppliers/{supplierId}
```

### Authorization
- **Required**: `INVENTORY:READ`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `supplierId` | Integer | Yes | ID của nhà cung cấp |

### Response

#### Success (200 OK)
```json
{
  "supplierId": 1,
  "name": "TechBox Supplier",
  "phone": "0123456789",
  "email": "supplier@techbox.com",
  "address": "123 Supplier Street, City",
  "taxCode": "1234567890",
  "createdAt": "2025-11-15T10:00:00",
  "updatedAt": "2025-11-15T10:00:00",
  "deletedAt": null,
  "deleted": false
}
```

#### Error (404 Not Found)
```json
{
  "timestamp": "2025-11-15T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Supplier not found with id: 999",
  "path": "/api/suppliers/999"
}
```

## 3. Tạo nhà cung cấp mới

### Endpoint
```
POST /api/suppliers
```

### Authorization
- **Required**: `INVENTORY:WRITE`

### Request Body
```json
{
  "name": "TechBox Supplier",
  "phone": "0123456789",
  "email": "supplier@techbox.com",
  "address": "123 Supplier Street, City",
  "taxCode": "1234567890"
}
```

### Validation Rules
| Field | Type | Required | Max Length | Description |
|-------|------|----------|------------|-------------|
| `name` | String | Yes | 255 | Tên nhà cung cấp |
| `phone` | String | No | 50 | Số điện thoại |
| `email` | String | No | 100 | Email (phải đúng định dạng) |
| `address` | String | No | 255 | Địa chỉ |
| `taxCode` | String | No | 50 | Mã số thuế |

### Response

#### Success (201 Created)
```json
{
  "supplierId": 1,
  "name": "TechBox Supplier",
  "phone": "0123456789",
  "email": "supplier@techbox.com",
  "address": "123 Supplier Street, City",
  "taxCode": "1234567890",
  "createdAt": "2025-11-15T10:00:00",
  "updatedAt": "2025-11-15T10:00:00",
  "deletedAt": null,
  "deleted": false
}
```

#### Error (400 Bad Request) - Validation Error
```json
{
  "timestamp": "2025-11-15T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "name",
      "message": "Supplier name is required"
    },
    {
      "field": "email",
      "message": "Invalid email format"
    }
  ],
  "path": "/api/suppliers"
}
```

## 4. Cập nhật nhà cung cấp

### Endpoint
```
PUT /api/suppliers/{supplierId}
```

### Authorization
- **Required**: `INVENTORY:UPDATE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `supplierId` | Integer | Yes | ID của nhà cung cấp cần cập nhật |

### Request Body
```json
{
  "name": "Updated TechBox Supplier",
  "phone": "0987654321",
  "email": "updated@techbox.com",
  "address": "456 Updated Street, City",
  "taxCode": "0987654321"
}
```

### Validation Rules
- Tất cả các trường đều optional
- Validation rules giống như tạo mới (xem phần 3)

### Response

#### Success (200 OK)
```json
{
  "supplierId": 1,
  "name": "Updated TechBox Supplier",
  "phone": "0987654321",
  "email": "updated@techbox.com",
  "address": "456 Updated Street, City",
  "taxCode": "0987654321",
  "createdAt": "2025-11-15T10:00:00",
  "updatedAt": "2025-11-15T11:00:00",
  "deletedAt": null,
  "deleted": false
}
```

#### Error (404 Not Found)
```json
{
  "timestamp": "2025-11-15T11:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Supplier not found with id: 999",
  "path": "/api/suppliers/999"
}
```

## 5. Xóa nhà cung cấp (Soft Delete)

### Endpoint
```
DELETE /api/suppliers/{supplierId}
```

### Authorization
- **Required**: `INVENTORY:DELETE`

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `supplierId` | Integer | Yes | ID của nhà cung cấp cần xóa |

### Response

#### Success (204 No Content)
- Không có response body

#### Error (404 Not Found)
```json
{
  "timestamp": "2025-11-15T11:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Supplier not found with id: 999",
  "path": "/api/suppliers/999"
}
```

## 6. Khôi phục nhà cung cấp đã xóa

### Endpoint
```
POST /api/suppliers/{supplierId}/restore
```

### Authorization
- **Required**: `INVENTORY:UPDATE` (hoặc có thể cần quyền riêng)

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `supplierId` | Integer | Yes | ID của nhà cung cấp cần khôi phục |

### Response

#### Success (200 OK)
```json
{
  "supplierId": 1,
  "name": "TechBox Supplier",
  "phone": "0123456789",
  "email": "supplier@techbox.com",
  "address": "123 Supplier Street, City",
  "taxCode": "1234567890",
  "createdAt": "2025-11-15T10:00:00",
  "updatedAt": "2025-11-15T11:00:00",
  "deletedAt": null,
  "deleted": false
}
```

#### Error (404 Not Found)
```json
{
  "timestamp": "2025-11-15T11:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Supplier not found with id: 999",
  "path": "/api/suppliers/999/restore"
}
```

## Error Codes
| Status Code | Description |
|-------------|-------------|
| 200 | Thành công |
| 201 | Tạo thành công |
| 204 | Xóa thành công |
| 400 | Dữ liệu đầu vào không hợp lệ |
| 401 | Chưa xác thực |
| 403 | Không có quyền truy cập |
| 404 | Không tìm thấy tài nguyên |
| 500 | Lỗi máy chủ nội bộ |

## Notes
- Tất cả các API đều yêu cầu JWT token trong header `Authorization: Bearer <token>`
- Các trường timestamp được trả về theo định dạng ISO 8601
- Soft delete: Khi xóa, bản ghi vẫn tồn tại trong database nhưng được đánh dấu `deleted = true`
- Pagination: Sử dụng Spring Boot Pageable, sort mặc định theo `createdAt DESC`
- Search: Tìm kiếm theo `name`, `email`, `phone` (case-insensitive)</content>
<parameter name="filePath">d:\Study_space\Ki7\PBL6\src\techbox_storebackend\SUPPLIER_API_DOCUMENTATION.md