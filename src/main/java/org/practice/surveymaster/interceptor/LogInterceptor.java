package org.practice.surveymaster.interceptor;

import org.practice.surveymaster.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一日志拦截器
 * 记录所有接口的访问日志
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
    
    private static final String START_TIME_ATTR = "requestStartTime";
    private static final ThreadLocal<Map<String, Object>> requestContext = ThreadLocal.withInitial(ConcurrentHashMap::new);

    /**
     * 请求开始处理前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置请求追踪ID
        LogUtil.setTraceId();
        
        // 记录请求开始时间
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        
        // 记录请求上下文信息
        Map<String, Object> context = requestContext.get();
        context.put("uri", request.getRequestURI());
        context.put("method", request.getMethod());
        context.put("ip", getClientIpAddress(request));
        context.put("userAgent", request.getHeader("User-Agent"));
        context.put("params", request.getParameterMap());
        
        // 记录请求开始日志
        LogUtil.logDebug(logger, "请求开始", context);
        
        return true;
    }

    /**
     * 请求处理完成后
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 可以在这里记录一些处理后的信息
    }

    /**
     * 请求完全结束后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            // 获取请求开始时间
            Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
            if (startTime == null) {
                startTime = System.currentTimeMillis();
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            // 获取请求信息
            String uri = request.getRequestURI();
            String method = request.getMethod();
            String ip = getClientIpAddress(request);
            Map<String, String[]> params = request.getParameterMap();
            
            // 构建结果信息
            Map<String, Object> result = new ConcurrentHashMap<>();
            int status = response.getStatus();
            result.put("status", status);
            if (ex != null) {
                result.put("error", ex.getMessage());
            }
            
            // 优化：不记录404等正常错误
            if (shouldLogRequest(status, ex)) {
                // 记录访问日志
                LogUtil.logAccess(logger, method, uri, ip, params, result, duration);
                
                // 如果有异常，记录异常日志
                if (ex != null && status != 404) {
                    LogUtil.logException(logger, uri + " 请求异常", ex, params);
                }
            }
            
            // 清理上下文
            requestContext.remove();
            LogUtil.clearTraceId();
            
        } catch (Exception e) {
            logger.error("记录访问日志失败", e);
        }
    }
    
    private boolean shouldLogRequest(int status, Exception ex) {
        // 不记录静态资源和404错误
        return status != 404 || (ex != null);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}