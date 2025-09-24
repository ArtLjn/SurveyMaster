package org.practice.surveymaster.service.impl;

import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.dto.AddQuestion;
import org.practice.surveymaster.exception.BusinessException;
import org.practice.surveymaster.mapper.QuestionMapper;
import org.practice.surveymaster.mapper.SurveyMapper;
import org.practice.surveymaster.model.Question;
import org.practice.surveymaster.model.Survey;
import org.practice.surveymaster.service.QuestionService;
import org.practice.surveymaster.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 问题服务实现类
 * 提供问题管理的业务逻辑实现
 *
 * @author ljn
 * @since 2025/9/24
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    
    private final QuestionMapper questionMapper;
    private final SurveyMapper surveyMapper;
    
    @Autowired
    public QuestionServiceImpl(QuestionMapper questionMapper, SurveyMapper surveyMapper) {
        this.questionMapper = questionMapper;
        this.surveyMapper = surveyMapper;
    }
    
    @Override
    public Question addQuestion(AddQuestion addQuestion, Long currentUserId) {
        AssertUtil.notNull(currentUserId, ErrorCode.AUTH_FAILURE, "用户未登录");
        
        // 验证问卷是否存在且用户有权限
        Survey survey = surveyMapper.selectById(addQuestion.getSurveyId());
        AssertUtil.notNull(survey, ErrorCode.NOT_FOUND, "问卷不存在");
        AssertUtil.isTrue(survey.getUserId().equals(currentUserId), ErrorCode.UNAUTHORIZED, "无权限操作此问卷");
        
        // 构建问题对象
        Question question = new Question();
        BeanUtils.copyProperties(addQuestion, question);
        
        // 插入数据库
        int result = questionMapper.insert(question);
        AssertUtil.isTrue(result > 0, ErrorCode.INTERNAL_SERVER_ERROR, "添加问题失败");
        
        return question;
    }
    
    @Override
    public Question getQuestionById(Long id) {
        AssertUtil.notNull(id, ErrorCode.BAD_REQUEST, "问题ID不能为空");
        
        Question question = questionMapper.selectById(id);
        AssertUtil.notNull(question, ErrorCode.NOT_FOUND, "问题不存在");
        
        return question;
    }
    
    @Override
    public List<Question> getQuestionsBySurveyId(Long surveyId) {
        AssertUtil.notNull(surveyId, ErrorCode.BAD_REQUEST, "问卷ID不能为空");
        
        // 验证问卷是否存在
        Survey survey = surveyMapper.selectById(surveyId);
        AssertUtil.notNull(survey, ErrorCode.BAD_REQUEST, "问卷不存在");
        
        return questionMapper.selectBySurveyId(surveyId);
    }
    
    @Override
    public void updateQuestion(Question question, Long currentUserId) {
        AssertUtil.notNull(question, ErrorCode.BAD_REQUEST, "问题对象不能为空");
        AssertUtil.notNull(question.getId(), ErrorCode.BAD_REQUEST, "问题ID不能为空");
        AssertUtil.notNull(currentUserId, ErrorCode.AUTH_FAILURE, "用户未登录");
        
        // 验证问题是否存在
        Question existingQuestion = questionMapper.selectById(question.getId());
        AssertUtil.notNull(existingQuestion, ErrorCode.NOT_FOUND, "问题不存在");
        
        // 验证用户权限（通过问卷验证）
        Survey survey = surveyMapper.selectById(existingQuestion.getSurveyId());
        AssertUtil.notNull(survey, ErrorCode.NOT_FOUND, "所属问卷不存在");
        AssertUtil.isTrue(survey.getUserId().equals(currentUserId), ErrorCode.UNAUTHORIZED, "无权限操作此问题");
        
        // 更新问题
        int result = questionMapper.update(question);
        AssertUtil.isTrue(result > 0, ErrorCode.INTERNAL_SERVER_ERROR, "更新问题失败");
    }
    
    @Override
    public void deleteQuestion(Long id, Long currentUserId) {
        AssertUtil.notNull(id, ErrorCode.BAD_REQUEST, "问题ID不能为空");
        AssertUtil.notNull(currentUserId, ErrorCode.AUTH_FAILURE, "用户未登录");
        
        // 验证问题是否存在
        Question question = questionMapper.selectById(id);
        AssertUtil.notNull(question, ErrorCode.NOT_FOUND, "问题不存在");
        // 验证用户权限（通过问卷验证）
        Survey survey = surveyMapper.selectById(question.getSurveyId());
        AssertUtil.notNull(survey, ErrorCode.NOT_FOUND, "所属问卷不存在");
        AssertUtil.isTrue(survey.getUserId().equals(currentUserId), ErrorCode.UNAUTHORIZED, "无权限操作此问题");
        
        // 删除问题
        int result = questionMapper.deleteById(id);
        AssertUtil.isTrue(result > 0, ErrorCode.INTERNAL_SERVER_ERROR, "删除问题失败");
    }
}