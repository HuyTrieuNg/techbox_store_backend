# API Documentation - TechBox Store

## Tổng quan
Tài liệu này mô tả các API endpoints cho việc quản lý Categories (Danh mục) và Brands (Thương hiệu) trong hệ thống TechBox Store theo thứ tự CRUD operations.

**Base URL:** `http://localhost:8080`

---

## 📂 Categories API

### 1. CREATE - Tạo danh mục mới

#### 1.1. Tạo danh mục gốc (không có parent)
**POST** `/api/categories`

**Request Body:**
```json
{
  "name": "Electronics"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Electronics"}'
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Electronics",
  "parentCategoryId": null,
  "parentCategoryName": null,
  "createdAt": "2025-09-22T10:00:00",
  "updatedAt": "2025-09-22T10:00:00",
  "childCategories": []
}
```

#### 1.2. Tạo danh mục con (có parent)
**POST** `/api/categories`

**Request Body:**
```json
{
  "name": "Smartphones",
  "parentCategoryId": 1
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Smartphones", "parentCategoryId": 1}'
```

**Response:** `201 Created`
```json
{
  "id": 2,
  "name": "Smartphones",
  "parentCategoryId": 1,
  "parentCategoryName": "Electronics",
  "createdAt": "2025-09-22T10:01:00",
  "updatedAt": "2025-09-22T10:01:00",
  "childCategories": []
}
```

**Validation Errors:**
- Tên trống: `400 Bad Request`
- Tên quá dài (>255 ký tự): `400 Bad Request`
- Tên đã tồn tại: `400 Bad Request`
- Parent category không tồn tại: `400 Bad Request`

### 2. READ - Đọc danh mục

#### 2.1. Lấy tất cả danh mục
**GET** `/api/categories`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/categories
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Electronics",
    "parentCategoryId": null,
    "parentCategoryName": null,
    "createdAt": "2025-09-22T10:00:00",
    "updatedAt": "2025-09-22T10:00:00",
    "childCategories": [
      {
        "id": 2,
        "name": "Smartphones",
        "parentCategoryId": 1,
        "parentCategoryName": "Electronics",
        "createdAt": "2025-09-22T10:01:00",
        "updatedAt": "2025-09-22T10:01:00",
        "childCategories": []
      }
    ]
  }
]
```

#### 2.2. Lấy danh mục theo ID
**GET** `/api/categories/{id}`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/categories/1
```

**Response:**
```json
{
  "id": 1,
  "name": "Electronics",
  "parentCategoryId": null,
  "parentCategoryName": null,
  "createdAt": "2025-09-22T10:00:00",
  "updatedAt": "2025-09-22T10:00:00",
  "childCategories": []
}
```

**Response khi không tìm thấy:** `404 Not Found`

#### 2.3. Lấy danh mục gốc (không có parent)
**GET** `/api/categories/root`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/categories/root
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Electronics",
    "parentCategoryId": null,
    "parentCategoryName": null,
    "createdAt": "2025-09-22T10:00:00",
    "updatedAt": "2025-09-22T10:00:00",
    "childCategories": []
  },
  {
    "id": 3,
    "name": "Clothing",
    "parentCategoryId": null,
    "parentCategoryName": null,
    "createdAt": "2025-09-22T10:02:00",
    "updatedAt": "2025-09-22T10:02:00",
    "childCategories": []
  }
]
```

#### 2.4. Lấy danh mục con
**GET** `/api/categories/{parentId}/children`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/categories/1/children
```

**Response:**
```json
[
  {
    "id": 2,
    "name": "Smartphones",
    "parentCategoryId": 1,
    "parentCategoryName": "Electronics",
    "createdAt": "2025-09-22T10:01:00",
    "updatedAt": "2025-09-22T10:01:00",
    "childCategories": []
  },
  {
    "id": 4,
    "name": "Laptops",
    "parentCategoryId": 1,
    "parentCategoryName": "Electronics",
    "createdAt": "2025-09-22T10:03:00",
    "updatedAt": "2025-09-22T10:03:00",
    "childCategories": []
  }
]
```

### 3. UPDATE - Cập nhật danh mục

#### 3.1. Cập nhật danh mục gốc (không thay đổi parent)
**PUT** `/api/categories/{id}`

**Request Body:**
```json
{
  "name": "Consumer Electronics"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/categories/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Consumer Electronics"}'
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Consumer Electronics",
  "parentCategoryId": null,
  "parentCategoryName": null,
  "createdAt": "2025-09-22T10:00:00",
  "updatedAt": "2025-09-22T11:30:00",
  "childCategories": []
}
```

#### 3.2. Cập nhật danh mục con (có parent)
**PUT** `/api/categories/{id}`

**Request Body:**
```json
{
  "name": "Gaming Laptops",
  "parentCategoryId": 1
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/categories/4 \
  -H "Content-Type: application/json" \
  -d '{"name": "Gaming Laptops", "parentCategoryId": 1}'
```

**Response:** `200 OK`
```json
{
  "id": 4,
  "name": "Gaming Laptops",
  "parentCategoryId": 1,
  "parentCategoryName": "Consumer Electronics",
  "createdAt": "2025-09-22T10:03:00",
  "updatedAt": "2025-09-22T11:30:00",
  "childCategories": []
}
```

**Validation Errors:**
- Category không tồn tại: `400 Bad Request`
- Tên đã tồn tại: `400 Bad Request`
- Circular reference (category làm parent của chính nó): `400 Bad Request`

### 4. DELETE - Xóa danh mục
**DELETE** `/api/categories/{id}`

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/categories/2
```

**Response:** `204 No Content`

**Errors:**
- Category không tồn tại: `400 Bad Request`
- Category có danh mục con: `400 Bad Request`

### 5. UTILITY - Tiện ích

#### 5.1. Kiểm tra tên danh mục tồn tại
**GET** `/api/categories/exists?name={categoryName}`

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/categories/exists?name=Electronics"
```

**Response:**
```json
true
```

---

## 🏷️ Brands API

### 1. CREATE - Tạo thương hiệu mới
**POST** `/api/brands`

**Request Body:**
```json
{
  "name": "Apple"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/brands \
  -H "Content-Type: application/json" \
  -d '{"name": "Apple"}'
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Apple",
  "createdAt": "2025-09-22T10:00:00",
  "updatedAt": "2025-09-22T10:00:00"
}
```

**Validation Errors:**
- Tên trống: `400 Bad Request`
- Tên quá dài (>255 ký tự): `400 Bad Request`
- Tên đã tồn tại: `400 Bad Request`

### 2. READ - Đọc thương hiệu

#### 2.1. Lấy tất cả thương hiệu
**GET** `/api/brands`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/brands
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Apple",
    "createdAt": "2025-09-22T10:00:00",
    "updatedAt": "2025-09-22T10:00:00"
  },
  {
    "id": 2,
    "name": "Samsung",
    "createdAt": "2025-09-22T10:01:00",
    "updatedAt": "2025-09-22T10:01:00"
  }
]
```

#### 2.2. Lấy thương hiệu theo ID
**GET** `/api/brands/{id}`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/brands/1
```

**Response:**
```json
{
  "id": 1,
  "name": "Apple",
  "createdAt": "2025-09-22T10:00:00",
  "updatedAt": "2025-09-22T10:00:00"
}
```

**Response khi không tìm thấy:** `404 Not Found`

### 3. UPDATE - Cập nhật thương hiệu
**PUT** `/api/brands/{id}`

**Request Body:**
```json
{
  "name": "Apple Inc."
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/brands/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Apple Inc."}'
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Apple Inc.",
  "createdAt": "2025-09-22T10:00:00",
  "updatedAt": "2025-09-22T11:30:00"
}
```

**Validation Errors:**
- Brand không tồn tại: `400 Bad Request`
- Tên đã tồn tại: `400 Bad Request`

### 4. DELETE - Xóa thương hiệu
**DELETE** `/api/brands/{id}`

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/brands/1
```

**Response:** `204 No Content`

**Errors:**
- Brand không tồn tại: `400 Bad Request`

### 5. UTILITY - Tiện ích

#### 5.1. Kiểm tra tên thương hiệu tồn tại
**GET** `/api/brands/exists?name={brandName}`

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/brands/exists?name=Apple"
```

**Response:**
```json
true
```

---

## 🧪 Kịch bản test từng bước

### Scenario 1: Quản lý Categories có cấu trúc phân cấp

#### Bước 1: Tạo danh mục gốc
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Electronics"}'
```

#### Bước 2: Tạo danh mục con
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Smartphones", "parentCategoryId": 1}'

curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Laptops", "parentCategoryId": 1}'
```

#### Bước 3: Lấy tất cả danh mục
```bash
curl -X GET http://localhost:8080/api/categories
```

#### Bước 4: Lấy danh mục theo ID
```bash
curl -X GET http://localhost:8080/api/categories/1
```

#### Bước 5: Lấy danh mục con
```bash
curl -X GET http://localhost:8080/api/categories/1/children
```

#### Bước 6: Cập nhật danh mục
```bash
curl -X PUT http://localhost:8080/api/categories/2 \
  -H "Content-Type: application/json" \
  -d '{"name": "Mobile Phones", "parentCategoryId": 1}'
```

#### Bước 7: Xóa danh mục (chỉ danh mục không có con) 
```bash
curl -X DELETE http://localhost:8080/api/categories/2
```

### Scenario 2: Quản lý Brands

#### Bước 1: Tạo thương hiệu
```bash
curl -X POST http://localhost:8080/api/brands \
  -H "Content-Type: application/json" \
  -d '{"name": "Apple"}'

curl -X POST http://localhost:8080/api/brands \
  -H "Content-Type: application/json" \
  -d '{"name": "Samsung"}'
```

#### Bước 2: Lấy tất cả thương hiệu
```bash
curl -X GET http://localhost:8080/api/brands
```

#### Bước 3: Lấy thương hiệu theo ID
```bash
curl -X GET http://localhost:8080/api/brands/1
```

#### Bước 4: Cập nhật thương hiệu
```bash
curl -X PUT http://localhost:8080/api/brands/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Apple Inc."}'
```

#### Bước 5: Xóa thương hiệu
```bash
curl -X DELETE http://localhost:8080/api/brands/2
```

---

## ⚠️ Lưu ý quan trọng

1. **Categories có cấu trúc phân cấp:** Danh mục có thể có danh mục cha và nhiều danh mục con
2. **Validation:** Tất cả các trường bắt buộc đều được validate
3. **Unique constraints:** Tên category và brand phải unique
4. **Cascading delete:** Không thể xóa category có danh mục con
5. **Timestamps:** Tự động tạo `createdAt` và `updatedAt`

## 🔍 Error Handling
Tất cả lỗi đều trả về format chuẩn với HTTP status code phù hợp và message mô tả chi tiết.