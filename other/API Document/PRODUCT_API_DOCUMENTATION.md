# Product API Documentation

## Overview
The Product API manages product catalog with integrated Cloudinary image upload functionality. Handles product CRUD operations, brand and category management, and image management.

## Base URL
```
http://localhost:8080/api/products
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
  "price": 999.99,
  "stock": 50,
  "sku": "IP15P-256-BLU",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567890/product_images/iphone_15_pro.jpg",
  "imageID": "product_images/iphone_15_pro",
  "brandId": 1,
  "brandName": "Apple",
  "categoryId": 1,
  "categoryName": "Smartphones",
  "isActive": true,
  "isFeatured": false,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "deletedAt": null,
  "rating": 4.5,
  "reviewCount": 128,
  "tags": ["smartphone", "apple", "premium", "5g"],
  "specifications": {
    "screen": "6.1-inch Super Retina XDR",
    "storage": "256GB",
    "camera": "48MP main camera",
    "battery": "Up to 23 hours video playback"
  }
}
```

### Brand Model
```json
{
  "id": 1,
  "name": "Apple",
  "description": "Premium technology brand",
  "logo": "https://res.cloudinary.com/demo/image/upload/brand_logos/apple_logo.png",
  "logoID": "brand_logos/apple_logo",
  "isActive": true,
  "productCount": 25,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Category Model
```json
{
  "id": 1,
  "name": "Smartphones",
  "description": "Mobile phones and accessories",
  "parentId": null,
  "parentName": null,
  "isActive": true,
  "productCount": 45,
  "subcategories": [
    {
      "id": 11,
      "name": "iPhone",
      "description": "Apple iPhone series"
    }
  ],
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

---

## Product Endpoints

### 1. Create Product with Image Upload

**POST** `/api/products`

Creates a new product with optional image upload to Cloudinary.

**Content-Type:** `multipart/form-data`

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | Yes | Product name (3-255 chars) |
| description | String | No | Product description |
| price | Decimal | Yes | Product price (positive) |
| stock | Integer | Yes | Stock quantity (non-negative) |
| sku | String | Yes | Stock Keeping Unit (unique) |
| brandId | Long | Yes | Brand ID (must exist) |
| categoryId | Long | Yes | Category ID (must exist) |
| image | File | No | Product image file |
| isFeatured | Boolean | No | Featured product flag |
| tags | String | No | Comma-separated tags |
| specifications | String | No | JSON string of specifications |

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/products" \
  -F "name=Samsung Galaxy S24 Ultra" \
  -F "description=Flagship Samsung smartphone with S Pen" \
  -F "price=1199.99" \
  -F "stock=30" \
  -F "sku=SGS24U-512-TIT" \
  -F "brandId=2" \
  -F "categoryId=1" \
  -F "isFeatured=true" \
  -F "tags=smartphone,samsung,flagship,s-pen" \
  -F "specifications={\"screen\":\"6.8-inch Dynamic AMOLED\",\"storage\":\"512GB\",\"ram\":\"12GB\"}" \
  -F "image=@/path/to/galaxy-s24-ultra.jpg"
```

**Success Response (201 Created):**
```json
{
  "id": 25,
  "name": "Samsung Galaxy S24 Ultra",
  "description": "Flagship Samsung smartphone with S Pen",
  "price": 1199.99,
  "stock": 30,
  "sku": "SGS24U-512-TIT",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567890/product_images/galaxy_s24_ultra.jpg",
  "imageID": "product_images/galaxy_s24_ultra",
  "brandId": 2,
  "brandName": "Samsung",
  "categoryId": 1,
  "categoryName": "Smartphones",
  "isActive": true,
  "isFeatured": true,
  "createdAt": "2024-12-15T14:30:00",
  "updatedAt": "2024-12-15T14:30:00",
  "deletedAt": null,
  "rating": 0.0,
  "reviewCount": 0,
  "tags": ["smartphone", "samsung", "flagship", "s-pen"],
  "specifications": {
    "screen": "6.8-inch Dynamic AMOLED",
    "storage": "512GB",
    "ram": "12GB"
  }
}
```

**Error Responses:**
```json
// 400 Bad Request - Validation Error
{
  "error": "Product with SKU 'SGS24U-512-TIT' already exists"
}

// 404 Not Found - Brand not found
{
  "error": "Brand not found with ID: 999"
}

// 500 Internal Server Error - Image upload failed
{
  "error": "Failed to upload product image"
}
```

---

### 2. Update Product with Image Replacement

**PUT** `/api/products/{id}`

Updates an existing product. If a new image is provided, the old image is replaced.

**Content-Type:** `multipart/form-data`

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/products/25" \
  -F "name=Samsung Galaxy S24 Ultra 5G" \
  -F "description=Enhanced flagship Samsung smartphone with 5G" \
  -F "price=1099.99" \
  -F "stock=45" \
  -F "isFeatured=false" \
  -F "image=@/path/to/updated-galaxy-s24-ultra.jpg"
```

**Success Response (200 OK):**
```json
{
  "id": 25,
  "name": "Samsung Galaxy S24 Ultra 5G",
  "description": "Enhanced flagship Samsung smartphone with 5G",
  "price": 1099.99,
  "stock": 45,
  "sku": "SGS24U-512-TIT",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567891/product_images/updated_galaxy_s24_ultra.jpg",
  "imageID": "product_images/updated_galaxy_s24_ultra",
  "brandId": 2,
  "brandName": "Samsung",
  "categoryId": 1,
  "categoryName": "Smartphones",
  "isActive": true,
  "isFeatured": false,
  "createdAt": "2024-12-15T14:30:00",
  "updatedAt": "2024-12-20T09:15:00",
  "deletedAt": null,
  "rating": 4.2,
  "reviewCount": 8,
  "tags": ["smartphone", "samsung", "flagship", "s-pen"],
  "specifications": {
    "screen": "6.8-inch Dynamic AMOLED",
    "storage": "512GB",
    "ram": "12GB"
  }
}
```

---

### 3. Get Product by ID

**GET** `/api/products/{id}`

Retrieves a specific product by its ID.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/25"
```

**Success Response (200 OK):**
```json
{
  "id": 25,
  "name": "Samsung Galaxy S24 Ultra",
  "description": "Flagship Samsung smartphone with S Pen",
  "price": 1199.99,
  "stock": 30,
  "sku": "SGS24U-512-TIT",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567890/product_images/galaxy_s24_ultra.jpg",
  "imageID": "product_images/galaxy_s24_ultra",
  "brandId": 2,
  "brandName": "Samsung",
  "categoryId": 1,
  "categoryName": "Smartphones",
  "isActive": true,
  "isFeatured": true,
  "createdAt": "2024-12-15T14:30:00",
  "updatedAt": "2024-12-15T14:30:00",
  "deletedAt": null,
  "rating": 4.2,
  "reviewCount": 8,
  "tags": ["smartphone", "samsung", "flagship", "s-pen"],
  "specifications": {
    "screen": "6.8-inch Dynamic AMOLED",
    "storage": "512GB",
    "ram": "12GB"
  }
}
```

---

### 4. Get All Products (Paginated & Filtered)

**GET** `/api/products`

Retrieves products with pagination, sorting, and filtering.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-based) |
| size | Integer | 10 | Page size (1-100) |
| sortBy | String | createdAt | Sort field |
| sortDir | String | DESC | Sort direction |
| brandId | Long | - | Filter by brand |
| categoryId | Long | - | Filter by category |
| minPrice | Decimal | - | Minimum price filter |
| maxPrice | Decimal | - | Maximum price filter |
| isFeatured | Boolean | - | Filter featured products |
| isActive | Boolean | true | Filter active products |
| search | String | - | Search in name/description |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=20&brandId=2&minPrice=500&maxPrice=1500&search=galaxy"
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 25,
      "name": "Samsung Galaxy S24 Ultra",
      "description": "Flagship Samsung smartphone with S Pen",
      "price": 1199.99,
      "stock": 30,
      "sku": "SGS24U-512-TIT",
      "image": "https://res.cloudinary.com/demo/image/upload/product_images/galaxy_s24_ultra.jpg",
      "imageID": "product_images/galaxy_s24_ultra",
      "brandId": 2,
      "brandName": "Samsung",
      "categoryId": 1,
      "categoryName": "Smartphones",
      "isActive": true,
      "isFeatured": true,
      "createdAt": "2024-12-15T14:30:00",
      "updatedAt": "2024-12-15T14:30:00",
      "deletedAt": null,
      "rating": 4.2,
      "reviewCount": 8,
      "tags": ["smartphone", "samsung", "flagship", "s-pen"]
    }
  ],
  "pageable": {
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "pageSize": 20,
    "pageNumber": 0
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 1,
  "size": 20,
  "number": 0,
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

---

### 5. Get Featured Products

**GET** `/api/products/featured`

Retrieves all featured products.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/featured"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "iPhone 15 Pro",
    "description": "Latest iPhone with Pro features",
    "price": 999.99,
    "stock": 50,
    "sku": "IP15P-256-BLU",
    "image": "https://res.cloudinary.com/demo/image/upload/product_images/iphone_15_pro.jpg",
    "imageID": "product_images/iphone_15_pro",
    "brandId": 1,
    "brandName": "Apple",
    "categoryId": 1,
    "categoryName": "Smartphones",
    "isActive": true,
    "isFeatured": true,
    "rating": 4.5,
    "reviewCount": 128,
    "tags": ["smartphone", "apple", "premium"]
  }
]
```

---

### 6. Get Products by Brand

**GET** `/api/products/brand/{brandId}`

Retrieves all products for a specific brand.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/brand/1"
```

---

### 7. Get Products by Category

**GET** `/api/products/category/{categoryId}`

Retrieves all products for a specific category.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/category/1"
```

---

### 8. Search Products

**GET** `/api/products/search`

Advanced product search with multiple criteria.

**Query Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| q | String | Search query (name, description, tags) |
| brands | String | Comma-separated brand IDs |
| categories | String | Comma-separated category IDs |
| minPrice | Decimal | Minimum price |
| maxPrice | Decimal | Maximum price |
| minRating | Decimal | Minimum rating |
| inStock | Boolean | Only in-stock products |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/products/search?q=smartphone&brands=1,2&minPrice=800&maxPrice=1200&minRating=4.0&inStock=true"
```

---

### 9. Toggle Product Status

**PATCH** `/api/products/{id}/toggle-status`

Toggles the active status of a product.

**Sample Request:**
```bash
curl -X PATCH "http://localhost:8080/api/products/25/toggle-status"
```

---

### 10. Delete Product (Soft Delete)

**DELETE** `/api/products/{id}`

Soft deletes a product. The product image remains on Cloudinary.

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/products/25"
```

---

## Brand Endpoints

### 1. Create Brand with Logo Upload

**POST** `/api/brands`

**Content-Type:** `multipart/form-data`

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/brands" \
  -F "name=Xiaomi" \
  -F "description=Chinese technology company" \
  -F "logo=@/path/to/xiaomi-logo.png"
```

**Success Response (201 Created):**
```json
{
  "id": 5,
  "name": "Xiaomi",
  "description": "Chinese technology company",
  "logo": "https://res.cloudinary.com/demo/image/upload/v1234567890/brand_logos/xiaomi_logo.png",
  "logoID": "brand_logos/xiaomi_logo",
  "isActive": true,
  "productCount": 0,
  "createdAt": "2024-12-15T16:00:00",
  "updatedAt": "2024-12-15T16:00:00"
}
```

---

### 2. Update Brand

**PUT** `/api/brands/{id}`

**Content-Type:** `multipart/form-data`

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/brands/5" \
  -F "name=Xiaomi Corporation" \
  -F "description=Leading Chinese technology company" \
  -F "logo=@/path/to/updated-xiaomi-logo.png"
```

---

### 3. Get All Brands

**GET** `/api/brands`

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/brands?page=0&size=20"
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Apple",
      "description": "Premium technology brand",
      "logo": "https://res.cloudinary.com/demo/image/upload/brand_logos/apple_logo.png",
      "logoID": "brand_logos/apple_logo",
      "isActive": true,
      "productCount": 25,
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

---

### 4. Get Brand by ID

**GET** `/api/brands/{id}`

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/brands/1"
```

---

### 5. Delete Brand

**DELETE** `/api/brands/{id}`

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/brands/5"
```

---

## Category Endpoints

### 1. Create Category

**POST** `/api/categories`

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "name": "Tablets",
  "description": "Tablet computers and accessories",
  "parentId": null
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/categories" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tablets",
    "description": "Tablet computers and accessories",
    "parentId": null
  }'
```

**Success Response (201 Created):**
```json
{
  "id": 5,
  "name": "Tablets",
  "description": "Tablet computers and accessories",
  "parentId": null,
  "parentName": null,
  "isActive": true,
  "productCount": 0,
  "subcategories": [],
  "createdAt": "2024-12-15T17:00:00",
  "updatedAt": "2024-12-15T17:00:00"
}
```

---

### 2. Update Category

**PUT** `/api/categories/{id}`

**Content-Type:** `application/json`

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/categories/5" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tablet Devices",
    "description": "All types of tablet computers"
  }'
```

---

### 3. Get All Categories (Tree Structure)

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
    "description": "All electronic products",
    "parentId": null,
    "parentName": null,
    "isActive": true,
    "productCount": 125,
    "subcategories": [
      {
        "id": 11,
        "name": "Smartphones",
        "description": "Mobile phones",
        "parentId": 1,
        "parentName": "Electronics",
        "isActive": true,
        "productCount": 45,
        "subcategories": []
      },
      {
        "id": 12,
        "name": "Laptops",
        "description": "Portable computers",
        "parentId": 1,
        "parentName": "Electronics",
        "isActive": true,
        "productCount": 30,
        "subcategories": []
      }
    ],
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

---

### 4. Get Category by ID

**GET** `/api/categories/{id}`

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/categories/1"
```

---

### 5. Get Root Categories

**GET** `/api/categories/root`

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/categories/root"
```

---

### 6. Delete Category

**DELETE** `/api/categories/{id}`

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/categories/5"
```

---

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Successful GET/PUT/PATCH request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource (SKU, name)
- `500 Internal Server Error` - Server errors, image upload failures

### Validation Rules
- **Product name**: 3-255 characters, required
- **SKU**: Unique, alphanumeric with hyphens, required
- **Price**: Positive decimal, required
- **Stock**: Non-negative integer, required
- **Brand/Category**: Must reference existing entities
- **Image**: JPG, PNG, GIF, WebP, max 10MB

---

## Image Upload Details

### Cloudinary Integration
- **Product images**: Stored in `product_images/` folder
- **Brand logos**: Stored in `brand_logos/` folder
- **Automatic optimization**: Cloudinary handles compression and format conversion
- **CDN delivery**: Fast global delivery via Cloudinary CDN

### Image Requirements
- **Formats**: JPG, PNG, GIF, WebP
- **Size limit**: 10MB per file
- **Product images**: Recommended 800x800px (1:1 aspect ratio)
- **Brand logos**: Recommended 200x200px (transparent background preferred)

### Image Management
- **Create**: Upload image with entity creation
- **Update**: Replace existing image (old image deleted automatically)
- **Delete**: Image preserved on Cloudinary (soft delete only affects database)

---

## Business Rules

### Product Lifecycle
1. **Creation**: Requires valid brand and category
2. **Stock Management**: Automatic stock tracking
3. **Featured Products**: Manual promotion flag
4. **Status Management**: Active/inactive toggle
5. **Soft Delete**: Preserves historical data

### Search and Filtering
- **Full-text search**: Searches name, description, and tags
- **Price range**: Min/max price filtering
- **Brand filtering**: Single or multiple brands
- **Category filtering**: Hierarchical category support
- **Rating filtering**: Based on customer reviews
- **Stock filtering**: In-stock only option

### Hierarchical Categories
- **Parent-Child relationship**: Categories can have subcategories
- **Tree structure**: Unlimited nesting depth
- **Product inheritance**: Products can belong to leaf categories only