# 数据脱敏功能使用指南

## 功能概述

SurveyMaster 系统集成了完善的数据脱敏功能，能够在数据输出、日志记录、JSON 序列化等场景中自动对敏感数据进行脱敏处理，保护用户隐私和数据安全。

## 支持的脱敏类型

### 1. 预定义脱敏类型

| 类型 | 枚举值 | 脱敏规则 | 示例 |
|------|--------|----------|------|
| 手机号 | `MOBILE` | 保留前3位和后4位，中间用*替换 | 138****5678 |
| 姓名 | `NAME` | 保留姓氏，名字用*替换 | 张* |
| 邮箱 | `EMAIL` | 保留前3位和@后的域名，用户名部分用*替换 | zha***@example.com |
| 密码 | `PASSWORD` | 完全用*替换 | ******** |
| 身份证号 | `ID_CARD` | 保留前4位和后4位，中间用*替换 | 1101***********1234 |
| 银行卡号 | `BANK_CARD` | 保留前4位和后4位，中间用*替换 | 6217************9012 |
| 地址 | `ADDRESS` | 保留省市区信息，详细地址用*替换 | 北京市朝阳区**** |
| 车牌号 | `CAR_LICENSE` | 保留前2位和后1位，中间用*替换 | 京A***5 |
| 固定电话 | `FIXED_PHONE` | 保留区号和后4位，中间用*替换 | 010-****5678 |
| 自定义 | `CUSTOM` | 根据注解配置进行脱敏 | 可自定义 |

### 2. 自定义脱敏

通过 `@Sensitive(value = SensitiveType.CUSTOM)` 可以实现自定义脱敏规则：

```java
@Sensitive(value = SensitiveType.CUSTOM, keepStart = 3, keepEnd = 2, maskChar = "#")
private String customField;
```

## 使用方法

### 1. 在实体类中使用

```java
@Data
public class UserInfo {
    @Sensitive(SensitiveType.NAME)
    private String username;
    
    @Sensitive(SensitiveType.MOBILE)
    private String mobile;
    
    @Sensitive(SensitiveType.EMAIL)
    private String email;
    
    @Sensitive(SensitiveType.PASSWORD)
    private String password;
    
    // 自定义脱敏
    @Sensitive(value = SensitiveType.CUSTOM, keepStart = 2, keepEnd = 2, maskChar = "*")
    private String customData;
}
```

### 2. 在 JSON 序列化中使用

系统已经自动配置了 JSON 序列化脱敏，标注了 `@Sensitive` 注解的字段在序列化为 JSON 时会自动脱敏。

### 3. 手动调用脱敏工具

```java
// 通过类型脱敏
String masked = SensitiveUtil.desensitize("13812345678", SensitiveType.MOBILE);

// 自定义脱敏
String masked = SensitiveUtil.desensitizeCustom("1234567890", 2, 2, "*");

// 批量脱敏
String[] data = {"13812345678", "15999999999"};
String[] masked = SensitiveUtil.desensitizeBatch(data, SensitiveType.MOBILE);
```

### 4. AOP 自动脱敏

系统通过 AOP 技术自动拦截 Controller 层的方法返回值并进行脱敏处理。无需额外配置，只要在实体类字段上添加 `@Sensitive` 注解即可。

## 配置选项

### 1. 全局配置

在 `application.yml` 中配置：

```yaml
sensitive:
  enabled: true              # 是否启用数据脱敏功能
  enable-log: true           # 是否在日志中启用脱敏
  enable-json: true          # 是否在 JSON 序列化时启用脱敏
  default-mask-char: "*"     # 默认的脱敏字符
```

### 2. 注解级别配置

```java
@Sensitive(
    value = SensitiveType.CUSTOM,
    keepStart = 2,        // 保留开始位数
    keepEnd = 2,          // 保留结束位数
    maskChar = "#",       // 脱敏字符
    enableLog = true,     // 是否在日志中脱敏
    enableJson = true     // 是否在JSON序列化时脱敏
)
private String sensitiveData;
```

## 测试演示

### 1. 访问演示接口

系统提供了演示接口来展示脱敏效果：

- `/api/demo/sensitive/user` - 单个用户信息脱敏
- `/api/demo/sensitive/users` - 用户列表脱敏
- `/api/demo/sensitive/sensitive-info` - 各种敏感信息类型脱敏

### 2. 运行单元测试

```bash
mvn test -Dtest=SensitiveUtilTest
```

## 注意事项

1. **性能考虑**：脱敏处理会增加一定的性能开销，建议根据实际需求选择性启用。

2. **数据完整性**：脱敏只影响数据的显示和输出，不会修改数据库中的原始数据。

3. **日志安全**：默认情况下，日志输出也会进行脱敏处理，确保日志文件的安全性。

4. **Java 8 时间类型支持**：系统已配置 Jackson JSR310 模块，完全支持 `LocalDateTime`、`LocalDate` 等 Java 8 时间类型的序列化和反序列化。

5. **兼容性**：脱敏功能兼容 Java 8+，使用了自定义的字符串重复方法替代 Java 11 的 `String.repeat()`。

6. **扩展性**：可以通过实现新的脱敏类型或修改 `SensitiveUtil` 来扩展脱敏规则。

## 最佳实践

1. **合理选择脱敏类型**：根据数据的实际类型选择合适的脱敏策略。

2. **测试验证**：在生产环境使用前，充分测试脱敏效果和性能影响。

3. **配置管理**：通过配置文件统一管理脱敏规则，便于维护和调整。

4. **日志监控**：定期检查日志输出，确保敏感数据得到有效保护。

5. **权限控制**：结合访问权限控制，在不同的用户角色下显示不同程度的脱敏数据。

## 故障排查

### 1. LocalDateTime 序列化异常

**问题**: `Java 8 date/time type LocalDateTime not supported by default`

**解决方案**:
- 确保已添加 Jackson JSR310 依赖
- 验证 ObjectMapper 配置是否正确
- 检查 application.yml 中的 Jackson 配置

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

### 2. 脱敏功能不生效

**可能原因**:
- 脱敏功能被禁用
- 注解使用不正确
- AOP 配置问题

**检查步骤**:
1. 确认 `sensitive.enabled=true`
2. 验证 `@Sensitive` 注解是否正确应用
3. 检查 AOP 是否正常工作

### 3. 性能问题

**优化建议**:
- 根据实际需求选择性启用脱敏功能
- 使用批量脱敏方法处理大量数据
- 考虑在不同环境使用不同的脱敏策略