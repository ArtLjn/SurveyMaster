package org.practice.surveymaster.model;

import lombok.Data;

/**
 * 选项实体类
 * 对应数据库中option_table表的结构
 *
 * @author ljn
 * @since 2025/9/24
 */

/**
 * CREATE TABLE option_table (
 *     id BIGINT PRIMARY KEY AUTO_INCREMENT,
 *     question_id BIGINT NOT NULL,
 *     content VARCHAR(200) NOT NULL,
 *     FOREIGN KEY (question_id) REFERENCES question(id)
 * );
 */
@Data
public class OptionTable {
    /**
     * 选项ID（对应数据库BIGINT类型）
     */
    private Long id;
    
    /**
     * 所属问题ID（对应数据库BIGINT类型）
     */
    private Long questionId;
    
    /**
     * 选项内容
     */
    private String content;
}