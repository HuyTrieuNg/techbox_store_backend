# TechBox Store API Documentation Overview

## Introduction

Welcome to the TechBox Store API documentation. This comprehensive guide covers all endpoints, models, and integration patterns for the TechBox Store e-commerce platform built with Spring Boot 3.x and integrated with Cloudinary for image management.

## Architecture Overview

The TechBox Store API follows a RESTful design pattern with the following core modules:

- **User Management**: Authentication, authorization, profile management
- **Product Catalog**: Products, brands, categories with image management
- **Campaign System**: Marketing campaigns with promotional banners
- **Promotion Engine**: Discount rules and promotional offers linked to campaigns
- **Voucher System**: Independent discount vouchers with unique codes

## Base Configuration

### Base URL
```
http://localhost:8080/api
```

### Content Types
- **JSON**: `application/json` (standard API requests)
- **Multipart**: `multipart/form-data` (image uploads)

### Authentication
Most endpoints require JWT Bearer token authentication:
```
Authorization: Bearer <jwt_token>
```

---

## Module Relationships & Differences

### 📢 Campaign vs 🎯 Promotion vs 🎫 Voucher

Understanding the distinction between these three discount systems:

**Campaign (Marketing Events)**:
- **Purpose**: Overall marketing campaign with branding and timeframe
- **Features**: Campaign banners, start/end dates, campaign status tracking
- **Image Support**: Yes (Cloudinary integration for banner uploads)
- **Independence**: Parent entity that can contain multiple promotions
- **Usage**: `Summer Sale 2024`, `Black Friday Campaign`
- **API Focus**: Image management, time-based status, campaign lifecycle

**Promotion (Automatic Discounts)**:
- **Purpose**: Specific discount rules tied to campaigns
- **Features**: Product-specific discounts, automatic application, complex rules
- **Image Support**: No (inherits campaign branding)
- **Independence**: Must be linked to a campaign
- **Usage**: `25% off Electronics in Summer Sale`, `Buy 2 Get 1 Free`
- **API Focus**: Discount calculation, product applicability, rule validation

**Voucher (Code-based Discounts)**:
- **Purpose**: Individual discount codes that customers redeem
- **Features**: Unique codes, usage limits, user-specific tracking
- **Image Support**: No (text-based codes)
- **Independence**: Completely independent system
- **Usage**: `WELCOME20`, `BLACKFRIDAY30`, `STUDENT15`
- **API Focus**: Code validation, usage tracking, user redemption

**Integration Flow**:
```
Order Processing:
1. Check applicable Promotions (automatic, based on products/campaign)
2. Apply Voucher code (if provided by user)
3. Calculate best discount combination
4. Track usage for both systems
```

---

## API Modules

### 📱 User API
**Base URL**: `/api/users`

**Core Features**:
- User registration and email verification
- JWT-based authentication with refresh tokens
- Profile management with avatar upload
- Address management
- Role-based access control (USER, MODERATOR, ADMIN)
- Password reset functionality

**Key Endpoints**:
- `POST /register` - User registration
- `POST /login` - Authentication
- `POST /refresh` - Token refresh
- `GET /profile` - Get current user profile
- `PUT /profile` - Update profile with avatar upload

**[Full Documentation →](USER_API_DOCUMENTATION.md)**

---

### 🛍️ Product API
**Base URL**: `/api/products`

**Core Features**:
- Product CRUD with Cloudinary image management
- Brand and category management
- Advanced search and filtering
- Hierarchical category structure
- Featured products
- Stock management

**Key Endpoints**:
- `POST /products` - Create product with image upload
- `GET /products` - Get products with filtering
- `POST /brands` - Create brand with logo upload
- `GET /categories` - Get category tree structure

**[Full Documentation →](PRODUCT_API_DOCUMENTATION.md)**

---

### 📢 Campaign API
**Base URL**: `/api/campaigns`

**Core Features**:
- Marketing campaign management
- Cloudinary integration for campaign banners
- Time-based campaign scheduling
- Campaign status tracking (ACTIVE, SCHEDULED, EXPIRED)
- Automatic status calculation
- Soft delete with image preservation

**Key Endpoints**:
- `POST /campaigns` - Create campaign with banner upload
- `PUT /campaigns/{id}` - Update with automatic image replacement
- `GET /campaigns/active` - Get currently active campaigns
- `GET /campaigns/scheduled` - Get upcoming campaigns

**[Full Documentation →](CAMPAIGN_API_DOCUMENTATION.md)**

---

### 🎯 Promotion API
**Base URL**: `/api/promotions`

**Core Features**:
- Discount management (percentage, fixed amount, free shipping)
- Campaign-linked promotions
- Product-specific applicability
- Usage tracking and limits
- Discount calculation engine
- Eligibility validation

**Key Endpoints**:
- `POST /promotions` - Create promotion
- `GET /promotions/campaign/{id}` - Get promotions by campaign
- `POST /promotions/{id}/calculate-discount` - Calculate discount
- `GET /promotions/product/{id}` - Get applicable promotions

**[Full Documentation →](PROMOTION_API_DOCUMENTATION.md)**

---

### 🎫 Voucher API
**Base URL**: `/api/vouchers`

**Core Features**:
- Independent discount vouchers with unique codes
- Percentage and fixed amount discounts
- Usage limit and tracking
- User-specific voucher usage history
- Validation and expiration management
- Code-based redemption system

**Key Endpoints**:
- `POST /vouchers` - Create voucher
- `GET /vouchers/code/{code}` - Get voucher by code
- `POST /vouchers/validate` - Validate voucher for order
- `POST /vouchers/use` - Use voucher for order

**[Full Documentation →](VOUCHER_API_DOCUMENTATION.md)**

---

## Common Features

### 🖼️ Image Management (Cloudinary Integration)

All modules support integrated image upload with the following features:

**Supported Formats**: JPG, PNG, GIF, WebP
**Size Limits**: 
- Product images: 10MB
- User avatars: 5MB
- Campaign banners: 10MB
- Brand logos: 5MB

**Storage Structure**:
```
cloudinary/
├── product_images/     # Product photos
├── brand_logos/        # Brand logos  
├── campaign_images/    # Campaign banners
└── user_avatars/       # User profile pictures
```

**Features**:
- Automatic image optimization and compression
- CDN delivery for fast loading
- Automatic old image deletion on updates
- Image preservation on soft deletes

### 📄 Pagination & Sorting

All list endpoints support standardized pagination:

**Parameters**:
- `page`: Page number (0-based, default: 0)
- `size`: Page size (1-100, default: 10)  
- `sortBy`: Sort field (default: createdAt)
- `sortDir`: Sort direction (ASC/DESC, default: DESC)

**Response Format**:
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 50,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

### 🔍 Filtering & Search

**Common Filters**:
- `isActive`: Filter by status (true/false)
- `search`: Text search in relevant fields
- Date range filters where applicable

**Advanced Search**:
- Product search across name, description, tags
- Multi-criteria filtering (price range, brands, categories)
- Full-text search capabilities

### 🗑️ Soft Delete Pattern

All entities use soft delete for data preservation:
- `deletedAt`: Timestamp field (null = active, timestamp = deleted)
- Deleted records excluded from normal queries
- Images preserved on Cloudinary for audit purposes
- Admin can view deleted records if needed

---

## Error Handling

### Standard HTTP Status Codes

| Code | Description | Use Case |
|------|-------------|----------|
| 200 | OK | Successful GET/PUT/PATCH |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation errors |
| 401 | Unauthorized | Authentication required |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate resource |
| 500 | Internal Server Error | Server/upload errors |

### Error Response Format

```json
{
  "error": "Descriptive error message",
  "timestamp": "2024-12-15T10:30:00",
  "path": "/api/products/999"
}
```

### Common Validation Rules

**Text Fields**:
- Names: 3-255 characters (products, campaigns)
- Descriptions: Optional, max 1000 characters
- Unique constraints on business keys (SKU, email, username)

**Numeric Fields**:
- Prices: Positive decimals with 2 decimal places
- Stock: Non-negative integers
- Discount values: Positive, within valid ranges

**Dates**:
- ISO 8601 format: `YYYY-MM-DDTHH:mm:ss`
- Business logic validation (end date after start date)
- Campaign/promotion date relationships

---

## Security & Authentication

### JWT Token System

**Access Token**:
- Lifespan: 1 hour
- Contains user claims and permissions
- Required for most API endpoints

**Refresh Token**:
- Lifespan: 7 days  
- Used to obtain new access tokens
- Stored securely, invalidated on logout

### Role-Based Access Control

**Roles**:
- `USER`: Standard customer access
- `MODERATOR`: Limited administrative functions
- `ADMIN`: Full system access

**Permission Matrix**:

| Endpoint Type | USER | MODERATOR | ADMIN |
|---------------|------|-----------|-------|
| Own Profile | ✅ | ✅ | ✅ |
| Product Browse | ✅ | ✅ | ✅ |
| Product Manage | ❌ | ✅ | ✅ |
| User Management | ❌ | Partial | ✅ |
| System Admin | ❌ | ❌ | ✅ |

### Security Best Practices

- Password hashing with BCrypt + salt
- Email verification required for activation
- Rate limiting on authentication endpoints
- CORS configuration for cross-origin requests
- Input sanitization and validation
- SQL injection prevention with JPA
- File upload security with type validation

---

## Development Guidelines

### Request/Response Patterns

**Creating Resources with Images**:
```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Authorization: Bearer <token>" \
  -F "name=Product Name" \
  -F "price=99.99" \
  -F "image=@/path/to/image.jpg"
```

**Updating Resources**:
```bash
curl -X PUT "http://localhost:8080/api/products/1" \
  -H "Authorization: Bearer <token>" \
  -F "name=Updated Name" \
  -F "image=@/path/to/new-image.jpg"  # Optional, replaces existing
```

**Filtering and Pagination**:
```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=20&brandId=1&minPrice=100&search=smartphone" \
  -H "Authorization: Bearer <token>"
```

### Integration Workflow

1. **Authentication Flow**:
   ```
   Register → Verify Email → Login → Get Tokens → Make API Calls → Refresh Tokens
   ```

2. **Product Management Flow**:
   ```
   Create Brand → Create Category → Create Product with Image → Update/Manage
   ```

3. **Campaign/Promotion Flow**:
   ```
   Create Campaign with Banner → Create Linked Promotions → Monitor Usage
   ```

---

## Testing & Documentation

### Postman Collections

Complete Postman collections are available for each module:
- `TechBox_Store_User_API.postman_collection.json`
- `TechBox_Store_Product_API.postman_collection.json` 
- `TechBox_Store_Campaign_API_Updated.postman_collection.json`
- `TechBox_Store_Promotion_API.postman_collection.json`
- `TechBox_Store_Voucher_API.postman_collection.json`

Each collection includes:
- Request examples with sample data
- Environment variables for base URL and tokens
- Test scripts for validation
- Error scenario testing

### Environment Setup

**Development Environment Variables**:
```env
BASE_URL=http://localhost:8080
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
JWT_SECRET=your_jwt_secret
SMTP_HOST=your_smtp_host
SMTP_PORT=587
SMTP_USERNAME=your_email
SMTP_PASSWORD=your_app_password
```

---

## Performance Considerations

### Image Optimization
- Cloudinary automatic optimization reduces bandwidth
- CDN delivery for global performance
- Lazy loading recommended for image-heavy pages
- Thumbnail generation for listings

### Database Optimization  
- Proper indexing on frequently queried fields
- Pagination to limit result sets
- Soft delete indexes for performance
- Connection pooling for database efficiency

### Caching Strategy
- Redis caching for frequently accessed data
- HTTP caching headers for static resources
- JWT token caching with expiration
- Category tree caching (hierarchical data)

---

## API Changelog

### Version 1.0.0 (Current)
- Initial release with core modules (User, Product, Campaign, Promotion, Voucher)
- Cloudinary integration across all image uploads
- JWT authentication with refresh tokens
- Complete CRUD operations for all entities
- Comprehensive error handling and validation
- Three-tier discount system (Campaign → Promotion, Independent Vouchers)

### Planned Features
- Order management module
- Payment integration
- Inventory tracking
- Analytics and reporting
- Mobile push notifications
- Advanced search with Elasticsearch

---

## Support & Resources

### Documentation Links
- [User API Documentation](USER_API_DOCUMENTATION.md)
- [Product API Documentation](PRODUCT_API_DOCUMENTATION.md)  
- [Campaign API Documentation](CAMPAIGN_API_DOCUMENTATION.md)
- [Promotion API Documentation](PROMOTION_API_DOCUMENTATION.md)
- [Voucher API Documentation](VOUCHER_API_DOCUMENTATION.md)

### API Design Principles
- RESTful URL structure
- Consistent response formats
- Comprehensive error messages
- Stateless design
- Resource-oriented endpoints
- HTTP verbs for actions

### Contact & Support
- API Issues: Create GitHub issue
- Feature Requests: Product roadmap discussion
- Security Issues: Direct contact required
- Documentation: Contribution guidelines in README

---

*This documentation is maintained by the TechBox Store development team. Last updated: December 2024*