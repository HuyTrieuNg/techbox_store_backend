# TechBox Store API - Complete Postman Collections

## 📋 Overview

This folder contains **complete Postman collections** for all TechBox Store API endpoints. These collections are auto-generated with comprehensive coverage of all controllers and endpoints.

## 📚 Collections List

### 1. Authentication API (3 endpoints)
**File:** `01_Authentication_API.postman_collection.json`

- POST `/auth/login` - User login
- POST `/auth/register` - User registration  
- POST `/auth/refresh-token` - Refresh access token

### 2. User Management API (5 endpoints)
**File:** `02_User_Management_API.postman_collection.json`

- GET `/api/users` - Get all users (paginated)
- GET `/api/users/{id}` - Get user by ID
- POST `/api/users` - Create new user
- PUT `/api/users/{id}` - Update user
- DELETE `/api/users/{id}` - Delete user

### 3. Product API (16 endpoints)
**File:** `03_Product_API_Complete.postman_collection.json`

- GET `/api/products` - Get all products (paginated)
- GET `/api/products/{id}` - Get product by ID
- GET `/api/products/category/{categoryId}` - Get by category
- GET `/api/products/brand/{brandId}` - Get by brand
- GET `/api/products/search` - Search products
- GET `/api/products/exists` - Check name exists
- POST `/api/products` - Create product
- PUT `/api/products/{id}` - Update product
- DELETE `/api/products/{id}` - Soft delete product
- POST `/api/products/{id}/restore` - Restore product
- POST `/api/products/{id}/images` - Upload product images (multipart/form-data)
- DELETE `/api/products/{id}/images/{imageId}` - Delete product image

### 4. Product Variation API (12 endpoints)
**File:** `04_Product_Variation_API_Complete.postman_collection.json`

- GET `/api/product-variations` - Get all variations (paginated)
- GET `/api/product-variations/{id}` - Get by ID
- GET `/api/product-variations/product/{productId}` - Get by product
- GET `/api/product-variations/in-stock` - Get in-stock variations
- GET `/api/product-variations/low-stock` - Get low-stock variations
- GET `/api/product-variations/sku/{sku}` - Get by SKU
- GET `/api/product-variations/exists` - Check SKU exists
- POST `/api/product-variations` - Create variation (with images)
- PUT `/api/product-variations/{id}` - Update variation (with images)
- PATCH `/api/product-variations/{id}/stock` - Update stock quantity
- DELETE `/api/product-variations/{id}` - Soft delete
- PATCH `/api/product-variations/{id}/restore` - Restore variation

### 5. Category API (8 endpoints)
**File:** `05_Category_API_Complete.postman_collection.json`

- GET `/api/categories` - Get all categories (paginated)
- GET `/api/categories/{id}` - Get category by ID
- GET `/api/categories/root` - Get root categories
- GET `/api/categories/{parentId}/children` - Get child categories
- GET `/api/categories/exists` - Check name exists
- POST `/api/categories` - Create category
- PUT `/api/categories/{id}` - Update category
- DELETE `/api/categories/{id}` - Delete category

### 6. Brand API (6 endpoints)
**File:** `06_Brand_API_Complete.postman_collection.json`

- GET `/api/brands` - Get all brands (paginated)
- GET `/api/brands/{id}` - Get brand by ID
- GET `/api/brands/exists` - Check name exists
- POST `/api/brands` - Create brand
- PUT `/api/brands/{id}` - Update brand
- DELETE `/api/brands/{id}` - Delete brand

### 7. Attribute API (7 endpoints)
**File:** `07_Attribute_API_Complete.postman_collection.json`

- GET `/api/attributes` - Get all attributes
- GET `/api/attributes/{id}` - Get attribute by ID
- GET `/api/attributes/search` - Search attributes by keyword
- GET `/api/attributes/exists` - Check name exists
- POST `/api/attributes` - Create attribute
- PUT `/api/attributes/{id}` - Update attribute
- DELETE `/api/attributes/{id}` - Delete attribute

### 8. Campaign API (10 endpoints)
**File:** `08_Campaign_API_Complete.postman_collection.json`

- GET `/api/campaigns` - Get all campaigns (paginated)
- GET `/api/campaigns/{id}` - Get campaign by ID
- GET `/api/campaigns/active` - Get active campaigns
- GET `/api/campaigns/scheduled` - Get scheduled campaigns
- GET `/api/campaigns/expired` - Get expired campaigns
- POST `/api/campaigns` - Create campaign (with/without image)
- PUT `/api/campaigns/{id}` - Update campaign (with/without image)
- DELETE `/api/campaigns/{id}` - Delete campaign
- POST `/api/campaigns/{id}/restore` - Restore campaign

**⏰ Date Format Required:** startDate, endDate

### 9. Promotion API (9 endpoints)
**File:** `09_Promotion_API_Complete.postman_collection.json`

- GET `/api/promotions` - Get all promotions (paginated)
- GET `/api/promotions/{id}` - Get promotion by ID
- GET `/api/promotions/campaign/{campaignId}` - Get by campaign
- GET `/api/promotions/product-variation/{productVariationId}` - Get by product variation
- POST `/api/promotions` - Create promotion (PERCENTAGE/FIXED_AMOUNT)
- PUT `/api/promotions/{id}` - Update promotion
- POST `/api/promotions/calculate` - Calculate discount (POST)
- GET `/api/promotions/product-variation/{id}/calculate` - Calculate discount (GET)
- DELETE `/api/promotions/{id}` - Delete promotion

### 10. Voucher API (16 endpoints)
**File:** `10_Voucher_API_Complete.postman_collection.json`

**CRUD Operations:**
- GET `/api/vouchers` - Get all vouchers (paginated)
- GET `/api/vouchers/valid` - Get valid vouchers
- GET `/api/vouchers/search` - Search vouchers
- GET `/api/vouchers/{code}` - Get voucher by code
- GET `/api/vouchers/code/exists` - Check code exists
- POST `/api/vouchers` - Create voucher (PERCENTAGE/FIXED_AMOUNT)
- PUT `/api/vouchers/{code}` - Update voucher
- DELETE `/api/vouchers/{code}` - Delete voucher
- POST `/api/vouchers/{code}/restore` - Restore voucher

**Validation & Usage:**
- POST `/api/vouchers/validate` - Validate voucher
- POST `/api/vouchers/use` - Use voucher

**Reports & Analytics:**
- GET `/api/vouchers/expired` - Get expired vouchers
- GET `/api/vouchers/expiring-soon` - Get expiring vouchers
- GET `/api/vouchers/usage/user/{userId}` - Get user usage
- GET `/api/vouchers/{code}/usage-count` - Get usage count

**⏰ Date Format Required:** validFrom, validUntil

### 11. Supplier API (6 endpoints)
**File:** `11_Supplier_API_Complete.postman_collection.json`

- GET `/api/suppliers` - Get all suppliers (paginated, with search)
- GET `/api/suppliers/{id}` - Get supplier by ID
- POST `/api/suppliers` - Create supplier
- PUT `/api/suppliers/{id}` - Update supplier
- DELETE `/api/suppliers/{id}` - Soft delete supplier
- POST `/api/suppliers/{id}/restore` - Restore supplier

### 12. Stock Import API (5 endpoints)
**File:** `12_Stock_Import_API_Complete.postman_collection.json`

- GET `/api/stock-imports` - Get all imports (with date/supplier filters)
- GET `/api/stock-imports/{id}` - Get import detail by ID
- GET `/api/stock-imports/by-code/{documentCode}` - Get by document code
- POST `/api/stock-imports` - Create stock import
- GET `/api/stock-imports/report` - Generate import report (day/month/supplier)

**⏰ Date Format Required:** importDate (in request body with microseconds), fromDate/toDate (query params as YYYY-MM-DD)

### 13. Stock Export API (6 endpoints)
**File:** `13_Stock_Export_API_Complete.postman_collection.json`

- GET `/api/stock-exports` - Get all exports (with date/order filters)
- GET `/api/stock-exports/{id}` - Get export detail by ID
- POST `/api/stock-exports` - Create manual stock export
- POST `/api/stock-exports/from-order/{orderId}` - Create export from order
- GET `/api/stock-exports/report` - Generate export report (day/month/product)

**⏰ Date Format Required:** exportDate (in request body with microseconds), fromDate/toDate (query params as YYYY-MM-DD)

### 14. Inventory Report API (11 endpoints)
**File:** `14_Inventory_Report_API_Complete.postman_collection.json`

**Stock Balance:**
- GET `/api/inventory/stock-balance` - Current stock levels (all products)
- GET `/api/inventory/stock-balance?lowStock=true` - Low stock items
- GET `/api/inventory/stock-balance?outOfStock=true` - Out of stock items

**Product History:**
- GET `/api/inventory/product-history/{productVariationId}` - Stock movements history

**Reports:**
- GET `/api/inventory/stock-value-report` - Stock value over time (by day/month)
- GET `/api/inventory/top-products` - Top products by import/export
- GET `/api/inventory/alerts` - Inventory alerts (out of stock, low stock, overstock)

**⏰ Date Format Required:** fromDate/toDate (query params as YYYY-MM-DD)

---

## ⏰ Date Format Standard

All date/time fields in these collections use the **microsecond-precision ISO 8601 format**:

```
YYYY-MM-DDTHH:MM:SS.uuuuuu
```

### Examples:
- `2025-10-01T03:30:24.624538`
- `2025-12-31T23:59:59.999999`

### JavaScript Date Formatter (Used in Pre-request Scripts):

```javascript
const formatDate = (date) => {
    const pad = (n) => String(n).padStart(2, '0');
    const padMs = (n) => String(n).padStart(6, '0');
    return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate()) +
           'T' + pad(date.getHours()) + ':' + pad(date.getMinutes()) + ':' + pad(date.getSeconds()) +
           '.' + padMs(date.getMilliseconds() * 1000);
};

// Usage:
const now = new Date();
const startDate = new Date(now.getTime() + 1 * 24 * 60 * 60 * 1000); // Tomorrow
const endDate = new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000); // +30 days

pm.collectionVariables.set("start_date", formatDate(startDate));
pm.collectionVariables.set("end_date", formatDate(endDate));
```

### Collections Using Date Formatting:
- **Campaign API**: `startDate`, `endDate` (microseconds format)
- **Voucher API**: `validFrom`, `validUntil` (microseconds format)
- **Stock Import API**: `importDate` (microseconds format), `fromDate`/`toDate` (YYYY-MM-DD for filters)
- **Stock Export API**: `exportDate` (microseconds format), `fromDate`/`toDate` (YYYY-MM-DD for filters)
- **Inventory Report API**: `fromDate`/`toDate` (YYYY-MM-DD for filters)

---

## 🔐 Authentication Flow

### Step 1: Register or Login
```json
POST /auth/login
{
  "username": "admin",
  "password": "password123"
}
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIs...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

### Step 2: Set Access Token
The `access_token` is automatically saved to collection variable and used in subsequent requests.

### Step 3: Use Protected Endpoints
```
Authorization: Bearer {{access_token}}
```

### Step 4: Refresh Token (When Expired)
```json
POST /auth/refresh-token
{
  "refresh_token": "{{refresh_token}}"
}
```

---

## 📝 Collection Variables

Each collection includes these common variables:

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `base_url` | API base URL | `http://localhost:8080` |
| `access_token` | JWT access token | (set after login) |
| `refresh_token` | JWT refresh token | (set after login) |

### Collection-Specific Variables:

**Product Collections:**
- `product_id`, `variation_id`, `category_id`, `brand_id`, `attribute_id`
- `variation_sku`, `product_name`, `category_name`, `brand_name`

**Campaign & Promotion:**
- `campaign_id`, `promotion_id`
- `campaign_start_date`, `campaign_end_date`

**Voucher:**
- `voucher_code`, `created_voucher_code`, `test_voucher_code`
- `voucher_valid_from`, `voucher_valid_until`

---

## 🧪 Test Scripts

All collections include automated test scripts:

### Response Status Tests
```javascript
pm.test("Status code is 200", () => pm.response.to.have.status(200));
```

### Variable Persistence
```javascript
if (pm.response.code === 201) {
    pm.collectionVariables.set("product_id", pm.response.json().id);
}
```

### Dynamic Data Generation
```javascript
pm.collectionVariables.set("product_name", "Product " + Date.now());
pm.collectionVariables.set("sku", "SKU" + Date.now());
```

---

## 🖼️ File Upload Endpoints

### Endpoints Using `multipart/form-data`:

1. **Product Images** (`POST /api/products/{id}/images`)
   ```
   Content-Type: multipart/form-data
   - images: [file, file, file]
   ```

2. **Product Variation** (`POST /api/product-variations`)
   ```
   Content-Type: multipart/form-data
   - name, sku, price, stockQuantity, etc.
   - images: [file, file, file]
   ```

3. **Campaign** (`POST /api/campaigns`, `PUT /api/campaigns/{id}`)
   ```
   Content-Type: multipart/form-data
   - name, description, startDate, endDate
   - image: [file] (optional)
   ```

---

## 🚀 Quick Start

### 1. Import Collections
1. Open Postman
2. Click **Import** button
3. Select all `.postman_collection.json` files from this folder
4. Collections will be imported with proper numbering (01-10)

### 2. Set Environment Variables
Create a Postman environment with:
```json
{
  "base_url": "http://localhost:8080"
}
```

Or use the default `base_url` variable in each collection.

### 3. Run Authentication
1. Open **01_Authentication_API** collection
2. Run **Login** request
3. `access_token` will be automatically saved

### 4. Test Endpoints
- All collections are ready to use with dynamic data generation
- Test scripts automatically persist IDs and tokens
- Pre-request scripts generate proper date formats where needed

---

## 📊 Collection Statistics

| Collection | Endpoints | Auth Required | File Upload |
|------------|-----------|---------------|-------------|
| Authentication | 3 | ❌ | ❌ |
| User Management | 5 | ✅ | ❌ |
| Product | 16 | ✅ | ✅ |
| Product Variation | 12 | ✅ | ✅ |
| Category | 8 | ✅ | ❌ |
| Brand | 6 | ✅ | ❌ |
| Attribute | 7 | ✅ | ❌ |
| Campaign | 10 | ✅ | ✅ |
| Promotion | 9 | ✅ | ❌ |
| Voucher | 16 | ✅ (some) | ❌ |
| Supplier | 6 | ✅ | ❌ |
| Stock Import | 5 | ✅ | ❌ |
| Stock Export | 6 | ✅ | ❌ |
| Inventory Report | 11 | ❌ | ❌ |
| **TOTAL** | **120** | - | - |

---

## 🛠️ Technical Details

### API Framework
- **Spring Boot**: 3.4.9
- **Java**: 21
- **Database**: PostgreSQL 14

### Response Format
All responses follow standard REST conventions:
- **200 OK**: Successful GET/PUT
- **201 Created**: Successful POST
- **204 No Content**: Successful DELETE
- **400 Bad Request**: Validation error
- **401 Unauthorized**: Missing/invalid token
- **404 Not Found**: Resource not found

### Pagination
Paginated endpoints support these query parameters:
- `page`: Page number (0-based, default: 0)
- `size`: Page size (default: 10)
- `sortBy`: Sort field (default: varies by endpoint)
- `sortDir`: Sort direction (`ASC`/`DESC`, default: varies)

**Response Structure:**
```json
{
  "content": [...],
  "pageable": {...},
  "totalPages": 10,
  "totalElements": 95,
  "size": 10,
  "number": 0
}
```

---

## 📞 Support

For issues or questions about these collections:
1. Check the [API Documentation](../API%20Document/API_OVERVIEW.md)
2. Review the controller source code in `src/main/java/vn/techbox/techbox_store/`
3. Check the [README](../../README.md) for project setup

---

## 📄 License

These Postman collections are part of the TechBox Store project.

---

**Generated:** 2025-10-06  
**Last Updated:** 2025-10-06  
**Total API Endpoints:** 120  
**Collections:** 14
