package org.practice.surveymaster.service;

import org.practice.surveymaster.dto.CreateSurvey;
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
     * 发布/关闭问卷
     * @param id 问卷id
     * @param status 问卷状态 1-发布 2-关闭
     */
    void ChangeSurveyStatus(Long id, int status);

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
