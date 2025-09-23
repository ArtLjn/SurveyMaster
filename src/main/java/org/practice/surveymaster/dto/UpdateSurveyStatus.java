package org.practice.surveymaster.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 更新问卷状态DTO
 * 用于接收更新问卷状态的请求参数
 *
 * @author ljn
 * @since 2025/9/23 上午11:30
 */

@Data
public class UpdateSurveyStatus {
    /**
     * 问卷ID
     */
    @NotNull(message = "问卷ID不能为空")
    @Positive(message = "问卷ID必须为正数")
    private Long id;
    
    /**
     * 问卷状态
     * 0=草稿, 1=发布, 2=关闭
     */
    @NotNull(message = "问卷状态不能为空")
    @Min(value = 0, message = "问卷状态无效，必须为0-2之间,0=草稿/1=发布/2=关闭")
    @Max(value = 2, message = "问卷状态无效，必须为0-2之间,0=草稿/1=发布/2=关闭")
    private Integer status;
    
    /**
     * 用户ID（由JWT拦截器自动设置，前端无需传递）
     */
    private Long userId;
}
