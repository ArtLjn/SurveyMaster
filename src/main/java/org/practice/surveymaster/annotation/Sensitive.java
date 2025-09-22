package org.practice.surveymaster.annotation;

import org.practice.surveymaster.constant.SensitiveType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 数据脱敏注解
 * </p>
 *
 * <p>
 * 用于标记需要进行数据脱敏处理的字段。支持多种脱敏类型和自定义配置。
 * 可以应用在实体类的字段上，在序列化输出或日志记录时自动进行脱敏处理。
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午2:12
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
    
    /**
     * 脱敏类型
     */
    SensitiveType value() default SensitiveType.CUSTOM;
    
    /**
     * 自定义脱敏时保留开始位数（仅在 type = CUSTOM 时生效）
     */
    int keepStart() default 2;
    
    /**
     * 自定义脱敏时保留结束位数（仅在 type = CUSTOM 时生效）
     */
    int keepEnd() default 2;
    
    /**
     * 自定义脱敏时的替换字符（仅在 type = CUSTOM 时生效）
     */
    String maskChar() default "*";
    
    /**
     * 是否在日志中脱敏（默认为true）
     */
    boolean enableLog() default true;
    
    /**
     * 是否在JSON序列化时脱敏（默认为true）
     */
    boolean enableJson() default true;
}
