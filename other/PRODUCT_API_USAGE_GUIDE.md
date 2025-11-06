# Hướng Dẫn Sử Dụng Product API

Tài liệu hướng dẫn chi tiết cách sử dụng các API liên quan đến Product, Product Variation, Category, Brand và Attribute.

---

## 📋 Mục Lục
1. [Product APIs](#product-apis)
   - [Search & Filter Products](#1-search--filter-products)
   - [Get All Products](#2-get-all-products)
   - [Get Product Detail](#3-get-product-detail)
   - [Update Product](#4-update-product)
   - [Delete Product (Soft Delete)](#5-delete-product-soft-delete)
   - [Restore Product](#6-restore-product)
   - [Get Deleted Products](#7-get-deleted-products-admin)
2. [Product Variation APIs](#product-variation-apis)
   - [Get Variation Detail](#1-get-variation-detail)
   - [Update Variation](#2-update-variation)
   - [Delete Variation (Soft Delete)](#3-delete-variation-soft-delete)
   - [Restore Variation](#4-restore-variation)
3. [Category APIs](#category-apis)
   - [Get All Categories](#1-get-all-categories)
   - [Get Category Detail](#2-get-category-detail)
   - [Get Root Categories](#3-get-root-categories)
   - [Get Child Categories](#4-get-child-categories)
4. [Brand APIs](#brand-apis)
   - [Get All Brands](#1-get-all-brands)
   - [Get Brand Detail](#2-get-brand-detail)
5. [Attribute APIs](#attribute-apis)
   - [Get All Attributes](#1-get-all-attributes)
   - [Search Attributes](#2-search-attributes)

---

## Product APIs

### 1. Search & Filter Products

Tìm kiếm và lọc sản phẩm với nhiều tiêu chí.

**Endpoint:** `GET /api/products/search`

**Authentication:** Không bắt buộc (Optional - nếu có JWT token sẽ hiện thêm trạng thái wishlist)

**Query Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `name` | String | No | Tìm kiếm theo tên sản phẩm (tìm kiếm gần đúng) |
| `brandId` | Integer | No | Lọc theo ID thương hiệu |
| `categoryId` | Integer | No | Lọc theo ID danh mục (bao gồm cả danh mục con) |
| `categoryIds` | List<Integer> | No | Lọc theo nhiều ID danh mục |
| `attributes` | List<String> | No | Lọc theo thuộc tính (format: "attributeName:value") |
| `minPrice` | BigDecimal | No | Giá tối thiểu (VNĐ) |
| `maxPrice` | BigDecimal | No | Giá tối đa (VNĐ) |
| `minRating` | Double | No | Điểm đánh giá tối thiểu (1.0 - 5.0) |
| `sortBy` | String | No | Trường sắp xếp (mặc định: "id") |
| `sortDirection` | String | No | Hướng sắp xếp: "ASC" hoặc "DESC" (mặc định: "ASC") |
| `page` | Integer | No | Số trang (bắt đầu từ 0, mặc định: 0) |
| `size` | Integer | No | Số phần tử/trang (mặc định: 20) |

**Ví dụ Request:**

```bash
# Tìm kiếm sản phẩm có tên chứa "iPhone"
GET /api/products/search?name=iPhone

# Lọc theo thương hiệu và khoảng giá
GET /api/products/search?brandId=1&minPrice=10000000&maxPrice=30000000

# Lọc theo nhiều danh mục và sắp xếp theo giá giảm dần
GET /api/products/search?categoryIds=1,2,3&sortBy=displaySalePrice&sortDirection=DESC

# Lọc theo thuộc tính (VD: Màu đen, RAM 8GB)
GET /api/products/search?attributes=Màu sắc:Đen&attributes=RAM:8GB

# Lọc theo rating và phân trang
GET /api/products/search?minRating=4.0&page=0&size=20
```

**Response:** [ProductListResponse](#productlistresponse) (Paginated)

---

### 2. Get All Products

Lấy tất cả sản phẩm active với phân trang.

**Endpoint:** `GET /api/products`

**Authentication:** Không bắt buộc

**Query Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `page` | Integer | No | Số trang (mặc định: 0) |
| `size` | Integer | No | Số phần tử/trang (mặc định: 20) |
| `sortBy` | String | No | Trường sắp xếp (mặc định: "id") |
| `sortDirection` | String | No | Hướng sắp xếp: "ASC" hoặc "DESC" (mặc định: "ASC") |

**Ví dụ Request:**

```bash
# Lấy trang đầu tiên
GET /api/products

# Lấy trang thứ 2, mỗi trang 10 sản phẩm
GET /api/products?page=1&size=10

# Sắp xếp theo tên A-Z
GET /api/products?sortBy=name&sortDirection=ASC
```

**Response:** [ProductListResponse](#productlistresponse) (Paginated)

---

### 3. Get Product Detail

Lấy thông tin chi tiết của một sản phẩm bao gồm variations, attributes, promotions.

**Endpoint:** `GET /api/products/{id}`

**Authentication:** Không bắt buộc

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của sản phẩm |

**Ví dụ Request:**

```bash
GET /api/products/1
```

**Response:** [ProductDetailResponse](#productdetailresponse)

```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max",
  "description": "Mô tả chi tiết về sản phẩm...",
  "categoryId": 5,
  "categoryName": "Điện thoại",
  "brandId": 2,
  "brandName": "Apple",
  "imageUrl": "https://cloudinary.com/...",
  "imagePublicId": "products/iphone15_abc123",
  "averageRating": 4.5,
  "totalRatings": 120,
  "displayOriginalPrice": 29990000,
  "displaySalePrice": 26990000,
  "discountType": "PERCENTAGE",
  "discountValue": 10,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-10-20T14:20:00",
  "inWishlist": false,
  "attributes": [
    {
      "id": 1,
      "name": "Chip",
      "value": "A17 Pro"
    }
  ],
  "variations": [
    {
      "id": 10,
      "variationName": "256GB - Titan Tự nhiên",
      "price": 29990000,
      "salePrice": 26990000,
      "sku": "IP15PM-256-TN",
      "availableQuantity": 50,
      "warrantyMonths": 12,
      "discountType": "PERCENTAGE",
      "discountValue": 10,
      "images": [...],
      "attributes": [...]
    }
  ]
}
```

---

### 4. Update Product

Cập nhật thông tin sản phẩm.

**Endpoint:** `PUT /api/products/{id}`

**Authentication:** Required (Bearer Token) - Permission: `PRODUCT:UPDATE`

**Content-Type:** `multipart/form-data`

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của sản phẩm cần cập nhật |

**Form Data:**

| Field | Type | Required | Mô tả |
|-------|------|----------|-------|
| `name` | String | No | Tên sản phẩm mới |
| `description` | String | No | Mô tả sản phẩm mới |
| `categoryId` | Integer | No | ID danh mục mới |
| `brandId` | Integer | No | ID thương hiệu mới |
| `image` | File | No | Ảnh sản phẩm mới (tự động xóa ảnh cũ) |
| `deleteImage` | Boolean | No | Xóa ảnh hiện tại (mặc định: false) |

**Ví dụ Request:**

```bash
# Cập nhật tên và mô tả
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "name=iPhone 15 Pro Max 2024" \
  -F "description=Mô tả mới..."

# Cập nhật ảnh sản phẩm
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "image=@/path/to/new-image.jpg"

# Xóa ảnh sản phẩm
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "deleteImage=true"

# Cập nhật danh mục và thương hiệu
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "categoryId=5" \
  -F "brandId=2"
```

**Response:** `ProductResponse`

```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max 2024",
  "description": "Mô tả mới...",
  "categoryId": 5,
  "categoryName": "Điện thoại",
  "brandId": 2,
  "brandName": "Apple",
  "imageUrl": "https://cloudinary.com/new-image.jpg",
  "imagePublicId": "products/new-image_xyz",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-11-01T15:20:00"
}
```

**Lưu ý:**
- Chỉ cần gửi các trường muốn cập nhật
- Khi upload ảnh mới, ảnh cũ sẽ tự động bị xóa khỏi Cloudinary
- Soft delete sẽ không xóa ảnh khỏi Cloudinary

---

### 5. Delete Product (Soft Delete)

Xóa sản phẩm (soft delete - đánh dấu là đã xóa, không xóa vật lý).

**Endpoint:** `DELETE /api/products/{id}`

**Authentication:** Required (Bearer Token) - Permission: `PRODUCT:DELETE`

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của sản phẩm cần xóa |

**Ví dụ Request:**

```bash
DELETE /api/products/1
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:** `204 No Content`

**Lưu ý:**
- Đây là soft delete, sản phẩm chỉ bị đánh dấu `deletedAt` chứ không xóa khỏi database
- Ảnh không bị xóa khỏi Cloudinary
- Có thể khôi phục lại sau này bằng API Restore

---

### 6. Restore Product

Khôi phục sản phẩm đã bị soft delete.

**Endpoint:** `PATCH /api/products/{id}/restore`

**Authentication:** Required (Bearer Token) - Permission: `PRODUCT:UPDATE`

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của sản phẩm cần khôi phục |

**Ví dụ Request:**

```bash
PATCH /api/products/1/restore
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:** `200 OK`

---

### 7. Get Deleted Products (Admin)

Lấy danh sách các sản phẩm đã bị soft delete (chỉ dành cho Admin).

**Endpoint:** `GET /api/products/admin/deleted`

**Authentication:** Required (Bearer Token) - Permission: `PRODUCT:READ`

**Query Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `page` | Integer | No | Số trang (mặc định: 0) |
| `size` | Integer | No | Số phần tử/trang (mặc định: 20) |
| `sortBy` | String | No | Trường sắp xếp (mặc định: "deletedAt") |
| `sortDirection` | String | No | Hướng sắp xếp (mặc định: "DESC") |

**Ví dụ Request:**

```bash
GET /api/products/admin/deleted?page=0&size=20
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:** [ProductListResponse](#productlistresponse) (Paginated)

---

## Product Variation APIs

### 1. Get Variation Detail

Lấy thông tin chi tiết của một biến thể sản phẩm.

**Endpoint:** `GET /api/product-variations/{id}`

**Authentication:** Không bắt buộc

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của biến thể |

**Query Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `includeDeleted` | Boolean | No | Bao gồm biến thể đã xóa (mặc định: false) |

**Ví dụ Request:**

```bash
# Lấy biến thể active
GET /api/product-variations/10

# Lấy biến thể kể cả đã xóa
GET /api/product-variations/10?includeDeleted=true
```

**Response:** `ProductVariationResponse`

```json
{
  "id": 10,
  "variationName": "256GB - Titan Tự nhiên",
  "productId": 1,
  "productName": "iPhone 15 Pro Max",
  "price": 29990000,
  "salePrice": 26990000,
  "sku": "IP15PM-256-TN",
  "stockQuantity": 100,
  "reservedQuantity": 50,
  "availableQuantity": 50,
  "warrantyMonths": 12,
  "discountType": "PERCENTAGE",
  "discountValue": 10,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-10-20T14:20:00",
  "deletedAt": null,
  "images": [
    {
      "id": 1,
      "imageUrl": "https://cloudinary.com/variation1.jpg",
      "imagePublicId": "variations/var1_abc"
    }
  ],
  "attributes": [
    {
      "id": 3,
      "name": "Dung lượng",
      "value": "256GB"
    },
    {
      "id": 4,
      "name": "Màu sắc",
      "value": "Titan Tự nhiên"
    }
  ]
}
```

---

### 2. Update Variation

Cập nhật thông tin biến thể sản phẩm.

**Endpoint:** `PUT /api/product-variations/{id}`

**Authentication:** Required (Bearer Token) - Permission: `PRODUCT:UPDATE`

**Content-Type:** `multipart/form-data`

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của biến thể cần cập nhật |

**Form Data:**

| Field | Type | Required | Mô tả |
|-------|------|----------|-------|
| `variationName` | String | No | Tên biến thể mới |
| `price` | BigDecimal | No | Giá mới (VNĐ) |
| `sku` | String | No | Mã SKU mới |
| `stockQuantity` | Integer | No | Số lượng tồn kho mới |
| `newImages` | File[] | No | Các ảnh mới cần thêm |
| `deleteImageIds` | List<String> | No | Danh sách Public ID của ảnh cần xóa |

**Ví dụ Request:**

```bash
# Cập nhật giá và tên biến thể
curl -X PUT http://localhost:8080/api/product-variations/10 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "variationName=512GB - Titan Đen" \
  -F "price=35990000"

# Thêm ảnh mới và xóa ảnh cũ
curl -X PUT http://localhost:8080/api/product-variations/10 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "newImages=@/path/to/image1.jpg" \
  -F "newImages=@/path/to/image2.jpg" \
  -F "deleteImageIds=variations/old_image1_abc" \
  -F "deleteImageIds=variations/old_image2_xyz"

# Cập nhật SKU và số lượng tồn kho
curl -X PUT http://localhost:8080/api/product-variations/10 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "sku=IP15PM-512-BK" \
  -F "stockQuantity=200"
```

**Response:** `ProductVariationResponse`

**Lưu ý:**
- Có thể cập nhật nhiều trường cùng lúc
- Ảnh cũ sẽ bị xóa khỏi Cloudinary khi `deleteImageIds` được chỉ định
- Có thể thêm nhiều ảnh mới cùng lúc

---

### 3. Delete Variation (Soft Delete)

Xóa biến thể sản phẩm (soft delete).

**Endpoint:** `DELETE /api/product-variations/{id}`

**Authentication:** Required (Bearer Token) - Permission: `PRODUCT:DELETE`

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của biến thể cần xóa |

**Ví dụ Request:**

```bash
DELETE /api/product-variations/10
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:** `204 No Content`

**Lưu ý:**
- Soft delete - chỉ đánh dấu `deletedAt`
- Ảnh không bị xóa khỏi Cloudinary
- Có thể khôi phục lại sau

---

### 4. Restore Variation

Khôi phục biến thể đã bị soft delete.

**Endpoint:** `PATCH /api/product-variations/{id}/restore`

**Authentication:** Required (Bearer Token) - Permission: `PRODUCT:UPDATE`

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của biến thể cần khôi phục |

**Ví dụ Request:**

```bash
PATCH /api/product-variations/10/restore
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:** `200 OK`

---

## Category APIs

### 1. Get All Categories

Lấy tất cả danh mục (có cấu trúc phân cấp đệ quy).

**Endpoint:** `GET /api/categories`

**Authentication:** Không bắt buộc

**Ví dụ Request:**

```bash
GET /api/categories
```

**Response:** `List<CategoryResponse>`

```json
[
  {
    "id": 1,
    "name": "Điện thoại",
    "parentCategoryId": null,
    "parentCategoryName": null,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "childCategories": [
      {
        "id": 5,
        "name": "iPhone",
        "parentCategoryId": 1,
        "parentCategoryName": "Điện thoại",
        "createdAt": "2024-01-01T00:00:00",
        "updatedAt": "2024-01-01T00:00:00",
        "childCategories": []
      }
    ]
  }
]
```

---

### 2. Get Category Detail

Lấy thông tin chi tiết của một danh mục.

**Endpoint:** `GET /api/categories/{id}`

**Authentication:** Không bắt buộc

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của danh mục |

**Ví dụ Request:**

```bash
GET /api/categories/1
```

**Response:** `CategoryResponse`

---

### 3. Get Root Categories

Lấy danh sách các danh mục gốc (không có danh mục cha).

**Endpoint:** `GET /api/categories/root`

**Authentication:** Không bắt buộc

**Ví dụ Request:**

```bash
GET /api/categories/root
```

**Response:** `List<CategoryResponse>`

```json
[
  {
    "id": 1,
    "name": "Điện thoại",
    "parentCategoryId": null,
    "parentCategoryName": null,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "childCategories": [...]
  },
  {
    "id": 2,
    "name": "Laptop",
    "parentCategoryId": null,
    "parentCategoryName": null,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "childCategories": [...]
  }
]
```

---

### 4. Get Child Categories

Lấy danh sách các danh mục con của một danh mục cha.

**Endpoint:** `GET /api/categories/{parentId}/children`

**Authentication:** Không bắt buộc

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `parentId` | Integer | Yes | ID của danh mục cha |

**Ví dụ Request:**

```bash
GET /api/categories/1/children
```

**Response:** `List<CategoryResponse>`

```json
[
  {
    "id": 5,
    "name": "iPhone",
    "parentCategoryId": 1,
    "parentCategoryName": "Điện thoại",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "childCategories": []
  },
  {
    "id": 6,
    "name": "Samsung",
    "parentCategoryId": 1,
    "parentCategoryName": "Điện thoại",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "childCategories": []
  }
]
```

---

## Brand APIs

### 1. Get All Brands

Lấy tất cả thương hiệu.

**Endpoint:** `GET /api/brands`

**Authentication:** Không bắt buộc

**Ví dụ Request:**

```bash
GET /api/brands
```

**Response:** `List<BrandResponse>`

```json
[
  {
    "id": 1,
    "name": "Apple",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  },
  {
    "id": 2,
    "name": "Samsung",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

---

### 2. Get Brand Detail

Lấy thông tin chi tiết của một thương hiệu.

**Endpoint:** `GET /api/brands/{id}`

**Authentication:** Không bắt buộc

**Path Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `id` | Integer | Yes | ID của thương hiệu |

**Ví dụ Request:**

```bash
GET /api/brands/1
```

**Response:** `BrandResponse`

```json
{
  "id": 1,
  "name": "Apple",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

---

## Attribute APIs

### 1. Get All Attributes

Lấy tất cả thuộc tính sản phẩm.

**Endpoint:** `GET /api/attributes`

**Authentication:** Không bắt buộc

**Ví dụ Request:**

```bash
GET /api/attributes
```

**Response:** `List<AttributeResponse>`

```json
[
  {
    "id": 1,
    "name": "Màu sắc"
  },
  {
    "id": 2,
    "name": "Dung lượng"
  },
  {
    "id": 3,
    "name": "RAM"
  },
  {
    "id": 4,
    "name": "Chip"
  }
]
```

---

### 2. Search Attributes

Tìm kiếm thuộc tính theo tên.

**Endpoint:** `GET /api/attributes/search`

**Authentication:** Không bắt buộc

**Query Parameters:**

| Parameter | Type | Required | Mô tả |
|-----------|------|----------|-------|
| `keyword` | String | Yes | Từ khóa tìm kiếm (tìm kiếm gần đúng) |

**Ví dụ Request:**

```bash
# Tìm thuộc tính có tên chứa "màu"
GET /api/attributes/search?keyword=màu

# Tìm thuộc tính có tên chứa "dung"
GET /api/attributes/search?keyword=dung
```

**Response:** `List<AttributeResponse>`

```json
[
  {
    "id": 1,
    "name": "Màu sắc"
  }
]
```

---

## 📝 Response Models

### ProductListResponse

```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max",
  "imageUrl": "https://cloudinary.com/...",
  "displayOriginalPrice": 29990000,
  "displaySalePrice": 26990000,
  "discountType": "PERCENTAGE",
  "discountValue": 10,
  "averageRating": 4.5,
  "totalRatings": 120,
  "inWishlist": false
}
```

### ProductDetailResponse

Xem chi tiết trong [CUSTOMER_API_RESPONSE_DOCUMENTATION.md](./CUSTOMER_API_RESPONSE_DOCUMENTATION.md#productdetailresponse)

---

## 🔒 Authentication & Authorization

### Authentication
- Sử dụng JWT Bearer Token
- Header: `Authorization: Bearer YOUR_JWT_TOKEN`

### Permissions Required

| API | Permission |
|-----|-----------|
| Update Product | `PRODUCT:UPDATE` |
| Delete Product | `PRODUCT:DELETE` |
| Restore Product | `PRODUCT:UPDATE` |
| Get Deleted Products | `PRODUCT:READ` |
| Update Variation | `PRODUCT:UPDATE` |
| Delete Variation | `PRODUCT:DELETE` |
| Restore Variation | `PRODUCT:UPDATE` |

**Public APIs** (không cần authentication):
- Search & Filter Products
- Get All Products
- Get Product Detail
- Get All Categories/Brands/Attributes
- Get Variation Detail

---

## ⚠️ Error Responses

### Common Error Codes

| Status Code | Mô tả |
|------------|-------|
| `400 Bad Request` | Dữ liệu đầu vào không hợp lệ |
| `401 Unauthorized` | Chưa đăng nhập hoặc token không hợp lệ |
| `403 Forbidden` | Không có quyền truy cập |
| `404 Not Found` | Không tìm thấy tài nguyên |
| `500 Internal Server Error` | Lỗi server |

### Error Response Format

```json
{
  "error": "Failed to update product: Product not found"
}
```

---

## 💡 Best Practices

### 1. Phân trang
- Luôn sử dụng phân trang cho danh sách lớn
- Kích thước trang hợp lý: 10-50 items
- Page bắt đầu từ 0

### 2. Tìm kiếm và Lọc
- Kết hợp nhiều điều kiện lọc để tìm kiếm chính xác
- Sử dụng `attributes` filter với format: "name:value"
- Sắp xếp theo giá/rating/tên để tối ưu UX

### 3. Upload File
- Định dạng hỗ trợ: JPG, PNG, WebP
- Kích thước tối đa: 10MB
- Luôn kiểm tra response để đảm bảo upload thành công

### 4. Soft Delete
- Soft delete cho phép khôi phục dữ liệu
- Ảnh không bị xóa khi soft delete
- Chỉ admin mới có thể xem dữ liệu đã xóa

---

## 📞 Contact & Support

**Base URL:** `http://localhost:8080/api`

**Postman Collections:**
- `03_Product_API_Complete.postman_collection.json`
- `04_Product_Variation_API_Complete.postman_collection.json`
- `05_Category_API_Complete.postman_collection.json`
- `06_Brand_API_Complete.postman_collection.json`
- `07_Attribute_API_Complete.postman_collection.json`

Nếu có thắc mắc, vui lòng liên hệ team Backend.

