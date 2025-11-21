# Tài liệu API: Quản lý Kho (Stock Management)

## Tổng quan
Tài liệu này mô tả các API endpoint để quản lý kho hàng trong hệ thống Techbox Store, bao gồm nhập kho (Stock Import), xuất kho (Stock Export) và điều chỉnh kho (Stock Adjustment).

## Base URL
```
/api/stock-imports
/api/stock-exports
/api/stock-adjustments
```

## Authentication & Authorization
- **Authentication**: JWT Bearer token
- **Authorities**:
  - `INVENTORY:READ` - Đọc thông tin kho
  - `INVENTORY:WRITE` - Tạo phiếu nhập/xuất/điều chỉnh
  - `INVENTORY:REPORT` - Xem báo cáo kho

---

## 1. STOCK IMPORT (Nhập Kho)

### 1.1 Lấy danh sách phiếu nhập kho

#### Endpoint
```
GET /api/stock-imports
```

#### Authorization
- **Required**: `INVENTORY:READ`

#### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | Integer | No | 1 | Số trang (bắt đầu từ 1) |
| `size` | Integer | No | 20 | Số bản ghi mỗi trang |
| `fromDate` | Date | No | - | Ngày nhập từ (ISO format: YYYY-MM-DD) |
| `toDate` | Date | No | - | Ngày nhập đến (ISO format: YYYY-MM-DD) |
| `supplierId` | Integer | No | - | ID nhà cung cấp |
| `userId` | Integer | No | - | ID người dùng |
| `documentCode` | String | No | - | Mã phiếu nhập |

#### Response (200 OK)
```json
{
  "content": [
    {
      "id": 1,
      "documentCode": "IMP-2025-001",
      "userId": 1,
      "userName": "Admin User",
      "importDate": "2025-11-15T10:00:00",
      "supplierId": 1,
      "supplierName": "TechBox Supplier",
      "totalCostValue": 22500.00,
      "note": "Initial stock import",
      "createdAt": "2025-11-15T10:00:00",
      "totalItems": 1
    }
  ],
  "pageable": { ... },
  "totalPages": 1,
  "totalElements": 1
}
```

### 1.2 Lấy chi tiết phiếu nhập kho

#### Endpoint
```
GET /api/stock-imports/{id}
```

#### Authorization
- **Required**: `INVENTORY:READ`

#### Response (200 OK)
```json
{
  "id": 1,
  "documentCode": "IMP-2025-001",
  "userId": 1,
  "userName": "Admin User",
  "importDate": "2025-11-15T10:00:00",
  "supplierId": 1,
  "supplierName": "TechBox Supplier",
  "totalCostValue": 22500.00,
  "note": "Initial stock import",
  "createdAt": "2025-11-15T10:00:00",
  "items": [
    {
      "id": 1,
      "productVariationId": 1,
      "productName": "Laptop",
      "variationName": "Laptop Model A",
      "sku": "TB-LAP-001",
      "quantity": 50,
      "costPrice": 450.00,
      "totalValue": 22500.00
    }
  ]
}
```

### 1.3 Tạo phiếu nhập kho mới

#### Endpoint
```
POST /api/stock-imports
```

#### Authorization
- **Required**: `INVENTORY:WRITE`

#### Request Body
```json
{
  "supplierId": 1,
  "importDate": "2025-11-15T10:00:00",
  "note": "Stock import from supplier",
  "items": [
    {
      "productVariationId": 1,
      "quantity": 50,
      "costPrice": 450.00
    }
  ]
}
```

#### Validation Rules
- `supplierId`: Bắt buộc, phải tồn tại
- `items`: Ít nhất 1 item
- `quantity`: > 0
- `costPrice`: >= 0

### 1.4 Tìm phiếu nhập theo mã

#### Endpoint
```
GET /api/stock-imports/by-code/{documentCode}
```

#### Authorization
- **Required**: `INVENTORY:READ`

### 1.5 Báo cáo nhập kho

#### Endpoint
```
GET /api/stock-imports/report?fromDate=2025-01-01&toDate=2025-12-31&supplierId=1&groupBy=day
```

#### Authorization
- **Required**: `INVENTORY:REPORT`

#### Query Parameters
- `fromDate`, `toDate`: Khoảng thời gian
- `supplierId`: Lọc theo nhà cung cấp
- `groupBy`: `day`, `month`, `supplier`

#### Response (200 OK)
```json
{
  "totalDocuments": 5,
  "totalQuantity": 250,
  "totalValue": 112500.00,
  "details": [
    {
      "date": "2025-11-15",
      "supplierName": "TechBox Supplier",
      "quantity": 50,
      "value": 22500.00
    }
  ]
}
```

---

## 2. STOCK EXPORT (Xuất Kho)

### 2.1 Lấy danh sách phiếu xuất kho

#### Endpoint
```
GET /api/stock-exports
```

#### Authorization
- **Required**: `INVENTORY:READ`

#### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | Integer | No | 1 | Số trang |
| `size` | Integer | No | 20 | Số bản ghi mỗi trang |
| `fromDate` | Date | No | - | Ngày xuất từ |
| `toDate` | Date | No | - | Ngày xuất đến |
| `userId` | Integer | No | - | ID người dùng |
| `orderId` | Integer | No | - | ID đơn hàng |
| `documentCode` | String | No | - | Mã phiếu xuất |

### 2.2 Lấy chi tiết phiếu xuất kho

#### Endpoint
```
GET /api/stock-exports/{id}
```

### 2.3 Báo cáo xuất kho

#### Endpoint
```
GET /api/stock-exports/report?fromDate=2025-01-01&toDate=2025-12-31&groupBy=day
```

#### Query Parameters
- `groupBy`: `day`, `month`, `product`

---

## 3. STOCK ADJUSTMENT (Điều Chỉnh Kho)

### 3.1 Lấy danh sách phiếu điều chỉnh kho

#### Endpoint
```
GET /api/stock-adjustments
```

#### Authorization
- **Required**: `INVENTORY:READ`

#### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | Integer | No | 1 | Số trang |
| `size` | Integer | No | 20 | Số bản ghi mỗi trang |
| `fromDate` | Date | No | - | Ngày điều chỉnh từ |
| `toDate` | Date | No | - | Ngày điều chỉnh đến |
| `userId` | Integer | No | - | ID người dùng |
| `checkName` | String | No | - | Tên đợt kiểm kê |

### 3.2 Lấy chi tiết phiếu điều chỉnh kho

#### Endpoint
```
GET /api/stock-adjustments/{id}
```

### 3.3 Tạo phiếu điều chỉnh kho mới

#### Endpoint
```
POST /api/stock-adjustments
```

#### Authorization
- **Required**: `INVENTORY:WRITE`

#### Request Body
```json
{
  "checkName": "Monthly Stock Check",
  "adjustmentDate": "2025-11-15T10:00:00",
  "note": "Stock adjustment after inventory check",
  "items": [
    {
      "productVariationId": 1,
      "realQty": 95,
      "costPrice": 450.00
    }
  ]
}
```

#### Validation Rules
- `checkName`: Bắt buộc, max 255 ký tự
- `items`: Ít nhất 1 item
- `realQty`: >= 0
- `costPrice`: >= 0

#### Logic
- `systemQty`: Lấy từ stock hiện tại của sản phẩm
- `diffQty`: = realQty - systemQty
- Cập nhật stock: stock += diffQty

### 3.4 Cập nhật phiếu điều chỉnh kho

#### Endpoint
```
PUT /api/stock-adjustments/{id}
```

#### Authorization
- **Required**: `INVENTORY:WRITE`

#### Path Parameters
- `id`: ID của phiếu điều chỉnh kho

#### Request Body
```json
{
  "checkName": "Updated Monthly Stock Check",
  "adjustmentDate": "2025-11-15T11:00:00",
  "note": "Updated stock adjustment note",
  "items": [
    {
      "productVariationId": 1,
      "realQty": 100,
      "costPrice": 450.00
    }
  ]
}
```

#### Response (200 OK)
```json
{
  "id": 1,
  "userId": 1,
  "userName": "Admin User",
  "checkName": "Updated Monthly Stock Check",
  "adjustmentDate": "2025-11-15T11:00:00",
  "note": "Updated stock adjustment note",
  "createdAt": "2025-11-15T09:00:00",
  "totalItems": 1
}
```

### 3.5 Xóa phiếu điều chỉnh kho

#### Endpoint
```
DELETE /api/stock-adjustments/{id}
```

#### Authorization
- **Required**: `INVENTORY:DELETE`

#### Path Parameters
- `id`: ID của phiếu điều chỉnh kho

#### Response (204 No Content)
No response body - successful deletion
Xóa thành công, không có response body.

### 3.6 Báo cáo điều chỉnh kho

#### Endpoint
```
GET /api/stock-adjustments/report?fromDate=2025-01-01&toDate=2025-12-31
```

#### Authorization
- **Required**: `INVENTORY:REPORT`

#### Query Parameters
- `fromDate`: Ngày bắt đầu (tùy chọn)
- `toDate`: Ngày kết thúc (tùy chọn)

#### Response (200 OK)
```json
[
  {
    "id": 1,
    "userId": 1,
    "userName": "Admin User",
    "checkName": "Monthly Stock Check",
    "adjustmentDate": "2025-11-15T10:00:00",
    "note": "Stock adjustment after inventory check",
    "createdAt": "2025-11-15T09:00:00",
    "totalItems": 2
  }
]
```
```

---

## Error Codes
| Status Code | Description |
|-------------|-------------|
| 200 | Thành công |
| 201 | Tạo thành công |
| 400 | Dữ liệu đầu vào không hợp lệ |
| 401 | Chưa xác thực |
| 403 | Không có quyền truy cập |
| 404 | Không tìm thấy tài nguyên |
| 500 | Lỗi máy chủ nội bộ |

## Business Rules
1. **Stock Import**: Tăng stock_quantity và cập nhật avg_cost_price
2. **Stock Export**: Giảm stock_quantity (không được âm)
3. **Stock Adjustment**: Điều chỉnh stock dựa trên kiểm kê thực tế
4. **Validation**: Không cho phép stock âm trong các operation
5. **Audit**: Tất cả thay đổi đều được ghi lại với user ID và timestamp

## Notes
- Tất cả timestamp theo định dạng ISO 8601
- Pagination sử dụng 1-based indexing (page bắt đầu từ 1)
- Search case-insensitive
- Stock operations là transactional để đảm bảo data consistency</content>
<parameter name="filePath">d:\Study_space\Ki7\PBL6\src\techbox_storebackend\STOCK_API_DOCUMENTATION.md