# TechBox Store API - Postman Collections

This directory contains comprehensive Postman collections for testing the TechBox Store API endpoints automatically generated based on the controller implementations.

## 📁 Collection Structure

### Individual Module Collections
- `Product_API.postman_collection.json` - Product, Brand, and Category management (13+ endpoints)
- `Voucher_API.postman_collection.json` - Voucher validation and usage tracking (15+ endpoints)
- `Campaign_API.postman_collection.json` - Marketing campaign management (12+ endpoints) 
- `Promotion_API.postman_collection.json` - Promotion calculations and rules (9+ endpoints)

### Master Collection
- `TechBox_Store_API_Complete.postman_collection.json` - Combined collection with all modules

### Environment Files
- `environments/TechBox_Store_Development.postman_environment.json` - Development environment (localhost:8080)
- `environments/TechBox_Store_Production.postman_environment.json` - Production environment template

## 🚀 Quick Start

### 1. Import Collections
1. Open Postman
2. Click "Import" button
3. Select the collection files you want to import
4. Collections will be automatically organized with folder structure

### 2. Import Environment
1. Import the appropriate environment file
2. For development: Use `TechBox_Store_Development.postman_environment.json`
3. For production: Import and configure `TechBox_Store_Production.postman_environment.json`

### 3. Configure Variables
Update environment variables as needed:
- `baseUrl`: API server URL
- `authToken`: JWT authentication token (if required)
- Entity IDs: `productId`, `brandId`, `categoryId`, etc.

## 📊 Collection Details

### 🛍️ Product API Collection
**Endpoints:** 13+ requests organized in folders
- **Products**: CRUD operations, search, filtering, soft delete
- **Brands**: Brand management with pagination
- **Categories**: Category hierarchy and tree structure

**Key Features:**
- Multipart/form-data support for image uploads
- Advanced search with multiple filters
- Pagination and sorting
- Soft delete and restore operations

### 🎟️ Voucher API Collection  
**Endpoints:** 15+ requests in organized folders
- **Management**: Create, update, delete vouchers
- **Validation**: Code validation and eligibility checks
- **Usage**: Voucher redemption and tracking
- **Analytics**: Performance metrics and statistics

**Key Features:**
- Voucher code validation logic
- Usage limit tracking
- User eligibility verification
- Discount calculation validation

### 📢 Campaign API Collection
**Endpoints:** 12+ requests with lifecycle management
- **Management**: Campaign CRUD with image support
- **Analytics**: Performance tracking and metrics

**Key Features:**
- Cloudinary image integration
- Status lifecycle management (DRAFT → ACTIVE → COMPLETED)
- Date-based automatic transitions
- Search and filtering capabilities

### 🎯 Promotion API Collection
**Endpoints:** 9+ requests for discount management
- **Management**: Promotion rule configuration
- **Calculation**: Discount computation (POST and GET variants)

**Key Features:**
- Multiple discount types (PERCENTAGE, FIXED_AMOUNT)
- Campaign association
- Product variation targeting
- Complex discount calculation logic

## 🛠️ Advanced Features

### Dynamic Variables
Collections use Postman's built-in dynamic variables:
- `{{$randomProductName}}` - Random product names
- `{{$randomCompanyName}}` - Random brand names
- `{{$randomPrice}}` - Random price values
- `{{$randomAlphaNumeric}}` - Random codes
- Date calculations for campaign periods

### Pre-request Scripts
Automatic setup of:
- Dynamic date ranges for campaigns
- Random test data generation
- Environment variable initialization

### Test Scripts
Automated validation of:
- HTTP status codes
- Response data structure
- Required fields presence
- Successful entity creation (with ID capture)

### Environment Variables
Comprehensive variable support:
- Base URLs for different environments
- Authentication tokens (secure storage)
- Entity IDs for related operations
- Pagination parameters
- Search and filter values

## 📋 Testing Scenarios

### Basic CRUD Operations
Each module includes complete CRUD testing:
1. **Create** - POST requests with validation
2. **Read** - GET requests with pagination
3. **Update** - PUT/PATCH requests
4. **Delete** - DELETE requests (soft delete where applicable)

### Advanced Operations
- **Search & Filter**: Multi-criteria search with pagination
- **File Upload**: Multipart/form-data for images
- **Validation**: Business rule validation
- **Analytics**: Performance metrics and statistics

### Error Handling
Collections include requests for testing:
- Invalid data validation
- Authentication/authorization
- Resource not found scenarios
- Business rule violations

## 🔧 Configuration Guide

### Development Environment
```json
{
  "baseUrl": "http://localhost:8080",
  "authToken": "your-jwt-token-here",
  "userId": "123"
}
```

### Production Environment
```json
{
  "baseUrl": "https://api.techbox-store.com",
  "authToken": "production-jwt-token",
  "userId": "real-user-id"
}
```

### Authentication Setup
1. Obtain JWT token from login endpoint
2. Set `authToken` environment variable
3. Token will be automatically included in request headers

## 📖 Usage Examples

### Testing Product Creation
1. Select "Product Management" → "Products" → "Create Product"
2. Request includes multipart/form-data with:
   - Product details (name, description, price)
   - Category and brand associations
   - Image file upload
3. Test script validates response and captures `productId`

### Voucher Validation Flow
1. Create voucher with "Create Voucher" request
2. Validate code with "Validate Voucher Code"  
3. Use voucher with "Use Voucher" request
4. Check analytics with "Get Voucher Analytics"

### Campaign Lifecycle Testing
1. Create campaign in DRAFT status
2. Upload campaign image (multipart/form-data)
3. Update status to ACTIVE
4. Monitor with analytics endpoints

## 🔍 Troubleshooting

### Common Issues
1. **401 Unauthorized**: Check `authToken` environment variable
2. **404 Not Found**: Verify entity IDs in environment variables
3. **400 Bad Request**: Check request body format and required fields
4. **File Upload Issues**: Ensure multipart/form-data content type

### Debug Tips
- Use Postman Console to view request/response details
- Check Pre-request Script execution
- Verify environment variable values
- Review test script results

## 🎯 Best Practices

### Environment Management
- Use separate environments for dev/staging/production
- Store sensitive data (tokens) as secret variables
- Regular token refresh for long testing sessions

### Test Data Management
- Use dynamic variables for realistic test data
- Clean up test data after testing sessions
- Use consistent naming conventions

### Collection Organization
- Import individual module collections for focused testing
- Use master collection for comprehensive API validation
- Organize requests in logical folders

## 📚 API Documentation Reference

For detailed API specifications, refer to:
- `API_DOCUMENTATION.md` - Complete API documentation
- `REFRESH_TOKEN_GUIDE.md` - Authentication guide
- Controller source code for implementation details

## 🔄 Maintenance

### Updating Collections
Collections are automatically generated from controller implementations:
1. Controller changes trigger collection updates
2. New endpoints are automatically included
3. Request/response formats match implementation

### Version Control
- Collections are stored in `/other/postman/` directory
- Environment files in `/other/postman/environments/`
- Track changes with your preferred version control system

---

**Generated automatically from TechBox Store API controllers**  
**Last Updated:** December 30, 2024  
**API Version:** 1.0  
**Collection Format:** Postman v2.1.0