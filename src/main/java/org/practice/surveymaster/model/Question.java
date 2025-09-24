package org.practice.surveymaster.model;

import lombok.Data;
import org.practice.surveymaster.constant.QuestionType;

/**
 * 问题实体类
 * 对应数据库中question表的结构
 *
 * @author ljn
 * @since 2025/9/24
 */

/**
 * CREATE TABLE question (
 *     id BIGINT PRIMARY KEY AUTO_INCREMENT,
 *     survey_id BIGINT NOT NULL,
 *     type VARCHAR(20) NOT NULL, -- single, multiple, text
 *     content TEXT NOT NULL,
 *     FOREIGN KEY (survey_id) REFERENCES survey(id)
 * );
 */
@Data
public class Question {
    /**
     * 问题ID（对应数据库BIGINT类型）
     */
    private Long id;
    
    /**
     * 所属问卷ID（对应数据库BIGINT类型）
     */
    private Long surveyId;
    
    /**
     * 问题类型
     * 使用枚举定义：SINGLE_CHOICE(单选)、MULTIPLE_CHOICE(多选)、TEXT(文本)、RATING(评分)
     */
    private QuestionType type;
    
    /**
     * 问题内容
     */
    private String content;
}