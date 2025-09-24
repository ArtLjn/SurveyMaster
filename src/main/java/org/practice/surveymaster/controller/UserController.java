package org.practice.surveymaster.controller;

import org.practice.surveymaster.annotation.LogBusiness;
import org.practice.surveymaster.dto.UserLogin;
import org.practice.surveymaster.dto.UserRegister;
import org.practice.surveymaster.service.UserService;
import org.practice.surveymaster.vo.ApiResponse;
import org.practice.surveymaster.vo.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * [用户服务基础功能模块]
 * </p>
 *
 * <p>
 * [可以在这里补充更详细的说明，例如设计思路、关键算法、使用示例或注意事项。]
 * </p>
 *
 * @author ljn
 * @since 2025/9/16 上午10:25
 */

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @LogBusiness("用户登录")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody UserLogin userLogin) {
        LoginResponse loginResponse = userService.login(userLogin.getUsername(), userLogin.getPassword());
        return ApiResponse.success("登录成功", loginResponse);
    }

    @PostMapping("/register")
    @LogBusiness("用户注册")
    public ApiResponse<String> register(@Valid @RequestBody UserRegister userRegister) {
        userService.register(userRegister);
        return ApiResponse.success("注册成功");
    }
    
    @PostMapping("/refresh-token")
    @LogBusiness("刷新令牌")
    public ApiResponse<LoginResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        LoginResponse loginResponse = userService.refreshAccessToken(refreshToken);
        return ApiResponse.success("令牌刷新成功", loginResponse);
    }
}
