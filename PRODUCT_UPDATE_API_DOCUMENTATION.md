# API Documentation: Update Product và Product Variation

## Tổng quan
Tài liệu này mô tả các API endpoint để cập nhật Product và Product Variation trong hệ thống Techbox Store.

## 1. Update Product

### Endpoint
```
PUT /api/products/{id}
```

### Authorization
- **Required**: `PRODUCT:UPDATE` authority
- **Authentication**: JWT Bearer token

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | Integer | Yes | ID của product cần cập nhật |

### Request Body
```json
{
  "name": "string",           // Tên sản phẩm (max 255 ký tự)
  "description": "string",    // Mô tả sản phẩm (max 5000 ký tự)
  "categoryId": 1,            // ID danh mục
  "brandId": 1,               // ID thương hiệu
  "imageUrl": "string",       // URL ảnh sản phẩm (max 255 ký tự)
  "imagePublicId": "string",  // Public ID của ảnh trên Cloudinary (max 255 ký tự)
  "status": "PUBLISHED",      // Trạng thái: PUBLISHED, DRAFT, ARCHIVED
  "warrantyMonths": 12,       // Số tháng bảo hành (≥ 0)
  "attributes": {             // Map các thuộc tính sản phẩm
    "key1": "value1",
    "key2": "value2"
  }
}
```

### Validation Rules
- `name`: Tối đa 255 ký tự
- `description`: Tối đa 5000 ký tự
- `imageUrl`: Tối đa 255 ký tự
- `imagePublicId`: Tối đa 255 ký tự
- `warrantyMonths`: ≥ 0
- `status`: Enum values: `PUBLISHED`, `DRAFT`, `ARCHIVED`

### Response

#### Success (200 OK)
```json
{
  "id": 1,
  "name": "Updated Product Name",
  "description": "Updated description",
  "spu": "SPU001",
  "categoryId": 1,
  "categoryName": "Electronics",
  "brandId": 1,
  "brandName": "Brand A",
  "imageUrl": "https://cloudinary.com/image.jpg",
  "imagePublicId": "product_images/abc123",
  "status": "PUBLISHED",
  "warrantyMonths": 12,
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-02T10:00:00Z",
  "attributes": {
    "Color": "Red",
    "Size": "M"
  }
}
```

#### Error Responses

##### 400 Bad Request - Validation Error
```json
{
  "error": "Product name must not exceed 255 characters",
  "type": "VALIDATION_ERROR"
}
```

##### 404 Not Found
```json
{
  "error": "Product not found with id: 123"
}
```

##### 500 Internal Server Error
```json
{
  "error": "Failed to update product: Database connection error",
  "type": "INTERNAL_ERROR"
}
```

### Image Handling
- **Cập nhật ảnh mới**: Cung cấp `imageUrl` và `imagePublicId` - ảnh cũ sẽ được xóa tự động từ Cloudinary
- **Giữ ảnh cũ**: Để `imageUrl` và `imagePublicId` là `null` - hệ thống sẽ giữ ảnh hiện tại
- **Xóa ảnh**: Cung cấp `imageUrl` và `imagePublicId` là `null` - ảnh cũ sẽ được xóa nhưng không có ảnh mới

### Partial Update Support
API này hỗ trợ **partial update** - chỉ cần cung cấp các field muốn cập nhật, các field khác sẽ giữ nguyên giá trị cũ.

**Ví dụ partial update:**
```json
PUT /api/products/123
{
  "name": "Tên mới",
  "warrantyMonths": 24
}
```
Chỉ cập nhật `name` và `warrantyMonths`, các field khác giữ nguyên.

---

## 2. Add Attributes to Product

### Endpoint
```
POST /api/products/{id}/attributes
```

### Authorization
- **Required**: `PRODUCT:UPDATE` authority
- **Authentication**: JWT Bearer token

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | Integer | Yes | ID của product cần thêm attributes |

### Request Body
```json
{
  "1": "Red",      // attributeId: value
  "2": "Large",
  "3": "Cotton"
}
```

### Response

#### Success (200 OK)
```json
{
  "message": "Attributes added successfully"
}
```

#### Error Responses

##### 400 Bad Request
```json
{
  "error": "Attribute not found with id: 999"
}
```

---

## 2. Update Product Variation

### Endpoint
```
PUT /api/products/{id}
```

### Authorization
- **Required**: `PRODUCT:UPDATE` authority
- **Authentication**: JWT Bearer token

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | Integer | Yes | ID của product cần cập nhật |

### Request Body
```json
{
  "name": "string",           // Tên sản phẩm (max 255 ký tự)
  "description": "string",    // Mô tả sản phẩm (max 5000 ký tự)
  "categoryId": 1,            // ID danh mục
  "brandId": 1,               // ID thương hiệu
  "imageUrl": "string",       // URL ảnh sản phẩm (max 255 ký tự)
  "imagePublicId": "string",  // Public ID của ảnh trên Cloudinary (max 255 ký tự)
  "status": "PUBLISHED",      // Trạng thái: PUBLISHED, DRAFT, ARCHIVED
  "warrantyMonths": 12,       // Số tháng bảo hành (≥ 0)
  "attributes": {             // Map các thuộc tính sản phẩm
    "key1": "value1",
    "key2": "value2"
  }
}
```

### Validation Rules
- `name`: Tối đa 255 ký tự
- `description`: Tối đa 5000 ký tự
- `imageUrl`: Tối đa 255 ký tự
- `imagePublicId`: Tối đa 255 ký tự
- `warrantyMonths`: ≥ 0
- `status`: Enum values: `PUBLISHED`, `DRAFT`, `ARCHIVED`

### Response

#### Success (200 OK)
```json
{
  "id": 1,
  "name": "Updated Product Name",
  "description": "Updated description",
  "spu": "SPU001",
  "categoryId": 1,
  "categoryName": "Electronics",
  "brandId": 1,
  "brandName": "Brand A",
  "imageUrl": "https://cloudinary.com/image.jpg",
  "imagePublicId": "product_images/abc123",
  "status": "PUBLISHED",
  "warrantyMonths": 12,
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-02T10:00:00Z",
  "attributes": {
    "Color": "Red",
    "Size": "M"
  }
}
```

#### Error Responses

##### 400 Bad Request - Validation Error
```json
{
  "error": "Product name must not exceed 255 characters",
  "type": "VALIDATION_ERROR"
}
```

##### 404 Not Found
```json
{
  "error": "Product not found with id: 123"
}
```

##### 500 Internal Server Error
```json
{
  "error": "Failed to update product: Database connection error",
  "type": "INTERNAL_ERROR"
}
```

### Image Handling
- **Cập nhật ảnh mới**: Cung cấp `imageUrl` và `imagePublicId` - ảnh cũ sẽ được xóa tự động từ Cloudinary
- **Giữ ảnh cũ**: Để `imageUrl` và `imagePublicId` là `null` - hệ thống sẽ giữ ảnh hiện tại
- **Xóa ảnh**: Cung cấp `imageUrl` và `imagePublicId` là `null` - ảnh cũ sẽ được xóa nhưng không có ảnh mới

---

## 2. Update Product Variation

### Endpoint
```
PUT /api/product-variations/{id}
```

### Authorization
- **Required**: `PRODUCT:UPDATE` authority
- **Authentication**: JWT Bearer token

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | Integer | Yes | ID của product variation cần cập nhật |

### Request Body
```json
{
  "variationName": "string",        // Tên biến thể (max 255 ký tự)
  "price": 100000.00,               // Giá bán (> 0)
  "sku": "SKU001-RED",              // Mã SKU (max 255 ký tự)
  "stockQuantity": 50,              // Số lượng tồn kho (≥ 0)
  "reservedQuantity": 5,            // Số lượng đã đặt trước
  "avgCostPrice": 80000.00,         // Giá vốn trung bình (> 0)
  "imageUrls": [                    // Danh sách URL ảnh mới
    "https://cloudinary.com/image1.jpg",
    "https://cloudinary.com/image2.jpg"
  ],
  "imagePublicIds": [               // Danh sách public ID ảnh mới
    "variation_images/abc123",
    "variation_images/def456"
  ],
  "deleteImageIds": [               // Danh sách public ID ảnh cần xóa
    "variation_images/old789"
  ],
  "variationAttributes": [          // Danh sách thuộc tính biến thể
    {
      "attributeId": 1,             // ID thuộc tính (required)
      "value": "Red"                // Giá trị thuộc tính
    },
    {
      "attributeId": 2,
      "value": "M"
    }
  ]
}
```

### Validation Rules
- `variationName`: Tối đa 255 ký tự
- `price`: > 0
- `sku`: Tối đa 255 ký tự
- `stockQuantity`: ≥ 0
- `avgCostPrice`: > 0
- `variationAttributes[].attributeId`: Required, not null

### Response

#### Success (200 OK)
```json
{
  "id": 1,
  "variationName": "Red - Medium",
  "productId": 1,
  "productName": "T-Shirt",
  "price": 100000.00,
  "sku": "TSHIRT-RED-M",
  "stockQuantity": 50,
  "reservedQuantity": 5,
  "avgCostPrice": 80000.00,
  "imageUrls": [
    "https://cloudinary.com/image1.jpg",
    "https://cloudinary.com/image2.jpg"
  ],
  "imagePublicIds": [
    "variation_images/abc123",
    "variation_images/def456"
  ],
  "variationAttributes": [
    {
      "attributeId": 1,
      "attributeName": "Color",
      "value": "Red"
    },
    {
      "attributeId": 2,
      "attributeName": "Size",
      "value": "M"
    }
  ],
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-02T10:00:00Z"
}
```

#### Error Responses

##### 400 Bad Request - Validation Error
```json
{
  "error": "Price must be greater than 0",
  "type": "VALIDATION_ERROR"
}
```

##### 500 Internal Server Error
```json
{
  "error": "Failed to update product variation: Database error",
  "type": "INTERNAL_ERROR"
}
```

### Image Handling
- **Thêm ảnh mới**: Cung cấp `imageUrls` và `imagePublicIds` - ảnh sẽ được thêm vào danh sách hiện tại
- **Xóa ảnh**: Cung cấp `deleteImageIds` - các ảnh này sẽ được xóa khỏi Cloudinary và database
- **Kết hợp**: Có thể thêm ảnh mới và xóa ảnh cũ trong cùng một request

### Variation Attributes
- Mỗi attribute phải có `attributeId` (required)
- `value` có thể là `null` để xóa giá trị attribute
- Attributes được validate dựa trên cấu hình attribute của hệ thống

---

## 3. Lưu ý chung

### Authentication
- Tất cả endpoints yêu cầu JWT token hợp lệ
- Token phải có authority tương ứng (`PRODUCT:UPDATE`)

### Transaction Safety
- Update operations được thực hiện trong database transaction
- Nếu có lỗi, tất cả thay đổi sẽ được rollback

### Image Management
- Ảnh được lưu trữ trên Cloudinary
- Public ID được sử dụng để quản lý và xóa ảnh
- Hệ thống tự động xử lý việc xóa ảnh cũ khi cập nhật

### Validation
- Sử dụng Jakarta Bean Validation
- Error messages được trả về chi tiết cho từng field
- Type validation cho enum values

### Soft Delete
- Products và Variations hỗ trợ soft delete
- Update operations chỉ work với records chưa bị hard delete