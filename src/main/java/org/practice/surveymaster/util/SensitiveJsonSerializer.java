package org.practice.surveymaster.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.practice.surveymaster.annotation.Sensitive;
import org.practice.surveymaster.constant.SensitiveType;

import java.io.IOException;

/**
 * <p>
 * JSON 序列化时的数据脱敏处理器
 * </p>
 *
 * <p>
 * 基于 Jackson 的自定义序列化器，在 JSON 序列化过程中自动对标注了 @Sensitive 注解的字段进行脱敏处理。
 * 支持上下文敏感的序列化，能够根据不同的注解配置采用不同的脱敏策略。
 * 集成了 SensitiveUtil 的所有脱敏算法，提供统一的序列化脱敏能力。
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午4:30
 */
public class SensitiveJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveType sensitiveType;
    private int keepStart;
    private int keepEnd;
    private String maskChar;
    private boolean enableJson;

    public SensitiveJsonSerializer() {
        // 默认构造函数
    }

    public SensitiveJsonSerializer(SensitiveType sensitiveType, int keepStart, int keepEnd, String maskChar, boolean enableJson) {
        this.sensitiveType = sensitiveType;
        this.keepStart = keepStart;
        this.keepEnd = keepEnd;
        this.maskChar = maskChar;
        this.enableJson = enableJson;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 如果禁用了 JSON 脱敏，直接输出原值
        if (!enableJson) {
            gen.writeString(value);
            return;
        }

        // 进行脱敏处理
        String maskedValue = desensitizeValue(value);
        gen.writeString(maskedValue);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            Sensitive sensitive = property.getAnnotation(Sensitive.class);
            if (sensitive != null) {
                return new SensitiveJsonSerializer(
                        sensitive.value(),
                        sensitive.keepStart(),
                        sensitive.keepEnd(),
                        sensitive.maskChar(),
                        sensitive.enableJson()
                );
            }
        }
        return this;
    }

    /**
     * 根据配置对数据进行脱敏处理
     */
    private String desensitizeValue(String value) {
        if (sensitiveType == null) {
            return value;
        }

        // 如果是自定义类型，使用自定义参数
        if (sensitiveType == SensitiveType.CUSTOM) {
            return SensitiveUtil.desensitizeCustom(value, keepStart, keepEnd, maskChar);
        }

        // 使用预定义的脱敏类型
        return SensitiveUtil.desensitize(value, sensitiveType);
    }
}