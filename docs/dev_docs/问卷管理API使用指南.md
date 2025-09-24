# 问卷管理API使用指南

## 概述

问卷管理模块提供了完整的问卷生命周期管理功能，包括创建、查询、状态管理等。所有API都需要JWT认证。

## API接口列表

### 1. 创建问卷

**接口地址**：`POST /api/survey/createSurvey`

**请求头**：
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**请求参数**：
```json
{
  "title": "问卷标题",
  "description": "问卷描述",
  "status": 0
}
```

**参数说明**：
- `title`：问卷标题（必填，不能为空）
- `description`：问卷描述（必填，不能为空）
- `status`：问卷状态（必填）
  - `0`：草稿
  - `1`：发布
  - `2`：关闭

**成功响应**：
```json
{
  "success": true,
  "message": "问卷创建成功",
  "data": null
}
```

**失败响应**：
```json
{
  "success": false,
  "code": "USER-0103",
  "message": "标题不能为空",
  "data": null
}
```

### 2. 测试创建问卷

**接口地址**：`POST /api/survey/test/create`

**请求头**：
```
Authorization: Bearer <access_token>
```

**说明**：无需请求参数，自动使用当前登录用户信息创建测试问卷

**成功响应**：
```json
{
  "success": true,
  "message": "成功",
  "data": "测试问卷创建成功！用户ID: 1, 用户名: admin"
}
```

### 3. 获取当前用户信息

**接口地址**：`GET /api/survey/test/user-info`

**请求头**：
```
Authorization: Bearer <access_token>
```

**成功响应**：
```json
{
  "success": true,
  "message": "获取用户信息成功",
  "data": {
    "userId": 1,
    "username": "admin",
    "message": "JWT认证成功，可以正常获取用户信息"
  }
}
```

### 4. 查询我的问卷列表

**接口地址**：`GET /api/survey/test/my-surveys`

**请求头**：
```
Authorization: Bearer <access_token>
```

**成功响应**：
```json
{
  "success": true,
  "message": "查询成功",
  "data": []
}
```

## 使用示例

### 1. 完整的问卷创建流程

```bash
# 1. 用户登录获取Token
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'

# 响应中获取 accessToken
{
  "success": true,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    ...
  }
}

# 2. 使用Token创建问卷
curl -X POST http://localhost:8080/api/survey/createSurvey \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "title": "用户满意度调查",
    "description": "这是一份关于产品使用体验的满意度调查问卷",
    "status": 0
  }'

# 3. 测试创建问卷（快速测试）
curl -X POST http://localhost:8080/api/survey/test/create \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# 4. 查看用户信息
curl -X GET http://localhost:8080/api/survey/test/user-info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 2. JavaScript/前端使用示例

```javascript
// 设置请求拦截器，自动添加Token
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// 创建问卷
async function createSurvey() {
    try {
        const response = await axios.post('/api/survey/createSurvey', {
            title: '客户反馈调查',
            description: '收集客户对我们服务的反馈意见',
            status: 0  // 草稿状态
        });
        
        console.log('问卷创建成功:', response.data);
        return response.data;
    } catch (error) {
        console.error('创建失败:', error.response.data);
        throw error;
    }
}

// 测试创建问卷
async function testCreateSurvey() {
    try {
        const response = await axios.post('/api/survey/test/create');
        console.log('测试问卷创建成功:', response.data);
        return response.data;
    } catch (error) {
        console.error('测试创建失败:', error.response.data);
        throw error;
    }
}

// 获取用户信息
async function getUserInfo() {
    try {
        const response = await axios.get('/api/survey/test/user-info');
        console.log('用户信息:', response.data);
        return response.data;
    } catch (error) {
        console.error('获取用户信息失败:', error.response.data);
        throw error;
    }
}
```

## 错误处理

### 常见错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| `COMM-0201` | 未授权访问 | 检查Token是否有效，重新登录 |
| `USER-0103` | 参数验证失败 | 检查请求参数是否符合要求 |
| `COMM-0701` | 服务器内部错误 | 检查服务器日志，联系管理员 |

### 认证相关错误

```json
// Token缺失
{
  "success": false,
  "code": "COMM-0201",
  "message": "缺少认证token",
  "data": null
}

// Token过期
{
  "success": false,
  "code": "COMM-0201",
  "message": "无效的访问令牌",
  "data": null
}
```

### 参数验证错误

```json
// 标题为空
{
  "success": false,
  "code": "BAD_REQUEST",
  "message": "标题不能为空",
  "data": null
}

// 描述为空
{
  "success": false,
  "code": "BAD_REQUEST",
  "message": "描述不能为空",
  "data": null
}
```

## 注意事项

1. **认证要求**：所有问卷API都需要有效的JWT访问令牌
2. **用户ID自动设置**：创建问卷时，用户ID会自动从JWT Token中获取，无需在请求中传递
3. **时间戳**：问卷创建时间会自动设置为当前时间
4. **状态说明**：
   - 草稿状态（0）：可以修改和删除
   - 发布状态（1）：对外可见，不能修改结构
   - 关闭状态（2）：不再接收新的回答

## 数据库结构

问卷表结构：
```sql
CREATE TABLE survey (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,           -- 创建者ID（从JWT获取）
    title VARCHAR(200) NOT NULL,       -- 问卷标题
    description TEXT,                  -- 问卷描述
    status TINYINT DEFAULT 0,          -- 状态：0=草稿,1=发布,2=关闭
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间（自动设置）
    FOREIGN KEY (user_id) REFERENCES user(id)
);
```

## 后续功能扩展

1. **问卷查询**：按条件查询问卷列表
2. **状态管理**：发布/关闭问卷
3. **问卷搜索**：按标题/描述搜索
4. **权限控制**：只能操作自己创建的问卷
5. **问卷统计**：查看问卷回答统计信息