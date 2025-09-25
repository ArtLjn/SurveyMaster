package org.practice.surveymaster.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加选项DTO
 * 用于接收前端添加选项的请求参数
 *
 * @author ljn
 * @since 2025/9/25
 */
@Data
public class AddOption {
    
    /**
     * 所属问题ID（必填）
     */
    @NotNull(message = "问题ID不能为空")
    private Long questionId;
    
    /**
     * 选项内容（必填）
     */
    @NotBlank(message = "选项内容不能为空")
    private String content;
}