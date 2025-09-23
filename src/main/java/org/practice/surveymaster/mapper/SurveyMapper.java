package org.practice.surveymaster.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.practice.surveymaster.model.Survey;

import java.util.List;

/**
 * <p>
 * [在此处用一两句话描述该类的核心职责和目的。]
 * </p>
 *
 * <p>
 * [可以在这里补充更详细的说明，例如设计思路、关键算法、使用示例或注意事项。]
 * </p>
 *
 * @author ljn
 * @since 2025/9/23 上午9:57
 */

@Mapper
public interface SurveyMapper {
    // 创建问卷
    int insert(Survey survey);
    // 查询问卷
    Survey selectById(@Param("id") long id);
    // 查询用户的所有问卷
    List<Survey> selectByUserId(@Param("userId") long userId);
    // 更新问卷
    int update(Survey survey);
    // 删除问卷
    int deleteById(@Param("id") long id);
}
