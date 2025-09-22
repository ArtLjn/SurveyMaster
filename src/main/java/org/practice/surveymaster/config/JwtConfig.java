package org.practice.surveymaster.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 * 配置JWT相关参数
 *
 * @author ljn
 * @since 2025/9/22
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT密钥
     */
    private String secret = "surveymaster-jwt-secret-key-for-authentication-very-secure";

    /**
     * 访问令牌过期时间（毫秒）- 默认2小时
     */
    private Long accessTokenExpiration = 7200000L;

    /**
     * 刷新令牌过期时间（毫秒）- 默认7天
     */
    private Long refreshTokenExpiration = 604800000L;

    /**
     * token请求头名称
     */
    private String tokenHeader = "Authorization";

    /**
     * token前缀
     */
    private String tokenPrefix = "Bearer ";

}