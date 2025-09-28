# Product API Documentation

## Overview
The Product API manages product catalog with integrated Cloudinary image upload functionality. Includes product CRUD operations, brand and category management.

## Base URL
```
http://localhost:8080/api/products
http://localhost:8080/api/brands  
http://localhost:8080/api/categories
```

## Authentication
All endpoints require authentication (implementation depends on your auth system).

## Models

### Product Model
```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "description": "Latest iPhone with Pro features and A17 Pro chip",
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1234567890/product_images/iphone_15_pro.jpg",
  "imagePublicId": "product_images/iphone_15_pro",
  "brandId": 1,
  "categoryId": 1,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "deletedAt": null
}
```

### Brand Model
```json
{
  "id": 1,
  "name": "Apple",
  "description": "Premium technology brand",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "deletedAt": null
}
```

### Category Model
```json
{
  "id": 1,
  "name": "Smartphones",
  "description": "Mobile phones and accessories",
  "parentId": null,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "deletedAt": null
}
```

---

## Product Endpoints

### 1. Get All Products

**GET** `/api/products`

Retrieves all products with optional inclusion of soft-deleted products.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| includeDeleted | Boolean | false | Include soft-deleted products |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products?includeDeleted=true"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "iPhone 15 Pro",
    "description": "Latest iPhone with Pro features",
    "imageUrl": "https://res.cloudinary.com/demo/image/upload/product_images/iphone_15_pro.jpg",
    "imagePublicId": "product_images/iphone_15_pro",
    "brandId": 1,
    "categoryId": 1,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "deletedAt": null
  }
]
```

---

### 2. Get Active Products

**GET** `/api/products/active`

Retrieves only active (non-deleted) products.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/active"
```

---

### 3. Get Product by ID

**GET** `/api/products/{id}`

Retrieves a specific product by its ID.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| includeDeleted | Boolean | false | Include soft-deleted product |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/1?includeDeleted=false"
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "description": "Latest iPhone with Pro features",
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/product_images/iphone_15_pro.jpg",
  "imagePublicId": "product_images/iphone_15_pro",
  "brandId": 1,
  "categoryId": 1,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "deletedAt": null
}
```

**Error Response:**
```json
// 404 Not Found
{
  "error": "Product not found"
}
```

---

### 4. Create Product with Image Upload

**POST** `/api/products`

Creates a new product with optional image upload to Cloudinary.

**Content-Type:** `multipart/form-data`

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | Yes | Product name |
| description | String | No | Product description |
| categoryId | Integer | No | Category ID |
| brandId | Integer | No | Brand ID |
| image | File | No | Image file |

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/products" \
  -F "name=Samsung Galaxy S24 Ultra" \
  -F "description=Flagship Samsung smartphone" \
  -F "categoryId=1" \
  -F "brandId=2" \
  -F "image=@/path/to/galaxy-s24-ultra.jpg"
```

**Success Response (201 Created):**
```json
{
  "id": 25,
  "name": "Samsung Galaxy S24 Ultra",
  "description": "Flagship Samsung smartphone",
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1234567890/product_images/galaxy_s24_ultra.jpg",
  "imagePublicId": "product_images/galaxy_s24_ultra",
  "brandId": 2,
  "categoryId": 1,
  "createdAt": "2024-12-15T14:30:00",
  "updatedAt": "2024-12-15T14:30:00",
  "deletedAt": null
}
```

**Error Responses:**
```json
// 500 Internal Server Error - Image upload failed
{
  "error": "Failed to upload image: Connection timeout"
}

// 400 Bad Request - Product creation failed
{
  "error": "Failed to create product: Name already exists"
}
```

---

### 5. Update Product with Image Replacement

**PUT** `/api/products/{id}`

Updates an existing product. If a new image is provided, the old image is automatically deleted and replaced.

**Content-Type:** `multipart/form-data`

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | No | Updated product name |
| description | String | No | Updated description |
| categoryId | Integer | No | Updated category ID |
| brandId | Integer | No | Updated brand ID |
| image | File | No | New image (replaces existing) |
| deleteImage | Boolean | No | Delete existing image (default: false) |

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/products/25" \
  -F "name=Samsung Galaxy S24 Ultra 5G" \
  -F "description=Enhanced flagship smartphone" \
  -F "image=@/path/to/updated-galaxy.jpg"
```

**Success Response (200 OK):**
```json
{
  "id": 25,
  "name": "Samsung Galaxy S24 Ultra 5G",
  "description": "Enhanced flagship smartphone",
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1234567891/product_images/updated_galaxy.jpg",
  "imagePublicId": "product_images/updated_galaxy",
  "brandId": 2,
  "categoryId": 1,
  "createdAt": "2024-12-15T14:30:00",
  "updatedAt": "2024-12-20T09:15:00",
  "deletedAt": null
}
```

---

### 6. Delete Product (Soft Delete)

**DELETE** `/api/products/{id}`

Soft deletes a product. The product image remains on Cloudinary.

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/products/25"
```

**Success Response (204 No Content):**
```
(Empty response body)
```

**Error Response:**
```json
// 400 Bad Request
{
  "error": "Failed to delete product: Product not found"
}
```

---

### 7. Restore Product

**PATCH** `/api/products/{id}/restore`

Restores a soft-deleted product.

**Sample Request:**
```bash
curl -X PATCH "http://localhost:8080/api/products/25/restore"
```

**Success Response (200 OK):**
```
(Empty response body)
```

---

### 8. Get Products by Category

**GET** `/api/products/category/{categoryId}`

Retrieves all products for a specific category.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/category/1"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "iPhone 15 Pro",
    "description": "Latest iPhone",
    "imageUrl": "https://res.cloudinary.com/demo/image/upload/product_images/iphone.jpg",
    "imagePublicId": "product_images/iphone",
    "brandId": 1,
    "categoryId": 1,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "deletedAt": null
  }
]
```

---

### 9. Get Products by Brand

**GET** `/api/products/brand/{brandId}`

Retrieves all products for a specific brand.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/brand/1"
```

---

### 10. Search Products

**GET** `/api/products/search`

Searches products by name keyword.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| keyword | String | Yes | Search keyword |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/search?keyword=iphone"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "iPhone 15 Pro",
    "description": "Latest iPhone with Pro features",
    "imageUrl": "https://res.cloudinary.com/demo/image/upload/product_images/iphone.jpg",
    "imagePublicId": "product_images/iphone",
    "brandId": 1,
    "categoryId": 1,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "deletedAt": null
  }
]
```

---

### 11. Check Product Name Exists

**GET** `/api/products/exists`

Checks if a product name already exists.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | Yes | Product name to check |
| excludeId | Integer | No | Exclude this product ID from check |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/exists?name=iPhone%2015%20Pro&excludeId=1"
```

**Success Response (200 OK):**
```json
true
```

---

### 12. Delete Product Image Only

**DELETE** `/api/products/{id}/image`

Deletes only the image from a product, keeping the product data.

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/products/25/image"
```

**Success Response (200 OK):**
```json
{
  "id": 25,
  "name": "Samsung Galaxy S24 Ultra",
  "description": "Flagship smartphone",
  "imageUrl": null,
  "imagePublicId": null,
  "brandId": 2,
  "categoryId": 1,
  "createdAt": "2024-12-15T14:30:00",
  "updatedAt": "2024-12-20T10:00:00",
  "deletedAt": null
}
```

**Error Responses:**
```json
// No image to delete
{
  "message": "Product has no image to delete"
}

// Cloudinary deletion failed
{
  "error": "Failed to delete image from Cloudinary: Network error"
}
```

---

### 13. Upload Product Image

**POST** `/api/products/upload-image`

Uploads an image to Cloudinary (independent of product creation).

**Content-Type:** `multipart/form-data`

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| file | File | Yes | Image file |

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/products/upload-image" \
  -F "file=@/path/to/product-image.jpg"
```

**Success Response (200 OK):**
```json
{
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1234567890/product_images/uploaded_image.jpg",
  "publicId": "product_images/uploaded_image",
  "message": "Image uploaded successfully"
}
```

**Error Responses:**
```json
// Empty file
{
  "error": "File is empty"
}

// Invalid file type
{
  "error": "File must be an image"
}

// Upload failed
{
  "error": "Failed to upload image: Upload timeout"
}
```

---

### 14. Create Product with Pre-uploaded Image

**POST** `/api/products/create-with-image`

Creates a product with a new image upload in one request.

**Content-Type:** `multipart/form-data`

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/products/create-with-image" \
  -F "name=New Product" \
  -F "description=Product description" \
  -F "categoryId=1" \
  -F "brandId=2" \
  -F "file=@/path/to/image.jpg"
```

---

### 15. Update Product with New Image

**PUT** `/api/products/{id}/update-with-image`

Updates a product with optional new image upload.

**Content-Type:** `multipart/form-data`

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/products/1/update-with-image" \
  -F "name=Updated Product Name" \
  -F "file=@/path/to/new-image.jpg"
```

---

### 16. Delete Image by Public ID

**DELETE** `/api/products/delete-image`

Deletes an image from Cloudinary using its public ID.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| publicId | String | Yes | Cloudinary public ID |

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/products/delete-image?publicId=product_images/image_to_delete"
```

**Success Response (200 OK):**
```json
{
  "result": "ok",
  "message": "Image deleted successfully"
}
```

---

## Brand Endpoints

### 1. Get All Brands

**GET** `/api/brands`

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/brands"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Apple",
    "description": "Premium technology brand",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "deletedAt": null
  }
]
```

---

### 2. Get Brand by ID

**GET** `/api/brands/{id}`

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/brands/1"
```

---

### 3. Create Brand

**POST** `/api/brands`

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "name": "Samsung",
  "description": "South Korean electronics company"
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/brands" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Samsung",
    "description": "South Korean electronics company"
  }'
```

**Success Response (201 Created):**
```json
{
  "id": 2,
  "name": "Samsung",
  "description": "South Korean electronics company",
  "createdAt": "2024-12-15T10:00:00",
  "updatedAt": "2024-12-15T10:00:00",
  "deletedAt": null
}
```

---

### 4. Update Brand

**PUT** `/api/brands/{id}`

**Content-Type:** `application/json`

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/brands/2" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Samsung Electronics",
    "description": "Leading South Korean electronics company"
  }'
```

---

### 5. Delete Brand

**DELETE** `/api/brands/{id}`

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/brands/2"
```

**Success Response (204 No Content):**
```
(Empty response body)
```

---

### 6. Check Brand Name Exists

**GET** `/api/brands/exists`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | Yes | Brand name to check |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/brands/exists?name=Apple"
```

**Success Response (200 OK):**
```json
true
```

---

## Category Endpoints

### 1. Get All Categories

**GET** `/api/categories`

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/categories"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic devices and accessories",
    "parentId": null,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "deletedAt": null
  }
]
```

---

### 2. Get Category by ID

**GET** `/api/categories/{id}`

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/categories/1"
```

---

### 3. Get Root Categories

**GET** `/api/categories/root`

Gets categories that have no parent (parentId is null).

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/categories/root"
```

---

### 4. Get Child Categories

**GET** `/api/categories/{parentId}/children`

Gets all subcategories of a parent category.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/categories/1/children"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 11,
    "name": "Smartphones",
    "description": "Mobile phones",
    "parentId": 1,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "deletedAt": null
  }
]
```

---

### 5. Create Category

**POST** `/api/categories`

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "name": "Tablets",
  "description": "Tablet computers",
  "parentId": 1
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/categories" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tablets",
    "description": "Tablet computers",
    "parentId": 1
  }'
```

**Success Response (201 Created):**
```json
{
  "id": 12,
  "name": "Tablets",
  "description": "Tablet computers",
  "parentId": 1,
  "createdAt": "2024-12-15T11:00:00",
  "updatedAt": "2024-12-15T11:00:00",
  "deletedAt": null
}
```

---

### 6. Update Category

**PUT** `/api/categories/{id}`

**Content-Type:** `application/json`

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/categories/12" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tablet Devices",
    "description": "All types of tablet computers"
  }'
```

---

### 7. Delete Category

**DELETE** `/api/categories/{id}`

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/categories/12"
```

**Success Response (204 No Content):**
```
(Empty response body)
```

---

### 8. Check Category Name Exists

**GET** `/api/categories/exists`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | Yes | Category name to check |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/categories/exists?name=Electronics"
```

**Success Response (200 OK):**
```json
true
```

---

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Successful GET/PUT/PATCH request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server errors, image upload failures

### Validation Rules
- **Product name**: Required for creation, must be unique
- **Brand/Category**: Must reference existing entities when provided
- **Image files**: Must be valid image formats, reasonable size limits
- **Names**: Cannot be empty or null when provided

---

## Image Upload Details

### Cloudinary Integration
- **Product images**: Stored in `product_images/` folder
- **Automatic optimization**: Cloudinary handles compression and format conversion
- **CDN delivery**: Fast global delivery via Cloudinary CDN
- **Old image cleanup**: Previous images automatically deleted when replaced

### Image Requirements
- **Formats**: JPG, PNG, GIF, WebP (validated by content type)
- **Upload folder**: `product_images/` (automatically created)
- **Response**: Returns both secure URL and public ID for management

### Image Management Flow
1. **Upload**: Use multipart/form-data with `image` parameter
2. **Replace**: New image upload automatically deletes old image
3. **Delete**: Separate endpoint to remove image while keeping product
4. **Validation**: Server-side validation of file type and content

---

## Business Rules

### Product Management
- **Soft Delete**: Products are soft deleted (deletedAt timestamp)
- **Image Persistence**: Images remain on Cloudinary after product deletion
- **Restoration**: Soft deleted products can be restored
- **Search**: Search only active products by default

### Category Hierarchy
- **Parent-Child**: Categories can have parent categories
- **Root Categories**: Categories with parentId = null
- **Nested Structure**: Support for unlimited category depth

### Brand Management
- **Unique Names**: Brand names must be unique
- **Simple Structure**: No hierarchy, flat brand structure
- **Product Association**: Brands can be associated with multiple products

---

## Performance Considerations

### Database Queries
- Soft delete filtering applied by default
- Indexes on frequently queried fields (name, brand, category)
- Efficient category hierarchy queries

### Image Handling
- Asynchronous Cloudinary uploads where possible
- Automatic image optimization by Cloudinary
- CDN caching for fast image delivery
- Cleanup of unused images during updates

### API Response
- Lightweight responses with essential data
- Batch operations for multiple products
- Efficient search with keyword matching