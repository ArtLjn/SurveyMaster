package org.practice.surveymaster.service;

import org.practice.surveymaster.dto.CreateSurvey;
import org.practice.surveymaster.dto.UpdateSurveyStatus;
import org.practice.surveymaster.model.Survey;

import java.util.List;

/**
 * <p>
 *  问卷服务接口
 * </p>
 *
 * <p>
 * [可以在这里补充更详细的说明，例如设计思路、关键算法、使用示例或注意事项。]
 * </p>
 *
 * @author ljn
 * @since 2025/9/23 上午10:20
 */

public interface SurveyService {
    /**
     * 创建问卷
     *
     * @param createSurvey 问卷基本信息
     */
    void CreateSurvey(CreateSurvey createSurvey);

    /**
     * 更新问卷状态
     * @param updateSurveyStatus 更新问卷状态参数
     */
    void ChangeSurveyStatus(UpdateSurveyStatus updateSurveyStatus);

    /**
     * 查询用户问卷列表
     * @param userId 用户id
     * @return 问卷列表
     */
    List<Survey> QuerySurveyListByUserId(Long userId);

    /**
     * 查询热门问卷列表
     * @return 问卷列表
     */
    List<Survey> QueryHotSurveyList();

    /**
     * 搜索问卷列表 按标题/描述/标签搜索
     * @param keyword 关键字
     * @return 问卷列表
     */
    List<Survey> SearchSurveyList(String keyword);
}
