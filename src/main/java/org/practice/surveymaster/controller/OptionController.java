package org.practice.surveymaster.controller;

import org.practice.surveymaster.annotation.LogBusiness;
import org.practice.surveymaster.dto.AddOption;
import org.practice.surveymaster.model.OptionTable;
import org.practice.surveymaster.service.OptionService;
import org.practice.surveymaster.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 选项管理控制器
 * 提供选项的增删改查功能
 *
 * @author ljn
 * @since 2025/9/25
 */
@RestController
@RequestMapping("/api/option")
public class OptionController {
    
    private final OptionService optionService;
    
    @Autowired
    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }
    
    /**
     * 添加选项
     *
     * @param addOption 添加选项请求
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @LogBusiness("添加选项")
    public ApiResponse<OptionTable> addOption(@Valid @RequestBody AddOption addOption,
                                            HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        OptionTable option = optionService.addOption(addOption, currentUserId);
        return ApiResponse.success(option);
    }
    
    /**
     * 批量添加选项
     *
     * @param questionId 问题ID
     * @param optionContents 选项内容列表
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 添加结果
     */
    @PostMapping("/batch-add/{questionId}")
    @LogBusiness("批量添加选项")
    public ApiResponse<List<OptionTable>> addOptions(@PathVariable Long questionId,
                                                    @RequestBody List<String> optionContents,
                                                    HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        List<OptionTable> options = optionService.addOptions(questionId, optionContents, currentUserId);
        return ApiResponse.success(options);
    }
    
    /**
     * 根据ID获取选项
     *
     * @param id 选项ID
     * @return 选项信息
     */
    @GetMapping("/{id}")
    @LogBusiness("获取选项详情")
    public ApiResponse<OptionTable> getOption(@PathVariable Long id) {
        OptionTable option = optionService.getOptionById(id);
        return ApiResponse.success(option);
    }
    
    /**
     * 根据问题ID获取所有选项
     *
     * @param questionId 问题ID
     * @return 选项列表
     */
    @GetMapping("/question/{questionId}")
    @LogBusiness("获取问题选项列表")
    public ApiResponse<List<OptionTable>> getOptionsByQuestionId(@PathVariable Long questionId) {
        List<OptionTable> options = optionService.getOptionsByQuestionId(questionId);
        return ApiResponse.success(options);
    }
    
    /**
     * 更新选项
     *
     * @param id 选项ID
     * @param option 选项更新信息
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @LogBusiness("更新选项")
    public ApiResponse<String> updateOption(@PathVariable Long id,
                                          @RequestBody OptionTable option,
                                          HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        // 设置选项ID
        option.setId(id);
        optionService.updateOption(option, currentUserId);
        
        return ApiResponse.success("选项更新成功");
    }
    
    /**
     * 删除选项
     *
     * @param id 选项ID
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @LogBusiness("删除选项")
    public ApiResponse<String> deleteOption(@PathVariable Long id,
                                          HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        optionService.deleteOption(id, currentUserId);
        return ApiResponse.success("选项删除成功");
    }
    
    /**
     * 删除问题的所有选项
     *
     * @param questionId 问题ID
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 删除结果
     */
    @DeleteMapping("/question/{questionId}")
    @LogBusiness("删除问题所有选项")
    public ApiResponse<String> deleteOptionsByQuestionId(@PathVariable Long questionId,
                                                        HttpServletRequest request) {
        // 从JWT拦截器中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        optionService.deleteOptionsByQuestionId(questionId, currentUserId);
        return ApiResponse.success("问题选项删除成功");
    }
}