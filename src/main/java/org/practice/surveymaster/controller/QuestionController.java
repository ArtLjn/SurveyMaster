package org.practice.surveymaster.controller;

import org.practice.surveymaster.annotation.LogBusiness;
import org.practice.surveymaster.dto.AddQuestion;
import org.practice.surveymaster.model.Question;
import org.practice.surveymaster.service.QuestionService;
import org.practice.surveymaster.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 问题管理控制器
 * 提供问题的增删改查功能
 *
 * @author ljn
 * @since 2025/9/24
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {
    
    private final QuestionService questionService;
    
    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
    
    /**
     * 添加问题
     *
     * @param addQuestion 添加问题请求
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @LogBusiness("添加问题")
    public ApiResponse<Question> addQuestion(@Valid @RequestBody AddQuestion addQuestion,
                                           HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        // 调用服务层添加问题
        Question question = questionService.addQuestion(addQuestion, currentUserId);
        
        return ApiResponse.success(question);
    }
    
    /**
     * 根据ID获取问题详情
     *
     * @param id 问题ID
     * @return 问题详情
     */
    @GetMapping("/{id}")
    @LogBusiness("获取问题详情")
    public ApiResponse<Question> getQuestion(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return ApiResponse.success(question);
    }
    
    /**
     * 根据问卷ID获取问题列表
     *
     * @param surveyId 问卷ID
     * @return 问题列表
     */
    @GetMapping("/survey/{surveyId}")
    @LogBusiness("获取问卷问题列表")
    public ApiResponse<List<Question>> getQuestionsBySurvey(@PathVariable Long surveyId) {
        List<Question> questions = questionService.getQuestionsBySurveyId(surveyId);
        return ApiResponse.success(questions);
    }
    
    /**
     * 更新问题
     *
     * @param question 问题对象
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 更新结果
     */
    @PutMapping("/update")
    @LogBusiness("更新问题")
    public ApiResponse<Void> updateQuestion(@Valid @RequestBody Question question,
                                          HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        // 调用服务层更新问题
        questionService.updateQuestion(question, currentUserId);
        
        return ApiResponse.success();
    }
    
    /**
     * 删除问题
     *
     * @param id 问题ID
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @LogBusiness("删除问题")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id,
                                          HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        // 调用服务层删除问题
        questionService.deleteQuestion(id, currentUserId);
        
        return ApiResponse.success();
    }
}