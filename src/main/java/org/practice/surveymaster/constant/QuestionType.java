package org.practice.surveymaster.constant;

/**
 * 问题类型枚举
 * 定义问卷中支持的问题类型
 *
 * @author ljn
 * @since 2025/9/24
 */
public enum QuestionType {
    /**
     * 单选题
     */
    SINGLE_CHOICE("single_choice", "单选题"),
    
    /**
     * 多选题
     */
    MULTIPLE_CHOICE("multiple_choice", "多选题"),
    
    /**
     * 文本题
     */
    TEXT("text", "文本题"),
    
    /**
     * 评分题
     */
    RATING("rating", "评分题");
    
    /**
     * 类型代码（存储到数据库）
     */
    private final String code;
    
    /**
     * 类型描述（用于显示）
     */
    private final String description;
    
    QuestionType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举值
     * 
     * @param code 类型代码
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果代码不存在
     */
    public static QuestionType fromCode(String code) {
        for (QuestionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的问题类型代码: " + code);
    }
    
    /**
     * 检查代码是否有效
     * 
     * @param code 类型代码
     * @return 是否有效
     */
    public static boolean isValidCode(String code) {
        for (QuestionType type : values()) {
            if (type.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}