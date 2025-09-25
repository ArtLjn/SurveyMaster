package org.practice.surveymaster.service.impl;

import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.dto.AddOption;
import org.practice.surveymaster.mapper.OptionMapper;
import org.practice.surveymaster.mapper.QuestionMapper;
import org.practice.surveymaster.mapper.SurveyMapper;
import org.practice.surveymaster.model.OptionTable;
import org.practice.surveymaster.model.Question;
import org.practice.surveymaster.model.Survey;
import org.practice.surveymaster.service.OptionService;
import org.practice.surveymaster.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 选项服务实现类
 * 提供选项管理的业务逻辑实现
 *
 * @author ljn
 * @since 2025/9/25
 */
@Service
public class OptionServiceImpl implements OptionService {
    
    private final OptionMapper optionMapper;
    private final QuestionMapper questionMapper;
    private final SurveyMapper surveyMapper;
    
    @Autowired
    public OptionServiceImpl(OptionMapper optionMapper, QuestionMapper questionMapper, SurveyMapper surveyMapper) {
        this.optionMapper = optionMapper;
        this.questionMapper = questionMapper;
        this.surveyMapper = surveyMapper;
    }
    
    @Override
    public OptionTable addOption(AddOption addOption, Long currentUserId) {
        AssertUtil.notNull(currentUserId, ErrorCode.AUTH_FAILURE, "用户未登录");
        
        questionF(addOption.getQuestionId(),currentUserId);
        
        // 创建选项对象
        OptionTable option = new OptionTable();
        BeanUtils.copyProperties(addOption, option);
        
        // 插入选项
        int result = optionMapper.insert(option);
        AssertUtil.isTrue(result > 0, ErrorCode.OPERATION_FAILED, "添加选项失败");
        
        return option;
    }
    
    @Override
    public List<OptionTable> addOptions(Long questionId, List<String> optionContents, Long currentUserId) {
        AssertUtil.notNull(currentUserId, ErrorCode.AUTH_FAILURE, "用户未登录");
        AssertUtil.notNull(questionId, ErrorCode.BAD_REQUEST, "问题ID不能为空");
        AssertUtil.notEmpty(optionContents, ErrorCode.BAD_REQUEST, "选项内容不能为空");
        
        questionF(questionId,currentUserId);
        
        // 创建选项列表
        List<OptionTable> options = new ArrayList<>();
        for (String content : optionContents) {
            AssertUtil.notBlank(content, ErrorCode.BAD_REQUEST, "选项内容不能为空");
            
            OptionTable option = new OptionTable();
            option.setQuestionId(questionId);
            option.setContent(content.trim());
            options.add(option);
        }
        
        // 批量插入选项
        if (!options.isEmpty()) {
            int result = optionMapper.batchInsert(options);
            AssertUtil.isTrue(result > 0, ErrorCode.OPERATION_FAILED, "批量添加选项失败");
        }
        
        return options;
    }
    
    @Override
    public OptionTable getOptionById(Long id) {
        AssertUtil.notNull(id, ErrorCode.BAD_REQUEST, "选项ID不能为空");
        
        OptionTable option = optionMapper.selectById(id);
        AssertUtil.notNull(option, ErrorCode.NOT_FOUND, "选项不存在");
        
        return option;
    }
    
    @Override
    public List<OptionTable> getOptionsByQuestionId(Long questionId) {
        AssertUtil.notNull(questionId, ErrorCode.BAD_REQUEST, "问题ID不能为空");
        
        // 验证问题是否存在
        Question question = questionMapper.selectById(questionId);
        AssertUtil.notNull(question, ErrorCode.NOT_FOUND, "问题不存在");
        
        return optionMapper.selectByQuestionId(questionId);
    }
    
    @Override
    public void updateOption(OptionTable option, Long currentUserId) {
        AssertUtil.notNull(option, ErrorCode.BAD_REQUEST, "选项对象不能为空");
        AssertUtil.notNull(option.getId(), ErrorCode.BAD_REQUEST, "选项ID不能为空");
        AssertUtil.notNull(currentUserId, ErrorCode.AUTH_FAILURE, "用户未登录");
        
        // 验证选项是否存在
        OptionTable existingOption = optionMapper.selectById(option.getId());
        AssertUtil.notNull(existingOption, ErrorCode.NOT_FOUND, "选项不存在");
        
        // 验证用户权限
        questionF(option.getQuestionId(),currentUserId);
        
        // 更新选项
        int result = optionMapper.update(option);
        AssertUtil.isTrue(result > 0, ErrorCode.OPERATION_FAILED, "更新选项失败");
    }
    
    @Override
    public void deleteOption(Long id, Long currentUserId) {
        AssertUtil.notNull(id, ErrorCode.BAD_REQUEST, "选项ID不能为空");
        AssertUtil.notNull(currentUserId, ErrorCode.AUTH_FAILURE, "用户未登录");
        
        // 验证选项是否存在
        OptionTable option = optionMapper.selectById(id);
        AssertUtil.notNull(option, ErrorCode.NOT_FOUND, "选项不存在");

        questionF(option.getQuestionId(),currentUserId);

        // 删除选项
        int result = optionMapper.deleteById(id);
        AssertUtil.isTrue(result > 0, ErrorCode.OPERATION_FAILED, "删除选项失败");
    }



    @Override
    public void deleteOptionsByQuestionId(Long questionId, Long currentUserId) {
        AssertUtil.notNull(questionId, ErrorCode.BAD_REQUEST, "问题ID不能为空");
        AssertUtil.notNull(currentUserId, ErrorCode.AUTH_FAILURE, "用户未登录");

        questionF(questionId,currentUserId);
        
        // 删除问题的所有选项
        optionMapper.deleteByQuestionId(questionId);
    }

    /**
     * 验证问题是否存在/验证用户权限
     * @param questionId 问题ID
     * @param currentUserId 当前用户ID
     */
    private void questionF(Long questionId,Long currentUserId) {
        // 验证问题是否存在
        Question question = questionMapper.selectById(questionId);
        AssertUtil.notNull(question, ErrorCode.NOT_FOUND, "问题不存在");

        // 验证用户权限
        Survey survey = surveyMapper.selectById(question.getSurveyId());
        AssertUtil.notNull(survey, ErrorCode.NOT_FOUND, "问卷不存在");
        AssertUtil.isTrue(survey.getUserId().equals(currentUserId), ErrorCode.PERMISSION_DENIED, "无权限操作该问卷");
    }
}