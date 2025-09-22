package org.practice.surveymaster.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * [在此处用一两句话描述该类的核心职责和目的。]
 * </p>
 *
 * <p>
 * [可以在这里补充更详细的说明，例如设计思路、关键算法、使用示例或注意事项。]
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午8:31
 */

@Data
public class CreateSurvey {
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotBlank(message = "描述不能为空")
    private String description;
    @NotBlank(message = "状态不为空")
    // -- 0=草稿,1=发布,2=关闭
    private int status;
}
