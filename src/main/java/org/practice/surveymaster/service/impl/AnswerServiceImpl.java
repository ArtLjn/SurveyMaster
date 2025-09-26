package org.practice.surveymaster.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.dto.AnswerQueryDTO;
import org.practice.surveymaster.dto.SubmitAnswerDTO;
import org.practice.surveymaster.dto.UpdateAnswerDTO;
import org.practice.surveymaster.exception.BusinessException;
import org.practice.surveymaster.mapper.QuestionMapper;
import org.practice.surveymaster.mapper.SurveyMapper;
import org.practice.surveymaster.model.mongo.Answer;
import org.practice.surveymaster.repository.AnswerRepository;
import org.practice.surveymaster.service.AnswerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 问卷答案业务服务实现类
 * </p>
 *
 * <p>
 * 实现AnswerService接口，提供问卷答案的完整业务操作。
 * 包含答案的提交、查询、更新、删除等功能，以及各种统计和验证操作。
 * 使用MongoDB作为数据存储，支持复杂的查询条件和分页功能。
 * </p>
 *
 * @author ljn
 * @since 2025/9/25 下午2:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public Answer submitAnswer(SubmitAnswerDTO submitAnswerDTO) {
        log.info("用户 {} 开始提交问卷 {} 的答案", submitAnswerDTO.getUserId(), submitAnswerDTO.getSurveyId());
        
        // 检查用户是否已经回答过该问卷
        if (hasUserAnswered(submitAnswerDTO.getSurveyId(), submitAnswerDTO.getUserId())) {
            throw new BusinessException(ErrorCode.ANSWER_ALREADY_SUBMITTED);
        }

        // 获取surveyId所有问题ID
        List<Long> questionIds = questionMapper.getQuestionIdsBySurveyId(submitAnswerDTO.getSurveyId());
        // 如果submitAnswerDTO 中 任意一个questionId 不在 questionIds 中，则认定为不合法
        List<Long> submittedQuestionIds = submitAnswerDTO.getAnswers().stream()
                .map(SubmitAnswerDTO.QuestionAnswerDTO::getQuestionId)
                .collect(Collectors.toList());
        
        if (!new HashSet<>(questionIds).containsAll(submittedQuestionIds)) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        // 创建答案实体
        Answer answer = new Answer();
        answer.setSurveyId(submitAnswerDTO.getSurveyId());
        answer.setUserId(submitAnswerDTO.getUserId());
        answer.setSubmittedAt(LocalDateTime.now());
        
        // 转换答案列表
        List<Answer.QuestionAnswer> questionAnswers = submitAnswerDTO.getAnswers().stream()
                .map(dto -> new Answer.QuestionAnswer(dto.getQuestionId(), dto.getAnswer()))
                .collect(Collectors.toList());
        answer.setAnswers(questionAnswers);
        
        // 保存答案
        Answer savedAnswer = answerRepository.save(answer);
        log.info("用户 {} 成功提交问卷 {} 的答案，答案ID: {}", 
                submitAnswerDTO.getUserId(), submitAnswerDTO.getSurveyId(), savedAnswer.getId());
        
        return savedAnswer;
    }

    @Override
    public Optional<Answer> getAnswerById(String id) {
        log.debug("根据ID查询答案: {}", id);
        return answerRepository.findById(id);
    }

    @Override
    public Optional<Answer> getAnswerBySurveyIdAndUserId(Long surveyId, Long userId) {
        log.debug("根据问卷ID {} 和用户ID {} 查询答案", surveyId, userId);
        return answerRepository.findBySurveyIdAndUserId(surveyId, userId);
    }

    @Override
    public Page<Answer> getAnswers(AnswerQueryDTO queryDTO) {
        log.debug("分页查询答案，条件: {}", queryDTO);
        
        // 创建分页参数
        Sort sort = Sort.by(Sort.Direction.fromString(queryDTO.getSortDir()), queryDTO.getSortBy());
        Pageable pageable = PageRequest.of(queryDTO.getPage() - 1, queryDTO.getSize(), sort);
        
        // 根据查询条件执行不同的查询
        if (queryDTO.getSurveyId() != null && queryDTO.getUserId() != null) {
            // 按问卷ID和用户ID查询（实际只会返回一条记录）
            Optional<Answer> answer = answerRepository.findBySurveyIdAndUserId(queryDTO.getSurveyId(), queryDTO.getUserId());
            return answer.map(a -> answerRepository.findAll(pageable))
                    .orElse(Page.empty(pageable));
        } else if (queryDTO.getSurveyId() != null) {
            // 按问卷ID查询
            return answerRepository.findBySurveyId(queryDTO.getSurveyId(), pageable);
        } else if (queryDTO.getUserId() != null) {
            // 按用户ID查询
            return answerRepository.findByUserId(queryDTO.getUserId(), pageable);
        } else if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            // 按时间范围查询
            List<Answer> answers = answerRepository.findBySubmittedAtBetween(queryDTO.getStartTime(), queryDTO.getEndTime());
            return answerRepository.findAll(pageable);
        } else {
            // 查询所有
            return answerRepository.findAll(pageable);
        }
    }

    @Override
    public List<Answer> getAnswersBySurveyId(Long surveyId) {
        log.debug("根据问卷ID {} 查询所有答案", surveyId);
        return answerRepository.findBySurveyId(surveyId);
    }

    @Override
    public List<Answer> getAnswersByUserId(Long userId) {
        log.debug("根据用户ID {} 查询所有答案", userId);
        return answerRepository.findByUserId(userId);
    }

    @Override
    public List<Answer> getAnswersByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("根据时间范围查询答案: {} - {}", startTime, endTime);
        return answerRepository.findBySubmittedAtBetween(startTime, endTime);
    }

    @Override
    @Transactional
    public Answer updateAnswer(UpdateAnswerDTO updateAnswerDTO) {
        log.info("开始更新答案，ID: {}", updateAnswerDTO.getId());
        
        // 查找现有答案
        Answer existingAnswer = answerRepository.findById(updateAnswerDTO.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        
        // 转换新的答案列表
        List<Answer.QuestionAnswer> questionAnswers = updateAnswerDTO.getAnswers().stream()
                .map(dto -> new Answer.QuestionAnswer(dto.getQuestionId(), dto.getAnswer()))
                .collect(Collectors.toList());
        
        // 更新答案内容
        existingAnswer.setAnswers(questionAnswers);
        existingAnswer.setSubmittedAt(LocalDateTime.now()); // 更新提交时间
        
        Answer updatedAnswer = answerRepository.save(existingAnswer);
        log.info("成功更新答案，ID: {}", updateAnswerDTO.getId());
        
        return updatedAnswer;
    }

    @Override
    @Transactional
    public boolean deleteAnswer(String id) {
        log.info("开始删除答案，ID: {}", id);
        
        if (!answerRepository.existsById(id)) {
            log.warn("要删除的答案不存在，ID: {}", id);
            return false;
        }
        
        answerRepository.deleteById(id);
        log.info("成功删除答案，ID: {}", id);
        return true;
    }

    @Override
    @Transactional
    public long deleteAnswersBySurveyId(Long surveyId) {
        log.info("开始删除问卷 {} 的所有答案", surveyId);
        long deletedCount = answerRepository.deleteBySurveyId(surveyId);
        log.info("成功删除问卷 {} 的 {} 条答案记录", surveyId, deletedCount);
        return deletedCount;
    }

    @Override
    @Transactional
    public long deleteAnswersByUserId(Long userId) {
        log.info("开始删除用户 {} 的所有答案", userId);
        long deletedCount = answerRepository.deleteByUserId(userId);
        log.info("成功删除用户 {} 的 {} 条答案记录", userId, deletedCount);
        return deletedCount;
    }

    @Override
    public long countAnswersBySurveyId(Long surveyId) {
        log.debug("统计问卷 {} 的答案数量", surveyId);
        return answerRepository.countBySurveyId(surveyId);
    }

    @Override
    public long countAnswersByUserId(Long userId) {
        log.debug("统计用户 {} 的答案数量", userId);
        return answerRepository.countByUserId(userId);
    }

    @Override
    public boolean hasUserAnswered(Long surveyId, Long userId) {
        log.debug("检查用户 {} 是否已回答问卷 {}", userId, surveyId);
        return answerRepository.existsBySurveyIdAndUserId(surveyId, userId);
    }

    @Override
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        log.debug("查询包含问题 {} 的所有答案", questionId);
        return answerRepository.findByQuestionId(questionId);
    }
}