# Hướng dẫn sử dụng Refresh Token

## Tổng quan
Hệ thống đã được cập nhật để hỗ trợ refresh token với các tính năng sau:

### 1. Token Expiry
- **Access Token**: 30 giờ
- **Refresh Token**: 7 ngày

### 2. API Endpoints

#### Login
```http
POST /login
Content-Type: application/json

{
    "username": "your_username",
    "password": "your_password"
}
```

**Response:**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 108000
}
```

#### Refresh Token
```http
POST /refresh-token
Content-Type: application/json

{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
    "accessToken": "new_access_token...",
    "refreshToken": "new_refresh_token...",
    "tokenType": "Bearer",
    "expiresIn": 108000
}
```

### 3. Xử lý lỗi khi Access Token hết hạn

Khi access token hết hạn, API sẽ trả về:
```json
{
    "error": "TOKEN_EXPIRED",
    "message": "Access token expired. Please use refresh token to get a new access token.",
    "timestamp": 1726876800000,
    "requiresRefresh": true
}
```

### 4. Cách xử lý trên Frontend

#### JavaScript Example:
```javascript
// Interceptor cho API requests
axios.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        
        if (error.response?.status === 401 && 
            error.response?.data?.requiresRefresh && 
            !originalRequest._retry) {
            
            originalRequest._retry = true;
            
            try {
                const refreshToken = localStorage.getItem('refreshToken');
                const response = await axios.post('/refresh-token', {
                    refreshToken: refreshToken
                });
                
                const { accessToken, refreshToken: newRefreshToken } = response.data;
                
                // Cập nhật tokens
                localStorage.setItem('accessToken', accessToken);
                localStorage.setItem('refreshToken', newRefreshToken);
                
                // Retry original request với token mới
                originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                return axios(originalRequest);
                
            } catch (refreshError) {
                // Refresh token cũng hết hạn - redirect to login
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                window.location.href = '/login';
            }
        }
        
        return Promise.reject(error);
    }
);
```

### 5. Các loại lỗi và cách xử lý

| Error Code | Description | Action |
|------------|-------------|---------|
| `TOKEN_EXPIRED` | Access token hết hạn | Sử dụng refresh token |
| `INVALID_SIGNATURE` | JWT signature không hợp lệ | Đăng nhập lại |
| `MALFORMED_TOKEN` | Token bị lỗi format | Đăng nhập lại |
| `REFRESH_FAILED` | Refresh token không hợp lệ hoặc hết hạn | Đăng nhập lại |

### 6. Best Practices

1. **Lưu trữ tokens an toàn**: Sử dụng httpOnly cookies hoặc secure storage
2. **Auto-refresh**: Implement interceptor để tự động refresh khi cần
3. **Logout**: Xóa cả access token và refresh token
4. **Token rotation**: Mỗi lần refresh sẽ tạo ra cả access token và refresh token mới

### 7. Security Features

- Sử dụng separate secret keys cho access và refresh tokens
- Refresh token có thời gian sống dài hơn nhưng bị thay thế mỗi lần sử dụng
- Comprehensive error handling với detailed error messages
- Logging cho security monitoring
