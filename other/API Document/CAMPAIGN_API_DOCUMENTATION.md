# Campaign API Documentation

## Overview
The Campaign API manages promotional campaigns with integrated Cloudinary image upload functionality. Campaigns represent overall promotional events with timeframes and branding.

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
  "description": "Big summer discount event for all products",
  "image": "https://res.cloudinary.com/your-cloud/image/upload/v1234567890/campaign_images/summer_banner.jpg",
  "imageID": "campaign_images/summer_banner",
  "startDate": "2024-06-01T00:00:00",
  "endDate": "2024-08-31T23:59:59",
  "createdAt": "2024-05-15T10:00:00",
  "updatedAt": "2024-05-15T10:00:00",
  "deletedAt": null,
  "status": "ACTIVE",
  "isActive": true,
  "isScheduled": false,
  "isExpired": false,
  "promotionCount": 3
}
```

### Campaign Status Types
- `ACTIVE` - Currently running (current time between start and end date)
- `SCHEDULED` - Future campaign (start date in future)
- `EXPIRED` - Past campaign (end date in past)

---

## API Endpoints

### 1. Create Campaign with Image Upload

**POST** `/api/campaigns`

Creates a new campaign with optional image upload to Cloudinary.

**Content-Type:** `multipart/form-data`

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | Yes | Campaign name (3-255 chars, unique) |
| description | String | No | Campaign description |
| startDate | String | Yes | ISO format: YYYY-MM-DDTHH:mm:ss |
| endDate | String | Yes | ISO format: YYYY-MM-DDTHH:mm:ss |
| image | File | No | Image file (JPG, PNG, GIF, WebP) |

**Sample Request:**
```bash
curl -X POST "http://localhost:8080/api/campaigns" \
  -F "name=Black Friday 2024" \
  -F "description=Biggest sale event of the year" \
  -F "startDate=2024-11-29T00:00:00" \
  -F "endDate=2024-11-29T23:59:59" \
  -F "image=@/path/to/black-friday-banner.jpg"
```

**Success Response (201 Created):**
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