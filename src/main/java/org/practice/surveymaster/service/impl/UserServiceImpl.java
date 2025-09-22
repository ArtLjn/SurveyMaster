package org.practice.surveymaster.service.impl;

import org.practice.surveymaster.config.JwtConfig;
import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.dto.UserRegister;
import org.practice.surveymaster.exception.BusinessException;
import org.practice.surveymaster.mapper.UserMapper;
import org.practice.surveymaster.model.User;
import org.practice.surveymaster.service.UserService;
import org.practice.surveymaster.util.JwtUtil;
import org.practice.surveymaster.util.MD5Util;
import org.practice.surveymaster.vo.LoginResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil, JwtConfig jwtConfig) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public void register(UserRegister userRegister) {
        // 1. 检查用户名是否已存在
        if (userMapper.existsByUsername(userRegister.getUsername()))  {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        } // 检查邮箱是否存在
        else if (userMapper.existsByEmail(userRegister.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        // 2. 对密码进行加密
        userRegister.setPassword(MD5Util.md5(userRegister.getPassword()));
        User user = new User();
        BeanUtils.copyProperties(userRegister, user);
        user.setCreatedAt(LocalDateTime.now());
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public LoginResponse login(String username, String password) {
        // 1. 根据用户名查找用户
        // 2. TODO 密码加密前端操作
        User user = userMapper.selectByUsernameAndPassword(username, MD5Util.md5(password));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }
        
        // 3. 生成JWT token
        String accessToken = jwtUtil.generateAccessToken((long) user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken((long) user.getId(), user.getUsername());
        
        // 4. 返回登录响应信息
        return new LoginResponse(user, accessToken, refreshToken, jwtConfig.getAccessTokenExpiration());
    }

    @Override
    public User findById(Long id) {
        // TODO: 实现根据ID查找用户逻辑
        return null;
    }
    
    @Override
    public LoginResponse refreshAccessToken(String refreshToken) {
        // 1. 验证刷新令牌的有效性
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
        
        try {
            // 2. 从刷新令牌中获取用户信息
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            
            // 3. 生成新的访问令牌和刷新令牌
            String newAccessToken = jwtUtil.generateAccessToken(userId, username);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);
            
            // 4. 查找用户信息
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                // 如果查不到用户，创建一个简单的用户对象用于响应
                user = new User();
                user.setId(userId.intValue());
                user.setUsername(username);
            }
            
            // 5. 返回新的登录响应
            return new LoginResponse(user, newAccessToken, newRefreshToken, jwtConfig.getAccessTokenExpiration());
            
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
    }
}