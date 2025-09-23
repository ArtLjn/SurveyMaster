package org.practice.surveymaster.controller;

import org.practice.surveymaster.annotation.LogBusiness;
import org.practice.surveymaster.dto.CreateSurvey;
import org.practice.surveymaster.dto.UpdateSurveyStatus;
import org.practice.surveymaster.service.SurveyService;
import org.practice.surveymaster.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 问卷管理控制器
 * 提供问卷创建、查询、状态管理等功能
 *
 * @author ljn
 * @since 2025/9/23 上午10:31
 */

@RestController
@RequestMapping("/api/survey")
public class SurveyController {
    
    private final SurveyService surveyService;
    
    @Autowired
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }
    
    /**
     * 创建问卷
     *
     * @param createSurvey 问卷创建请求
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 创建结果
     */
    @PostMapping("/createSurvey")
    @LogBusiness("创建问卷")
    public ApiResponse<Void> createSurvey(@Valid @RequestBody CreateSurvey createSurvey, 
                                          HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");

        // 设置创建者ID
        createSurvey.setUserId(currentUserId);
        
        // 调用服务层创建问卷
        surveyService.CreateSurvey(createSurvey);
        
        return ApiResponse.success();
    }


    @GetMapping("/querySurveyListByUser")
    @LogBusiness("查询用户问卷列表")
    public ApiResponse<Object> querySurveyListByUser(HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");

        // 调用服务层查询用户问卷列表
        return ApiResponse.success(surveyService.QuerySurveyListByUserId(currentUserId));
    }


    /**
     * 更新问卷状态
     *
     * @param updateSurveyStatus 更新问卷状态请求
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 更新结果
     */
    /**
     * 更新问卷状态
     *
     * @param updateSurveyStatus 更新状态请求
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 更新结果
     */
    @PutMapping("/updateSurvey")
    @LogBusiness("更新问卷")
    public ApiResponse<Void> updateSurvey(@Valid @RequestBody UpdateSurveyStatus updateSurveyStatus,
                                          HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");

        // 设置创建者ID（保持Long类型，避免类型转换异常）
        updateSurveyStatus.setUserId(currentUserId);

        // 调用服务层更新问卷
        surveyService.ChangeSurveyStatus(updateSurveyStatus);

        return ApiResponse.success();
    }
}
