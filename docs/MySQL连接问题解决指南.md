# MySQL 连接问题解决指南

## 问题描述

在使用 Spring Boot 连接 MySQL 8.0+ 时，可能会遇到以下错误：

```
Public Key Retrieval is not allowed
```

## 错误原因

这是 MySQL 8.0+ 引入的安全特性，默认情况下不允许客户端检索服务器的公钥，以防止恶意代理拦截。

## 解决方案

### 方案1：添加 allowPublicKeyRetrieval=true 参数（推荐）

在 JDBC URL 中添加 `allowPublicKeyRetrieval=true` 参数：

```yaml
spring:
  datasource:
    url: jdbc:mysql://host:port/database?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
```

### 方案2：使用 SSL 连接（生产环境推荐）

```yaml
spring:
  datasource:
    # 启用 SSL 连接
    url: jdbc:mysql://host:port/database?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=Asia/Shanghai
```

### 方案3：服务器端配置（DBA 操作）

在 MySQL 服务器配置文件中设置：

```ini
[mysqld]
default_authentication_plugin=mysql_native_password
```

## 不同环境的配置

### 开发环境配置
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: password
```

### 测试环境配置
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:mysql://test-server:3306/test_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: test_user
    password: test_password
```

### 生产环境配置（使用SSL）
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

## 安全注意事项

### 1. allowPublicKeyRetrieval=true 的安全风险

- **风险**：允许客户端检索服务器公钥，可能被恶意代理利用
- **建议**：仅在开发和测试环境使用，生产环境建议使用 SSL

### 2. 生产环境最佳实践

```yaml
spring:
  datasource:
    # 使用 SSL 连接
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=true&serverTimezone=Asia/Shanghai&requireSSL=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    # 连接池配置
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 3. 环境变量配置

```bash
# 生产环境环境变量
export DB_HOST=prod-mysql-server
export DB_PORT=3306
export DB_NAME=survey_master
export DB_USERNAME=app_user
export DB_PASSWORD=secure_password
```

## 其他常见 MySQL 连接问题

### 1. 时区问题
```
The server time zone value 'CST' is unrecognized
```

**解决方案**：添加 `serverTimezone=Asia/Shanghai` 参数

### 2. SSL 连接问题
```
Communications link failure
```

**解决方案**：
- 开发环境：使用 `useSSL=false`
- 生产环境：正确配置 SSL 证书

### 3. 字符编码问题
```
Incorrect string value
```

**解决方案**：添加 `useUnicode=true&characterEncoding=utf-8` 参数

## 完整的 JDBC URL 参数说明

```
jdbc:mysql://host:port/database?
  useUnicode=true                    # 启用 Unicode 支持
  &characterEncoding=utf-8           # 设置字符编码
  &useSSL=false                      # 开发环境关闭 SSL（生产环境建议开启）
  &serverTimezone=Asia/Shanghai      # 设置服务器时区
  &allowPublicKeyRetrieval=true      # 允许检索公钥（开发环境）
  &useAffectedRows=true              # 返回匹配的行数而不是更改的行数
  &allowMultiQueries=true            # 允许多语句查询
  &rewriteBatchedStatements=true     # 批量操作优化
```

## 测试连接

创建一个简单的测试接口来验证数据库连接：

```java
@RestController
@RequestMapping("/test")
public class DatabaseTestController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/db-connection")
    public Map<String, Object> testDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            result.put("status", "success");
            result.put("url", connection.getMetaData().getURL());
            result.put("username", connection.getMetaData().getUserName());
            result.put("database", connection.getCatalog());
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }
}
```

## 问题排查步骤

1. **检查 MySQL 版本**：
   ```sql
   SELECT VERSION();
   ```

2. **检查用户权限**：
   ```sql
   SHOW GRANTS FOR 'username'@'host';
   ```

3. **检查网络连通性**：
   ```bash
   telnet mysql-host 3306
   ```

4. **查看 MySQL 错误日志**：
   ```bash
   tail -f /var/log/mysql/error.log
   ```

5. **测试命令行连接**：
   ```bash
   mysql -h host -P port -u username -p database
   ```

## 总结

- **开发环境**：使用 `allowPublicKeyRetrieval=true` 和 `useSSL=false`
- **测试环境**：使用 `allowPublicKeyRetrieval=true` 和 `useSSL=false`
- **生产环境**：使用 SSL 连接，避免 `allowPublicKeyRetrieval=true`
- **安全第一**：生产环境必须使用安全的连接方式
- **环境变量**：敏感信息通过环境变量管理