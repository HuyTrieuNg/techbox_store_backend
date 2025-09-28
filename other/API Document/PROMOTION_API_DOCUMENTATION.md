# Promotion API Documentation

## Overview
The Promotion API manages promotional rules and discounts that can be applied to products. Promotions are linked to campaigns and define discount calculation logic.

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
  "ruleName": "Electronics 25% Off",
  "discountType": "PERCENTAGE",
  "discountValue": 25.00,
  "minOrderAmount": 100.00,
  "maxDiscountAmount": 200.00,
  "campaignId": 1,
  "campaignName": "Summer Sale 2024",
  "productVariationId": null,
  "isActive": true,
  "createdAt": "2024-06-01T10:00:00",
  "updatedAt": "2024-06-01T10:00:00"
}
```

### Promotion Calculation Request
```json
{
  "productVariationId": 123,
  "originalPrice": 150.00,
  "quantity": 2,
  "orderAmount": 500.00
}
```

### Promotion Calculation Response
```json
{
  "productVariationId": 123,
  "originalPrice": 150.00,
  "finalPrice": 112.50,
  "totalDiscount": 75.00,
  "applicablePromotions": [
    {
      "id": 1,
      "ruleName": "Electronics 25% Off",
      "discountType": "PERCENTAGE",
      "discountValue": 25.00,
      "discountAmount": 37.50
    }
  ],
  "quantity": 2,
  "orderAmount": 500.00
}
```

---

## Promotion Management Endpoints

### 1. Create Promotion

**POST** `/api/promotions`

Creates a new promotion rule.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "ruleName": "Fashion Week 30% Off",
  "discountType": "PERCENTAGE",
  "discountValue": 30.00,
  "minOrderAmount": 75.00,
  "maxDiscountAmount": 150.00,
  "campaignId": 2,
  "productVariationId": 456
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/promotions" \
  -H "Content-Type: application/json" \
  -d '{
    "ruleName": "Fashion Week 30% Off",
    "discountType": "PERCENTAGE",
    "discountValue": 30.00,
    "minOrderAmount": 75.00,
    "maxDiscountAmount": 150.00,
    "campaignId": 2,
    "productVariationId": 456
  }'
```

**Success Response (201 Created):**
```json
{
  "id": 25,
  "ruleName": "Fashion Week 30% Off",
  "discountType": "PERCENTAGE",
  "discountValue": 30.00,
  "minOrderAmount": 75.00,
  "maxDiscountAmount": 150.00,
  "campaignId": 2,
  "campaignName": "Fashion Week 2024",
  "productVariationId": 456,
  "isActive": true,
  "createdAt": "2024-09-15T10:30:00",
  "updatedAt": "2024-09-15T10:30:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Invalid promotion rule: Campaign not found"
}
```

---

### 2. Update Promotion

**PUT** `/api/promotions/{id}`

Updates an existing promotion rule.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "ruleName": "Enhanced Fashion Week 35% Off",
  "discountValue": 35.00,
  "maxDiscountAmount": 200.00
}
```

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/promotions/25" \
  -H "Content-Type: application/json" \
  -d '{
    "ruleName": "Enhanced Fashion Week 35% Off",
    "discountValue": 35.00,
    "maxDiscountAmount": 200.00
  }'
```

**Success Response (200 OK):**
```json
{
  "id": 25,
  "ruleName": "Enhanced Fashion Week 35% Off",
  "discountType": "PERCENTAGE",
  "discountValue": 35.00,
  "minOrderAmount": 75.00,
  "maxDiscountAmount": 200.00,
  "campaignId": 2,
  "campaignName": "Fashion Week 2024",
  "productVariationId": 456,
  "isActive": true,
  "createdAt": "2024-09-15T10:30:00",
  "updatedAt": "2024-09-20T14:15:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Invalid promotion update: Discount value must be positive"
}
```

---

### 3. Get Promotion by ID

**GET** `/api/promotions/{id}`

Retrieves a specific promotion by its ID.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions/25"
```

**Success Response (200 OK):**
```json
{
  "id": 25,
  "ruleName": "Enhanced Fashion Week 35% Off",
  "discountType": "PERCENTAGE",
  "discountValue": 35.00,
  "minOrderAmount": 75.00,
  "maxDiscountAmount": 200.00,
  "campaignId": 2,
  "campaignName": "Fashion Week 2024",
  "productVariationId": 456,
  "isActive": true,
  "createdAt": "2024-09-15T10:30:00",
  "updatedAt": "2024-09-20T14:15:00"
}
```

**Error Response (404 Not Found):**
```json
{
  "error": "Promotion not found"
}
```

---

### 4. Get All Promotions (Paginated)

**GET** `/api/promotions`

Retrieves all promotions with pagination and sorting.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-based) |
| size | Integer | 10 | Page size |
| sortBy | String | createdAt | Sort field |
| sortDir | String | DESC | Sort direction (ASC/DESC) |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions?page=0&size=20&sortBy=discountValue&sortDir=DESC"
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 25,
      "ruleName": "Enhanced Fashion Week 35% Off",
      "discountType": "PERCENTAGE",
      "discountValue": 35.00,
      "minOrderAmount": 75.00,
      "maxDiscountAmount": 200.00,
      "campaignId": 2,
      "campaignName": "Fashion Week 2024",
      "productVariationId": 456,
      "isActive": true,
      "createdAt": "2024-09-15T10:30:00",
      "updatedAt": "2024-09-20T14:15:00"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

---

### 5. Get Promotions by Campaign

**GET** `/api/promotions/campaign/{campaignId}`

Retrieves all promotions associated with a specific campaign.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions/campaign/2"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 25,
    "ruleName": "Enhanced Fashion Week 35% Off",
    "discountType": "PERCENTAGE",
    "discountValue": 35.00,
    "minOrderAmount": 75.00,
    "maxDiscountAmount": 200.00,
    "campaignId": 2,
    "campaignName": "Fashion Week 2024",
    "productVariationId": 456,
    "isActive": true,
    "createdAt": "2024-09-15T10:30:00",
    "updatedAt": "2024-09-20T14:15:00"
  }
]
```

---

### 6. Get Promotions by Product Variation

**GET** `/api/promotions/product-variation/{productVariationId}`

Retrieves all promotions applicable to a specific product variation.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions/product-variation/456"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 25,
    "ruleName": "Enhanced Fashion Week 35% Off",
    "discountType": "PERCENTAGE",
    "discountValue": 35.00,
    "minOrderAmount": 75.00,
    "maxDiscountAmount": 200.00,
    "campaignId": 2,
    "campaignName": "Fashion Week 2024",
    "productVariationId": 456,
    "isActive": true,
    "createdAt": "2024-09-15T10:30:00",
    "updatedAt": "2024-09-20T14:15:00"
  }
]
```

---

### 7. Delete Promotion

**DELETE** `/api/promotions/{id}`

Deletes a promotion by ID.

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/promotions/25"
```

**Success Response (204 No Content):**
```
(Empty response body)
```

**Error Response (404 Not Found):**
```json
{
  "error": "Promotion not found"
}
```

---

## Promotion Calculation Endpoints

### 8. Calculate Promotions (POST)

**POST** `/api/promotions/calculate`

Calculates applicable promotions for a product with detailed parameters.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "productVariationId": 456,
  "originalPrice": 120.00,
  "quantity": 3,
  "orderAmount": 600.00
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/promotions/calculate" \
  -H "Content-Type: application/json" \
  -d '{
    "productVariationId": 456,
    "originalPrice": 120.00,
    "quantity": 3,
    "orderAmount": 600.00
  }'
```

**Success Response (200 OK):**
```json
{
  "productVariationId": 456,
  "originalPrice": 120.00,
  "finalPrice": 78.00,
  "totalDiscount": 126.00,
  "applicablePromotions": [
    {
      "id": 25,
      "ruleName": "Enhanced Fashion Week 35% Off",
      "discountType": "PERCENTAGE",
      "discountValue": 35.00,
      "discountAmount": 42.00
    }
  ],
  "quantity": 3,
  "orderAmount": 600.00
}
```

---

### 9. Calculate Promotions (GET)

**GET** `/api/promotions/product-variation/{productVariationId}/calculate`

Calculates applicable promotions using query parameters.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| originalPrice | Decimal | Yes | Original product price |
| quantity | Integer | No | Quantity (default: 1) |
| orderAmount | Decimal | Yes | Total order amount |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/promotions/product-variation/456/calculate?originalPrice=120.00&quantity=2&orderAmount=400.00"
```

**Success Response (200 OK):**
```json
{
  "productVariationId": 456,
  "originalPrice": 120.00,
  "finalPrice": 78.00,
  "totalDiscount": 84.00,
  "applicablePromotions": [
    {
      "id": 25,
      "ruleName": "Enhanced Fashion Week 35% Off",
      "discountType": "PERCENTAGE",
      "discountValue": 35.00,
      "discountAmount": 42.00
    }
  ],
  "quantity": 2,
  "orderAmount": 400.00
}
```

---

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Successful GET/POST request
- `201 Created` - Successful promotion creation
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Validation errors, invalid promotion rules
- `404 Not Found` - Promotion not found
- `500 Internal Server Error` - Server errors

### Validation Rules
- **Rule name**: Required, non-empty string
- **Discount type**: Must be PERCENTAGE or FIXED
- **Discount value**: Must be positive number
- **Min order amount**: Must be non-negative
- **Max discount amount**: Must be positive if provided
- **Campaign**: Must exist if campaignId provided
- **Product variation**: Must exist if productVariationId provided

---

## Business Rules

### Discount Types
1. **PERCENTAGE**: Applies percentage discount up to maxDiscountAmount
2. **FIXED**: Applies fixed amount discount

### Promotion Logic
- **Campaign Association**: Promotions can be linked to campaigns
- **Product Specific**: Promotions can target specific product variations
- **Order Minimum**: Promotions can require minimum order amount
- **Discount Cap**: Maximum discount amount can be set for percentage discounts

### Calculation Rules
1. **Eligibility Check**: Verify product and order amount requirements
2. **Discount Calculation**: Apply percentage or fixed discount
3. **Cap Application**: Apply maximum discount limit if set
4. **Multiple Promotions**: Stack compatible promotions
5. **Final Price**: Calculate discounted price per item

---

## Performance Considerations

### Database Queries
- Indexes on frequently queried fields (campaignId, productVariationId, isActive)
- Efficient filtering for active promotions
- Optimized joins for campaign and product data

### Calculation Performance
- In-memory calculation of discount amounts
- Minimal database queries during calculation
- Cached promotion rules for frequent calculations

### API Response
- Lightweight responses for list endpoints
- Detailed calculation results with breakdown
- Efficient filtering by campaign and product variation

---

## Integration Notes

### Campaign Integration
- Promotions inherit campaign lifecycle
- Campaign deactivation affects promotion availability
- Campaign dates may influence promotion applicability

### Product Integration
- Product variation specific promotions
- Category-wide promotions via product associations
- Inventory considerations for promotional pricing

### Order Integration
- Real-time promotion calculation during checkout
- Order amount validation for minimum requirements
- Promotion application tracking for analytics