package org.practice.surveymaster.service.impl;

import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.dto.CreateSurvey;
import org.practice.surveymaster.exception.BusinessException;
import org.practice.surveymaster.mapper.SurveyMapper;
import org.practice.surveymaster.model.Survey;
import org.practice.surveymaster.service.SurveyService;
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
    public SurveyServiceImpl(SurveyMapper surveyMapper) {
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
    public void ChangeSurveyStatus(Long id, int status) {

    }

    @Override
    public List<Survey> QuerySurveyListByUserId(Long userId) {
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
