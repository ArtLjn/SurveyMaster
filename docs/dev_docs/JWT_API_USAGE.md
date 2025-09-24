# JWT认证API使用示例

## 1. 用户登录获取Token

### 请求
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'
```

### 成功响应
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "user": {
      "id": 1,
      "username": "your_username",
      "email": "your_email@example.com",
      "createdAt": "2025-09-22 10:30:00"
    },
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdCIsInRva2VuVHlwZSI6ImFjY2VzcyIsInN1YiI6InRlc3QiLCJpYXQiOjE2OTU0MjcyMDAsImV4cCI6MTY5NTQzNDQwMH0.abc123...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdCIsInRva2VuVHlwZSI6InJlZnJlc2giLCJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjk1NDI3MjAwLCJleHAiOjE2OTYwMzIwMDB9.def456...",
    "tokenType": "Bearer",
    "expiresIn": 7200000
  }
}
```

## 2. 使用Token访问受保护的API

### 请求
```bash
curl -X GET http://localhost:8080/api/protected-endpoint \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdCIsInRva2VuVHlwZSI6ImFjY2VzcyIsInN1YiI6InRlc3QiLCJpYXQiOjE2OTU0MjcyMDAsImV4cCI6MTY5NTQzNDQwMH0.abc123..."
```

### 未授权响应 (401)
```json
{
  "success": false,
  "code": "COMM-0201",
  "message": "缺少认证token",
  "data": null
}
```

## 3. 刷新Token

### 请求
```bash
curl -X POST http://localhost:8080/api/user/refresh-token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "refreshToken=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdCIsInRva2VuVHlwZSI6InJlZnJlc2giLCJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjk1NDI3MjAwLCJleHAiOjE2OTYwMzIwMDB9.def456..."
```

### 成功响应
```json
{
  "success": true,
  "message": "令牌刷新成功",
  "data": {
    "user": {
      "id": 1,
      "username": "your_username",
      "email": "your_email@example.com"
    },
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.new_access_token...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.new_refresh_token...",
    "tokenType": "Bearer",
    "expiresIn": 7200000
  }
}
```

## 4. Token失效响应

### 无效或过期Token响应 (401)
```json
{
  "success": false,
  "code": "COMM-0201",
  "message": "无效的访问令牌",
  "data": null
}
```

## JWT配置说明

- **访问令牌有效期**: 2小时 (7200000毫秒)
- **刷新令牌有效期**: 7天 (604800000毫秒)
- **请求头**: Authorization
- **Token前缀**: Bearer
- **加密算法**: HS256

## 注意事项

1. 访问令牌用于日常API访问，有效期较短
2. 刷新令牌用于获取新的访问令牌，有效期较长
3. 登录和注册接口不需要Token验证
4. 其他所有 `/api/**` 路径都需要有效的访问令牌
5. Token应该安全存储在客户端，避免泄露