package org.practice.surveymaster.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.practice.surveymaster.model.User;

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
 * @since 2025/9/11 下午3:02
 */

@Mapper
public interface UserMapper {
    User selectById(@Param("id") Long id);
    
    int insert(User user);

    List<User> selectAll();
    
    User selectByUsername(@Param("username") String username);
    
    boolean existsByUsername(@Param("username") String username);
    
    boolean existsByEmail(@Param("email") String email);

    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
}