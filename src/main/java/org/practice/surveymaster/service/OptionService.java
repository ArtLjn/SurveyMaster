package org.practice.surveymaster.service;

import org.practice.surveymaster.dto.AddOption;
import org.practice.surveymaster.model.OptionTable;

import java.util.List;

/**
 * 选项服务接口
 * 提供选项管理的业务逻辑
 *
 * @author ljn
 * @since 2025/9/25
 */
public interface OptionService {
    
    /**
     * 添加选项
     * 
     * @param addOption 添加选项请求
     * @param currentUserId 当前用户ID（用于权限验证）
     * @return 创建的选项对象
     */
    OptionTable addOption(AddOption addOption, Long currentUserId);
    
    /**
     * 批量添加选项
     * 
     * @param questionId 问题ID
     * @param optionContents 选项内容列表
     * @param currentUserId 当前用户ID（用于权限验证）
     * @return 创建的选项列表
     */
    List<OptionTable> addOptions(Long questionId, List<String> optionContents, Long currentUserId);
    
    /**
     * 根据ID获取选项
     * 
     * @param id 选项ID
     * @return 选项对象
     */
    OptionTable getOptionById(Long id);
    
    /**
     * 根据问题ID获取所有选项
     * 
     * @param questionId 问题ID
     * @return 选项列表
     */
    List<OptionTable> getOptionsByQuestionId(Long questionId);
    
    /**
     * 更新选项
     * 
     * @param option 选项对象
     * @param currentUserId 当前用户ID（用于权限验证）
     */
    void updateOption(OptionTable option, Long currentUserId);
    
    /**
     * 删除选项
     * 
     * @param id 选项ID
     * @param currentUserId 当前用户ID（用于权限验证）
     */
    void deleteOption(Long id, Long currentUserId);
    
    /**
     * 删除问题的所有选项
     * 
     * @param questionId 问题ID
     * @param currentUserId 当前用户ID（用于权限验证）
     */
    void deleteOptionsByQuestionId(Long questionId, Long currentUserId);
}