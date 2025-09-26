package org.practice.surveymaster.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.practice.surveymaster.model.Question;

import java.util.List;

/**
 * 问题数据访问层
 * 提供问题相关的数据库操作
 *
 * @author ljn
 * @since 2025/9/24
 */

@Mapper
public interface QuestionMapper {
    
    /**
     * 插入问题
     * 
     * @param question 问题对象
     * @return 影响行数
     */
    int insert(Question question);
    
    /**
     * 根据ID查询问题
     * 
     * @param id 问题ID
     * @return 问题对象
     */
    Question selectById(@Param("id") Long id);
    
    /**
     * 根据问卷ID查询所有问题
     * 
     * @param surveyId 问卷ID
     * @return 问题列表
     */
    List<Question> selectBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 更新问题
     * 
     * @param question 问题对象
     * @return 影响行数
     */
    int update(Question question);
    
    /**
     * 根据ID删除问题
     * 
     * @param id 问题ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据问卷ID删除所有问题
     * 
     * @param surveyId 问卷ID
     * @return 影响行数
     */
    int deleteBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 根据问卷ID查询所有问题ID
     * @param surveyId 问卷ID
     * @return 问题ID列表
     */
    List<Long> getQuestionIdsBySurveyId(@Param("surveyId") Long surveyId);
}
