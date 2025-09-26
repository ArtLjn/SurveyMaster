package org.practice.surveymaster.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 问卷答案实体类，用于存储用户提交的问卷答案数据
 * </p>
 *
 * <p>
 * 该类对应MongoDB中的answer集合，存储用户对问卷的完整回答。
 * 包含问卷ID、用户ID、提交时间以及具体的答案列表。
 * 每个答案包含问题ID和对应的回答内容，支持单选、多选和文本等多种题型。
 * </p>
 *
 * @author ljn
 * @since 2025/9/25 下午2:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "answer")
public class Answer {
    
    /**
     * MongoDB文档ID
     */
    @Id
    private String id;
    
    /**
     * 问卷ID
     */
    private Long surveyId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 提交时间
     */
    private LocalDateTime submittedAt;
    
    /**
     * 答案列表，包含该用户对问卷中所有问题的回答
     */
    private List<QuestionAnswer> answers;
    
    /**
     * 单个问题的答案实体
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionAnswer {
        
        /**
         * 问题ID
         */
        private Long questionId;
        
        /**
         * 答案内容
         * - 对于单选题：单个选项值
         * - 对于多选题：选项值列表
         * - 对于文本题：文本内容
         * - 对于评分题：分数值
         */
        private Object answer;
    }
}
