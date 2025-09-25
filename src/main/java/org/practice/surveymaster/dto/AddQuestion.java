package org.practice.surveymaster.dto;

import lombok.Data;
import org.practice.surveymaster.constant.QuestionType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 添加问题DTO
 * 用于接收前端添加问题的请求参数
 *
 * @author ljn
 * @since 2025/9/24
 */
@Data
public class AddQuestion {
    
    /**
     * 所属问卷ID（必填）
     */
    @NotNull(message = "问卷ID不能为空")
    private Long surveyId;
    
    /**
     * 问题类型（必填）
     * 支持：SINGLE_CHOICE(单选)、MULTIPLE_CHOICE(多选)、TEXT(文本)、RATING(评分)
     */
    @NotNull(message = "问题类型不能为空")
    private QuestionType type;
    
    /**
     * 问题内容（必填）
     */
    @NotBlank(message = "问题内容不能为空")
    private String content;
    
    /**
     * 问题选项列表（可选）
     * 仅适用于单选题和多选题
     */
    private List<String> options;
}