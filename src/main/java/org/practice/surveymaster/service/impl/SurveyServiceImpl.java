package org.practice.surveymaster.service.impl;

import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.dto.CreateSurvey;
import org.practice.surveymaster.dto.UpdateSurveyStatus;
import org.practice.surveymaster.exception.BusinessException;
import org.practice.surveymaster.mapper.SurveyMapper;
import org.practice.surveymaster.mapper.UserMapper;
import org.practice.surveymaster.model.Survey;
import org.practice.surveymaster.service.SurveyService;
import org.practice.surveymaster.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 问卷服务实现类
 * 提供问卷的创建、查询、状态管理等功能
 *
 * @author ljn
 * @since 2025/9/23 上午10:26
 */
@Service
public class SurveyServiceImpl implements SurveyService {
    private final SurveyMapper surveyMapper;

    @Autowired
    public SurveyServiceImpl(SurveyMapper surveyMapper, UserMapper userMapper) {
        this.surveyMapper = surveyMapper;
    }

    @Override
    public void CreateSurvey(CreateSurvey createSurvey) {
        // 创建问卷对象
        Survey survey = new Survey();
        
        // 复制基本属性
        BeanUtils.copyProperties(createSurvey, survey);
        
        // 设置创建时间
        survey.setCreatedAt(LocalDateTime.now());
        
        // 插入数据库
        int result = surveyMapper.insert(survey);

        if (result <= 0) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void ChangeSurveyStatus(UpdateSurveyStatus updateSurveyStatus) {
        // 检查问卷是否存在且属于当前用户
        Survey existingSurvey = surveyMapper.selectById(updateSurveyStatus.getId());
        AssertUtil.notNull(existingSurvey, ErrorCode.NOT_FOUND, "问卷不存在");
        AssertUtil.isTrue(existingSurvey.getUserId().equals(updateSurveyStatus.getUserId()),
                ErrorCode.UNAUTHORIZED, "无权操作此问卷");

        // 更新问卷状态
        int result = surveyMapper.updateStatus(
                updateSurveyStatus.getId(),
                updateSurveyStatus.getStatus(),
                updateSurveyStatus.getUserId()
        );
        
        if (result <= 0) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "更新问卷状态失败");
        }
    }


    @Override
    public List<Survey> QuerySurveyListByUserId(Long userId) {
        if (userId != null) {
            return surveyMapper.selectByUserId(userId);
        }

        return Collections.emptyList();
    }

    @Override
    public List<Survey> QueryHotSurveyList() {
        return Collections.emptyList();
    }

    @Override
    public List<Survey> SearchSurveyList(String keyword) {
        return Collections.emptyList();
    }
}
