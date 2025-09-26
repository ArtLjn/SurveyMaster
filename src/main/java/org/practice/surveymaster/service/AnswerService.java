package org.practice.surveymaster.service;

import org.practice.surveymaster.dto.AnswerQueryDTO;
import org.practice.surveymaster.dto.SubmitAnswerDTO;
import org.practice.surveymaster.dto.UpdateAnswerDTO;
import org.practice.surveymaster.model.mongo.Answer;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 问卷答案业务服务接口
 * </p>
 *
 * <p>
 * 提供问卷答案的完整业务操作，包括提交答案、查询答案、更新答案、删除答案等功能。
 * 支持多种查询条件和分页查询，为前端提供丰富的数据操作接口。
 * </p>
 *
 * @author ljn
 * @since 2025/9/25 下午2:22
 */
public interface AnswerService {

    /**
     * 提交问卷答案
     *
     * @param submitAnswerDTO 提交答案DTO
     * @return 答案记录
     */
    Answer submitAnswer(SubmitAnswerDTO submitAnswerDTO);

    /**
     * 根据ID查询答案
     *
     * @param id 答案ID
     * @return 答案记录
     */
    Optional<Answer> getAnswerById(String id);

    /**
     * 根据问卷ID和用户ID查询答案
     *
     * @param surveyId 问卷ID
     * @param userId 用户ID
     * @return 答案记录
     */
    Optional<Answer> getAnswerBySurveyIdAndUserId(Long surveyId, Long userId);

    /**
     * 根据查询条件分页查询答案
     *
     * @param queryDTO 查询条件
     * @return 分页答案列表
     */
    Page<Answer> getAnswers(AnswerQueryDTO queryDTO);

    /**
     * 根据问卷ID查询所有答案
     *
     * @param surveyId 问卷ID
     * @return 答案列表
     */
    List<Answer> getAnswersBySurveyId(Long surveyId);

    /**
     * 根据用户ID查询所有答案
     *
     * @param userId 用户ID
     * @return 答案列表
     */
    List<Answer> getAnswersByUserId(Long userId);

    /**
     * 根据时间范围查询答案
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 答案列表
     */
    List<Answer> getAnswersByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 更新答案
     *
     * @param updateAnswerDTO 更新答案DTO
     * @return 更新后的答案记录
     */
    Answer updateAnswer(UpdateAnswerDTO updateAnswerDTO);

    /**
     * 根据ID删除答案
     *
     * @param id 答案ID
     * @return 是否删除成功
     */
    boolean deleteAnswer(String id);

    /**
     * 根据问卷ID删除所有答案
     *
     * @param surveyId 问卷ID
     * @return 删除的记录数
     */
    long deleteAnswersBySurveyId(Long surveyId);

    /**
     * 根据用户ID删除所有答案
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    long deleteAnswersByUserId(Long userId);

    /**
     * 统计某个问卷的答案数量
     *
     * @param surveyId 问卷ID
     * @return 答案数量
     */
    long countAnswersBySurveyId(Long surveyId);

    /**
     * 统计某个用户的答案数量
     *
     * @param userId 用户ID
     * @return 答案数量
     */
    long countAnswersByUserId(Long userId);

    /**
     * 检查用户是否已回答某个问卷
     *
     * @param surveyId 问卷ID
     * @param userId 用户ID
     * @return 是否已回答
     */
    boolean hasUserAnswered(Long surveyId, Long userId);

    /**
     * 查询包含特定问题ID的答案
     *
     * @param questionId 问题ID
     * @return 答案列表
     */
    List<Answer> getAnswersByQuestionId(Long questionId);
}
