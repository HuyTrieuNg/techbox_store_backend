# Product API Documentation - Reorganized

## Overview
Product APIs đã được tách thành 2 controllers:
- **ProductController** (`/products`) - Public read-only APIs
- **ProductAdminController** (`/admin/products`) - Admin CUD operations

---

## Public APIs - ProductController

### 1. Filter/Search Products
**Endpoint:** `GET /products`  
**Auth:** Not required (Public)  
**Description:** Search and filter products with pagination

**Query Parameters:**
- `name` (optional) - Product name search
- `brandId` (optional) - Filter by brand
- `categoryId` (optional) - Filter by category (includes child categories)
- `attributes` (optional) - Filter by attributes (format: "attributeId:value")
- `minPrice` (optional) - Minimum price
- `maxPrice` (optional) - Maximum price
- `minRating` (optional) - Minimum rating
- `campaignId` (optional) - Filter by campaign
- `sortBy` (default: "id") - Sort field (id, price, rating, name, time)
- `sortDirection` (default: "ASC") - Sort direction (ASC, DESC)
- `page` (default: 0) - Page number
- `size` (default: 20) - Page size

**Example:**
```http
GET /products?name=iphone&brandId=1&minPrice=1000&maxPrice=2000&sortBy=price&sortDirection=ASC&page=0&size=20
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "iPhone 15 Pro",
      "imageUrl": "...",
      "displayOriginalPrice": 999.99,
      "displaySalePrice": 899.99,
      "discountType": "PERCENTAGE",
      "discountValue": 10.00,
      "averageRating": 4.5,
      "totalRatings": 120
    }
  ],
  "totalElements": 50,
  "totalPages": 3,
  "size": 20,
  "number": 0
}
```

---

### 2. Get Product Detail
**Endpoint:** `GET /products/{id}`  
**Auth:** Not required (Public)  
**Description:** Get detailed information about a product

**Example:**
```http
GET /products/1
```

**Response:**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "description": "...",
  "categoryId": 1,
  "categoryName": "Smartphones",
  "brandId": 1,
  "brandName": "Apple",
  "imageUrl": "...",
  "status": "PUBLISHED",
  "warrantyMonths": 12,
  "averageRating": 4.5,
  "totalRatings": 120,
  "displayOriginalPrice": 999.99,
  "displaySalePrice": 899.99,
  "discountType": "PERCENTAGE",
  "discountValue": 10.00,
  "attributes": [...],
  "variations": [...]
}
```

---

## Admin APIs - ProductAdminController

### 1. Create Product
**Endpoint:** `POST /admin/products`  
**Auth:** Required - `PRODUCT:WRITE` permission  
**Content-Type:** `multipart/form-data`  
**Description:** Create a new product

**Form Data:**
- `name` (required) - Product name
- `description` (optional) - Product description
- `categoryId` (optional) - Category ID
- `brandId` (optional) - Brand ID
- `image` (optional) - Product image file

**Example:**
```http
POST /admin/products
Authorization: Bearer <token>
Content-Type: multipart/form-data

name=iPhone 15 Pro
description=Latest iPhone model
categoryId=1
brandId=1
image=<file>
```

**Response:**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "description": "Latest iPhone model",
  "categoryId": 1,
  "brandId": 1,
  "imageUrl": "...",
  "imagePublicId": "...",
  "status": "DRAFT",
  "warrantyMonths": null,
  "createdAt": "2025-11-05T10:00:00",
  "updatedAt": "2025-11-05T10:00:00"
}
```

---

### 2. Update Product
**Endpoint:** `PUT /admin/products/{id}`  
**Auth:** Required - `PRODUCT:UPDATE` permission  
**Content-Type:** `multipart/form-data`  
**Description:** Update an existing product

**Form Data:**
- `name` (optional) - Product name
- `description` (optional) - Product description
- `categoryId` (optional) - Category ID
- `brandId` (optional) - Brand ID
- `image` (optional) - New product image file
- `deleteImage` (optional, default: false) - Delete existing image

**Example:**
```http
PUT /admin/products/1
Authorization: Bearer <token>
Content-Type: multipart/form-data

name=iPhone 15 Pro Max
image=<new_file>
```

**Response:**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max",
  "imageUrl": "...",
  "updatedAt": "2025-11-05T11:00:00"
}
```

---

### 3. Delete Product (Soft Delete)
**Endpoint:** `DELETE /admin/products/{id}`  
**Auth:** Required - `PRODUCT:DELETE` permission  
**Description:** Soft delete a product (sets deletedAt timestamp)

**Example:**
```http
DELETE /admin/products/1
Authorization: Bearer <token>
```

**Response:**
```
204 No Content
```

---

### 4. Restore Product
**Endpoint:** `PATCH /admin/products/{id}/restore`  
**Auth:** Required - `PRODUCT:UPDATE` permission  
**Description:** Restore a soft-deleted product

**Example:**
```http
PATCH /admin/products/1/restore
Authorization: Bearer <token>
```

**Response:**
```
200 OK
```

---

### 5. Check Product Name Exists
**Endpoint:** `GET /admin/products/exists`  
**Auth:** Required - `PRODUCT:READ` permission  
**Description:** Check if a product name already exists (for validation)

**Query Parameters:**
- `name` (required) - Product name to check
- `excludeId` (optional) - Exclude this product ID from check (for updates)

**Example:**
```http
GET /admin/products/exists?name=iPhone+15+Pro&excludeId=1
Authorization: Bearer <token>
```

**Response:**
```json
true
```

---

## Migration Notes

### URL Changes
| Old Endpoint | New Endpoint | Notes |
|--------------|--------------|-------|
| `POST /products` | `POST /admin/products` | Moved to admin |
| `PUT /products/{id}` | `PUT /admin/products/{id}` | Moved to admin |
| `DELETE /products/{id}` | `DELETE /admin/products/{id}` | Moved to admin |
| `PATCH /products/{id}/restore` | `PATCH /admin/products/{id}/restore` | Moved to admin |
| `GET /products/exists` | `GET /admin/products/exists` | Moved to admin |
| `GET /products` | `GET /products` | No change (public) |
| `GET /products/{id}` | `GET /products/{id}` | No change (public) |

### Required Actions
1. Update frontend API calls for CUD operations to use `/admin/products`
2. Ensure authentication tokens are included in admin requests
3. Update any API documentation or Postman collections
4. Test all endpoints with proper permissions

---

## Error Responses

### 400 Bad Request
```json
{
  "error": "Failed to create product: Product name already exists"
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized"
}
```

### 403 Forbidden
```json
{
  "error": "Access Denied"
}
```

### 404 Not Found
```json
{
  "error": "Product not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Failed to upload image: Connection timeout"
}
```
