package org.practice.surveymaster.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建问卷请求DTO
 * 用于接收创建问卷的请求参数
 *
 * @author ljn
 * @since 2025/9/22 下午8:31
 */

@Data
public class CreateSurvey {
    /**
     * 用户ID（由JWT拦截器自动设置，前端无需传递）
     */
    private Long userId;
    
    /**
     * 问卷标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;
    
    /**
     * 问卷描述
     */
    @NotBlank(message = "描述不能为空")
    private String description;
    
    /**
     * 问卷状态
     * 0=草稿, 1=发布, 2=关闭
     */
    private int status;
}
