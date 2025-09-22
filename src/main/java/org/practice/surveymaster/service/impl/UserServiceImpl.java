package org.practice.surveymaster.service.impl;

import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.dto.UserRegister;
import org.practice.surveymaster.exception.BusinessException;
import org.practice.surveymaster.mapper.UserMapper;
import org.practice.surveymaster.model.User;
import org.practice.surveymaster.service.UserService;
import org.practice.surveymaster.util.MD5Util;
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

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
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
    public User login(String username, String password) {
        // 1. 根据用户名查找用户
        // 2. TODO 密码加密前端操作
        User user = userMapper.selectByUsernameAndPassword(username,MD5Util.md5(password));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }
        // 3. 返回用户信息
        return user;
    }

    @Override
    public User findById(Long id) {
        // TODO: 实现根据ID查找用户逻辑
        return null;
    }
}