# Promotion API Documentation

## Overview
The Promotion API manages discount promotions tied to campaigns. Promotions define specific discount rules, applicable products, and conditions.

## Base URL
```
http://localhost:8080/api/promotions
```

## Authentication
All endpoints require authentication (implementation depends on your auth system).

## Models

### Promotion Model
```json
{
  "id": 1,
  "name": "Summer Electronics Discount",
  "description": "25% off all electronics during summer sale",
  "discountType": "PERCENTAGE",
  "discountValue": 25.00,
  "minOrderAmount": 100.00,
  "maxDiscountAmount": 500.00,
  "usageLimit": 1000,
  "usedCount": 45,
  "isActive": true,
  "startDate": "2024-06-01T00:00:00",
  "endDate": "2024-08-31T23:59:59",
  "createdAt": "2024-05-15T10:00:00",
  "updatedAt": "2024-05-15T10:00:00",
  "deletedAt": null,
  "campaignId": 1,
  "campaignName": "Summer Sale 2024",
  "applicableProducts": [
    {
      "productId": 1,
      "productName": "iPhone 15 Pro",
      "sku": "IP15P-256-BLU"
    }
  ],
  "remainingUsage": 955,
  "isExpired": false,
  "isUsageLimitReached": false
}
```

### Discount Types
- `PERCENTAGE` - Percentage discount (e.g., 25% off)
- `FIXED_AMOUNT` - Fixed amount discount (e.g., $50 off)
- `FREE_SHIPPING` - Free shipping promotion

---

## API Endpoints

### 1. Create Promotion

**POST** `/api/promotions`

Creates a new promotion linked to a campaign.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "name": "Black Friday Electronics Deal",
  "description": "Huge discounts on electronics for Black Friday",
  "discountType": "PERCENTAGE",
  "discountValue": 30.00,
  "minOrderAmount": 200.00,
  "maxDiscountAmount": 1000.00,
  "usageLimit": 500,
  "startDate": "2024-11-29T00:00:00",
  "endDate": "2024-11-29T23:59:59",
  "campaignId": 2,
  "productIds": [1, 2, 3, 4, 5]
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/promotions" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Black Friday Electronics Deal",
    "description": "Huge discounts on electronics for Black Friday",
    "discountType": "PERCENTAGE",
    "discountValue": 30.00,
    "minOrderAmount": 200.00,
    "maxDiscountAmount": 1000.00,
    "usageLimit": 500,
    "startDate": "2024-11-29T00:00:00",
    "endDate": "2024-11-29T23:59:59",
    "campaignId": 2,
    "productIds": [1, 2, 3, 4, 5]
  }'
```

**Success Response (201 Created):**
```json
{
  "id": 5,
  "name": "Black Friday Electronics Deal",
  "description": "Huge discounts on electronics for Black Friday",
  "discountType": "PERCENTAGE",
  "discountValue": 30.00,
  "minOrderAmount": 200.00,
  "maxDiscountAmount": 1000.00,
  "usageLimit": 500,
  "usedCount": 0,
  "isActive": true,
  "startDate": "2024-11-29T00:00:00",
  "endDate": "2024-11-29T23:59:59",
  "createdAt": "2024-11-20T14:30:00",
  "updatedAt": "2024-11-20T14:30:00",
  "deletedAt": null,
  "campaignId": 2,
  "campaignName": "Black Friday 2024",
  "applicableProducts": [
    {
      "productId": 1,
      "productName": "iPhone 15 Pro",
      "sku": "IP15P-256-BLU"
    },
    {
      "productId": 2,
      "productName": "Samsung Galaxy S24",
      "sku": "SGS24-512-BLK"
    }
  ],
  "remainingUsage": 500,
  "isExpired": false,
  "isUsageLimitReached": false
}
```

**Error Responses:**
```json
// 400 Bad Request - Validation Error
{
  "error": "Promotion name must be unique within the campaign"
}

// 404 Not Found - Campaign not found
{
  "error": "Campaign not found with ID: 999"
}

// 400 Bad Request - Date validation
{
  "error": "Promotion dates must be within campaign period"
}
```

---

### 2. Update Promotion

**PUT** `/api/promotions/{id}`

Updates an existing promotion.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "name": "Extended Black Friday Electronics Deal",
  "description": "Extended huge discounts on electronics",
  "discountValue": 35.00,
  "maxDiscountAmount": 1500.00,
  "usageLimit": 1000,
  "endDate": "2024-12-01T23:59:59",
  "productIds": [1, 2, 3, 4, 5, 6, 7]
}
```

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/promotions/5" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Extended Black Friday Electronics Deal",
    "description": "Extended huge discounts on electronics",
    "discountValue": 35.00,
    "maxDiscountAmount": 1500.00,
    "usageLimit": 1000,
    "endDate": "2024-12-01T23:59:59",
    "productIds": [1, 2, 3, 4, 5, 6, 7]
  }'
```

**Success Response (200 OK):**
```json
{
  "id": 5,
  "name": "Extended Black Friday Electronics Deal",
  "description": "Extended huge discounts on electronics",
  "discountType": "PERCENTAGE",
  "discountValue": 35.00,
  "minOrderAmount": 200.00,
  "maxDiscountAmount": 1500.00,
  "usageLimit": 1000,
  "usedCount": 0,
  "isActive": true,
  "startDate": "2024-11-29T00:00:00",
  "endDate": "2024-12-01T23:59:59",
  "createdAt": "2024-11-20T14:30:00",
  "updatedAt": "2024-11-25T09:15:00",
  "deletedAt": null,
  "campaignId": 2,
  "campaignName": "Black Friday 2024",
  "applicableProducts": [
    {
      "productId": 1,
      "productName": "iPhone 15 Pro",
      "sku": "IP15P-256-BLU"
    },
    {
      "productId": 2,
      "productName": "Samsung Galaxy S24",
      "sku": "SGS24-512-BLK"
    }
  ],
  "remainingUsage": 1000,
  "isExpired": false,
  "isUsageLimitReached": false
}
```

**Error Responses:**
```json
// 404 Not Found
{
  "error": "Promotion not found with ID: 999"
}

// 400 Bad Request
{
  "error": "Cannot update expired promotion"
}
```

---

### 3. Get Promotion by ID

**GET** `/api/promotions/{id}`

Retrieves a specific promotion by its ID.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions/5"
```

**Success Response (200 OK):**
```json
{
  "id": 5,
  "name": "Black Friday Electronics Deal",
  "description": "Huge discounts on electronics for Black Friday",
  "discountType": "PERCENTAGE",
  "discountValue": 30.00,
  "minOrderAmount": 200.00,
  "maxDiscountAmount": 1000.00,
  "usageLimit": 500,
  "usedCount": 45,
  "isActive": true,
  "startDate": "2024-11-29T00:00:00",
  "endDate": "2024-11-29T23:59:59",
  "createdAt": "2024-11-20T14:30:00",
  "updatedAt": "2024-11-20T14:30:00",
  "deletedAt": null,
  "campaignId": 2,
  "campaignName": "Black Friday 2024",
  "applicableProducts": [
    {
      "productId": 1,
      "productName": "iPhone 15 Pro",
      "sku": "IP15P-256-BLU"
    }
  ],
  "remainingUsage": 455,
  "isExpired": false,
  "isUsageLimitReached": false
}
```

**Error Response:**
```json
// 404 Not Found
{
  "error": "Promotion not found with ID: 999"
}
```

---

### 4. Get All Promotions (Paginated)

**GET** `/api/promotions`

Retrieves all promotions with pagination and optional filtering.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-based) |
| size | Integer | 10 | Page size (1-100) |
| sortBy | String | createdAt | Sort field |
| sortDir | String | DESC | Sort direction (ASC/DESC) |
| campaignId | Long | - | Filter by campaign ID |
| discountType | String | - | Filter by discount type |
| isActive | Boolean | - | Filter by active status |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions?page=0&size=20&campaignId=2&isActive=true"
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 5,
      "name": "Black Friday Electronics Deal",
      "description": "Huge discounts on electronics",
      "discountType": "PERCENTAGE",
      "discountValue": 30.00,
      "minOrderAmount": 200.00,
      "maxDiscountAmount": 1000.00,
      "usageLimit": 500,
      "usedCount": 45,
      "isActive": true,
      "startDate": "2024-11-29T00:00:00",
      "endDate": "2024-11-29T23:59:59",
      "createdAt": "2024-11-20T14:30:00",
      "updatedAt": "2024-11-20T14:30:00",
      "deletedAt": null,
      "campaignId": 2,
      "campaignName": "Black Friday 2024",
      "applicableProducts": [
        {
          "productId": 1,
          "productName": "iPhone 15 Pro",
          "sku": "IP15P-256-BLU"
        }
      ],
      "remainingUsage": 455,
      "isExpired": false,
      "isUsageLimitReached": false
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
    "pageNumber": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 1,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

---

### 5. Get Promotions by Campaign

**GET** `/api/promotions/campaign/{campaignId}`

Retrieves all promotions for a specific campaign.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions/campaign/2"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 5,
    "name": "Black Friday Electronics Deal",
    "description": "Huge discounts on electronics",
    "discountType": "PERCENTAGE",
    "discountValue": 30.00,
    "minOrderAmount": 200.00,
    "maxDiscountAmount": 1000.00,
    "usageLimit": 500,
    "usedCount": 45,
    "isActive": true,
    "startDate": "2024-11-29T00:00:00",
    "endDate": "2024-11-29T23:59:59",
    "createdAt": "2024-11-20T14:30:00",
    "updatedAt": "2024-11-20T14:30:00",
    "deletedAt": null,
    "campaignId": 2,
    "campaignName": "Black Friday 2024",
    "applicableProducts": [
      {
        "productId": 1,
        "productName": "iPhone 15 Pro",
        "sku": "IP15P-256-BLU"
      }
    ],
    "remainingUsage": 455,
    "isExpired": false,
    "isUsageLimitReached": false
  },
  {
    "id": 6,
    "name": "Black Friday Accessories Deal",
    "description": "Fixed discount on accessories",
    "discountType": "FIXED_AMOUNT",
    "discountValue": 50.00,
    "minOrderAmount": 100.00,
    "maxDiscountAmount": 50.00,
    "usageLimit": 200,
    "usedCount": 12,
    "isActive": true,
    "startDate": "2024-11-29T00:00:00",
    "endDate": "2024-11-29T23:59:59",
    "createdAt": "2024-11-21T10:00:00",
    "updatedAt": "2024-11-21T10:00:00",
    "deletedAt": null,
    "campaignId": 2,
    "campaignName": "Black Friday 2024",
    "applicableProducts": [
      {
        "productId": 10,
        "productName": "AirPods Pro",
        "sku": "APP-2ND-WHT"
      }
    ],
    "remainingUsage": 188,
    "isExpired": false,
    "isUsageLimitReached": false
  }
]
```

---

### 6. Get Active Promotions

**GET** `/api/promotions/active`

Retrieves all currently active promotions.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions/active"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 7,
    "name": "Current Winter Promo",
    "description": "Winter season discounts",
    "discountType": "PERCENTAGE",
    "discountValue": 20.00,
    "minOrderAmount": 150.00,
    "maxDiscountAmount": 300.00,
    "usageLimit": 300,
    "usedCount": 78,
    "isActive": true,
    "startDate": "2024-12-01T00:00:00",
    "endDate": "2024-12-31T23:59:59",
    "createdAt": "2024-11-25T09:00:00",
    "updatedAt": "2024-11-25T09:00:00",
    "deletedAt": null,
    "campaignId": 3,
    "campaignName": "Winter Sale 2024",
    "applicableProducts": [
      {
        "productId": 15,
        "productName": "Winter Jacket",
        "sku": "WJ-L-BLK"
      }
    ],
    "remainingUsage": 222,
    "isExpired": false,
    "isUsageLimitReached": false
  }
]
```

---

### 7. Get Applicable Promotions for Product

**GET** `/api/promotions/product/{productId}`

Retrieves all promotions applicable to a specific product.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions/product/1"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 5,
    "name": "Black Friday Electronics Deal",
    "description": "Huge discounts on electronics",
    "discountType": "PERCENTAGE",
    "discountValue": 30.00,
    "minOrderAmount": 200.00,
    "maxDiscountAmount": 1000.00,
    "usageLimit": 500,
    "usedCount": 45,
    "isActive": true,
    "startDate": "2024-11-29T00:00:00",
    "endDate": "2024-11-29T23:59:59",
    "createdAt": "2024-11-20T14:30:00",
    "updatedAt": "2024-11-20T14:30:00",
    "deletedAt": null,
    "campaignId": 2,
    "campaignName": "Black Friday 2024",
    "applicableProducts": [
      {
        "productId": 1,
        "productName": "iPhone 15 Pro",
        "sku": "IP15P-256-BLU"
      }
    ],
    "remainingUsage": 455,
    "isExpired": false,
    "isUsageLimitReached": false
  }
]
```

---

### 8. Activate/Deactivate Promotion

**PATCH** `/api/promotions/{id}/toggle-status`

Toggles the active status of a promotion.

**Sample Request (Deactivate):**
```bash
curl -X PATCH "http://localhost:8080/api/promotions/5/toggle-status"
```

**Success Response (200 OK):**
```json
{
  "id": 5,
  "name": "Black Friday Electronics Deal",
  "description": "Huge discounts on electronics",
  "discountType": "PERCENTAGE",
  "discountValue": 30.00,
  "minOrderAmount": 200.00,
  "maxDiscountAmount": 1000.00,
  "usageLimit": 500,
  "usedCount": 45,
  "isActive": false,
  "startDate": "2024-11-29T00:00:00",
  "endDate": "2024-11-29T23:59:59",
  "createdAt": "2024-11-20T14:30:00",
  "updatedAt": "2024-11-25T16:30:00",
  "deletedAt": null,
  "campaignId": 2,
  "campaignName": "Black Friday 2024",
  "applicableProducts": [
    {
      "productId": 1,
      "productName": "iPhone 15 Pro",
      "sku": "IP15P-256-BLU"
    }
  ],
  "remainingUsage": 455,
  "isExpired": false,
  "isUsageLimitReached": false
}
```

---

### 9. Calculate Promotion Discount

**POST** `/api/promotions/{id}/calculate-discount`

Calculates the discount amount for a given order total.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "orderAmount": 500.00,
  "productIds": [1, 2]
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/promotions/5/calculate-discount" \
  -H "Content-Type: application/json" \
  -d '{
    "orderAmount": 500.00,
    "productIds": [1, 2]
  }'
```

**Success Response (200 OK):**
```json
{
  "promotionId": 5,
  "promotionName": "Black Friday Electronics Deal",
  "orderAmount": 500.00,
  "discountType": "PERCENTAGE",
  "discountValue": 30.00,
  "calculatedDiscount": 150.00,
  "finalAmount": 350.00,
  "applicableProducts": [
    {
      "productId": 1,
      "productName": "iPhone 15 Pro",
      "sku": "IP15P-256-BLU"
    },
    {
      "productId": 2,
      "productName": "Samsung Galaxy S24",
      "sku": "SGS24-512-BLK"
    }
  ],
  "meetsMinimumAmount": true,
  "isEligible": true
}
```

**Error Responses:**
```json
// 400 Bad Request - Not eligible
{
  "error": "Order amount does not meet minimum requirement of 200.00"
}

// 400 Bad Request - No applicable products
{
  "error": "No applicable products in the order"
}
```

---

### 10. Delete Promotion (Soft Delete)

**DELETE** `/api/promotions/{id}`

Soft deletes a promotion.

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/promotions/5"
```

**Success Response (204 No Content):**
```
(Empty response body)
```

**Error Response:**
```json
// 404 Not Found
{
  "error": "Promotion not found with ID: 999"
}
```

---

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Successful GET/PUT/PATCH request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Validation errors, business logic errors
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server errors

### Validation Rules
- `name`: Required, 3-255 characters, unique within campaign
- `discountValue`: Required, must be positive
- `discountType`: Required, valid enum value
- `usageLimit`: Optional, must be positive if provided
- `minOrderAmount`: Optional, must be positive if provided
- `maxDiscountAmount`: Required for PERCENTAGE type
- `startDate/endDate`: Must be within campaign period
- `campaignId`: Required, must reference existing campaign
- `productIds`: Optional, must reference existing products

---

## Business Rules

### Promotion Lifecycle
1. **Creation**: Must be linked to an active campaign
2. **Activation**: Can be toggled active/inactive
3. **Usage Tracking**: Automatically tracks usage count
4. **Expiration**: Automatically determined by date range
5. **Soft Delete**: Preserves historical data

### Discount Calculation
- **PERCENTAGE**: `min(orderAmount * (discountValue/100), maxDiscountAmount)`
- **FIXED_AMOUNT**: `min(discountValue, orderAmount)`
- **FREE_SHIPPING**: Special handling in order processing

### Eligibility Rules
- Order must meet minimum amount requirement
- At least one product must be in applicable products list
- Promotion must be active and not expired
- Usage limit must not be exceeded

---

## Integration Notes

### Order Processing Integration
```java
// Example usage in order service
List<Promotion> applicablePromotions = promotionService.getApplicablePromotions(productIds);
DiscountCalculation bestDiscount = promotionService.calculateBestDiscount(applicablePromotions, orderAmount);
```

### Campaign Relationship
- Promotions are tied to campaigns
- Promotion dates must fall within campaign dates
- Deleting a campaign soft deletes all associated promotions