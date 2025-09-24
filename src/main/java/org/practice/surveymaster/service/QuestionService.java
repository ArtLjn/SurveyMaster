package org.practice.surveymaster.service;

import org.practice.surveymaster.dto.AddQuestion;
import org.practice.surveymaster.model.Question;

import java.util.List;

/**
 * 问题服务接口
 * 提供问题管理的业务逻辑
 *
 * @author ljn
 * @since 2025/9/24
 */
public interface QuestionService {
    
    /**
     * 添加问题
     * 
     * @param addQuestion 添加问题请求
     * @param currentUserId 当前用户ID（用于权限验证）
     * @return 创建的问题对象
     */
    Question addQuestion(AddQuestion addQuestion, Long currentUserId);
    
    /**
     * 根据ID获取问题
     * 
     * @param id 问题ID
     * @return 问题对象
     */
    Question getQuestionById(Long id);
    
    /**
     * 根据问卷ID获取所有问题
     * 
     * @param surveyId 问卷ID
     * @return 问题列表
     */
    List<Question> getQuestionsBySurveyId(Long surveyId);
    
    /**
     * 更新问题
     * 
     * @param question 问题对象
     * @param currentUserId 当前用户ID（用于权限验证）
     */
    void updateQuestion(Question question, Long currentUserId);
    
    /**
     * 删除问题
     * 
     * @param id 问题ID
     * @param currentUserId 当前用户ID（用于权限验证）
     */
    void deleteQuestion(Long id, Long currentUserId);
}