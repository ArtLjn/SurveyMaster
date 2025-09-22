# Spring Boot 多环境配置完整操作指南

> 本文档详细记录了如何在 Spring Boot 项目中实现多环境配置的完整过程，包含开发、测试、生产环境的配置分离。适用于任何 Spring Boot 项目的多环境配置需求。

## 📋 目录

- [1. 概述](#1-概述)
- [2. 配置文件结构设计](#2-配置文件结构设计)
- [3. 操作步骤详解](#3-操作步骤详解)
- [4. Maven Profiles 配置](#4-maven-profiles-配置)
- [5. 配置文件内容详解](#5-配置文件内容详解)
- [6. 使用方法](#6-使用方法)
- [7. 最佳实践](#7-最佳实践)
- [8. 常见问题解决](#8-常见问题解决)
- [9. 项目模板](#9-项目模板)

## 1. 概述

### 1.1 为什么需要多环境配置

- **环境隔离**：开发、测试、生产环境使用不同的数据库和服务
- **安全性**：生产环境敏感信息不暴露在代码中
- **灵活性**：不同环境可以有不同的性能参数
- **团队协作**：统一的配置管理，避免环境差异导致的问题

### 1.2 实现目标

- ✅ 一套代码支持多个环境
- ✅ 敏感信息通过环境变量管理
- ✅ 配置文件结构清晰，易于维护
- ✅ 支持 Maven/IDE 多种启动方式
- ✅ 生产环境安全性保障

## 2. 配置文件结构设计

### 2.1 目标文件结构

```
src/main/resources/
├── application.yml              # 主配置文件（通用配置）
├── application-dev.yml          # 开发环境配置
├── application-test.yml         # 测试环境配置
├── application-prod.yml         # 生产环境配置
└── logback-spring.xml          # 日志配置（可选）
```

### 2.2 配置文件职责分工

| 配置文件 | 职责 | 内容特点 |
|---------|-----|---------|
| `application.yml` | 通用配置 | MyBatis、应用名称等环境无关配置 |
| `application-dev.yml` | 开发环境 | 本地服务、详细日志、完整监控 |
| `application-test.yml` | 测试环境 | 测试数据库、适度日志、独立配置 |
| `application-prod.yml` | 生产环境 | 环境变量、严格日志、安全配置 |

## 3. 操作步骤详解

### 步骤1：修改主配置文件

将现有的 `application.yml` 改造为主配置文件，只保留通用配置：

```yaml
# 主配置文件 - 包含所有环境的通用配置
spring:
  profiles:
    active: @profiles.active@  # 使用 Maven 的 profile 占位符，默认值在 pom.xml 中定义
  
  # 应用基本信息
  application:
    name: survey-master

# MyBatis 配置 - 所有环境通用
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: org.practice.surveymaster.model
  configuration:
    map-underscore-to-camel-case: true

# 默认日志配置 - 可被各环境覆盖
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%logger{50}] - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%logger{50}] - %msg%n"
```

### 步骤2：创建开发环境配置

创建 `application-dev.yml`：

```yaml
# 开发环境配置文件
server:
  port: 8080

spring:
  # 数据库配置 - 开发环境
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:23306/survey_master?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  
  # MongoDB 配置 - 开发环境
  data:
    mongodb:
      username: admin
      password: admin123
      host: localhost
      port: 27017
      database: surveydb
  
  # Redis 配置 - 开发环境
  redis:
    host: localhost
    port: 26379
    password: 123456
    database: 0
    lettuce:
      pool:
        max-active: 8     # 开发环境连接数较少
        max-idle: 4
        min-idle: 1
        max-wait: 5000ms
  
  # RabbitMQ 配置 - 开发环境
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: 123456
    virtual-host: /

# 开发环境日志配置 - 更详细的日志输出
logging:
  level:
    root: INFO
    org.practice.surveymaster: DEBUG
    org.mybatis: DEBUG
    java.sql: DEBUG
    org.springframework.web: DEBUG
  file:
    name: logs/dev/survey-master.log
    max-size: 100MB
    max-history: 7

# 开发环境特定配置
management:
  endpoints:
    web:
      exposure:
        include: "*"  # 开发环境暴露所有监控端点
  endpoint:
    health:
      show-details: always
```

### 步骤3：创建测试环境配置

创建 `application-test.yml`：

```yaml
# 测试环境配置文件
server:
  port: 8081

spring:
  # 数据库配置 - 测试环境（通常使用内存数据库或测试专用数据库）
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:23306/survey_master_test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  
  # MongoDB 配置 - 测试环境
  data:
    mongodb:
      username: admin
      password: admin123
      host: localhost
      port: 27017
      database: surveydb_test
  
  # Redis 配置 - 测试环境
  redis:
    host: localhost
    port: 26379
    password: 123456
    database: 1  # 使用不同的数据库避免与开发环境冲突
    lettuce:
      pool:
        max-active: 4     # 测试环境资源限制
        max-idle: 2
        min-idle: 1
        max-wait: 3000ms
  
  # RabbitMQ 配置 - 测试环境
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: 123456
    virtual-host: /test

# 测试环境日志配置 - 详细但不保存太久
logging:
  level:
    root: INFO
    org.practice.surveymaster: DEBUG
    org.mybatis: DEBUG
    java.sql: DEBUG
  file:
    name: logs/test/survey-master.log
    max-size: 50MB
    max-history: 3

# 测试环境监控配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics  # 测试环境适度暴露端点
  endpoint:
    health:
      show-details: always
```

### 步骤4：创建生产环境配置

创建 `application-prod.yml`：

```yaml
# 生产环境配置文件
server:
  port: 8080
  tomcat:
     threads:
        max: 200
        min-spare: 10
     accept-count: 100
     max-connections: 8192
spring:
  # 数据库配置 - 生产环境
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:prod-mysql-server}:${DB_PORT:3306}/${DB_NAME:survey_master}?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:survey_user}
    password: ${DB_PASSWORD:your_prod_password}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  # MongoDB 配置 - 生产环境
  data:
    mongodb:
      username: ${MONGO_USERNAME:survey_user}
      password: ${MONGO_PASSWORD:your_mongo_password}
      host: ${MONGO_HOST:prod-mongo-server}
      port: ${MONGO_PORT:27017}
      database: ${MONGO_DATABASE:surveydb_prod}
      authentication-database: admin
  
  # Redis 配置 - 生产环境
  redis:
    host: ${REDIS_HOST:prod-redis-server}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:your_redis_password}
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 50    # 生产环境更多连接
        max-idle: 20
        min-idle: 5
        max-wait: 3000ms
  
  # RabbitMQ 配置 - 生产环境
  rabbitmq:
    host: ${RABBITMQ_HOST:prod-rabbitmq-server}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:survey_user}
    password: ${RABBITMQ_PASSWORD:your_rabbitmq_password}
    virtual-host: ${RABBITMQ_VHOST:/survey}

# 生产环境日志配置 - 更严格的日志级别
logging:
  level:
    root: WARN
    org.practice.surveymaster: INFO
    org.springframework.web: WARN
  file:
    name: /var/log/survey-master/survey-master.log
    max-size: 200MB
    max-history: 30

# 生产环境监控配置 - 更安全的端点暴露
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus  # 只暴露必要的监控端点
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  security:
    enabled: true

```

### 步骤5：配置 Maven Profiles

修改 `pom.xml`，添加 Maven Profiles 支持：

```xml
<!-- 在 properties 标签中添加 -->
<properties>
    <java.version>1.8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <spring-boot.version>2.5.6</spring-boot.version>
    <!-- 默认激活开发环境 -->
    <profiles.active>dev</profiles.active>
</properties>

<!-- Maven Profiles 配置 -->
<profiles>
    <!-- 开发环境 -->
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <profiles.active>dev</profiles.active>
        </properties>
    </profile>
    
    <!-- 测试环境 -->
    <profile>
        <id>test</id>
        <properties>
            <profiles.active>test</profiles.active>
        </properties>
    </profile>
    
    <!-- 生产环境 -->
    <profile>
        <id>prod</id>
        <properties>
            <profiles.active>prod</profiles.active>
        </properties>
    </profile>
</profiles>
```

### 步骤6：配置资源文件过滤

在 `pom.xml` 的 `<build>` 标签中添加资源过滤配置：

```xml
<build>
    <!-- 资源文件过滤配置 -->
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
            <includes>
                <include>**/*.yml</include>
                <include>**/*.yaml</include>
                <include>**/*.properties</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>false</filtering>
            <excludes>
                <exclude>**/*.yml</exclude>
                <exclude>**/*.yaml</exclude>
                <exclude>**/*.properties</exclude>
            </excludes>
        </resource>
    </resources>
    
    <!-- ... 其他插件配置 ... -->
</build>
```

## 4. Maven Profiles 配置

### 4.1 完整的 profiles 配置

```xml
<profiles>
    <!-- 开发环境 -->
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <profiles.active>dev</profiles.active>
            <maven.test.skip>false</maven.test.skip>
        </properties>
    </profile>
    
    <!-- 测试环境 -->
    <profile>
        <id>test</id>
        <properties>
            <profiles.active>test</profiles.active>
            <maven.test.skip>false</maven.test.skip>
        </properties>
    </profile>
    
    <!-- 生产环境 -->
    <profile>
        <id>prod</id>
        <properties>
            <profiles.active>prod</profiles.active>
            <maven.test.skip>true</maven.test.skip>
        </properties>
    </profile>
</profiles>
```

### 4.2 Profile 激活方式

1. **默认激活**：`<activeByDefault>true</activeByDefault>`
2. **命令行激活**：`mvn -Ptest`
3. **属性激活**：根据系统属性激活
4. **JDK版本激活**：根据JDK版本激活

## 5. 配置文件内容详解

### 5.1 环境变量使用方式

生产环境配置中的环境变量语法：

```yaml
# 语法：${环境变量名:默认值}
database:
  url: ${DB_URL:jdbc:mysql://localhost:3306/test}
  username: ${DB_USERNAME:root}
  password: ${DB_PASSWORD:}
```

### 5.2 配置优先级

Spring Boot 配置加载优先级（从高到低）：

1. 命令行参数
2. 操作系统环境变量
3. `application-{profile}.yml`
4. `application.yml`
5. 默认值

### 5.3 配置分层策略

```yaml
# application.yml - 通用配置
spring:
  application:
    name: my-app
mybatis:
  configuration:
    map-underscore-to-camel-case: true

---
# application-dev.yml - 开发环境特有
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dev_db
logging:
  level:
    com.example: DEBUG

---
# application-prod.yml - 生产环境特有
spring:
  datasource:
    url: ${DB_URL}
logging:
  level:
    com.example: WARN
```

## 6. 使用方法

### 6.1 Maven 命令行使用

```bash
# 开发环境运行（默认）
mvn spring-boot:run

# 显式指定开发环境
mvn spring-boot:run -Pdev

# 测试环境运行
mvn spring-boot:run -Ptest

# 生产环境运行
mvn spring-boot:run -Pprod

# 打包不同环境
mvn clean package -Pdev
mvn clean package -Ptest
mvn clean package -Pprod
```

### 6.2 JAR 包运行时指定环境

```bash
# 方式1：使用 JVM 参数
java -jar -Dspring.profiles.active=prod target/myapp.jar

# 方式2：使用 Spring Boot 参数
java -jar target/myapp.jar --spring.profiles.active=prod

# 方式3：使用环境变量
export SPRING_PROFILES_ACTIVE=prod
java -jar target/myapp.jar
```

### 6.3 IDE 中配置

#### IntelliJ IDEA 配置

1. **Run Configuration 配置**：
   - VM options: `-Dspring.profiles.active=dev`
   - Program arguments: `--spring.profiles.active=dev`

2. **Maven Projects 面板**：
   - 勾选对应的 Profile（dev/test/prod）

#### Eclipse 配置

1. **Run Configuration**：
   - Arguments 标签页
   - VM arguments: `-Dspring.profiles.active=dev`

### 6.4 Docker 部署配置

```dockerfile
# Dockerfile
FROM openjdk:8-jre-alpine

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=prod
ENV DB_HOST=mysql-server
ENV DB_USERNAME=app_user
ENV DB_PASSWORD=secure_password

COPY target/myapp.jar app.jar

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
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - DB_USERNAME=app_user
      - DB_PASSWORD=secure_password
    ports:
      - "8080:8080"
    depends_on:
      - mysql
  
  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=root_password
      - MYSQL_DATABASE=survey_master
      - MYSQL_USER=app_user
      - MYSQL_PASSWORD=secure_password
```

## 7. 最佳实践

### 7.1 配置管理原则

1. **敏感信息处理**：
   - ✅ 开发/测试环境：配置文件中直接配置
   - ✅ 生产环境：使用环境变量
   - ❌ 避免：敏感信息提交到版本控制

2. **配置分层原则**：
   - ✅ 通用配置：`application.yml`
   - ✅ 环境特定：`application-{profile}.yml`
   - ✅ 本地覆盖：`application-local.yml`（不提交）

3. **默认值策略**：
   - ✅ 提供合理的默认值
   - ✅ 环境变量语法：`${VAR_NAME:default_value}`
   - ✅ 关键配置必须显式设置

### 7.2 安全配置建议

```yaml
# 生产环境安全配置示例
spring:
  datasource:
    # 使用连接池
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
    # 数据库连接使用SSL
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=true&requireSSL=true

# 限制监控端点暴露
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized

# 生产环境日志配置
logging:
  level:
    root: WARN
    com.yourapp: INFO
  file:
    name: /var/log/app/app.log
    max-size: 200MB
    max-history: 30
```

### 7.3 配置验证

```java
@Component
@ConfigurationProperties(prefix = "app.config")
@Validated
public class AppConfig {
    
    @NotBlank
    private String databaseUrl;
    
    @Min(1)
    @Max(100)
    private int maxConnections;
    
    @Email
    private String adminEmail;
    
    // getters and setters
}
```

### 7.4 配置监控

```java
@RestController
@RequestMapping("/admin")
public class ConfigController {
    
    @Autowired
    private Environment environment;
    
    @GetMapping("/profile")
    public Map<String, Object> getCurrentProfile() {
        Map<String, Object> result = new HashMap<>();
        result.put("activeProfiles", environment.getActiveProfiles());
        result.put("defaultProfiles", environment.getDefaultProfiles());
        return result;
    }
}
```

## 8. 常见问题解决

### 8.1 配置不生效问题

**问题**：配置文件修改后不生效

**解决方案**：
1. 检查 Profile 是否正确激活
2. 验证配置文件语法是否正确
3. 确认资源过滤是否配置
4. 清除缓存重新编译

```bash
# 清除缓存重新编译
mvn clean compile
```

### 8.2 环境变量读取问题

**问题**：生产环境环境变量读取失败

**解决方案**：
1. 确认环境变量是否设置
2. 检查变量名是否正确
3. 验证默认值是否合理

```bash
# 检查环境变量
echo $DB_HOST
env | grep DB_
```

### 8.3 Maven Profile 不生效

**问题**：Maven Profile 激活失败

**解决方案**：
1. 检查 `pom.xml` 中 profiles 配置
2. 确认资源过滤配置正确
3. 验证占位符语法

```xml
<!-- 确保资源过滤开启 -->
<resource>
    <directory>src/main/resources</directory>
    <filtering>true</filtering>
</resource>
```

### 8.4 多环境数据库连接问题

**问题**：不同环境数据库连接失败

**解决方案**：
1. 检查数据库连接参数
2. 验证网络连通性
3. 确认数据库用户权限

```yaml
# 添加连接测试
spring:
  datasource:
    test-while-idle: true
    test-on-borrow: true
    validation-query: SELECT 1
```

### 8.5 日志文件路径问题

**问题**：日志文件无法创建

**解决方案**：
1. 确认目录权限
2. 创建日志目录
3. 使用相对路径

```bash
# 创建日志目录
mkdir -p logs/{dev,test,prod}
chmod 755 logs
```

## 9. 项目模板

### 9.1 快速开始模板

对于新项目，可以直接复制以下配置结构：

```
src/main/resources/
├── application.yml              # 复制通用配置模板
├── application-dev.yml          # 复制开发环境模板
├── application-test.yml         # 复制测试环境模板
├── application-prod.yml         # 复制生产环境模板
└── application-local.yml        # 本地开发配置（可选，不提交）
```

### 9.2 pom.xml 配置模板

```xml
<properties>
    <profiles.active>dev</profiles.active>
</properties>

<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <profiles.active>dev</profiles.active>
        </properties>
    </profile>
    <profile>
        <id>test</id>
        <properties>
            <profiles.active>test</profiles.active>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <profiles.active>prod</profiles.active>
        </properties>
    </profile>
</profiles>

<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
            <includes>
                <include>**/*.yml</include>
                <include>**/*.yaml</include>
                <include>**/*.properties</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>false</filtering>
            <excludes>
                <exclude>**/*.yml</exclude>
                <exclude>**/*.yaml</exclude>
                <exclude>**/*.properties</exclude>
            </excludes>
        </resource>
    </resources>
</build>
```

### 9.3 生产环境部署脚本

```bash
#!/bin/bash
# deploy.sh - 生产环境部署脚本

# 设置环境变量
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=prod-mysql-server
export DB_USERNAME=app_user
export DB_PASSWORD=secure_password
export REDIS_HOST=prod-redis-server
export REDIS_PASSWORD=redis_password

# 打包应用
mvn clean package -Pprod -DskipTests

# 停止旧应用
pkill -f "java.*myapp.*jar"

# 启动新应用
nohup java -jar target/myapp.jar > /var/log/app/app.out 2>&1 &

echo "应用已启动，Profile: $SPRING_PROFILES_ACTIVE"
```

## 总结

通过以上完整的操作指南，你可以在任何 Spring Boot 项目中