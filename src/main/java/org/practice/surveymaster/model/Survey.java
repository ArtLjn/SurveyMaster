package org.practice.surveymaster.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * [问卷类]
 * </p>
 *
 * <p>
 * [可以在这里补充更详细的说明，例如设计思路、关键算法、使用示例或注意事项。]
 * </p>
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
    private int id;
    private int userId;
    private String title;
    private String description;
    private int status;
    private LocalDateTime createdAt;
}
