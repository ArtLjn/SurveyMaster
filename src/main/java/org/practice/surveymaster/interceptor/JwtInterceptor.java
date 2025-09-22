package org.practice.surveymaster.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.practice.surveymaster.config.JwtConfig;
import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.util.JwtUtil;
import org.practice.surveymaster.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT拦截器
 * 验证请求中的JWT token
 *
 * @author ljn
 * @since 2025/9/22
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public JwtInterceptor(JwtUtil jwtUtil, JwtConfig jwtConfig, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = getTokenFromRequest(request);
        
        if (!StringUtils.hasText(token)) {
            return handleUnauthorized(response, "缺少认证token");
        }

        try {
            // 验证token是否为有效的访问令牌
            if (!jwtUtil.validateAccessToken(token)) {
                return handleUnauthorized(response, "无效的访问令牌");
            }

            // 从token中获取用户信息并设置到请求属性中
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            request.setAttribute("currentUsername", username);
            request.setAttribute("currentUserId", userId);
            
            return true;
        } catch (Exception e) {
            return handleUnauthorized(response, "token验证失败：" + e.getMessage());
        }
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getTokenHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getTokenPrefix())) {
            return bearerToken.substring(jwtConfig.getTokenPrefix().length());
        }
        return null;
    }

    /**
     * 处理未授权的请求
     */
    private boolean handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        ApiResponse<Object> apiResponse = ApiResponse.error(ErrorCode.UNAUTHORIZED, message);
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        
        response.getWriter().write(jsonResponse);
        return false;
    }
}