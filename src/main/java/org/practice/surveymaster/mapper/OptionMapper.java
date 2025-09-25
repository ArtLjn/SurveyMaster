package org.practice.surveymaster.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.practice.surveymaster.model.OptionTable;

import java.util.List;

/**
 * 选项数据访问层接口
 * 提供选项的CRUD操作方法
 *
 * @author ljn
 * @since 2025/9/25
 */
@Mapper
public interface OptionMapper {
    
    /**
     * 插入选项
     * 
     * @param option 选项对象
     * @return 影响的行数
     */
    int insert(OptionTable option);
    
    /**
     * 根据ID查询选项
     * 
     * @param id 选项ID
     * @return 选项对象
     */
    OptionTable selectById(Long id);
    
    /**
     * 根据问题ID查询所有选项
     * 
     * @param questionId 问题ID
     * @return 选项列表
     */
    List<OptionTable> selectByQuestionId(Long questionId);
    
    /**
     * 批量插入选项
     * 
     * @param options 选项列表
     * @return 影响的行数
     */
    int batchInsert(List<OptionTable> options);
    
    /**
     * 更新选项
     * 
     * @param option 选项对象
     * @return 影响的行数
     */
    int update(OptionTable option);
    
    /**
     * 根据ID删除选项
     * 
     * @param id 选项ID
     * @return 影响的行数
     */
    int deleteById(Long id);
    
    /**
     * 根据问题ID删除所有选项
     * 
     * @param questionId 问题ID
     * @return 影响的行数
     */
    int deleteByQuestionId(Long questionId);
}