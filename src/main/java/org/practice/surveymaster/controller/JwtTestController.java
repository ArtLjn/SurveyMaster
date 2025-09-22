package org.practice.surveymaster.controller;

import org.practice.surveymaster.annotation.LogBusiness;
import org.practice.surveymaster.vo.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * JWT测试控制器
 * 用于验证JWT认证功能
 *
 * @author ljn
 * @since 2025/9/22
 */
@RestController
@RequestMapping("/api/jwt")
public class JwtTestController {

    @GetMapping("/protected")
    @LogBusiness("访问受保护资源")
    public ApiResponse<String> protectedEndpoint(HttpServletRequest request) {
        // 从请求属性中获取当前用户信息（由JWT拦截器设置）
        String currentUsername = (String) request.getAttribute("currentUsername");
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        String message = String.format("欢迎，%s (用户ID: %d)！这是一个受JWT保护的资源。", 
                                      currentUsername, currentUserId);
        
        return ApiResponse.success("访问成功", message);
    }

    @GetMapping("/user-info")
    @LogBusiness("获取当前用户信息")
    public ApiResponse<Object> getCurrentUserInfo(HttpServletRequest request) {
        String currentUsername = (String) request.getAttribute("currentUsername");
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        Object userInfo = new Object() {
            public final String username = currentUsername;
            public final Long userId = currentUserId;
            public final String message = "通过JWT认证获取的用户信息";
        };
        
        return ApiResponse.success("获取用户信息成功", userInfo);
    }
}