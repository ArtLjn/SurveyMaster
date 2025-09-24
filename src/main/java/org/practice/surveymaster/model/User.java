package org.practice.surveymaster.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.practice.surveymaster.annotation.Sensitive;
import org.practice.surveymaster.constant.SensitiveType;
import org.practice.surveymaster.util.SensitiveJsonSerializer;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户实体类
 * </p>
 *
 * <p>
 * 用户信息实体，包含用户的基本信息如用户名、密码、邮箱等。
 * 集成了数据脱敏功能，敏感字段在序列化和日志输出时会自动进行脱敏处理。
 * 支持多种脱敏类型，确保用户隐私数据的安全性。
 * </p>
 *
 * @author ljn
 * @since 2025/9/11 下午4:35
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    
    @Sensitive(SensitiveType.NAME)
    @JsonSerialize(using = SensitiveJsonSerializer.class)
    private String username;
    
    @Sensitive(SensitiveType.PASSWORD)
    @JsonSerialize(using = SensitiveJsonSerializer.class)
    private String password;
    
    @Sensitive(SensitiveType.EMAIL)
    @JsonSerialize(using = SensitiveJsonSerializer.class)
    private String email;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}