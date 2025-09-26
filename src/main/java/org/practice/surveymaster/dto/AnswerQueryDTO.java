package org.practice.surveymaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 问卷答案查询条件DTO
 * </p>
 *
 * <p>
 * 用于接收查询问卷答案的条件参数，支持按问卷ID、用户ID、时间范围等条件进行查询。
 * 该DTO类用于数据传输和参数验证。
 * </p>
 *
 * @author ljn
 * @since 2025/9/25 下午2:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerQueryDTO {

    /**
     * 问卷ID（可选）
     */
    private Long surveyId;

    /**
     * 用户ID（可选）
     */
    private Long userId;

    /**
     * 开始时间（可选）
     */
    private LocalDateTime startTime;

    /**
     * 结束时间（可选）
     */
    private LocalDateTime endTime;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 10;

    /**
     * 排序字段（可选，默认按提交时间降序）
     */
    private String sortBy = "submittedAt";

    /**
     * 排序方向（asc或desc，默认desc）
     */
    private String sortDir = "desc";
}