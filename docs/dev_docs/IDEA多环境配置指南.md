# IntelliJ IDEA 多环境配置指南

> 本文档详细说明如何在 IntelliJ IDEA 中配置和切换 Spring Boot 项目的不同环境（dev、test、prod）。

## 📋 目录

- [方法1：Run Configuration 配置（推荐）](#方法1run-configuration-配置推荐)
- [方法2：Maven Profile 配置](#方法2maven-profile-配置)
- [方法3：环境变量配置](#方法3环境变量配置)
- [方法4：创建多个运行配置](#方法4创建多个运行配置)
- [验证环境配置](#验证环境配置)
- [常见问题解决](#常见问题解决)

## 方法1：Run Configuration 配置（推荐）

### 步骤1：打开运行配置
1. 点击 IDEA 右上角的运行配置下拉菜单（绿色三角形旁边）
2. 选择 **"Edit Configurations..."**

![Run Configuration](https://img.shields.io/badge/IDEA-Edit%20Configurations-blue)

### 步骤2：配置环境参数

在打开的 "Run/Debug Configurations" 窗口中：

#### 方式A：使用 VM options（推荐）
1. 找到 **"VM options"** 输入框
2. 输入：
```
-Dspring.profiles.active=test
```

#### 方式B：使用 Program arguments
1. 找到 **"Program arguments"** 输入框
2. 输入：
```
--spring.profiles.active=test
```

### 步骤3：应用配置
1. 点击 **"Apply"**
2. 点击 **"OK"**
3. 运行应用

### 不同环境的配置

| 环境 | VM options | 说明 |
|-----|-----------|------|
| 开发环境 | `-Dspring.profiles.active=dev` | 默认环境，本地开发 |
| 测试环境 | `-Dspring.profiles.active=test` | 测试环境，独立数据库 |
| 生产环境 | `-Dspring.profiles.active=prod` | 生产环境，使用环境变量 |

## 方法2：Maven Profile 配置

### 步骤1：打开 Maven 面板
1. 点击 IDEA 右侧边栏的 **"Maven"** 面板
2. 如果没有看到，可以通过 `View` → `Tool Windows` → `Maven` 打开

### 步骤2：激活 Profile
1. 在 Maven 面板中展开你的项目
2. 找到 **"Profiles"** 部分
3. 勾选你想要的环境：
   - ☑️ `test`（勾选测试环境）
   - ☐ `dev`（取消开发环境）
   - ☐ `prod`（取消生产环境）

### 步骤3：刷新项目
1. 点击 Maven 面板左上角的 **刷新按钮** 🔄
2. 或者右键项目选择 **"Reload Maven Projects"**

### 步骤4：运行应用
使用 Maven 运行：
```bash
mvn spring-boot:run
```

## 方法3：环境变量配置

### 步骤1：打开运行配置
1. 打开 **"Edit Configurations..."**

### 步骤2：设置环境变量
1. 找到 **"Environment variables"** 部分
2. 点击右侧的文件夹图标 📁
3. 添加新的环境变量：
   - **Name**: `SPRING_PROFILES_ACTIVE`
   - **Value**: `test`

### 步骤3：保存并运行
1. 点击 **"OK"** 保存配置
2. 运行应用

## 方法4：创建多个运行配置

### 步骤1：复制现有配置
1. 在 "Run/Debug Configurations" 窗口中
2. 右键现有的 Spring Boot 配置
3. 选择 **"Copy Configuration"**

### 步骤2：创建环境特定配置
创建三个不同的配置：

#### 开发环境配置
- **Name**: `SurveyMaster - Dev`
- **VM options**: `-Dspring.profiles.active=dev`

#### 测试环境配置
- **Name**: `SurveyMaster - Test`
- **VM options**: `-Dspring.profiles.active=test`

#### 生产环境配置
- **Name**: `SurveyMaster - Prod`
- **VM options**: `-Dspring.profiles.active=prod`

### 步骤3：快速切换环境
现在你可以在运行配置下拉菜单中快速选择不同的环境配置。

## 验证环境配置

### 方法1：查看启动日志
启动应用后，在控制台查看启动日志：
```
2024-01-01 10:00:00.000 INFO  --- [main] o.s.b.SpringApplication: The following profiles are active: test
```

### 方法2：访问环境信息接口
我已经在 TestController 中添加了环境信息接口，启动应用后访问：

```
GET http://localhost:8081/api/test/environment
```

**测试环境响应示例**：
```json
{
  "code": 200,
  "message": "当前环境信息",
  "data": {
    "activeProfiles": ["test"],
    "serverPort": "8081",
    "databaseUrl": "jdbc:mysql://localhost:23306/survey_master_test?...",
    "redisHost": "localhost",
    "redisDatabase": "1",
    "mongoDatabase": "surveydb_test"
  }
}
```

### 方法3：检查配置属性
在代码中注入 Environment 来检查：

```java
@Autowired
private Environment environment;

@GetMapping("/check-env")
public String checkEnvironment() {
    String[] profiles = environment.getActiveProfiles();
    return "当前激活的环境: " + Arrays.toString(profiles);
}
```

## 环境特定配置对比

| 配置项 | 开发环境(dev) | 测试环境(test) | 生产环境(prod) |
|-------|--------------|---------------|---------------|
| 端口 | 8080 | 8081 | 8080 |
| 数据库 | survey_master | survey_master_test | 环境变量配置 |
| Redis DB | 0 | 1 | 0 |
| MongoDB | surveydb | surveydb_test | 环境变量配置 |
| 日志级别 | DEBUG | DEBUG | INFO |
| 监控端点 | 全部暴露 | 部分暴露 | 最小暴露 |

## 常见问题解决

### 问题1：配置不生效
**现象**：修改了 Profile 但配置没有生效

**解决方案**：
1. 检查 VM options 或 Program arguments 是否正确设置
2. 重新启动应用（不要使用热重载）
3. 清除 IDEA 缓存：`File` → `Invalidate Caches and Restart`

### 问题2：Maven Profile 冲突
**现象**：同时勾选了多个 Maven Profile

**解决方案**：
1. 在 Maven 面板中只勾选一个 Profile
2. 取消其他 Profile 的勾选
3. 刷新 Maven 项目

### 问题3：环境变量覆盖
**现象**：系统环境变量覆盖了 IDEA 配置

**解决方案**：
1. 检查系统环境变量 `SPRING_PROFILES_ACTIVE`
2. 在终端运行 `echo $SPRING_PROFILES_ACTIVE`
3. 如果有值，需要在 IDEA 中明确设置覆盖

### 问题4：端口冲突
**现象**：启动时提示端口被占用

**解决方案**：
1. 检查配置文件中的端口设置
2. 测试环境使用 8081 端口
3. 停止其他占用端口的应用

## 最佳实践

### 1. 推荐配置方式
- **日常开发**：使用方法1（Run Configuration）
- **团队协作**：使用方法2（Maven Profile）
- **CI/CD**：使用环境变量

### 2. 配置命名规范
```
项目名称 - 环境名称
例如：SurveyMaster - Dev
     SurveyMaster - Test
     SurveyMaster - Prod
```

### 3. 快捷键配置
可以为不同环境配置快捷键：
1. `File` → `Settings` → `Keymap`
2. 搜索 "Run Configuration"
3. 为每个配置设置快捷键

### 4. 团队共享配置
将运行配置保存到项目中：
1. 在 `.idea/runConfigurations/` 目录下
2. 提交到版本控制
3. 团队成员可以直接使用

## 调试不同环境

### Debug 模式启动
1. 选择对应的环境配置
2. 点击绿色虫子图标 🐛（Debug）
3. 在代码中设置断点进行调试

### 远程调试
对于生产环境，可以配置远程调试：
```
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```

## 总结

使用 IntelliJ IDEA 配置多环境的最佳实践：

1. **开发阶段**：使用 Run Configuration 快速切换
2. **构建部署**：使用 Maven Profile 控制打包
3. **生产运行**：使用环境变量保证安全性
4. **团队协作**：共享运行配置，统一开发环境

通过以上配置，你可以轻松在 IDEA 中切换和管理不同的运行环境！