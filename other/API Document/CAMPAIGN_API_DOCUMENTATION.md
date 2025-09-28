# Campaign API Documentation

## Overview
The Campaign API manages marketing campaigns with integrated Cloudinary image upload functionality. Supports campaign lifecycle management with automatic image handling.

## Base URL
```
http://localhost:8080/api/campaigns
```

## Authentication
All endpoints require authentication (implementation depends on your auth system).

## Models

### Campaign Model
```json
{
  "id": 1,
  "name": "Summer Sale 2024",
  "description": "Great summer discounts on all products",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567890/campaign_images/summer_sale_2024.jpg",
  "imageID": "campaign_images/summer_sale_2024",
  "startDate": "2024-06-01T00:00:00",
  "endDate": "2024-08-31T23:59:59",
  "isActive": true,
  "status": "ACTIVE",
  "createdAt": "2024-05-15T10:00:00",
  "updatedAt": "2024-05-20T14:30:00"
---

## Campaign Endpoints

### 1. Create Campaign with Image Upload

**POST** `/api/campaigns`

Creates a new campaign with optional image upload to Cloudinary.

**Content-Type:** `multipart/form-data`

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | Yes | Campaign name |
| description | String | No | Campaign description |
| startDate | String | Yes | Start date (ISO 8601 format) |
| endDate | String | Yes | End date (ISO 8601 format) |
| image | File | No | Campaign image file |

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/campaigns" \
  -F "name=Winter Holiday Sale" \
  -F "description=Special holiday discounts for all customers" \
  -F "startDate=2024-12-01T00:00:00" \
  -F "endDate=2024-12-25T23:59:59" \
  -F "image=@/path/to/winter-holiday-banner.jpg"
```

**Success Response (201 Created):**
```json
{
  "id": 25,
  "name": "Winter Holiday Sale",
  "description": "Special holiday discounts for all customers",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567890/campaign_images/winter_holiday_sale.jpg",
  "imageID": "campaign_images/winter_holiday_sale",
  "startDate": "2024-12-01T00:00:00",
  "endDate": "2024-12-25T23:59:59",
  "isActive": true,
  "status": "SCHEDULED",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-15T10:30:00"
}
```

**Error Responses:**
```json
// 500 Internal Server Error - Image upload failed
{
  "error": "Failed to upload image: Connection timeout"
}

// 400 Bad Request - Campaign creation failed
{
  "error": "Failed to create campaign: Start date must be before end date"
}
```

---

### 2. Update Campaign with Image Replacement

**PUT** `/api/campaigns/{id}`

Updates an existing campaign. If a new image is provided, the old image is automatically deleted and replaced.

**Content-Type:** `multipart/form-data`

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | No | Updated campaign name |
| description | String | No | Updated description |
| startDate | String | No | Updated start date (ISO 8601) |
| endDate | String | No | Updated end date (ISO 8601) |
| image | File | No | New image (replaces existing) |

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/campaigns/25" \
  -F "name=Extended Winter Holiday Sale" \
  -F "description=Extended holiday discounts with more products" \
  -F "endDate=2024-12-31T23:59:59" \
  -F "image=@/path/to/updated-holiday-banner.jpg"
```

**Success Response (200 OK):**
```json
{
  "id": 25,
  "name": "Extended Winter Holiday Sale",
  "description": "Extended holiday discounts with more products",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567891/campaign_images/updated_winter_holiday_sale.jpg",
  "imageID": "campaign_images/updated_winter_holiday_sale",
  "startDate": "2024-12-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "isActive": true,
  "status": "SCHEDULED",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-20T09:15:00"
}
```

**Error Responses:**
```json
// 500 Internal Server Error - Image processing failed
{
  "error": "Failed to process image: Upload timeout"
}

// 404 Not Found - Campaign not found
{
  "error": "Campaign not found: Invalid campaign ID"
}

// 400 Bad Request - Update failed
{
  "error": "Failed to update campaign: Invalid date range"
}
```

---

### 3. Get Campaign by ID

**GET** `/api/campaigns/{id}`

Retrieves a specific campaign by its ID.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns/25"
```

**Success Response (200 OK):**
```json
{
  "id": 25,
  "name": "Extended Winter Holiday Sale",
  "description": "Extended holiday discounts with more products",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567891/campaign_images/updated_winter_holiday_sale.jpg",
  "imageID": "campaign_images/updated_winter_holiday_sale",
  "startDate": "2024-12-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "isActive": true,
  "status": "SCHEDULED",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-20T09:15:00"
}
```

**Error Response (404 Not Found):**
```json
{
  "error": "Campaign not found"
}
```

---

### 4. Get All Campaigns (Paginated)

**GET** `/api/campaigns`

Retrieves all campaigns with pagination and sorting.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-based) |
| size | Integer | 10 | Page size |
| sortBy | String | createdAt | Sort field |
| sortDir | String | DESC | Sort direction (ASC/DESC) |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns?page=0&size=20&sortBy=startDate&sortDir=ASC"
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 25,
      "name": "Extended Winter Holiday Sale",
      "description": "Extended holiday discounts with more products",
      "image": "https://res.cloudinary.com/demo/image/upload/v1234567891/campaign_images/updated_winter_holiday_sale.jpg",
      "imageID": "campaign_images/updated_winter_holiday_sale",
      "startDate": "2024-12-01T00:00:00",
      "endDate": "2024-12-31T23:59:59",
      "isActive": true,
      "status": "SCHEDULED",
      "createdAt": "2024-11-15T10:30:00",
      "updatedAt": "2024-11-20T09:15:00"
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

### 5. Get Active Campaigns

**GET** `/api/campaigns/active`

Retrieves all currently active campaigns.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns/active"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 20,
    "name": "Black Friday Sale",
    "description": "Massive discounts on Black Friday",
    "image": "https://res.cloudinary.com/demo/image/upload/campaign_images/black_friday_sale.jpg",
    "imageID": "campaign_images/black_friday_sale",
    "startDate": "2024-11-24T00:00:00",
    "endDate": "2024-11-24T23:59:59",
    "isActive": true,
    "status": "ACTIVE",
    "createdAt": "2024-11-01T10:00:00",
    "updatedAt": "2024-11-01T10:00:00"
  }
]
```

---

### 6. Get Scheduled Campaigns

**GET** `/api/campaigns/scheduled`

Retrieves all campaigns that are scheduled but not yet started.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns/scheduled"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 25,
    "name": "Extended Winter Holiday Sale",
    "description": "Extended holiday discounts with more products",
    "image": "https://res.cloudinary.com/demo/image/upload/v1234567891/campaign_images/updated_winter_holiday_sale.jpg",
    "imageID": "campaign_images/updated_winter_holiday_sale",
    "startDate": "2024-12-01T00:00:00",
    "endDate": "2024-12-31T23:59:59",
    "isActive": true,
    "status": "SCHEDULED",
    "createdAt": "2024-11-15T10:30:00",
    "updatedAt": "2024-11-20T09:15:00"
  }
]
```

---

### 7. Get Expired Campaigns

**GET** `/api/campaigns/expired`

Retrieves all campaigns that have ended.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns/expired"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 15,
    "name": "Summer Sale 2024",
    "description": "Great summer discounts on all products",
    "image": "https://res.cloudinary.com/demo/image/upload/campaign_images/summer_sale_2024.jpg",
    "imageID": "campaign_images/summer_sale_2024",
    "startDate": "2024-06-01T00:00:00",
    "endDate": "2024-08-31T23:59:59",
    "isActive": true,
    "status": "EXPIRED",
    "createdAt": "2024-05-15T10:00:00",
    "updatedAt": "2024-05-20T14:30:00"
  }
]
```

---

### 8. Delete Campaign

**DELETE** `/api/campaigns/{id}`

Deletes a campaign by ID. Note: This does not automatically delete the associated image from Cloudinary.

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/campaigns/25"
```

**Success Response (204 No Content):**
```
(Empty response body)
```

**Error Response (404 Not Found):**
```json
{
  "error": "Campaign not found"
}
```

---

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Successful GET/PUT request
- `201 Created` - Successful campaign creation
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Validation errors, invalid date ranges
- `404 Not Found` - Campaign not found
- `500 Internal Server Error` - Server errors, image upload failures

### Validation Rules
- **Campaign name**: Required, non-empty string
- **Date range**: startDate must be before endDate
- **Dates**: Must be in valid ISO 8601 format
- **Image files**: Must be valid image formats if provided

---

## Image Upload Details

### Cloudinary Integration
- **Campaign images**: Stored in `campaign_images/` folder
- **Automatic optimization**: Cloudinary handles compression and format conversion
- **CDN delivery**: Fast global delivery via Cloudinary CDN
- **Old image cleanup**: Previous images automatically deleted when replaced

### Image Requirements
- **Formats**: JPG, PNG, GIF, WebP (validated by content type)
- **Upload folder**: `campaign_images/` (automatically created)
- **Response**: Returns both secure URL and public ID for management

### Image Management Flow
1. **Upload**: Use multipart/form-data with `image` parameter
2. **Replace**: New image upload automatically deletes old image via imageID
3. **Validation**: Server-side validation of file type and content
4. **Error handling**: Failed uploads return detailed error messages

---

## Business Rules

### Campaign Lifecycle
1. **Creation**: Campaign starts in SCHEDULED status if startDate is future
2. **Activation**: Status changes to ACTIVE when current time >= startDate
3. **Expiration**: Status changes to EXPIRED when current time > endDate
4. **Image Management**: Only one image per campaign, automatic replacement

### Date Validation
- **Start Date**: Can be in past, present, or future
- **End Date**: Must be after start date
- **ISO Format**: Dates must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ss)
- **Timezone**: Server timezone used for date comparisons

### Status Logic
- **SCHEDULED**: startDate > current time
- **ACTIVE**: startDate <= current time <= endDate
- **EXPIRED**: endDate < current time

---

## Performance Considerations

### Database Queries
- Indexes on frequently queried fields (startDate, endDate, status)
- Efficient pagination for large campaign datasets
- Status filtering optimized for common queries

### Image Handling
- Asynchronous Cloudinary uploads where possible
- Automatic image optimization by Cloudinary
- CDN caching for fast image delivery
- Cleanup of replaced images during updates

### API Response
- Lightweight responses with essential campaign data
- Batch operations for multiple campaigns
- Efficient date-based filtering for status queries

---

## Integration Notes

### Cloudinary Setup
- Requires valid Cloudinary configuration
- Uses `campaign_images` folder for organization
- Returns both public URL and public ID for management
- Handles image format conversion automatically

### Error Recovery
- Failed image uploads return detailed error messages
- Campaign creation/update can succeed without image
- Image replacement failures preserve existing image
- Comprehensive error logging for debugging
  "deletedAt": null,
  "status": "SCHEDULED",
  "isActive": false,
  "isScheduled": true,
  "isExpired": false,
  "promotionCount": 0
}
```

**Error Responses:**
```json
// 400 Bad Request - Validation Error
{
  "error": "Campaign with name 'Black Friday 2024' already exists"
}

// 400 Bad Request - Date Validation
{
  "error": "End date must be after start date"
}

// 500 Internal Server Error - Image Upload Failed
{
  "error": "Failed to upload image: Connection timeout"
}
```

---

### 2. Update Campaign with Image Replacement

**PUT** `/api/campaigns/{id}`

Updates an existing campaign. If a new image is provided, the old image is automatically deleted and replaced.

**Content-Type:** `multipart/form-data`

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | No | Updated campaign name |
| description | String | No | Updated description |
| startDate | String | No | Updated start date |
| endDate | String | No | Updated end date |
| image | File | No | New image (replaces existing) |

**Sample Request:**
```bash
curl -X PUT "http://localhost:8080/api/campaigns/1" \
  -F "name=Extended Black Friday 2024" \
  -F "description=Extended biggest sale event with more discounts" \
  -F "endDate=2024-12-01T23:59:59" \
  -F "image=@/path/to/extended-sale-banner.jpg"
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Extended Black Friday 2024",
  "description": "Extended biggest sale event with more discounts",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567891/campaign_images/extended_black_friday.jpg",
  "imageID": "campaign_images/extended_black_friday",
  "startDate": "2024-11-29T00:00:00",
  "endDate": "2024-12-01T23:59:59",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-20T14:45:00",
  "deletedAt": null,
  "status": "SCHEDULED",
  "isActive": false,
  "isScheduled": true,
  "isExpired": false,
  "promotionCount": 0
}
```

**Error Responses:**
```json
// 404 Not Found
{
  "error": "Campaign not found: 999"
}

// 500 Internal Server Error
{
  "error": "Failed to process image: Upload failed"
}
```

---

### 3. Get Campaign by ID

**GET** `/api/campaigns/{id}`

Retrieves a specific campaign by its ID.

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns/1"
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Black Friday 2024",
  "description": "Biggest sale event of the year",
  "image": "https://res.cloudinary.com/demo/image/upload/v1234567890/campaign_images/black_friday_2024.jpg",
  "imageID": "campaign_images/black_friday_2024",
  "startDate": "2024-11-29T00:00:00",
  "endDate": "2024-11-29T23:59:59",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-15T10:30:00",
  "deletedAt": null,
  "status": "ACTIVE",
  "isActive": true,
  "isScheduled": false,
  "isExpired": false,
  "promotionCount": 5
}
```

**Error Response:**
```json
// 404 Not Found
{
  "error": "Campaign not found with ID: 999"
}
```

---

### 4. Get All Campaigns (Paginated)

**GET** `/api/campaigns`

Retrieves all campaigns with pagination and sorting.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-based) |
| size | Integer | 10 | Page size (1-100) |
| sortBy | String | createdAt | Sort field |
| sortDir | String | DESC | Sort direction (ASC/DESC) |

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns?page=0&size=20&sortBy=startDate&sortDir=ASC"
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Summer Sale 2024",
      "description": "Big summer discount event",
      "image": "https://res.cloudinary.com/demo/image/upload/campaign_images/summer_sale.jpg",
      "imageID": "campaign_images/summer_sale",
      "startDate": "2024-06-01T00:00:00",
      "endDate": "2024-08-31T23:59:59",
      "createdAt": "2024-05-15T10:00:00",
      "updatedAt": "2024-05-15T10:00:00",
      "deletedAt": null,
      "status": "EXPIRED",
      "isActive": false,
      "isScheduled": false,
      "isExpired": true,
      "promotionCount": 3
    },
    {
      "id": 2,
      "name": "Black Friday 2024",
      "description": "Biggest sale event of the year",
      "image": "https://res.cloudinary.com/demo/image/upload/campaign_images/black_friday.jpg",
      "imageID": "campaign_images/black_friday",
      "startDate": "2024-11-29T00:00:00",
      "endDate": "2024-11-29T23:59:59",
      "createdAt": "2024-11-15T10:30:00",
      "updatedAt": "2024-11-15T10:30:00",
      "deletedAt": null,
      "status": "SCHEDULED",
      "isActive": false,
      "isScheduled": true,
      "isExpired": false,
      "promotionCount": 0
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
  "totalElements": 2,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "first": true,
  "numberOfElements": 2,
  "empty": false
}
```

---

### 5. Get Active Campaigns

**GET** `/api/campaigns/active`

Retrieves all currently active campaigns (within their validity period).

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns/active"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 3,
    "name": "Current Winter Sale",
    "description": "Ongoing winter discount event",
    "image": "https://res.cloudinary.com/demo/image/upload/campaign_images/winter_sale.jpg",
    "imageID": "campaign_images/winter_sale",
    "startDate": "2024-12-01T00:00:00",
    "endDate": "2024-12-31T23:59:59",
    "createdAt": "2024-11-25T09:00:00",
    "updatedAt": "2024-11-25T09:00:00",
    "deletedAt": null,
    "status": "ACTIVE",
    "isActive": true,
    "isScheduled": false,
    "isExpired": false,
    "promotionCount": 8
  }
]
```

---

### 6. Get Scheduled Campaigns

**GET** `/api/campaigns/scheduled`

Retrieves all scheduled campaigns (start date in the future).

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns/scheduled"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 4,
    "name": "New Year Sale 2025",
    "description": "Welcome the new year with great deals",
    "image": "https://res.cloudinary.com/demo/image/upload/campaign_images/new_year_2025.jpg",
    "imageID": "campaign_images/new_year_2025",
    "startDate": "2025-01-01T00:00:00",
    "endDate": "2025-01-15T23:59:59",
    "createdAt": "2024-12-10T15:30:00",
    "updatedAt": "2024-12-10T15:30:00",
    "deletedAt": null,
    "status": "SCHEDULED",
    "isActive": false,
    "isScheduled": true,
    "isExpired": false,
    "promotionCount": 0
  }
]
```

---

### 7. Get Expired Campaigns

**GET** `/api/campaigns/expired`

Retrieves all expired campaigns (end date in the past).

**Sample Request:**
```bash
curl -X GET "http://localhost:8080/api/campaigns/expired"
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Summer Sale 2024",
    "description": "Big summer discount event",
    "image": "https://res.cloudinary.com/demo/image/upload/campaign_images/summer_sale.jpg",
    "imageID": "campaign_images/summer_sale",
    "startDate": "2024-06-01T00:00:00",
    "endDate": "2024-08-31T23:59:59",
    "createdAt": "2024-05-15T10:00:00",
    "updatedAt": "2024-05-15T10:00:00",
    "deletedAt": null,
    "status": "EXPIRED",
    "isActive": false,
    "isScheduled": false,
    "isExpired": true,
    "promotionCount": 3
  }
]
```

---

### 8. Delete Campaign (Soft Delete)

**DELETE** `/api/campaigns/{id}`

Soft deletes a campaign. The campaign image remains on Cloudinary.

**Sample Request:**
```bash
curl -X DELETE "http://localhost:8080/api/campaigns/1"
```

**Success Response (204 No Content):**
```
(Empty response body)
```

**Error Response:**
```json
// 404 Not Found
{
  "error": "Campaign not found with ID: 999"
}
```

---

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Successful GET/PUT request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Validation errors, business logic errors
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server errors, image upload failures

### Validation Rules
- `name`: Required, 3-255 characters, must be unique
- `startDate`: Required, valid ISO datetime format
- `endDate`: Required, must be after startDate
- `image`: Optional, valid image file (JPG, PNG, GIF, WebP), max 10MB

---

## Image Upload Details

### Cloudinary Integration
- Images are automatically uploaded to Cloudinary during campaign creation/update
- Old images are automatically deleted when replaced
- Images are stored in the `campaign_images` folder
- CDN delivery for optimal performance

### Image Requirements
- **Formats**: JPG, PNG, GIF, WebP
- **Size limit**: 10MB
- **Recommended dimensions**: 1920x1080 (16:9 aspect ratio)
- **Optimization**: Automatic by Cloudinary

### Image Management
- **Create**: Upload image with campaign creation
- **Update**: Replace existing image (old image deleted automatically)
- **Delete**: Image preserved on Cloudinary (soft delete only affects database)