# Techbox Store Backend

## 1) Yêu cầu môi trường

- Java `21` và Maven (`mvn`) nếu bạn chạy local không dùng Docker
- Docker & Docker Compose (khuyến nghị cho dev)
- PostgreSQL (nếu chạy local không dùng Compose)

## 2) Tạo file .env

Tại thư mục gốc của backend (`techbox_store_backend`), tạo file tên `.env` với nội dung sau (có thể giữ mặc định để chạy nhanh):

```env
# App port
SERVER_PORT=8080

# Database config
POSTGRES_DB=techbox_db
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin
POSTGRES_PORT=5432

# Timezone
TZ=Asia/Ho_Chi_Minh
```

## 3) Docker

Backend đã kèm `docker-compose.yml` và `.env`:

```pwsh
# tại thư mục backend
cd techbox_store_backend

# khởi chạy DB + app (hot-reload Maven, mount source)
docker compose up -d --build
```

- API Base URL (mặc định): `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- PostgreSQL: cổng `localhost:5432` (theo `.env`)
- pgAdmin: `http://localhost:5050` (user/pass mặc định trong compose)

## 🔗 API Endpoints

### Categories API
- `GET /api/categories` - Lấy tất cả danh mục
- `GET /api/categories/{id}` - Lấy danh mục theo ID
- `GET /api/categories/root` - Lấy danh mục gốc
- `GET /api/categories/{parentId}/children` - Lấy danh mục con
- `POST /api/categories` - Tạo danh mục mới
- `PUT /api/categories/{id}` - Cập nhật danh mục
- `DELETE /api/categories/{id}` - Xóa danh mục
- `GET /api/categories/exists?name=...` - Kiểm tra tên tồn tại

### Brands API
- `GET /api/brands` - Lấy tất cả thương hiệu
- `GET /api/brands/{id}` - Lấy thương hiệu theo ID
- `POST /api/brands` - Tạo thương hiệu mới
- `PUT /api/brands/{id}` - Cập nhật thương hiệu
- `DELETE /api/brands/{id}` - Xóa thương hiệu
- `GET /api/brands/exists?name=...` - Kiểm tra tên tồn tại

## 🧪 Quick Test

```bash
# Test tạo danh mục
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Electronics"}'

# Test tạo thương hiệu
curl -X POST http://localhost:8080/api/brands \
  -H "Content-Type: application/json" \
  -d '{"name": "Apple"}'

# Test lấy tất cả danh mục
curl -X GET http://localhost:8080/api/categories
```

## 4) Sử dụng pgAdmin (tuỳ chọn)

- Truy cập: `http://localhost:5050`
  - Tài khoản: admin@admin.com / mk: admin
- Tạo Server mới trỏ tới DB:
  - Host: `db`
  - Port: `5432`
  - User/Password: lấy từ `.env` (`admin/admin` mặc định)
