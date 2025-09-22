package org.practice.surveymaster.service;

import org.practice.surveymaster.dto.UserRegister;
import org.practice.surveymaster.model.User;
import org.practice.surveymaster.vo.LoginResponse;

/**
 * 用户服务接口
 * 提供用户相关的业务功能
 *
 * @author ljn
 * @since 2025/9/11 下午4:34
 */

public interface UserService {
    /**
     * 用户注册
     *
     * @param userRegister 注册信息
     */
    void register(UserRegister userRegister);
    
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录响应信息（包含用户信息和token）
     */
    LoginResponse login(String username, String password);
    
    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(Long id);
    
    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的登录响应信息
     */
    LoginResponse refreshAccessToken(String refreshToken);
}
