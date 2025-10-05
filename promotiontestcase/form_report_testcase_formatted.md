# Promotion & Campaign API Test Case Form Report

## Tổng quan
- **API Module**: Promotion & Campaign Management
- **Base URL**: 
  - Promotion: `/api/promotions`
  - Campaign: `/api/campaigns`
- **Authentication**: Required (JWT Token)
- **Test Environment**: Development/Staging
- **Features**: Campaign management, Promotion calculations, Cloudinary integration, Business rules validation

---

## Campaign API Test Cases

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| CAMP_001 | Create campaign with valid data | POST /api/campaigns with valid campaign data including image | 201 Created, campaign object returned with auto-generated ID | Admin authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_002 | Create campaign without name | POST /api/campaigns with empty name field | 400 Bad Request, validation error | Admin authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_003 | Create campaign with invalid date range | POST /api/campaigns with endDate before startDate | 400 Bad Request, date validation error | Admin authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_004 | Create campaign with duplicate name | POST /api/campaigns with existing campaign name | 400 Bad Request, duplicate name error | Admin authentication, existing campaign | Pending | | | Pending | | | Pending | | | |
| CAMP_005 | Create campaign without authentication | POST /api/campaigns without authorization header | 401 Unauthorized | No authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_006 | Create campaign with user role | POST /api/campaigns with user role token | 403 Forbidden, admin required | User authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_007 | Create campaign with large image | POST /api/campaigns with >10MB image file | 400 Bad Request, file size limit error | Admin authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_008 | Create campaign with invalid image format | POST /api/campaigns with .txt file as image | 400 Bad Request, invalid format error | Admin authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_009 | Update campaign with valid data | PUT /api/campaigns/{id} with updated campaign info | 200 OK, updated campaign object | Admin authentication, existing campaign | Pending | | | Pending | | | Pending | | | |
| CAMP_010 | Update campaign image | PUT /api/campaigns/{id} with new image file | 200 OK, old image deleted from Cloudinary, new image uploaded | Admin authentication, existing campaign | Pending | | | Pending | | | Pending | | | |
| CAMP_011 | Update non-existent campaign | PUT /api/campaigns/999 with valid data | 404 Not Found | Admin authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_012 | Update campaign with invalid dates | PUT /api/campaigns/{id} with invalid date range | 400 Bad Request, validation error | Admin authentication, existing campaign | Pending | | | Pending | | | Pending | | | |
| CAMP_013 | Get campaign by ID | GET /api/campaigns/{id} | 200 OK, campaign object with status calculation | Valid campaign ID | Pending | | | Pending | | | Pending | | | |
| CAMP_014 | Get non-existent campaign | GET /api/campaigns/999 | 404 Not Found | Valid ID format | Pending | | | Pending | | | Pending | | | |
| CAMP_015 | Get all campaigns with pagination | GET /api/campaigns?page=0&size=10 | 200 OK, paginated campaign list | None | Pending | | | Pending | | | Pending | | | |
| CAMP_016 | Get campaigns with custom sorting | GET /api/campaigns?sortBy=name&sortDir=ASC | 200 OK, campaigns sorted by name ascending | None | Pending | | | Pending | | | Pending | | | |
| CAMP_017 | Get campaigns with invalid pagination | GET /api/campaigns?page=-1&size=0 | 400 Bad Request, invalid pagination parameters | None | Pending | | | Pending | | | Pending | | | |
| CAMP_018 | Get active campaigns | GET /api/campaigns/active | 200 OK, list of currently active campaigns | Active campaigns exist | Pending | | | Pending | | | Pending | | | |
| CAMP_019 | Get scheduled campaigns | GET /api/campaigns/scheduled | 200 OK, list of future campaigns | Scheduled campaigns exist | Pending | | | Pending | | | Pending | | | |
| CAMP_020 | Get expired campaigns | GET /api/campaigns/expired | 200 OK, list of past campaigns | Expired campaigns exist | Pending | | | Pending | | | Pending | | | |
| CAMP_021 | Search campaigns by keyword | GET /api/campaigns/search?keyword=summer | 200 OK, filtered campaign list | Campaigns with matching keywords | Pending | | | Pending | | | Pending | | | |
| CAMP_022 | Search campaigns with empty keyword | GET /api/campaigns/search?keyword= | 200 OK, all campaigns returned | None | Pending | | | Pending | | | Pending | | | |
| CAMP_023 | Check campaign name exists | GET /api/campaigns/name/exists?name=Summer Sale | 200 OK, boolean existence result | None | Pending | | | Pending | | | Pending | | | |
| CAMP_024 | Delete campaign | DELETE /api/campaigns/{id} | 204 No Content, soft delete performed | Admin authentication, existing campaign | Pending | | | Pending | | | Pending | | | |
| CAMP_025 | Delete non-existent campaign | DELETE /api/campaigns/999 | 404 Not Found | Admin authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_026 | Delete campaign without authentication | DELETE /api/campaigns/{id} without token | 401 Unauthorized | Existing campaign | Pending | | | Pending | | | Pending | | | |
| CAMP_027 | Delete campaign with user role | DELETE /api/campaigns/{id} with user token | 403 Forbidden | User authentication, existing campaign | Pending | | | Pending | | | Pending | | | |
| CAMP_028 | Verify campaign status auto-calculation | GET /api/campaigns/{id} at different time periods | Correct status (ACTIVE/SCHEDULED/EXPIRED) based on dates | Campaign with specific date range | Pending | | | Pending | | | Pending | | | |
| CAMP_029 | Test campaign image Cloudinary integration | POST/PUT /api/campaigns with image upload | Image uploaded to Cloudinary, URL returned | Admin authentication | Pending | | | Pending | | | Pending | | | |
| CAMP_030 | Test campaign image deletion on update | PUT /api/campaigns/{id} with new image | Old image removed from Cloudinary | Admin authentication, campaign with existing image | Pending | | | Pending | | | Pending | | | |

---

## Promotion API Test Cases

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| PROM_001 | Create promotion with valid data | POST /api/promotions with valid promotion rules | 201 Created, promotion object returned | Admin authentication, valid campaign | Pending | | | Pending | | | Pending | | | |
| PROM_002 | Create promotion without rule name | POST /api/promotions with empty ruleName | 400 Bad Request, validation error | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_003 | Create promotion with invalid discount type | POST /api/promotions with unknown discountType | 400 Bad Request, enum validation error | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_004 | Create promotion with negative discount value | POST /api/promotions with discountValue < 0 | 400 Bad Request, validation error | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_005 | Create promotion with percentage > 100 | POST /api/promotions with discountValue > 100 for PERCENTAGE | 400 Bad Request, validation error | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_006 | Create promotion with invalid campaign | POST /api/promotions with non-existent campaignId | 400 Bad Request, campaign not found | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_007 | Create promotion without authentication | POST /api/promotions without authorization header | 401 Unauthorized | No authentication | Pending | | | Pending | | | Pending | | | |
| PROM_008 | Create promotion with user role | POST /api/promotions with user role token | 403 Forbidden, admin required | User authentication | Pending | | | Pending | | | Pending | | | |
| PROM_009 | Create promotion with invalid product variation | POST /api/promotions with non-existent productVariationId | 400 Bad Request, product variation not found | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_010 | Create promotion with max discount > min order | POST /api/promotions where maxDiscountAmount > minOrderAmount | 400 Bad Request, business logic validation error | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_011 | Update promotion with valid data | PUT /api/promotions/{id} with updated rules | 200 OK, updated promotion object | Admin authentication, existing promotion | Pending | | | Pending | | | Pending | | | |
| PROM_012 | Update non-existent promotion | PUT /api/promotions/999 with valid data | 404 Not Found | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_013 | Update promotion discount type | PUT /api/promotions/{id} changing PERCENTAGE to FIXED_AMOUNT | 200 OK, promotion updated with new type | Admin authentication, existing promotion | Pending | | | Pending | | | Pending | | | |
| PROM_014 | Update promotion with invalid campaign | PUT /api/promotions/{id} with non-existent campaignId | 400 Bad Request, campaign validation error | Admin authentication, existing promotion | Pending | | | Pending | | | Pending | | | |
| PROM_015 | Get promotion by ID | GET /api/promotions/{id} | 200 OK, promotion object with campaign details | Valid promotion ID | Pending | | | Pending | | | Pending | | | |
| PROM_016 | Get non-existent promotion | GET /api/promotions/999 | 404 Not Found | Valid ID format | Pending | | | Pending | | | Pending | | | |
| PROM_017 | Get all promotions with pagination | GET /api/promotions?page=0&size=10 | 200 OK, paginated promotion list | None | Pending | | | Pending | | | Pending | | | |
| PROM_018 | Get promotions with sorting by discount value | GET /api/promotions?sortBy=discountValue&sortDir=DESC | 200 OK, promotions sorted by discount value descending | None | Pending | | | Pending | | | Pending | | | |
| PROM_019 | Get promotions with invalid pagination | GET /api/promotions?page=-1&size=0 | 400 Bad Request, invalid pagination parameters | None | Pending | | | Pending | | | Pending | | | |
| PROM_020 | Get promotions by campaign | GET /api/promotions/campaign/{campaignId} | 200 OK, list of promotions for specific campaign | Valid campaign with promotions | Pending | | | Pending | | | Pending | | | |
| PROM_021 | Get promotions by non-existent campaign | GET /api/promotions/campaign/999 | 200 OK, empty list | Valid campaign ID format | Pending | | | Pending | | | Pending | | | |
| PROM_022 | Get promotions by product variation | GET /api/promotions/product-variation/{productVariationId} | 200 OK, applicable promotions for product | Valid product variation with promotions | Pending | | | Pending | | | Pending | | | |
| PROM_023 | Get promotions by non-existent product | GET /api/promotions/product-variation/999 | 200 OK, empty list | Valid product variation ID format | Pending | | | Pending | | | Pending | | | |
| PROM_024 | Delete promotion | DELETE /api/promotions/{id} | 204 No Content | Admin authentication, existing promotion | Pending | | | Pending | | | Pending | | | |
| PROM_025 | Delete non-existent promotion | DELETE /api/promotions/999 | 404 Not Found | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_026 | Delete promotion without authentication | DELETE /api/promotions/{id} without token | 401 Unauthorized | Existing promotion | Pending | | | Pending | | | Pending | | | |
| PROM_027 | Delete promotion with user role | DELETE /api/promotions/{id} with user token | 403 Forbidden | User authentication, existing promotion | Pending | | | Pending | | | Pending | | | |
| PROM_028 | Calculate promotion discount (POST) | POST /api/promotions/calculate with product details | 200 OK, calculated discount and final price | Valid product variation with applicable promotions | Pending | | | Pending | | | Pending | | | |
| PROM_029 | Calculate promotion with invalid product | POST /api/promotions/calculate with non-existent productVariationId | 400 Bad Request, product not found | None | Pending | | | Pending | | | Pending | | | |
| PROM_030 | Calculate promotion with zero quantity | POST /api/promotions/calculate with quantity=0 | 400 Bad Request, invalid quantity | None | Pending | | | Pending | | | Pending | | | |
| PROM_031 | Calculate promotion with negative price | POST /api/promotions/calculate with originalPrice<0 | 400 Bad Request, invalid price | None | Pending | | | Pending | | | Pending | | | |
| PROM_032 | Calculate promotion (GET method) | GET /api/promotions/product-variation/{id}/calculate?originalPrice=100&quantity=2 | 200 OK, calculated discount information | Valid product variation | Pending | | | Pending | | | Pending | | | |
| PROM_033 | Calculate promotion with missing parameters | GET /api/promotions/product-variation/{id}/calculate without required params | 400 Bad Request, missing parameters | Valid product variation | Pending | | | Pending | | | Pending | | | |
| PROM_034 | Test percentage discount calculation | Calculate 25% discount on $100 item | Discount amount = $25, final price = $75 | PERCENTAGE promotion with 25% discount | Pending | | | Pending | | | Pending | | | |
| PROM_035 | Test fixed amount discount calculation | Calculate $20 fixed discount on $100 item | Discount amount = $20, final price = $80 | FIXED_AMOUNT promotion with $20 discount | Pending | | | Pending | | | Pending | | | |
| PROM_036 | Test discount with minimum order amount | Calculate discount below minimum order threshold | No discount applied | Promotion with minOrderAmount requirement | Pending | | | Pending | | | Pending | | | |
| PROM_037 | Test discount with maximum discount limit | Calculate discount exceeding maximum limit | Discount capped at maximum amount | Promotion with maxDiscountAmount limit | Pending | | | Pending | | | Pending | | | |
| PROM_038 | Test multiple applicable promotions | Calculate with multiple valid promotions | Best discount applied | Multiple promotions for same product | Pending | | | Pending | | | Pending | | | |
| PROM_039 | Test promotion on inactive campaign | Calculate promotion linked to inactive campaign | No discount applied | Promotion linked to expired/inactive campaign | Pending | | | Pending | | | Pending | | | |
| PROM_040 | Test promotion with quantity multiplication | Calculate discount for multiple quantities | Discount applied per item correctly | Promotion with quantity > 1 | Pending | | | Pending | | | Pending | | | |

---

## Additional Test Cases

| Test Case ID | Description | Procedure | Expected Results | Pre-conditions | Round 1 | Test Date | Tester | Round 2 | Test Date | Tester | Round 3 | Test Date | Tester | Note |
|--------------|-------------|-----------|------------------|----------------|---------|-----------|--------|---------|-----------|--------|---------|-----------|--------|------|
| PROM_041 | Verify promotion business rule validation | Create promotion violating business rules | 400 Bad Request with specific validation messages | Various invalid business rule combinations | Pending | | | Pending | | | Pending | | | |
| PROM_042 | Test promotion activation/deactivation | Toggle promotion isActive status | Promotion availability changes accordingly | Existing promotion | Pending | | | Pending | | | Pending | | | |
| PROM_043 | Test promotion campaign relationship | Update campaign affecting linked promotions | Promotions reflect campaign status changes | Promotion linked to campaign | Pending | | | Pending | | | Pending | | | |
| PROM_044 | Test concurrent promotion calculations | Multiple simultaneous discount calculations | All calculations return correct results | High concurrency scenario | Pending | | | Pending | | | Pending | | | |
| PROM_045 | Test promotion with edge case amounts | Calculate with very large/small monetary values | Calculations handle edge cases correctly | Promotions with extreme values | Pending | | | Pending | | | Pending | | | |
| PROM_046 | Verify promotion audit trail | Create/update/delete promotions | All operations logged with timestamps | Database logging enabled | Pending | | | Pending | | | Pending | | | |
| PROM_047 | Test promotion error handling | Send malformed JSON to promotion endpoints | Proper error responses with meaningful messages | None | Pending | | | Pending | | | Pending | | | |
| PROM_048 | Test promotion with special characters | Create promotion with special characters in name | Handles special characters correctly | Admin authentication | Pending | | | Pending | | | Pending | | | |
| PROM_049 | Test promotion performance | Load test promotion calculation endpoint | Response time within acceptable limits | High load scenario | Pending | | | Pending | | | Pending | | | |
| PROM_050 | Test promotion data integrity | Verify promotion data consistency after operations | Data remains consistent across operations | Database with existing data | Pending | | | Pending | | | Pending | | | |
| PROM_051 | Test campaign with Unicode names | Create campaign with Unicode characters | Unicode support works correctly | Admin authentication | Pending | | | Pending | | | Pending | | | |

---

## Execution Instructions

### Pre-test Setup
1. **Environment Setup**: Configure test environment with clean database
2. **Authentication**: Prepare valid admin and user JWT tokens
3. **Test Data**: Create required campaigns, products, and variations
4. **Cloudinary**: Configure image upload service
5. **Database**: Ensure proper indexes and constraints

### Test Execution Guidelines
1. **Sequential Testing**: Execute test cases in order for dependencies
2. **Data Cleanup**: Clean test data between rounds
3. **Error Logging**: Document all error responses and status codes
4. **Performance**: Monitor response times for calculation endpoints
5. **Business Logic**: Verify discount calculations manually

### Validation Criteria
- **HTTP Status Codes**: Verify correct status codes for all scenarios
- **Response Format**: Validate JSON structure and required fields
- **Business Rules**: Confirm discount calculations follow business logic
- **Security**: Ensure proper authentication and authorization
- **Data Integrity**: Verify database consistency after operations

### Test Data Requirements
- **Campaigns**: Active, scheduled, and expired campaigns
- **Products**: Various product variations with different prices
- **Promotions**: Different discount types and rules
- **Users**: Admin and regular user accounts
- **Images**: Valid and invalid image files for testing

---

## Notes
- Test cases marked with * require manual verification
- Performance tests should be conducted under load
- Security tests must validate proper access controls
- All monetary calculations should be verified to 2 decimal places
- Image upload tests require valid Cloudinary configuration