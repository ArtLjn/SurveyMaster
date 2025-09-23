package org.practice.surveymaster.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问卷实体类
 * 对应数据库中survey表的结构
 *
 * @author ljn
 * @since 2025/9/22 下午8:28
 */

/**
 * CREATE TABLE survey (
 *     id BIGINT PRIMARY KEY AUTO_INCREMENT,
 *     user_id BIGINT NOT NULL, -- 创建者
 *     title VARCHAR(200) NOT NULL,
 *     description TEXT,
 *     status TINYINT DEFAULT 0, -- 0=草稿,1=发布,2=关闭
 *     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 *     FOREIGN KEY (user_id) REFERENCES user(id)
 * );
 */
@Data
public class Survey {
    /**
     * 问卷ID（对应数据库BIGINT类型）
     */
    private Long id;
    
    /**
     * 创建者ID（对应数据库BIGINT类型）
     */
    private Long userId;
    
    /**
     * 问卷标题
     */
    private String title;
    
    /**
     * 问卷描述
     */
    private String description;
    
    /**
     * 问卷状态
     * 0=草稿, 1=发布, 2=关闭
     */
    private int status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
