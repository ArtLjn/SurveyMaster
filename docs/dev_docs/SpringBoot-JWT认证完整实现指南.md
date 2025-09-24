# Spring Boot JWT 认证完整实现指南

> 在现代Web应用开发中，JWT（JSON Web Token）已经成为最流行的身份认证解决方案之一。本文将基于Spring Boot项目实战，详细介绍如何实现一套完整的JWT认证系统，包括双Token机制、安全配置、最佳实践等内容。

## 📋 目录

- [1. JWT概述](#1-jwt概述)
- [2. 技术架构设计](#2-技术架构设计)
- [3. 依赖配置](#3-依赖配置)
- [4. 核心组件实现](#4-核心组件实现)
- [5. 安全配置](#5-安全配置)
- [6. API接口实现](#6-api接口实现)
- [7. 客户端使用](#7-客户端使用)
- [8. 最佳实践](#8-最佳实践)
- [9. 常见问题](#9-常见问题)
- [10. 总结](#10-总结)

## 1. JWT概述

### 1.1 什么是JWT

JWT（JSON Web Token）是一种开放标准（RFC 7519），它定义了一种紧凑且自包含的方式，用于在各方之间以JSON对象的形式安全地传输信息。

### 1.2 JWT结构

JWT由三部分组成，用点（.）分隔：

```
Header.Payload.Signature
```

- **Header（头部）**：包含令牌类型和加密算法
- **Payload（载荷）**：包含声明（claims）
- **Signature（签名）**：用于验证令牌的完整性

### 1.3 JWT优势

✅ **无状态**：服务器不需要存储会话信息
✅ **跨域支持**：支持跨域认证
✅ **扩展性强**：易于水平扩展
✅ **性能优良**：减少数据库查询
✅ **移动友好**：适合移动应用开发

### 1.4 双Token机制

为了平衡安全性和用户体验，我们采用双Token机制：

- **Access Token（访问令牌）**：短期有效（2小时）
- **Refresh Token（刷新令牌）**：长期有效（7天）

## 2. 技术架构设计

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    JWT认证系统架构                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │  前端应用    │    │  移动应用    │    │  第三方应用  │      │
│  └─────────────┘    └─────────────┘    └─────────────┘      │
│         │                   │                   │           │
│         └───────────────────┼───────────────────┘           │
│                             │                               │
│                             ▼                               │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                    API网关层                            │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │ │
│  │  │ JWT拦截器   │  │ 异常处理器  │  │ 跨域配置    │     │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘     │ │
│  └─────────────────────────────────────────────────────────┘ │
│                             │                               │
│                             ▼                               │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                    业务服务层                            │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │ │
│  │  │ 用户服务    │  │ JWT工具类   │  │ 认证服务    │     │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘     │ │
│  └─────────────────────────────────────────────────────────┘ │
│                             │                               │
│                             ▼                               │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                    数据存储层                            │ │
│  │       MySQL数据库        Redis缓存        配置文件      │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 核心组件说明

| 组件 | 功能 | 作用 |
|------|------|------|
| **JwtUtil** | JWT工具类 | Token生成、解析、验证 |
| **JwtConfig** | JWT配置类 | 配置参数管理 |
| **JwtInterceptor** | JWT拦截器 | 自动验证Token |
| **WebMvcConfig** | Web配置类 | 注册拦截器 |
| **UserService** | 用户服务 | 认证业务逻辑 |

## 3. 依赖配置

### 3.1 Maven依赖

在`pom.xml`中添加JWT相关依赖：

```xml
<!-- JWT 依赖 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Jackson JSR310 模块，支持 Java 8 时间类型 -->
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

### 3.2 配置文件

在`application.yml`中添加JWT配置：

```yaml
# JWT配置
jwt:
  secret: surveymaster-jwt-secret-key-for-authentication-very-secure-2025
  access-token-expiration: 7200000      # 访问令牌过期时间（毫秒）- 2小时
  refresh-token-expiration: 604800000   # 刷新令牌过期时间（毫秒）- 7天
  token-header: Authorization           # token请求头名称
  token-prefix: "Bearer "               # token前缀

# Jackson 配置（支持 Java 8 时间类型）
spring:
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    serialization:
      write-dates-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false
```

## 4. 核心组件实现

### 4.1 JWT工具类

创建功能完善的JWT工具类：

```java
@Component
public class JwtUtil {

    @Value("${jwt.secret:surveymaster-jwt-secret-key-for-authentication-very-secure}")
    private String secret;

    @Value("${jwt.access-token-expiration:7200000}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private Long refreshTokenExpiration;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "access");
        
        return createToken(claims, username, accessTokenExpiration);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "refresh");
        
        return createToken(claims, username, refreshTokenExpiration);
    }

    /**
     * 创建Token
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 验证访问令牌
     */
    public Boolean validateAccessToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "access".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证刷新令牌
     */
    public Boolean validateRefreshToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "refresh".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // ... 其他辅助方法
}
```

### 4.2 JWT配置类

创建配置类管理JWT参数：

```java
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String secret = "surveymaster-jwt-secret-key-for-authentication-very-secure";
    private Long accessTokenExpiration = 7200000L;
    private Long refreshTokenExpiration = 604800000L;
    private String tokenHeader = "Authorization";
    private String tokenPrefix = "Bearer ";

    // getters and setters...
}
```

### 4.3 JWT拦截器

实现自动验证Token的拦截器：

```java
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public JwtInterceptor(JwtUtil jwtUtil, JwtConfig jwtConfig, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = getTokenFromRequest(request);
        
        if (!StringUtils.hasText(token)) {
            return handleUnauthorized(response, "缺少认证token");
        }

        try {
            // 验证token是否为有效的访问令牌
            if (!jwtUtil.validateAccessToken(token)) {
                return handleUnauthorized(response, "无效的访问令牌");
            }

            // 从token中获取用户信息并设置到请求属性中
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            request.setAttribute("currentUsername", username);
            request.setAttribute("currentUserId", userId);
            
            return true;
        } catch (Exception e) {
            return handleUnauthorized(response, "token验证失败：" + e.getMessage());
        }
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getTokenHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getTokenPrefix())) {
            return bearerToken.substring(jwtConfig.getTokenPrefix().length());
        }
        return null;
    }

    /**
     * 处理未授权的请求
     */
    private boolean handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        ApiResponse<Object> apiResponse = ApiResponse.error(ErrorCode.UNAUTHORIZED, message);
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        
        response.getWriter().write(jsonResponse);
        return false;
    }
}
```

### 4.4 Web配置类

注册JWT拦截器：

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Autowired
    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")                    // 拦截所有API路径
                .excludePathPatterns(
                        "/api/user/login",                     // 排除登录接口
                        "/api/user/register",                  // 排除注册接口
                        "/api/user/refresh-token",            // 排除刷新token接口
                        "/api/health/**",                     // 排除健康检查接口
                        "/api/public/**"                      // 排除公共接口
                );
    }
}
```

## 5. 安全配置

### 5.1 登录响应对象

设计包含Token信息的响应对象：

```java
@Data
public class LoginResponse {
    /**
     * 用户信息
     */
    private User user;
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 访问令牌类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 访问令牌过期时间（毫秒时间戳）
     */
    private Long expiresIn;
    
    public LoginResponse() {}
    
    public LoginResponse(User user, String accessToken, String refreshToken, Long expiresIn) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }
}
```

### 5.2 用户服务实现

实现包含JWT功能的用户服务：

```java
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil, JwtConfig jwtConfig) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public LoginResponse login(String username, String password) {
        // 1. 验证用户凭据
        User user = userMapper.selectByUsernameAndPassword(username, MD5Util.md5(password));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }
        
        // 2. 生成JWT token
        String accessToken = jwtUtil.generateAccessToken((long) user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken((long) user.getId(), user.getUsername());
        
        // 3. 返回登录响应信息
        return new LoginResponse(user, accessToken, refreshToken, jwtConfig.getAccessTokenExpiration());
    }
    
    @Override
    public LoginResponse refreshAccessToken(String refreshToken) {
        // 1. 验证刷新令牌的有效性
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
        
        try {
            // 2. 从刷新令牌中获取用户信息
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            
            // 3. 生成新的访问令牌和刷新令牌
            String newAccessToken = jwtUtil.generateAccessToken(userId, username);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);
            
            // 4. 查找用户信息
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                // 如果查不到用户，创建一个简单的用户对象用于响应
                user = new User();
                user.setId(userId.intValue());
                user.setUsername(username);
            }
            
            // 5. 返回新的登录响应
            return new LoginResponse(user, newAccessToken, newRefreshToken, jwtConfig.getAccessTokenExpiration());
            
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
    }
}
```

## 6. API接口实现

### 6.1 用户控制器

实现JWT相关的API接口：

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @LogBusiness("用户登录")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody UserLogin userLogin) {
        LoginResponse loginResponse = userService.login(userLogin.getUsername(), userLogin.getPassword());
        return ApiResponse.success("登录成功", loginResponse);
    }

    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh-token")
    @LogBusiness("刷新令牌")
    public ApiResponse<LoginResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        LoginResponse loginResponse = userService.refreshAccessToken(refreshToken);
        return ApiResponse.success("令牌刷新成功", loginResponse);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @LogBusiness("用户注册")
    public ApiResponse<String> register(@Valid @RequestBody UserRegister userRegister) {
        userService.register(userRegister);
        return ApiResponse.success("注册成功");
    }
}
```

### 6.2 测试控制器

创建测试JWT功能的控制器：

```java
@RestController
@RequestMapping("/api/jwt")
public class JwtTestController {

    @GetMapping("/protected")
    @LogBusiness("访问受保护资源")
    public ApiResponse<String> protectedEndpoint(HttpServletRequest request) {
        // 从请求属性中获取当前用户信息（由JWT拦截器设置）
        String currentUsername = (String) request.getAttribute("currentUsername");
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        String message = String.format("欢迎，%s (用户ID: %d)！这是一个受JWT保护的资源。", 
                                      currentUsername, currentUserId);
        
        return ApiResponse.success("访问成功", message);
    }

    @GetMapping("/user-info")
    @LogBusiness("获取当前用户信息")
    public ApiResponse<Object> getCurrentUserInfo(HttpServletRequest request) {
        String currentUsername = (String) request.getAttribute("currentUsername");
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        Object userInfo = new Object() {
            public final String username = currentUsername;
            public final Long userId = currentUserId;
            public final String message = "通过JWT认证获取的用户信息";
        };
        
        return ApiResponse.success("获取用户信息成功", userInfo);
    }
}
```

## 7. 客户端使用

### 7.1 登录获取Token

**请求示例：**
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

**响应示例：**
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "createdAt": "2025-09-22 10:30:00"
    },
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdCIsInRva2VuVHlwZSI6ImFjY2VzcyIsInN1YiI6InRlc3QiLCJpYXQiOjE2OTU0MjcyMDAsImV4cCI6MTY5NTQzNDQwMH0.abc123...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdCIsInRva2VuVHlwZSI6InJlZnJlc2giLCJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjk1NDI3MjAwLCJleHAiOjE2OTYwMzIwMDB9.def456...",
    "tokenType": "Bearer",
    "expiresIn": 7200000
  }
}
```

### 7.2 使用Token访问受保护API

**请求示例：**
```bash
curl -X GET http://localhost:8080/api/jwt/protected \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdCIsInRva2VuVHlwZSI6ImFjY2VzcyIsInN1YiI6InRlc3QiLCJpYXQiOjE2OTU0MjcyMDAsImV4cCI6MTY5NTQzNDQwMH0.abc123..."
```

**成功响应：**
```json
{
  "success": true,
  "message": "访问成功",
  "data": "欢迎，admin (用户ID: 1)！这是一个受JWT保护的资源。"
}
```

**未授权响应：**
```json
{
  "success": false,
  "code": "COMM-0201",
  "message": "缺少认证token",
  "data": null
}
```

### 7.3 刷新Token

**请求示例：**
```bash
curl -X POST http://localhost:8080/api/user/refresh-token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "refreshToken=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdCIsInRva2VuVHlwZSI6InJlZnJlc2giLCJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjk1NDI3MjAwLCJleHAiOjE2OTYwMzIwMDB9.def456..."
```

**成功响应：**
```json
{
  "success": true,
  "message": "令牌刷新成功",
  "data": {
    "user": {
      "id": 1,
      "username": "admin"
    },
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.new_access_token...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.new_refresh_token...",
    "tokenType": "Bearer",
    "expiresIn": 7200000
  }
}
```

### 7.4 前端集成示例

**JavaScript/Vue.js示例：**
```javascript
// API配置
const API_BASE_URL = 'http://localhost:8080/api';

// Token管理
class TokenManager {
    static setTokens(accessToken, refreshToken) {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
    }
    
    static getAccessToken() {
        return localStorage.getItem('accessToken');
    }
    
    static getRefreshToken() {
        return localStorage.getItem('refreshToken');
    }
    
    static clearTokens() {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    }
}

// HTTP请求拦截器
axios.interceptors.request.use(config => {
    const token = TokenManager.getAccessToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// HTTP响应拦截器
axios.interceptors.response.use(
    response => response,
    async error => {
        if (error.response?.status === 401) {
            // Token过期，尝试刷新
            const refreshToken = TokenManager.getRefreshToken();
            if (refreshToken) {
                try {
                    const response = await axios.post(`${API_BASE_URL}/user/refresh-token`, 
                        `refreshToken=${refreshToken}`, 
                        { headers: { 'Content-Type': 'application/x-www-form-urlencoded' }}
                    );
                    
                    const { accessToken, refreshToken: newRefreshToken } = response.data.data;
                    TokenManager.setTokens(accessToken, newRefreshToken);
                    
                    // 重试原请求
                    error.config.headers.Authorization = `Bearer ${accessToken}`;
                    return axios.request(error.config);
                } catch (refreshError) {
                    // 刷新失败，跳转到登录页
                    TokenManager.clearTokens();
                    window.location.href = '/login';
                }
            } else {
                // 没有刷新Token，跳转到登录页
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

// 登录方法
async function login(username, password) {
    try {
        const response = await axios.post(`${API_BASE_URL}/user/login`, {
            username,
            password
        });
        
        const { accessToken, refreshToken } = response.data.data;
        TokenManager.setTokens(accessToken, refreshToken);
        
        return response.data;
    } catch (error) {
        throw error;
    }
}

// 退出登录
function logout() {
    TokenManager.clearTokens();
    window.location.href = '/login';
}
```

**React示例：**
```jsx
import { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

// 认证上下文
const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            // 验证token有效性
            validateToken();
        } else {
            setLoading(false);
        }
    }, []);

    const validateToken = async () => {
        try {
            const response = await axios.get('/api/jwt/user-info');
            setUser(response.data.data);
        } catch (error) {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
        } finally {
            setLoading(false);
        }
    };

    const login = async (username, password) => {
        const response = await axios.post('/api/user/login', {
            username,
            password
        });
        
        const { user, accessToken, refreshToken } = response.data.data;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        setUser(user);
        
        return response.data;
    };

    const logout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setUser(null);
    };

    const value = {
        user,
        login,
        logout,
        loading
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
```

## 8. 最佳实践

### 8.1 安全配置建议

#### 密钥管理
```yaml
# 生产环境配置
jwt:
  secret: ${JWT_SECRET:your-very-secure-secret-key-here}
  access-token-expiration: ${JWT_ACCESS_EXPIRATION:7200000}
  refresh-token-expiration: ${JWT_REFRESH_EXPIRATION:604800000}
```

#### 密钥要求
- **长度**：至少256位（32字符）
- **复杂性**：包含字母、数字、特殊字符
- **唯一性**：每个环境使用不同密钥
- **定期更换**：建议每季度更换一次

#### 环境变量配置
```bash
# 生产环境环境变量
export JWT_SECRET="your-production-secret-key-very-secure-2025"
export JWT_ACCESS_EXPIRATION=7200000
export JWT_REFRESH_EXPIRATION=604800000
```

### 8.2 Token过期时间策略

| 场景 | 访问令牌 | 刷新令牌 | 说明 |
|------|----------|----------|------|
| **Web应用** | 2小时 | 7天 | 平衡安全性和用户体验 |
| **移动应用** | 4小时 | 30天 | 减少用户登录频率 |
| **API服务** | 1小时 | 24小时 | 高安全要求 |
| **内部系统** | 8小时 | 7天 | 办公时间内免登录 |

### 8.3 错误处理最佳实践

#### 统一错误码
```java
public enum JwtErrorCode {
    TOKEN_MISSING("JWT001", "缺少认证令牌"),
    TOKEN_EXPIRED("JWT002", "令牌已过期"),
    TOKEN_INVALID("JWT003", "无效的令牌"),
    TOKEN_MALFORMED("JWT004", "令牌格式错误"),
    ACCESS_DENIED("JWT005", "访问被拒绝"),
    REFRESH_TOKEN_EXPIRED("JWT006", "刷新令牌已过期");
    
    private final String code;
    private final String message;
    
    // constructor, getters...
}
```

#### 异常处理器
```java
@ControllerAdvice
public class JwtExceptionHandler {
    
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpiredJwt(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(JwtErrorCode.TOKEN_EXPIRED));
    }
    
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleMalformedJwt(MalformedJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(JwtErrorCode.TOKEN_MALFORMED));
    }
}
```

### 8.4 性能优化建议

#### 缓存用户信息
```java
@Service
public class UserCacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_CACHE_KEY = "user:info:";
    private static final Duration CACHE_DURATION = Duration.ofMinutes(30);
    
    public User getUserFromCache(Long userId) {
        String key = USER_CACHE_KEY + userId;
        return (User) redisTemplate.opsForValue().get(key);
    }
    
    public void cacheUser(User user) {
        String key = USER_CACHE_KEY + user.getId();
        redisTemplate.opsForValue().set(key, user, CACHE_DURATION);
    }
}
```

#### Token黑名单
```java
@Service
public class TokenBlacklistService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String BLACKLIST_KEY = "jwt:blacklist:";
    
    public void addToBlacklist(String token, long expiration) {
        String key = BLACKLIST_KEY + DigestUtils.md5Hex(token);
        redisTemplate.opsForValue().set(key, "true", 
                Duration.ofMillis(expiration - System.currentTimeMillis()));
    }
    
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_KEY + DigestUtils.md5Hex(token);
        return redisTemplate.hasKey(key);
    }
}
```

### 8.5 监控和日志

#### JWT监控指标
```java
@Component
public class JwtMetrics {
    
    private final Counter tokenGeneratedCounter = Counter.builder("jwt.token.generated")
            .description("生成的JWT令牌数量")
            .register(Metrics.globalRegistry);
    
    private final Counter tokenValidatedCounter = Counter.builder("jwt.token.validated")
            .description("验证的JWT令牌数量")
            .tag("result", "success")
            .register(Metrics.globalRegistry);
    
    private final Timer tokenValidationTimer = Timer.builder("jwt.token.validation.duration")
            .description("JWT令牌验证耗时")
            .register(Metrics.globalRegistry);
    
    public void recordTokenGenerated() {
        tokenGeneratedCounter.increment();
    }
    
    public void recordTokenValidation(boolean success, Duration duration) {
        tokenValidatedCounter.increment(Tags.of("result", success ? "success" : "failure"));
        tokenValidationTimer.record(duration);
    }
}
```

#### 安全日志
```java
@Component
public class JwtSecurityLogger {
    
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    
    public void logTokenGenerated(String username, String clientIp) {
        securityLogger.info("JWT令牌生成 - 用户: {}, IP: {}, 时间: {}", 
                username, clientIp, LocalDateTime.now());
    }
    
    public void logTokenValidationFailure(String token, String reason, String clientIp) {
        securityLogger.warn("JWT令牌验证失败 - Token: {}, 原因: {}, IP: {}, 时间: {}", 
                maskToken(token), reason, clientIp, LocalDateTime.now());
    }
    
    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "[INVALID_TOKEN]";
        }
        return token.substring(0, 10) + "***" + token.substring(token.length() - 10);
    }
}
```

## 9. 常见问题

### 9.1 Token相关问题

#### Q1: Token过期后如何处理？
**A**: 实现自动刷新机制：
1. 前端拦截401响应
2. 使用refresh token获取新的access token
3. 重试原请求
4. 如果refresh token也过期，跳转登录页

#### Q2: 如何实现单点登录（SSO）？
**A**: 
1. 使用统一的JWT签名密钥
2. 在多个应用间共享用户信息
3. 实现统一的认证中心
4. 配置跨域支持

#### Q3: 如何实现强制下线？
**A**:
1. 维护Token黑名单（Redis）
2. 在验证Token时检查黑名单
3. 管理员可以将特定Token加入黑名单

### 9.2 安全问题

#### Q1: JWT被盗用怎么办？
**A**: 多层防护：
1. 使用HTTPS传输
2. 设置合理的过期时间
3. 实现Token黑名单机制
4. 监控异常登录行为
5. 绑定设备指纹

#### Q2: 如何防止CSRF攻击？
**A**:
1. JWT存储在localStorage而非Cookie
2. 使用自定义请求头传递Token
3. 验证请求来源
4. 实现双Token验证

#### Q3: 密钥泄露如何应对？
**A**:
1. 立即更换密钥
2. 使所有现有Token失效
3. 强制用户重新登录
4. 审计相关访问日志

### 9.3 性能问题

#### Q1: JWT验证性能如何优化？
**A**:
1. 缓存用户信息减少数据库查询
2. 使用异步验证
3. 合理设置Token过期时间
4. 使用更高效的加密算法

#### Q2: 大量并发时如何处理？
**A**:
1. 使用连接池
2. 实现限流机制
3. 缓存热点数据
4. 使用负载均衡

### 9.4 部署问题

#### Q1: 多实例部署注意事项？
**A**:
1. 使用相同的JWT密钥
2. 共享Redis缓存
3. 同步时钟
4. 统一配置管理

#### Q2: 容器化部署配置？
**A**:
```dockerfile
# Dockerfile
FROM openjdk:8-jre-alpine

# 设置JWT环境变量
ENV JWT_SECRET=${JWT_SECRET}
ENV JWT_ACCESS_EXPIRATION=${JWT_ACCESS_EXPIRATION}

COPY target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    image: myapp:latest
    environment:
      - JWT_SECRET=your-production-secret-key
      - JWT_ACCESS_EXPIRATION=7200000
      - JWT_REFRESH_EXPIRATION=604800000
    ports:
      - "8080:8080"
```

## 10. 总结

### 10.1 实现要点回顾

✅ **双Token机制**：访问令牌+刷新令牌，平衡安全性和用户体验
✅ **自动拦截验证**：使用Spring MVC拦截器自动验证所有API请求
✅ **灵活配置**：支持多环境配置，密钥和过期时间可配置
✅ **异常处理**：完善的异常处理机制，统一错误响应格式
✅ **安全最佳实践**：HTTPS传输、合理过期时间、黑名单机制

### 10.2 架构优势

- **无状态设计**：服务器不需要存储会话，便于水平扩展
- **高性能**：减少数据库查询，提升系统响应速度
- **安全可控**：多层安全防护，支持细粒度权限控制
- **易于维护**：模块化设计，配置集中管理
- **扩展性强**：支持微服务架构，便于系统拆分

### 10.3 下一步优化方向

1. **引入OAuth2**：支持第三方登录
2. **实现RBAC**：基于角色的权限控制
3. **添加限流**：防止暴力破解
4. **集成监控**：完善监控和告警
5. **多租户支持**：支持SaaS架构


通过本文的详细介绍，相信您已经掌握了在Spring Boot中实现JWT认证的完整方案。这套实现不仅满足了基本的认证需求，还考虑了安全性、性能、可维护性等多个方面。在实际项目中，您可以根据具体需求进行调整和优化。

**关键提醒**：
- 生产环境务必使用HTTPS
- 定期更换JWT密钥
- 监控异常登录行为
- 保持依赖库版本更新

希望这篇指南对您的项目开发有所帮助！