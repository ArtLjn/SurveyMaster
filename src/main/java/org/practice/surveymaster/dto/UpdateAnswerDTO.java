package org.practice.surveymaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>
 * 更新问卷答案的请求DTO
 * </p>
 *
 * <p>
 * 用于接收更新已有问卷答案的请求数据，包含答案ID以及新的答案列表。
 * 该DTO类用于数据传输和参数验证。
 * </p>
 *
 * @author ljn
 * @since 2025/9/25 下午2:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAnswerDTO {

    /**
     * 答案记录ID
     */
    @NotNull(message = "答案ID不能为空")
    private String id;

    /**
     * 答案列表
     */
    @NotNull(message = "答案列表不能为空")
    @Size(min = 1, message = "至少需要回答一个问题")
    @Valid
    private List<QuestionAnswerDTO> answers;

    /**
     * 单个问题的答案DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionAnswerDTO {

        /**
         * 问题ID
         */
        @NotNull(message = "问题ID不能为空")
        private Long questionId;

        /**
         * 答案内容
         * - 对于单选题：单个选项值
         * - 对于多选题：选项值列表
         * - 对于文本题：文本内容
         * - 对于评分题：分数值
         */
        @NotNull(message = "答案内容不能为空")
        private Object answer;
    }
}