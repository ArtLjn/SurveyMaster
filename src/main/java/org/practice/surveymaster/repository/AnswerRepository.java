package org.practice.surveymaster.repository;

import org.practice.surveymaster.model.mongo.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 问卷答案数据访问接口
 * </p>
 *
 * <p>
 * 基于Spring Data MongoDB的Repository接口，提供对Answer实体的数据访问操作。
 * 包含基本的CRUD操作以及自定义的查询方法，支持按问卷ID、用户ID等条件查询答案数据。
 * </p>
 *
 * @author ljn
 * @since 2025/9/25 下午2:30
 */
@Repository
public interface AnswerRepository extends MongoRepository<Answer, String> {

    /**
     * 根据问卷ID查询所有答案
     *
     * @param surveyId 问卷ID
     * @return 答案列表
     */
    List<Answer> findBySurveyId(Long surveyId);

    /**
     * 根据用户ID查询所有答案
     *
     * @param userId 用户ID
     * @return 答案列表
     */
    List<Answer> findByUserId(Long userId);

    /**
     * 根据问卷ID和用户ID查询答案
     *
     * @param surveyId 问卷ID
     * @param userId 用户ID
     * @return 答案对象
     */
    Optional<Answer> findBySurveyIdAndUserId(Long surveyId, Long userId);

    /**
     * 根据问卷ID分页查询答案
     *
     * @param surveyId 问卷ID
     * @param pageable 分页参数
     * @return 分页答案列表
     */
    Page<Answer> findBySurveyId(Long surveyId, Pageable pageable);

    /**
     * 根据用户ID分页查询答案
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页答案列表
     */
    Page<Answer> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据提交时间范围查询答案
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 答案列表
     */
    List<Answer> findBySubmittedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计某个问卷的答案数量
     *
     * @param surveyId 问卷ID
     * @return 答案数量
     */
    long countBySurveyId(Long surveyId);

    /**
     * 统计某个用户的答案数量
     *
     * @param userId 用户ID
     * @return 答案数量
     */
    long countByUserId(Long userId);

    /**
     * 检查用户是否已回答某个问卷
     *
     * @param surveyId 问卷ID
     * @param userId 用户ID
     * @return 是否存在答案记录
     */
    boolean existsBySurveyIdAndUserId(Long surveyId, Long userId);

    /**
     * 根据问卷ID删除所有答案
     *
     * @param surveyId 问卷ID
     * @return 删除的记录数
     */
    long deleteBySurveyId(Long surveyId);

    /**
     * 根据用户ID删除所有答案
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    long deleteByUserId(Long userId);

    /**
     * 查询包含特定问题ID的答案
     *
     * @param questionId 问题ID
     * @return 答案列表
     */
    @Query("{'answers.questionId': ?0}")
    List<Answer> findByQuestionId(Long questionId);

    /**
     * 根据时间范围和问卷ID查询答案
     *
     * @param surveyId 问卷ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 答案列表
     */
    @Query("{'surveyId': ?0, 'submittedAt': {'$gte': ?1, '$lte': ?2}}")
    List<Answer> findBySurveyIdAndTimeRange(Long surveyId, LocalDateTime startTime, LocalDateTime endTime);
}