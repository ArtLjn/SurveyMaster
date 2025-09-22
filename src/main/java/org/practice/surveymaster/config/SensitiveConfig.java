package org.practice.surveymaster.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.practice.surveymaster.util.SensitiveJsonSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <p>
 * 数据脱敏配置类
 * </p>
 *
 * <p>
 * 配置数据脱敏相关的 Bean 和属性，包括 JSON 序列化器的注册、脱敏开关配置等。
 * 提供统一的脱敏配置管理，支持通过配置文件动态调整脱敏行为。
 * 集成 Jackson 序列化器，自动对敏感字段进行脱敏处理。
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午4:35
 */
@Configuration
@ConfigurationProperties(prefix = "sensitive")
public class SensitiveConfig {

    /**
     * 是否启用数据脱敏功能
     */
    private boolean enabled = true;

    /**
     * 是否在日志中启用脱敏
     */
    private boolean enableLog = true;

    /**
     * 是否在 JSON 序列化时启用脱敏
     */
    private boolean enableJson = true;

    /**
     * 默认的脱敏字符
     */
    private String defaultMaskChar = "*";

    /**
     * 注册带有脱敏功能的 ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper sensitiveObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册 JSR310 模块支持 Java 8 时间类型
        objectMapper.registerModule(new JavaTimeModule());
        
        // 配置时间序列化格式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 其他通用配置
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 创建简单模块并注册脱敏序列化器
        SimpleModule sensitiveModule = new SimpleModule("SensitiveModule");
        sensitiveModule.addSerializer(String.class, new SensitiveJsonSerializer());
        
        // 注册模块到 ObjectMapper
        objectMapper.registerModule(sensitiveModule);
        
        return objectMapper;
    }

    /**
     * 创建脱敏属性配置 Bean
     */
    @Bean
    public SensitiveProperties sensitiveProperties() {
        SensitiveProperties properties = new SensitiveProperties();
        properties.setEnabled(this.enabled);
        properties.setEnableLog(this.enableLog);
        properties.setEnableJson(this.enableJson);
        properties.setDefaultMaskChar(this.defaultMaskChar);
        return properties;
    }

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnableLog() {
        return enableLog;
    }

    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

    public boolean isEnableJson() {
        return enableJson;
    }

    public void setEnableJson(boolean enableJson) {
        this.enableJson = enableJson;
    }

    public String getDefaultMaskChar() {
        return defaultMaskChar;
    }

    public void setDefaultMaskChar(String defaultMaskChar) {
        this.defaultMaskChar = defaultMaskChar;
    }

    /**
     * 脱敏属性配置类
     */
    public static class SensitiveProperties {
        private boolean enabled = true;
        private boolean enableLog = true;
        private boolean enableJson = true;
        private String defaultMaskChar = "*";

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnableLog() {
            return enableLog;
        }

        public void setEnableLog(boolean enableLog) {
            this.enableLog = enableLog;
        }

        public boolean isEnableJson() {
            return enableJson;
        }

        public void setEnableJson(boolean enableJson) {
            this.enableJson = enableJson;
        }

        public String getDefaultMaskChar() {
            return defaultMaskChar;
        }

        public void setDefaultMaskChar(String defaultMaskChar) {
            this.defaultMaskChar = defaultMaskChar;
        }
    }
}