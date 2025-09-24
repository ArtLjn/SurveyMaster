# TypeException 修复指南

## 问题描述

在调用更新问卷状态接口时，出现以下错误：

```
nested exception is org.apache.ibatis.type.TypeException: 
Could not set parameters for mapping: ParameterMapping{property='id', mode=IN, javaType=int, jdbcType=null, numericScale=null, resultMapId='null', jdbcTypeName='null', expression='null'}. 
Cause: org.apache.ibatis.type.TypeException: Error setting non null for parameter #2 with JdbcType null . 
Try setting a different JdbcType for this parameter or a different configuration property. 
Cause: java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.Integer
```

## 问题根因分析

### 1. 类型不匹配链条

```
JWT拦截器 → Long类型 → DTO int字段 → MyBatis Long参数 → 类型转换异常
```

### 2. 具体问题点

| 组件 | 期望类型 | 实际类型 | 问题描述 |
|------|----------|----------|----------|
| **JWT拦截器** | - | `Long` | 返回Long类型的userId |
| **UpdateSurveyStatus DTO** | `int` | `Long` | 字段类型为int，但接收Long值 |
| **MyBatis Mapper** | `long` | `int` | 映射参数期望long，但传入int |
| **数据库表** | `BIGINT` | - | 数据库字段为BIGINT类型 |

### 3. 错误的类型转换

```java
// 问题代码
updateSurveyStatus.setUserId(currentUserId.intValue()); // Long -> int -> long (有损转换)
```

## 解决方案

### 1. 统一类型架构

采用端到端的`Long`类型统一：

```
JWT(Long) → DTO(Long) → Service(Long) → Mapper(long) → Database(BIGINT)
```

### 2. 具体修复内容

#### A. 更新 UpdateSurveyStatus DTO

**修复前：**
```java
@Data
public class UpdateSurveyStatus {
    private int id;        // 问题：使用int类型
    private int status;
    private int userId;    // 问题：使用int类型
}
```

**修复后：**
```java
@Data
public class UpdateSurveyStatus {
    private Long id;       // ✅ 修复：使用Long类型
    private int status;
    private Long userId;   // ✅ 修复：使用Long类型
}
```

#### B. 更新 Survey 实体类

**修复前：**
```java
@Data
public class Survey {
    private int id;        // 问题：与数据库BIGINT不匹配
    private int userId;    // 问题：与数据库BIGINT不匹配
    // ...
}
```

**修复后：**
```java
@Data
public class Survey {
    private Long id;       // ✅ 修复：匹配数据库BIGINT
    private Long userId;   // ✅ 修复：匹配数据库BIGINT
    // ...
}
```

#### C. 更新 CreateSurvey DTO

**修复前：**
```java
@Data
public class CreateSurvey {
    private int userId;    // 问题：类型不一致
    // ...
}
```

**修复后：**
```java
@Data
public class CreateSurvey {
    private Long userId;   // ✅ 修复：保持类型一致
    // ...
}
```

#### D. 更新 Controller 逻辑

**修复前：**
```java
// 问题：有损类型转换
updateSurveyStatus.setUserId(currentUserId.intValue());
createSurvey.setUserId(currentUserId.intValue());
```

**修复后：**
```java
// ✅ 修复：直接使用Long类型，无需转换
updateSurveyStatus.setUserId(currentUserId);
createSurvey.setUserId(currentUserId);
```

#### E. 更新 Service 层验证逻辑

**修复前：**
```java
// 问题：类型转换比较
if (existingSurvey.getUserId() != updateSurveyStatus.getUserId().intValue()) {
    throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此问卷");
}
```

**修复后：**
```java
// ✅ 修复：使用equals方法比较Long对象
if (!existingSurvey.getUserId().equals(updateSurveyStatus.getUserId())) {
    throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此问卷");
}
```

### 3. 增强的验证逻辑

添加了完善的参数验证：

```java
@Override
public void ChangeSurveyStatus(UpdateSurveyStatus updateSurveyStatus) {
    // 参数验证
    if (updateSurveyStatus.getId() == null || updateSurveyStatus.getId() <= 0) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "问卷ID不能为空");
    }
    
    if (updateSurveyStatus.getUserId() == null || updateSurveyStatus.getUserId() <= 0) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "用户ID不能为空");
    }
    
    // 状态验证（0=草稿, 1=发布, 2=关闭）
    if (updateSurveyStatus.getStatus() < 0 || updateSurveyStatus.getStatus() > 2) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "问卷状态无效");
    }
    
    // 检查问卷是否存在且属于当前用户
    Survey existingSurvey = surveyMapper.selectById(updateSurveyStatus.getId());
    if (existingSurvey == null) {
        throw new BusinessException(ErrorCode.NOT_FOUND, "问卷不存在");
    }
    
    if (!existingSurvey.getUserId().equals(updateSurveyStatus.getUserId())) {
        throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此问卷");
    }
    
    // 更新问卷状态
    int result = surveyMapper.updateStatus(
            updateSurveyStatus.getId(),
            updateSurveyStatus.getStatus(),
            updateSurveyStatus.getUserId()
    );
    
    if (result <= 0) {
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "更新问卷状态失败");
    }
}
```

## 测试验证

### 1. API 测试

**更新问卷状态测试：**
```bash
# 1. 先登录获取Token
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}'

# 2. 使用Token更新问卷状态
curl -X PUT http://localhost:8080/api/survey/updateSurvey \
  -H "Authorization: Bearer <your_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "status": 1
  }'

# 3. 快速测试接口
curl -X PUT "http://localhost:8080/api/survey/test/update-status?surveyId=1&status=1" \
  -H "Authorization: Bearer <your_token>"
```

### 2. 预期响应

**成功响应：**
```json
{
  "success": true,
  "message": "问卷状态更新成功",
  "data": null
}
```

**验证失败响应：**
```json
{
  "success": false,
  "code": "FORBIDDEN",
  "message": "无权操作此问卷",
  "data": null
}
```

## 类型设计原则

### 1. 统一性原则

- **数据库 BIGINT** ↔ **Java Long** ↔ **JSON Number**
- 避免在传输链路中进行类型转换

### 2. 安全性原则

- 避免有损类型转换（Long → int）
- 使用对象比较而非基本类型比较
- 添加空值检查和边界验证

### 3. 一致性原则

- 所有ID类型统一使用Long
- 所有DTO、Entity、Mapper参数类型保持一致
- 错误处理和验证逻辑统一

## 最佳实践建议

### 1. 设计阶段

- 数据库设计时就确定ID类型（推荐BIGINT）
- 实体类设计时与数据库类型保持一致
- DTO设计时考虑端到端的类型一致性

### 2. 开发阶段

- 避免不必要的类型转换
- 使用对象的equals方法比较，而非==
- 添加充分的参数验证和边界检查

### 3. 测试阶段

- 测试边界值（null、0、负数）
- 测试权限验证逻辑
- 测试不同用户操作他人数据的场景

### 4. 维护阶段

- 保持类型的一致性
- 及时更新相关联的类型
- 定期review类型转换相关的代码

## 总结

这次TypeException的修复涉及了整个数据流的类型统一：

1. **根本原因**：Java类型与数据库类型不匹配，导致MyBatis类型转换失败
2. **解决方案**：端到端使用Long类型，避免类型转换
3. **改进措施**：增加参数验证、权限检查、错误处理
4. **预防措施**：建立类型设计规范，确保一致性

修复后的代码不仅解决了TypeException问题，还提升了代码的健壮性和安全性。