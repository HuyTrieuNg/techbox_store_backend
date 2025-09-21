# 🚀 Hướng dẫn sử dụng API TechBox Store

## 📖 Tài liệu API

Xem tài liệu chi tiết tại: [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## 🛠️ Cách sử dụng

### 1. Khởi chạy ứng dụng
```bash
# Sử dụng Docker Compose
docker-compose up

# Hoặc chạy trực tiếp với Maven
./mvnw spring-boot:run
```

### 2. Kiểm tra kết nối
Truy cập: `http://localhost:8080`

### 3. Test API với Postman

1. Import file `TechBox_Store_API.postman_collection.json` vào Postman
2. Set variable `base_url` = `http://localhost:8080`
3. Chạy các request test

### 4. Test API với cURL

#### Tạo danh mục gốc:
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Electronics"}'
```

#### Lấy tất cả danh mục:
```bash
curl -X GET http://localhost:8080/api/categories
```

#### Tạo thương hiệu:
```bash
curl -X POST http://localhost:8080/api/brands \
  -H "Content-Type: application/json" \
  -d '{"name": "Apple"}'
```

## 📋 Kịch bản test mẫu

### Scenario 1: Quản lý danh mục phân cấp

1. **Tạo danh mục gốc "Electronics"**
   ```bash
   POST /api/categories
   {"name": "Electronics"}
   ```

2. **Tạo danh mục con "Smartphones"**
   ```bash
   POST /api/categories
   {"name": "Smartphones", "parentCategoryId": 1}
   ```

3. **Tạo danh mục con của "Smartphones"**
   ```bash
   POST /api/categories
   {"name": "iPhone", "parentCategoryId": 2}
   ```

4. **Lấy cây danh mục**
   ```bash
   GET /api/categories
   ```

5. **Lấy chỉ danh mục gốc**
   ```bash
   GET /api/categories/root
   ```

6. **Lấy danh mục con của Electronics**
   ```bash
   GET /api/categories/1/children
   ```

### Scenario 2: Quản lý thương hiệu

1. **Tạo các thương hiệu**
   ```bash
   POST /api/brands
   {"name": "Apple"}
   
   POST /api/brands  
   {"name": "Samsung"}
   
   POST /api/brands
   {"name": "Dell"}
   ```

2. **Lấy tất cả thương hiệu**
   ```bash
   GET /api/brands
   ```

3. **Cập nhật thương hiệu**
   ```bash
   PUT /api/brands/1
   {"name": "Apple Inc."}
   ```

4. **Kiểm tra tên tồn tại**
   ```bash
   GET /api/brands/exists?name=Apple
   ```

### Scenario 3: Validation Testing

1. **Test tên trống (sẽ lỗi 400)**
   ```bash
   POST /api/categories
   {"name": ""}
   ```

2. **Test tên trùng lặp (sẽ lỗi 400)**
   ```bash
   POST /api/categories
   {"name": "Electronics"}  # Nếu đã tồn tại
   ```

3. **Test xóa danh mục có con (sẽ lỗi 400)**
   ```bash
   DELETE /api/categories/1  # Nếu có danh mục con
   ```

4. **Test circular reference (sẽ lỗi 400)**
   ```bash
   PUT /api/categories/1
   {"name": "Electronics", "parentCategoryId": 1}  # Tự làm parent
   ```

## 🔧 Troubleshooting

### Lỗi thường gặp:

1. **Connection refused**
   - Kiểm tra ứng dụng đã chạy chưa
   - Kiểm tra port 8080 có bị chiếm không

2. **400 Bad Request**
   - Kiểm tra format JSON
   - Kiểm tra validation rules
   - Xem console log để biết chi tiết lỗi

3. **404 Not Found**
   - Kiểm tra URL endpoint
   - Kiểm tra ID có tồn tại không

### Database Issues:

1. **Kiểm tra database connection**
   - Xem file `application.properties`
   - Kiểm tra Docker containers running

2. **Reset database**
   ```bash
   docker-compose down -v
   docker-compose up
   ```

## 📊 Database Schema

### Categories Table:
- `id` (Primary Key)
- `name` (Unique, Not Null)
- `parent_category_id` (Foreign Key to categories.id)
- `created_at`
- `updated_at`

### Brands Table:
- `id` (Primary Key)
- `name` (Unique, Not Null)
- `created_at`
- `updated_at`

## 🎯 Next Steps

1. Thêm authentication/authorization
2. Implement pagination cho list endpoints
3. Thêm search functionality
4. Thêm soft delete
5. Thêm audit logging
6. Implement caching
7. Thêm OpenAPI/Swagger documentation

## 📞 Support

Nếu gặp vấn đề, hãy kiểm tra:
1. Console logs
2. Database connection
3. Request format
4. API documentation

Happy coding! 🎉