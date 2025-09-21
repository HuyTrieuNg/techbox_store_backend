# API Documentation - TechBox Store

## Tổng quan
Tài liệu này mô tả các API endpoints cho việc quản lý Categories (Danh mục) và Brands (Thương hiệu) trong hệ thống TechBox Store.

**Base URL:** `http://localhost:8080`

---

## 📂 Categories API

### 1. Lấy tất cả danh mục
**GET** `/api/categories`

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

### 2. Lấy danh mục theo ID
**GET** `/api/categories/{id}`

**Ví dụ:** `GET /api/categories/1`

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

### 3. Lấy danh mục gốc (không có parent)
**GET** `/api/categories/root`

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

### 4. Lấy danh mục con
**GET** `/api/categories/{parentId}/children`

**Ví dụ:** `GET /api/categories/1/children`

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

### 5. Tạo danh mục mới
**POST** `/api/categories`

**Request Body:**
```json
{
  "name": "Gaming Laptops",
  "parentCategoryId": 4
}
```

**Response:** `201 Created`
```json
{
  "id": 5,
  "name": "Gaming Laptops",
  "parentCategoryId": 4,
  "parentCategoryName": "Laptops",
  "createdAt": "2025-09-22T11:00:00",
  "updatedAt": "2025-09-22T11:00:00",
  "childCategories": []
}
```

**Validation Errors:**
- Tên trống: `400 Bad Request`
- Tên quá dài (>255 ký tự): `400 Bad Request`
- Tên đã tồn tại: `400 Bad Request`
- Parent category không tồn tại: `400 Bad Request`

### 6. Cập nhật danh mục
**PUT** `/api/categories/{id}`

**Request Body:**
```json
{
  "name": "Gaming Laptops Updated",
  "parentCategoryId": 4
}
```

**Response:** `200 OK`
```json
{
  "id": 5,
  "name": "Gaming Laptops Updated",
  "parentCategoryId": 4,
  "parentCategoryName": "Laptops",
  "createdAt": "2025-09-22T11:00:00",
  "updatedAt": "2025-09-22T11:30:00",
  "childCategories": []
}
```

**Validation Errors:**
- Category không tồn tại: `400 Bad Request`
- Tên đã tồn tại: `400 Bad Request`
- Circular reference (category làm parent của chính nó): `400 Bad Request`

### 7. Xóa danh mục
**DELETE** `/api/categories/{id}`

**Response:** `204 No Content`

**Errors:**
- Category không tồn tại: `400 Bad Request`
- Category có danh mục con: `400 Bad Request`

### 8. Kiểm tra tên danh mục tồn tại
**GET** `/api/categories/exists?name={categoryName}`

**Ví dụ:** `GET /api/categories/exists?name=Electronics`

**Response:**
```json
true
```

---

## 🏷️ Brands API

### 1. Lấy tất cả thương hiệu
**GET** `/api/brands`

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

### 2. Lấy thương hiệu theo ID
**GET** `/api/brands/{id}`

**Ví dụ:** `GET /api/brands/1`

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

### 3. Tạo thương hiệu mới
**POST** `/api/brands`

**Request Body:**
```json
{
  "name": "Dell"
}
```

**Response:** `201 Created`
```json
{
  "id": 3,
  "name": "Dell",
  "createdAt": "2025-09-22T11:00:00",
  "updatedAt": "2025-09-22T11:00:00"
}
```

**Validation Errors:**
- Tên trống: `400 Bad Request`
- Tên quá dài (>255 ký tự): `400 Bad Request`
- Tên đã tồn tại: `400 Bad Request`

### 4. Cập nhật thương hiệu
**PUT** `/api/brands/{id}`

**Request Body:**
```json
{
  "name": "Dell Technologies"
}
```

**Response:** `200 OK`
```json
{
  "id": 3,
  "name": "Dell Technologies",
  "createdAt": "2025-09-22T11:00:00",
  "updatedAt": "2025-09-22T11:30:00"
}
```

**Validation Errors:**
- Brand không tồn tại: `400 Bad Request`
- Tên đã tồn tại: `400 Bad Request`

### 5. Xóa thương hiệu
**DELETE** `/api/brands/{id}`

**Response:** `204 No Content`

**Errors:**
- Brand không tồn tại: `400 Bad Request`

### 6. Kiểm tra tên thương hiệu tồn tại
**GET** `/api/brands/exists?name={brandName}`

**Ví dụ:** `GET /api/brands/exists?name=Apple`

**Response:**
```json
true
```

---

## 🧪 Ví dụ sử dụng với cURL

### Tạo danh mục gốc:
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics"
  }'
```

### Tạo danh mục con:
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smartphones",
    "parentCategoryId": 1
  }'
```

### Tạo thương hiệu:
```bash
curl -X POST http://localhost:8080/api/brands \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Apple"
  }'
```

### Lấy tất cả danh mục:
```bash
curl -X GET http://localhost:8080/api/categories
```

### Cập nhật thương hiệu:
```bash
curl -X PUT http://localhost:8080/api/brands/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Apple Inc."
  }'
```

### Xóa danh mục:
```bash
curl -X DELETE http://localhost:8080/api/categories/1
```

---

## 🧪 Ví dụ sử dụng với JavaScript (Fetch API)

### Lấy tất cả danh mục:
```javascript
fetch('http://localhost:8080/api/categories')
  .then(response => response.json())
  .then(data => console.log(data));
```

### Tạo danh mục mới:
```javascript
fetch('http://localhost:8080/api/categories', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: 'New Category',
    parentCategoryId: 1
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

### Cập nhật thương hiệu:
```javascript
fetch('http://localhost:8080/api/brands/1', {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: 'Updated Brand Name'
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

---

## 📝 Ghi chú quan trọng

1. **Categories có cấu trúc phân cấp:** Danh mục có thể có danh mục cha và nhiều danh mục con
2. **Validation:** Tất cả các trường bắt buộc đều được validate
3. **Unique constraints:** Tên category và brand phải unique
4. **Soft delete:** Có thể implement soft delete trong tương lai
5. **Timestamps:** Tự động tạo `createdAt` và `updatedAt`

## 🔒 Authentication & Authorization
Hiện tại API chưa có authentication. Trong tương lai sẽ thêm JWT authentication cho các endpoint này.

## 🐛 Error Handling
Tất cả lỗi đều trả về format chuẩn với HTTP status code phù hợp và message mô tả chi tiết.