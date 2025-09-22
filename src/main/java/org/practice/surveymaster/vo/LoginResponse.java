package org.practice.surveymaster.vo;

import lombok.Data;
import org.practice.surveymaster.model.User;

/**
 * 登录响应VO
 * 包含用户信息和JWT token信息
 *
 * @author ljn
 * @since 2025/9/16 下午4:20
 */

@Data
public class LoginResponse {
    /**
     * 用户信息
     */
    private User user;
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 访问令牌类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 访问令牌过期时间（毫秒时间戳）
     */
    private Long expiresIn;
    
    public LoginResponse() {}
    
    public LoginResponse(User user, String accessToken, String refreshToken, Long expiresIn) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }
}

