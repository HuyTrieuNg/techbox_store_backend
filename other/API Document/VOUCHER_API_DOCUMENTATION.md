# Voucher API Documentation

## Overview
The Voucher API manages discount vouchers with unique codes that customers can apply to their orders. Vouchers are independent from campaigns and promotions, providing flexible discount mechanisms.

## Base URL
```
http://localhost:8080/api/vouchers
```

## Authentication
All endpoints require authentication (implementation depends on your auth system).

## Models

### Voucher Model
```json
{
  "id": 1,
  "code": "WELCOME20",
  "voucherType": "PERCENTAGE",
  "value": 20.00,
  "minOrderAmount": 100.00,
  "usageLimit": 1000,
  "usedCount": 45,
  "validFrom": "2024-12-01T00:00:00",
  "validUntil": "2024-12-31T23:59:59",
  "createdAt": "2024-11-15T10:00:00",
  "updatedAt": "2024-11-15T10:00:00",
  "isActive": true,
  "isValid": true,
  "hasUsageLeft": true,
  "displayValue": "20%",
  "displayValidityPeriod": "2024-12-01T00:00:00 - 2024-12-31T23:59:59",
  "status": "ACTIVE"
}
```

### Voucher Types
- `PERCENTAGE` - Percentage discount (e.g., 20% off)
- `FIXED_AMOUNT` - Fixed amount discount (e.g., $50 off)

### Voucher Status
- `ACTIVE` - Voucher is active and can be used
- `EXPIRED` - Voucher has passed its validity period
- `EXHAUSTED` - Voucher has reached its usage limit
- `DELETED` - Voucher has been soft deleted

### User Voucher Usage Model
```json
{
  "id": 1,
  "userId": 123,
  "voucherId": 1,
  "orderId": 456,
  "usedAt": "2024-12-15T14:30:00",
  "discountAmount": 25.50
}
```

---

## API Endpoints

### 1. Create Voucher

**POST** `/api/vouchers`

Creates a new voucher with unique code.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "code": "BLACKFRIDAY30",
  "voucherType": "PERCENTAGE",
  "value": 30.00,
  "minOrderAmount": 200.00,
  "usageLimit": 500,
  "validFrom": "2024-11-29T00:00:00",
  "validUntil": "2024-11-29T23:59:59"
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/vouchers" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "BLACKFRIDAY30",
    "voucherType": "PERCENTAGE",
    "value": 30.00,
    "minOrderAmount": 200.00,
    "usageLimit": 500,
    "validFrom": "2024-11-29T00:00:00",
    "validUntil": "2024-11-29T23:59:59"
  }'
```

**Success Response (201 Created):**
```json
{
  "id": 5,
  "code": "BLACKFRIDAY30",
  "voucherType": "PERCENTAGE",
  "value": 30.00,
  "minOrderAmount": 200.00,
  "usageLimit": 500,
  "usedCount": 0,
  "validFrom": "2024-11-29T00:00:00",
  "validUntil": "2024-11-29T23:59:59",
  "createdAt": "2024-11-20T14:30:00",
  "updatedAt": "2024-11-20T14:30:00",
  "isActive": true,
  "isValid": true,
  "hasUsageLeft": true,
  "displayValue": "30%",
  "displayValidityPeriod": "2024-11-29T00:00:00 - 2024-11-29T23:59:59",
  "status": "ACTIVE"
}
```

**Error Responses:**
```json
// 409 Conflict - Duplicate code
{
  "error": "Voucher code 'BLACKFRIDAY30' already exists"
}

// 400 Bad Request - Validation error
{
  "error": "Valid until date must be after valid from date"
}
```

---

### 2. Update Voucher

**PUT** `/api/vouchers/{id}`

Updates an existing voucher.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "value": 35.00,
  "minOrderAmount": 150.00,
  "usageLimit": 750,
  "validUntil": "2024-12-01T23:59:59"
}
```

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/vouchers/5" \
  -H "Content-Type: application/json" \
  -d '{
    "value": 35.00,
    "minOrderAmount": 150.00,
    "usageLimit": 750,
    "validUntil": "2024-12-01T23:59:59"
  }'
```

**Success Response (200 OK):**
```json
{
  "id": 5,
  "code": "BLACKFRIDAY30",
  "voucherType": "PERCENTAGE",
  "value": 35.00,
  "minOrderAmount": 150.00,
  "usageLimit": 750,
  "usedCount": 0,
  "validFrom": "2024-11-29T00:00:00",
  "validUntil": "2024-12-01T23:59:59",
  "createdAt": "2024-11-20T14:30:00",
  "updatedAt": "2024-11-25T09:15:00",
  "isActive": true,
  "isValid": true,
  "hasUsageLeft": true,
  "displayValue": "35%",
  "displayValidityPeriod": "2024-11-29T00:00:00 - 2024-12-01T23:59:59",
  "status": "ACTIVE"
}
```

---

### 3. Get Voucher by ID

**GET** `/api/vouchers/{id}`

Retrieves a specific voucher by its ID.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/vouchers/5"
```

**Success Response (200 OK):**
```json
{
  "id": 5,
  "code": "BLACKFRIDAY30",
  "voucherType": "PERCENTAGE",
  "value": 30.00,
  "minOrderAmount": 200.00,
  "usageLimit": 500,
  "usedCount": 45,
  "validFrom": "2024-11-29T00:00:00",
  "validUntil": "2024-11-29T23:59:59",
  "createdAt": "2024-11-20T14:30:00",
  "updatedAt": "2024-11-20T14:30:00",
  "isActive": true,
  "isValid": true,
  "hasUsageLeft": true,
  "displayValue": "30%",
  "displayValidityPeriod": "2024-11-29T00:00:00 - 2024-11-29T23:59:59",
  "status": "ACTIVE"
}
```

**Error Response:**
```json
// 404 Not Found
{
  "error": "Voucher not found with ID: 999"
}
```

---

### 4. Get Voucher by Code

**GET** `/api/vouchers/code/{code}`

Retrieves a voucher by its unique code.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/vouchers/code/BLACKFRIDAY30"
```

**Success Response (200 OK):**
```json
{
  "id": 5,
  "code": "BLACKFRIDAY30",
  "voucherType": "PERCENTAGE",
  "value": 30.00,
  "minOrderAmount": 200.00,
  "usageLimit": 500,
  "usedCount": 45,
  "validFrom": "2024-11-29T00:00:00",
  "validUntil": "2024-11-29T23:59:59",
  "createdAt": "2024-11-20T14:30:00",
  "updatedAt": "2024-11-20T14:30:00",
  "isActive": true,
  "isValid": true,
  "hasUsageLeft": true,
  "displayValue": "30%",
  "displayValidityPeriod": "2024-11-29T00:00:00 - 2024-11-29T23:59:59",
  "status": "ACTIVE"
}
```

**Error Response:**
```json
// 404 Not Found
{
  "error": "Voucher not found with code: INVALID_CODE"
}
```

---

### 5. Get All Vouchers (Paginated)

**GET** `/api/vouchers`

Retrieves all vouchers with pagination and sorting.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-based) |
| size | Integer | 10 | Page size (1-100) |
| sortBy | String | createdAt | Sort field |
| sortDir | String | DESC | Sort direction (ASC/DESC) |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/vouchers?page=0&size=20&sortBy=validUntil&sortDir=ASC"
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 5,
      "code": "BLACKFRIDAY30",
      "voucherType": "PERCENTAGE",
      "value": 30.00,
      "minOrderAmount": 200.00,
      "usageLimit": 500,
      "usedCount": 45,
      "validFrom": "2024-11-29T00:00:00",
      "validUntil": "2024-11-29T23:59:59",
      "createdAt": "2024-11-20T14:30:00",
      "updatedAt": "2024-11-20T14:30:00",
      "isActive": true,
      "isValid": true,
      "hasUsageLeft": true,
      "displayValue": "30%",
      "displayValidityPeriod": "2024-11-29T00:00:00 - 2024-11-29T23:59:59",
      "status": "ACTIVE"
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

### 6. Get Valid Vouchers

**GET** `/api/vouchers/valid`

Retrieves all currently valid vouchers (within validity period and have usage left).

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-based) |
| size | Integer | 10 | Page size (1-100) |
| sortBy | String | validUntil | Sort field |
| sortDir | String | ASC | Sort direction |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/vouchers/valid?page=0&size=20"
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 3,
      "code": "WELCOME20",
      "voucherType": "PERCENTAGE",
      "value": 20.00,
      "minOrderAmount": 100.00,
      "usageLimit": 1000,
      "usedCount": 234,
      "validFrom": "2024-12-01T00:00:00",
      "validUntil": "2024-12-31T23:59:59",
      "createdAt": "2024-11-15T10:00:00",
      "updatedAt": "2024-11-15T10:00:00",
      "isActive": true,
      "isValid": true,
      "hasUsageLeft": true,
      "displayValue": "20%",
      "displayValidityPeriod": "2024-12-01T00:00:00 - 2024-12-31T23:59:59",
      "status": "ACTIVE"
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

### 7. Search Vouchers

**GET** `/api/vouchers/search`

Searches vouchers by code pattern.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| searchTerm | String | Yes | Search term for voucher code |
| page | Integer | No | Page number (default: 0) |
| size | Integer | No | Page size (default: 10) |
| sortBy | String | No | Sort field (default: createdAt) |
| sortDir | String | No | Sort direction (default: DESC) |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/vouchers/search?searchTerm=BLACK&page=0&size=10"
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 5,
      "code": "BLACKFRIDAY30",
      "voucherType": "PERCENTAGE",
      "value": 30.00,
      "minOrderAmount": 200.00,
      "usageLimit": 500,
      "usedCount": 45,
      "validFrom": "2024-11-29T00:00:00",
      "validUntil": "2024-11-29T23:59:59",
      "isActive": true,
      "isValid": true,
      "hasUsageLeft": true,
      "displayValue": "30%",
      "status": "ACTIVE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

---

### 8. Validate Voucher

**POST** `/api/vouchers/validate`

Validates if a voucher can be used for a specific order.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "code": "BLACKFRIDAY30",
  "orderAmount": 500.00,
  "userId": 123
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/vouchers/validate" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "BLACKFRIDAY30",
    "orderAmount": 500.00,
    "userId": 123
  }'
```

**Success Response (200 OK):**
```json
{
  "isValid": true,
  "voucherId": 5,
  "code": "BLACKFRIDAY30",
  "voucherType": "PERCENTAGE",
  "discountValue": 30.00,
  "orderAmount": 500.00,
  "discountAmount": 150.00,
  "finalAmount": 350.00,
  "message": "Voucher is valid and can be applied"
}
```

**Error Responses:**
```json
// Voucher not found
{
  "isValid": false,
  "message": "Voucher code 'INVALID_CODE' not found"
}

// Order amount too low
{
  "isValid": false,
  "voucherId": 5,
  "code": "BLACKFRIDAY30",
  "orderAmount": 50.00,
  "message": "Order amount must be at least $200.00"
}

// Voucher expired
{
  "isValid": false,
  "voucherId": 5,
  "code": "BLACKFRIDAY30",
  "message": "Voucher has expired"
}

// Usage limit reached
{
  "isValid": false,
  "voucherId": 5,
  "code": "BLACKFRIDAY30",
  "message": "Voucher usage limit has been reached"
}
```

---

### 9. Use Voucher

**POST** `/api/vouchers/use`

Marks a voucher as used for a specific order.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "code": "BLACKFRIDAY30",
  "userId": 123,
  "orderId": 456,
  "orderAmount": 500.00,
  "discountAmount": 150.00
}
```

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/vouchers/use" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "BLACKFRIDAY30",
    "userId": 123,
    "orderId": 456,
    "orderAmount": 500.00,
    "discountAmount": 150.00
  }'
```

**Success Response (200 OK):**
```json
"Voucher used successfully"
```

**Error Responses:**
```json
// 400 Bad Request - Voucher cannot be used
"Voucher has expired and cannot be used"

// 400 Bad Request - Already used by user
"User has already used this voucher"
```

---

### 10. Get Expired Vouchers

**GET** `/api/vouchers/expired`

Retrieves all expired vouchers.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/vouchers/expired"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "code": "SUMMER2024",
    "voucherType": "PERCENTAGE",
    "value": 25.00,
    "minOrderAmount": 150.00,
    "usageLimit": 300,
    "usedCount": 287,
    "validFrom": "2024-06-01T00:00:00",
    "validUntil": "2024-08-31T23:59:59",
    "createdAt": "2024-05-15T10:00:00",
    "updatedAt": "2024-05-15T10:00:00",
    "isActive": true,
    "isValid": false,
    "hasUsageLeft": true,
    "displayValue": "25%",
    "displayValidityPeriod": "2024-06-01T00:00:00 - 2024-08-31T23:59:59",
    "status": "EXPIRED"
  }
]
```

---

### 11. Get Vouchers Expiring Soon

**GET** `/api/vouchers/expiring-soon`

Retrieves vouchers expiring within specified days.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| days | Integer | 7 | Number of days ahead to check |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/vouchers/expiring-soon?days=3"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 7,
    "code": "ENDYEAR2024",
    "voucherType": "FIXED_AMOUNT",
    "value": 50.00,
    "minOrderAmount": 300.00,
    "usageLimit": 100,
    "usedCount": 45,
    "validFrom": "2024-12-20T00:00:00",
    "validUntil": "2024-12-31T23:59:59",
    "createdAt": "2024-12-15T14:00:00",
    "updatedAt": "2024-12-15T14:00:00",
    "isActive": true,
    "isValid": true,
    "hasUsageLeft": true,
    "displayValue": "$50.00",
    "displayValidityPeriod": "2024-12-20T00:00:00 - 2024-12-31T23:59:59",
    "status": "ACTIVE"
  }
]
```

---

### 12. Get User Voucher Usage

**GET** `/api/vouchers/usage/user/{userId}`

Retrieves all vouchers used by a specific user.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/vouchers/usage/user/123"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 15,
    "userId": 123,
    "voucherId": 5,
    "orderId": 456,
    "usedAt": "2024-11-29T14:30:00",
    "discountAmount": 150.00
  },
  {
    "id": 8,
    "userId": 123,
    "voucherId": 3,
    "orderId": 289,
    "usedAt": "2024-12-10T09:15:00",
    "discountAmount": 75.00
  }
]
```

---

### 13. Get Voucher Usage Count

**GET** `/api/vouchers/{voucherId}/usage-count`

Gets the number of times a voucher has been used.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/vouchers/5/usage-count"
```

**Success Response (200 OK):**
```json
45
```

---

### 14. Delete Voucher (Soft Delete)

**DELETE** `/api/vouchers/{id}`

Soft deletes a voucher.

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/vouchers/5"
```

**Success Response (204 No Content):**
```
(Empty response body)
```

**Error Response:**
```json
// 404 Not Found
{
  "error": "Voucher not found with ID: 999"
}
```

---

### 15. Restore Voucher

**POST** `/api/vouchers/{id}/restore`

Restores a soft-deleted voucher.

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/vouchers/5/restore"
```

**Success Response (200 OK):**
```
(Empty response body)
```

**Error Response:**
```json
// 404 Not Found
{
  "error": "Voucher not found with ID: 999"
}
```

---

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Successful GET/POST request
- `201 Created` - Successful POST request (voucher creation)
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Validation errors, business logic errors
- `404 Not Found` - Voucher not found
- `409 Conflict` - Duplicate voucher code
- `500 Internal Server Error` - Server errors

### Validation Rules
- `code`: Required, unique, 3-50 characters, alphanumeric with underscores/hyphens
- `voucherType`: Required, valid enum value (PERCENTAGE, FIXED_AMOUNT)
- `value`: Required, positive decimal
- `minOrderAmount`: Optional, non-negative decimal (default: 0)
- `usageLimit`: Required, positive integer
- `validFrom`: Required, valid ISO datetime format
- `validUntil`: Required, must be after validFrom
- `orderAmount`: Required for validation/usage, positive decimal
- `userId`: Required for usage tracking
- `orderId`: Required for voucher usage

---

## Business Rules

### Voucher Lifecycle
1. **Creation**: Voucher created with unique code and validity period
2. **Validation**: Check if voucher can be applied to order
3. **Usage**: Mark voucher as used and track usage count
4. **Expiration**: Automatic expiration based on validUntil date
5. **Exhaustion**: Automatic deactivation when usage limit reached
6. **Soft Delete**: Preserve historical usage data

### Validation Logic
- **Time validity**: Current time must be between validFrom and validUntil
- **Minimum order amount**: Order total must meet minimum requirement
- **Usage limit**: Must have remaining usage count
- **User restriction**: Each user can use a voucher only once
- **Active status**: Voucher must not be soft deleted

### Discount Calculation
- **PERCENTAGE**: `min(orderAmount * (value/100), orderAmount)`
- **FIXED_AMOUNT**: `min(value, orderAmount)`

### Usage Tracking
- **User-Voucher relationship**: Track which users used which vouchers
- **Order association**: Link voucher usage to specific orders
- **Usage count**: Automatic increment when voucher is used
- **Historical data**: Preserve usage data even after voucher deletion

---

## Integration Notes

### Order Processing Integration
```java
// Example usage in order service
VoucherValidationResponse validation = voucherService.validateVoucher(
    VoucherValidationRequest.builder()
        .code("BLACKFRIDAY30")
        .orderAmount(500.00)
        .userId(123)
        .build()
);

if (validation.isValid()) {
    // Apply discount to order
    BigDecimal discountAmount = validation.getDiscountAmount();
    
    // Use voucher
    voucherService.useVoucher(
        VoucherUseRequest.builder()
            .code("BLACKFRIDAY30")
            .userId(123)
            .orderId(456)
            .orderAmount(500.00)
            .discountAmount(discountAmount)
            .build()
    );
}
```

### Frontend Integration
```javascript
// Validate voucher code
const validateVoucher = async (code, orderAmount, userId) => {
  const response = await fetch('/api/vouchers/validate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ code, orderAmount, userId })
  });
  return await response.json();
};

// Get available vouchers for user
const getValidVouchers = async () => {
  const response = await fetch('/api/vouchers/valid');
  return await response.json();
};
```

---

## Security Considerations

### Code Generation
- Voucher codes should be unique and not easily guessable
- Consider using cryptographically secure random generation
- Avoid predictable patterns that could be exploited

### Usage Validation
- Always validate voucher server-side before applying discount
- Prevent duplicate usage by same user
- Check all validation rules before marking voucher as used

### Rate Limiting
- Implement rate limiting on validation endpoint to prevent brute force
- Monitor for suspicious voucher usage patterns
- Log all voucher validation and usage attempts

---

## Performance Optimization

### Database Indexing
- Index on voucher code for fast lookups
- Index on validity dates for expired/valid voucher queries
- Composite index on userId + voucherId for usage tracking

### Caching Strategy
- Cache frequently accessed vouchers
- Cache validation results for short periods
- Use Redis for session-based voucher validations

### Bulk Operations
- Batch voucher creation for promotional campaigns
- Bulk expiration processing for maintenance
- Efficient pagination for large voucher datasets