# Customer API Response Documentation

Tài liệu mô tả chi tiết các trường dữ liệu trong response của các API GET dành cho khách hàng.

---

## 📦 Table of Contents
1. [Product APIs](#product-apis)
   - [ProductListResponse](#productlistresponse)
   - [ProductDetailResponse](#productdetailresponse)
2. [Category APIs](#category-apis)
   - [CategoryResponse](#categoryresponse)
3. [Brand APIs](#brand-apis)
   - [BrandResponse](#brandresponse)
4. [Attribute APIs](#attribute-apis)
   - [AttributeResponse](#attributeresponse)
5. [Campaign APIs](#campaign-apis)
   - [CampaignResponse](#campaignresponse)
6. [Pagination Response](#pagination-response)

---

## Product APIs

### ProductListResponse

Response dùng cho danh sách sản phẩm (Get All Products, Search/Filter, Get by Campaign)

```json
{
  "content": [
    {
      "id": 1,
      "name": "iPhone 15 Pro Max",
      "imageUrl": "https://cloudinary.com/...",
      "displayOriginalPrice": 29990000,
      "displaySalePrice": 26990000,
      "discountType": "PERCENTAGE",
      "discountValue": 10,
      "averageRating": 4.5,
      "totalRatings": 120,
      "inWishlist": false
    }
  ],
  "pageable": {...},
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0
}
```

#### Mô tả các trường:

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `id` | Integer | ID duy nhất của sản phẩm |
| `name` | String | Tên sản phẩm |
| `imageUrl` | String | URL ảnh đại diện của sản phẩm (từ Cloudinary) |
| `displayOriginalPrice` | BigDecimal/Number | Giá gốc của biến thể có giá **thấp nhất** (VNĐ) |
| `displaySalePrice` | BigDecimal/Number | Giá sau khuyến mãi của biến thể có giá thấp nhất (VNĐ) |
| `discountType` | String | Loại giảm giá: `"PERCENTAGE"` (giảm theo %) hoặc `"FIXED"` (giảm cố định) |
| `discountValue` | BigDecimal/Number | Mức giảm giá (nếu PERCENTAGE thì là %, nếu FIXED thì là số tiền VNĐ) |
| `averageRating` | Double/Number | Điểm đánh giá trung bình (1.0 - 5.0) |
| `totalRatings` | Integer | Tổng số lượt đánh giá |
| `inWishlist` | Boolean | `true` nếu sản phẩm có trong wishlist của user, `false` nếu chưa đăng nhập hoặc chưa thêm |

---

### ProductDetailResponse

Response cho chi tiết sản phẩm (Get Product Detail)

```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max",
  "description": "Mô tả chi tiết về sản phẩm...",
  "categoryId": 5,
  "categoryName": "Điện thoại",
  "brandId": 2,
  "brandName": "Apple",
  "imageUrl": "https://cloudinary.com/...",
  "imagePublicId": "products/iphone15_abc123",
  "averageRating": 4.5,
  "totalRatings": 120,
  "displayOriginalPrice": 29990000,
  "displaySalePrice": 26990000,
  "discountType": "PERCENTAGE",
  "discountValue": 10,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-10-20T14:20:00",
  "inWishlist": false,
  "attributes": [
    {
      "id": 1,
      "name": "Chip",
      "value": "A17 Pro"
    },
    {
      "id": 2,
      "name": "Hệ điều hành",
      "value": "iOS 17"
    }
  ],
  "variations": [
    {
      "id": 10,
      "variationName": "256GB - Titan Tự nhiên",
      "price": 29990000,
      "sku": "IP15PM-256-TN",
      "availableQuantity": 50,
      "warrantyMonths": 12,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-10-20T14:20:00",
      "salePrice": 26990000,
      "discountType": "PERCENTAGE",
      "discountValue": 10,
      "images": [
        {
          "id": 1,
          "imageUrl": "https://cloudinary.com/image1.jpg"
        }
      ],
      "attributes": [
        {
          "id": 3,
          "name": "Dung lượng",
          "value": "256GB"
        },
        {
          "id": 4,
          "name": "Màu sắc",
          "value": "Titan Tự nhiên"
        }
      ]
    }
  ]
}
```

#### Mô tả các trường chính:

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `id` | Integer | ID của sản phẩm |
| `name` | String | Tên sản phẩm |
| `description` | String | Mô tả chi tiết về sản phẩm |
| `categoryId` | Integer | ID danh mục sản phẩm |
| `categoryName` | String | Tên danh mục |
| `brandId` | Integer | ID thương hiệu |
| `brandName` | String | Tên thương hiệu |
| `imageUrl` | String | URL ảnh đại diện |
| `imagePublicId` | String | Public ID của ảnh trên Cloudinary (dùng để xóa/cập nhật) |
| `averageRating` | Double | Điểm đánh giá trung bình (1.0 - 5.0) |
| `totalRatings` | Integer | Tổng số lượt đánh giá |
| `displayOriginalPrice` | BigDecimal | Giá gốc của biến thể rẻ nhất |
| `displaySalePrice` | BigDecimal | Giá khuyến mãi của biến thể rẻ nhất |
| `discountType` | String | Loại giảm giá: `"PERCENTAGE"` hoặc `"FIXED"` |
| `discountValue` | BigDecimal | Mức giảm giá |
| `createdAt` | DateTime (ISO 8601) | Thời gian tạo sản phẩm |
| `updatedAt` | DateTime (ISO 8601) | Thời gian cập nhật gần nhất |
| `inWishlist` | Boolean | Trạng thái trong wishlist |
| `attributes` | Array[AttributeDto] | Danh sách thuộc tính **chung** của sản phẩm (VD: Chip, OS) |
| `variations` | Array[VariationDto] | Danh sách các biến thể của sản phẩm |

#### AttributeDto (thuộc tính sản phẩm)

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `id` | Integer | ID của thuộc tính |
| `name` | String | Tên thuộc tính (VD: "Chip", "RAM", "Màu sắc") |
| `value` | String | Giá trị thuộc tính (VD: "A17 Pro", "8GB", "Đen") |

#### VariationDto (biến thể sản phẩm)

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `id` | Integer | ID của biến thể |
| `variationName` | String | Tên biến thể (VD: "256GB - Titan Tự nhiên") |
| `price` | BigDecimal | Giá gốc của biến thể (VNĐ) |
| `sku` | String | Mã SKU để quản lý kho |
| `availableQuantity` | Integer | Số lượng khả dụng (= stockQuantity - reservedQuantity) |
| `warrantyMonths` | Integer | Số tháng bảo hành |
| `createdAt` | DateTime | Thời gian tạo biến thể |
| `updatedAt` | DateTime | Thời gian cập nhật |
| `salePrice` | BigDecimal | Giá sau khuyến mãi (được tính realtime) |
| `discountType` | String | Loại giảm giá: `"PERCENTAGE"` hoặc `"FIXED"` |
| `discountValue` | BigDecimal | Mức giảm giá |
| `images` | Array[ImageDto] | Danh sách ảnh của biến thể |
| `attributes` | Array[AttributeDto] | Thuộc tính **riêng** của biến thể (VD: Dung lượng, Màu sắc) |

#### ImageDto (ảnh biến thể)

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `id` | Integer | ID của ảnh |
| `imageUrl` | String | URL ảnh từ Cloudinary |

---

## Category APIs

### CategoryResponse

Response cho danh sách danh mục và chi tiết danh mục

```json
{
  "id": 1,
  "name": "Điện thoại",
  "parentCategoryId": null,
  "parentCategoryName": null,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "childCategories": [
    {
      "id": 5,
      "name": "iPhone",
      "parentCategoryId": 1,
      "parentCategoryName": "Điện thoại",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00",
      "childCategories": []
    }
  ]
}
```

#### Mô tả các trường:

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `id` | Integer | ID của danh mục |
| `name` | String | Tên danh mục |
| `parentCategoryId` | Integer/null | ID của danh mục cha (null nếu là danh mục gốc) |
| `parentCategoryName` | String/null | Tên danh mục cha |
| `createdAt` | DateTime | Thời gian tạo |
| `updatedAt` | DateTime | Thời gian cập nhật |
| `childCategories` | Array[CategoryResponse] | Danh sách danh mục con (có thể rỗng `[]`) |

**Lưu ý:** Cấu trúc danh mục là **đệ quy** (recursive), danh mục con cũng có thể chứa danh mục con khác.

---

## Brand APIs

### BrandResponse

Response cho danh sách thương hiệu và chi tiết thương hiệu

```json
{
  "id": 1,
  "name": "Apple",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### Mô tả các trường:

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `id` | Integer | ID của thương hiệu |
| `name` | String | Tên thương hiệu |
| `createdAt` | DateTime | Thời gian tạo |
| `updatedAt` | DateTime | Thời gian cập nhật |

---

## Attribute APIs

### AttributeResponse

Response cho danh sách thuộc tính và tìm kiếm thuộc tính

```json
{
  "id": 1,
  "name": "Màu sắc"
}
```

#### Mô tả các trường:

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `id` | Integer | ID của thuộc tính |
| `name` | String | Tên thuộc tính (VD: "Màu sắc", "Dung lượng", "RAM", "Chip") |

**Lưu ý:** Đây là **định nghĩa thuộc tính**, giá trị thực tế của thuộc tính nằm trong `ProductDetailResponse.AttributeDto` với trường `value`.

---

## Campaign APIs

### CampaignResponse

Response cho danh sách chiến dịch active và chi tiết chiến dịch

```json
{
  "id": 1,
  "name": "Khuyến mãi Black Friday 2024",
  "description": "Giảm giá sốc lên đến 50% cho tất cả sản phẩm",
  "image": "https://cloudinary.com/campaign_banner.jpg",
  "imageID": "campaigns/blackfriday2024_xyz",
  "startDate": "2024-11-24T00:00:00",
  "endDate": "2024-11-30T23:59:59",
  "promotionCount": 15
}
```

#### Mô tả các trường:

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `id` | Integer | ID của chiến dịch |
| `name` | String | Tên chiến dịch |
| `description` | String | Mô tả chi tiết về chiến dịch |
| `image` | String | URL ảnh banner của chiến dịch |
| `imageID` | String | Public ID của ảnh trên Cloudinary |
| `startDate` | DateTime (ISO 8601) | Ngày bắt đầu chiến dịch |
| `endDate` | DateTime (ISO 8601) | Ngày kết thúc chiến dịch |
| `promotionCount` | Integer | Số lượng khuyến mãi trong chiến dịch này |

**Lưu ý:** API `/campaigns/active` chỉ trả về các chiến dịch:
- Đang trong thời gian hiệu lực (hiện tại >= startDate và <= endDate)
- Có trạng thái `active = true`

---

## Pagination Response

Hầu hết các API danh sách đều trả về dữ liệu dạng **phân trang** (Page)

```json
{
  "content": [...],           // Mảng chứa dữ liệu
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 100,       // Tổng số phần tử
  "totalPages": 5,            // Tổng số trang
  "last": false,              // Có phải trang cuối không
  "first": true,              // Có phải trang đầu không
  "size": 20,                 // Kích thước trang (số phần tử/trang)
  "number": 0,                // Số thứ tự trang hiện tại (bắt đầu từ 0)
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 20,     // Số phần tử trong trang hiện tại
  "empty": false              // Trang có rỗng không
}
```

#### Các trường quan trọng:

| Trường | Kiểu dữ liệu | Mô tả |
|--------|-------------|-------|
| `content` | Array | Mảng chứa dữ liệu của trang hiện tại |
| `totalElements` | Integer | Tổng số phần tử trong toàn bộ dữ liệu |
| `totalPages` | Integer | Tổng số trang |
| `size` | Integer | Số phần tử tối đa trên mỗi trang |
| `number` | Integer | Số thứ tự trang hiện tại (bắt đầu từ 0) |
| `first` | Boolean | `true` nếu là trang đầu tiên |
| `last` | Boolean | `true` nếu là trang cuối cùng |
| `numberOfElements` | Integer | Số phần tử thực tế trong trang hiện tại |
| `empty` | Boolean | `true` nếu trang không có dữ liệu |

---

## 🔍 Các giá trị Enum

### DiscountType (Loại giảm giá)

| Giá trị | Mô tả |
|---------|-------|
| `PERCENTAGE` | Giảm giá theo phần trăm (%) |
| `FIXED` | Giảm giá cố định (số tiền VNĐ) |

**Ví dụ:**
- `discountType = "PERCENTAGE"`, `discountValue = 10` → Giảm 10%
- `discountType = "FIXED"`, `discountValue = 1000000` → Giảm 1,000,000 VNĐ

---

## 💡 Lưu ý khi sử dụng

### 1. Định dạng thời gian
- Tất cả các trường thời gian đều sử dụng định dạng **ISO 8601**: `yyyy-MM-dd'T'HH:mm:ss`
- Múi giờ: **UTC**

### 2. Định dạng số tiền
- Tất cả giá tiền đều tính bằng **VNĐ** (Việt Nam Đồng)
- Kiểu dữ liệu: `BigDecimal` (để đảm bảo độ chính xác)

### 3. Authentication
- Tất cả các API GET dành cho khách hàng đều **KHÔNG yêu cầu authentication**
- Tuy nhiên, nếu có JWT token, một số trường bổ sung sẽ được trả về:
  - `inWishlist`: Trạng thái wishlist của user

### 4. Phân trang
- Tham số `page` bắt đầu từ **0** (không phải 1)
- Tham số `size` mặc định là **20** (có thể tùy chỉnh)
- Sắp xếp: `sortBy` + `sortDirection` (ASC/DESC)

### 5. Giá sản phẩm
- `displayOriginalPrice`: Giá gốc của **biến thể rẻ nhất**
- `displaySalePrice`: Giá sau khuyến mãi của **biến thể rẻ nhất**
- Giá được tính **realtime** dựa trên các promotion đang active

---

## 📞 Contact & Support

Nếu có thắc mắc về API, vui lòng liên hệ team Backend.

**Base URL:** `http://localhost:8080/api`

**Import Postman Collection:** `Customer_GET_APIs.postman_collection.json`
