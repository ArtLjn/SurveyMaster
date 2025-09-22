package org.practice.surveymaster.config;

import org.practice.surveymaster.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 配置拦截器等Web相关组件
 *
 * @author ljn
 * @since 2025/9/22
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Autowired
    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")                    // 拦截所有API路径
                .excludePathPatterns(
                        "/api/user/login",                     // 排除登录接口
                        "/api/user/register",                  // 排除注册接口
                        "/api/user/refresh-token",            // 排除刷新token接口
                        "/api/health/**",                     // 排除健康检查接口
                        "/api/public/**"                      // 排除公共接口
                );
    }
}