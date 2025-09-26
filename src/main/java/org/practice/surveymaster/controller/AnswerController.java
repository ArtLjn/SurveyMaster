package org.practice.surveymaster.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.practice.surveymaster.annotation.LogBusiness;
import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.dto.AnswerQueryDTO;
import org.practice.surveymaster.dto.SubmitAnswerDTO;
import org.practice.surveymaster.dto.UpdateAnswerDTO;
import org.practice.surveymaster.model.mongo.Answer;
import org.practice.surveymaster.service.AnswerService;
import org.practice.surveymaster.vo.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 问卷答案管理控制器
 * </p>
 *
 * <p>
 * 提供问卷答案的完整REST API接口，包括答案的提交、查询、更新、删除等操作。
 * 支持多种查询条件和分页功能，为前端提供丰富的数据操作接口。
 * 所有接口都使用统一的ApiResponse进行响应封装，并记录业务操作日志。
 * </p>
 *
 * @author ljn
 * @since 2025/9/25 下午2:45
 */
@Slf4j
@RestController
@RequestMapping("/api/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    /**
     * 提交问卷答案
     *
     * @param submitAnswerDTO 提交答案DTO
     * @return API响应结果
     */
    @PostMapping("/submit")
    @LogBusiness("提交问卷答案")
    public ApiResponse<Answer> submitAnswer(@Valid @RequestBody SubmitAnswerDTO submitAnswerDTO) {
        log.info("接收提交答案请求: 问卷ID={}, 用户ID={}", submitAnswerDTO.getSurveyId(), submitAnswerDTO.getUserId());
        Answer answer = answerService.submitAnswer(submitAnswerDTO);
        return ApiResponse.success("答案提交成功", answer);
    }

    /**
     * 根据ID查询答案
     *
     * @param id 答案ID
     * @return API响应结果
     */
    @GetMapping("/{id}")
    @LogBusiness("查询答案详情")
    public ApiResponse<Answer> getAnswerById(@PathVariable String id) {
        log.info("查询答案详情: ID={}", id);
        Optional<Answer> answer = answerService.getAnswerById(id);
        return answer.map(value -> ApiResponse.success("查询成功", value)).orElseGet(() -> ApiResponse.error(ErrorCode.ANSWER_NOT_FOUND));
    }

    /**
     * 根据问卷ID和用户ID查询答案
     *
     * @param surveyId 问卷ID
     * @param userId 用户ID
     * @return API响应结果
     */
    @GetMapping("/survey/{surveyId}/user/{userId}")
    @LogBusiness("根据问卷和用户查询答案")
    public ApiResponse<Answer> getAnswerBySurveyAndUser(@PathVariable Long surveyId, @PathVariable Long userId) {
        log.info("根据问卷ID和用户ID查询答案: 问卷ID={}, 用户ID={}", surveyId, userId);
        Optional<Answer> answer = answerService.getAnswerBySurveyIdAndUserId(surveyId, userId);
        return answer.map(value -> ApiResponse.success("查询成功", value)).orElseGet(() -> ApiResponse.error(ErrorCode.ANSWER_NOT_FOUND));
    }

    /**
     * 分页查询答案
     *
     * @param queryDTO 查询条件DTO
     * @return API响应结果
     */
    @GetMapping("/page")
    @LogBusiness("分页查询答案")
    public ApiResponse<Page<Answer>> getAnswers(@Valid AnswerQueryDTO queryDTO) {
        log.info("分页查询答案: {}", queryDTO);
        Page<Answer> answers = answerService.getAnswers(queryDTO);
        return ApiResponse.success("查询成功", answers);
    }

    /**
     * 根据问卷ID查询所有答案
     *
     * @param surveyId 问卷ID
     * @return API响应结果
     */
    @GetMapping("/survey/{surveyId}")
    @LogBusiness("根据问卷查询答案")
    public ApiResponse<List<Answer>> getAnswersBySurveyId(@PathVariable Long surveyId) {
        log.info("根据问卷ID查询所有答案: 问卷ID={}", surveyId);
        List<Answer> answers = answerService.getAnswersBySurveyId(surveyId);
        return ApiResponse.success("查询成功", answers);
    }

    /**
     * 根据用户ID查询所有答案
     *
     * @param userId 用户ID
     * @return API响应结果
     */
    @GetMapping("/user/{userId}")
    @LogBusiness("根据用户查询答案")
    public ApiResponse<List<Answer>> getAnswersByUserId(@PathVariable Long userId) {
        log.info("根据用户ID查询所有答案: 用户ID={}", userId);
        List<Answer> answers = answerService.getAnswersByUserId(userId);
        return ApiResponse.success("查询成功", answers);
    }

    /**
     * 根据时间范围查询答案
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return API响应结果
     */
    @GetMapping("/time-range")
    @LogBusiness("根据时间范围查询答案")
    public ApiResponse<List<Answer>> getAnswersByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("根据时间范围查询答案: {} - {}", startTime, endTime);
        List<Answer> answers = answerService.getAnswersByTimeRange(startTime, endTime);
        return ApiResponse.success("查询成功", answers);
    }

    /**
     * 更新答案
     *
     * @param updateAnswerDTO 更新答案DTO
     * @return API响应结果
     */
    @PutMapping("/update")
    @LogBusiness("更新问卷答案")
    public ApiResponse<Answer> updateAnswer(@Valid @RequestBody UpdateAnswerDTO updateAnswerDTO) {
        log.info("更新答案: ID={}", updateAnswerDTO.getId());
        Answer answer = answerService.updateAnswer(updateAnswerDTO);
        return ApiResponse.success("答案更新成功", answer);
    }

    /**
     * 删除答案
     *
     * @param id 答案ID
     * @return API响应结果
     */
    @DeleteMapping("/{id}")
    @LogBusiness("删除问卷答案")
    public ApiResponse<String> deleteAnswer(@PathVariable String id) {
        log.info("删除答案: ID={}", id);
        boolean deleted = answerService.deleteAnswer(id);
        if (deleted) {
            return ApiResponse.success("答案删除成功");
        } else {
            return ApiResponse.error(ErrorCode.ANSWER_NOT_FOUND);
        }
    }

    /**
     * 根据问卷ID删除所有答案
     *
     * @param surveyId 问卷ID
     * @return API响应结果
     */
    @DeleteMapping("/survey/{surveyId}")
    @LogBusiness("根据问卷删除答案")
    public ApiResponse<String> deleteAnswersBySurveyId(@PathVariable Long surveyId) {
        log.info("根据问卷ID删除所有答案: 问卷ID={}", surveyId);
        long deletedCount = answerService.deleteAnswersBySurveyId(surveyId);
        return ApiResponse.success("成功删除 " + deletedCount + " 条答案记录");
    }

    /**
     * 根据用户ID删除所有答案
     *
     * @param userId 用户ID
     * @return API响应结果
     */
    @DeleteMapping("/user/{userId}")
    @LogBusiness("根据用户删除答案")
    public ApiResponse<String> deleteAnswersByUserId(@PathVariable Long userId) {
        log.info("根据用户ID删除所有答案: 用户ID={}", userId);
        long deletedCount = answerService.deleteAnswersByUserId(userId);
        return ApiResponse.success("成功删除 " + deletedCount + " 条答案记录");
    }

    /**
     * 统计问卷答案数量
     *
     * @param surveyId 问卷ID
     * @return API响应结果
     */
    @GetMapping("/count/survey/{surveyId}")
    @LogBusiness("统计问卷答案数量")
    public ApiResponse<Long> countAnswersBySurveyId(@PathVariable Long surveyId) {
        log.info("统计问卷答案数量: 问卷ID={}", surveyId);
        long count = answerService.countAnswersBySurveyId(surveyId);
        return ApiResponse.success("查询成功", count);
    }

    /**
     * 统计用户答案数量
     *
     * @param userId 用户ID
     * @return API响应结果
     */
    @GetMapping("/count/user/{userId}")
    @LogBusiness("统计用户答案数量")
    public ApiResponse<Long> countAnswersByUserId(@PathVariable Long userId) {
        log.info("统计用户答案数量: 用户ID={}", userId);
        long count = answerService.countAnswersByUserId(userId);
        return ApiResponse.success("查询成功", count);
    }

    /**
     * 检查用户是否已回答问卷
     *
     * @param surveyId 问卷ID
     * @param userId 用户ID
     * @return API响应结果
     */
    @GetMapping("/check/survey/{surveyId}/user/{userId}")
    @LogBusiness("检查用户是否已回答问卷")
    public ApiResponse<Boolean> checkUserAnswered(@PathVariable Long surveyId, @PathVariable Long userId) {
        log.info("检查用户是否已回答问卷: 问卷ID={}, 用户ID={}", surveyId, userId);
        boolean hasAnswered = answerService.hasUserAnswered(surveyId, userId);
        return ApiResponse.success("查询成功", hasAnswered);
    }

    /**
     * 查询包含特定问题的答案
     *
     * @param questionId 问题ID
     * @return API响应结果
     */
    @GetMapping("/question/{questionId}")
    @LogBusiness("根据问题查询答案")
    public ApiResponse<List<Answer>> getAnswersByQuestionId(@PathVariable Long questionId) {
        log.info("查询包含问题的答案: 问题ID={}", questionId);
        List<Answer> answers = answerService.getAnswersByQuestionId(questionId);
        return ApiResponse.success("查询成功", answers);
    }
}