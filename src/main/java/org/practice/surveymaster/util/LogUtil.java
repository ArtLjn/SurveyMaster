package org.practice.surveymaster.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 统一日志工具类
 * 提供标准化的日志记录方法
 */
public class LogUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 获取Logger实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 获取Logger实例
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    /**
     * 设置请求追踪ID
     */
    public static void setTraceId() {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put("traceId", traceId);
    }

    /**
     * 设置请求追踪ID
     */
    public static void setTraceId(String traceId) {
        MDC.put("traceId", traceId);
    }

    /**
     * 获取请求追踪ID
     */
    public static String getTraceId() {
        return MDC.get("traceId");
    }

    /**
     * 清除请求追踪ID
     */
    public static void clearTraceId() {
        MDC.clear();
    }

    /**
     * 记录业务日志
     */
    public static void logBusiness(Logger logger, String operation, Object request, Object response) {
        try {
            String requestStr = serializeSafely(request);
            String responseStr = serializeSafely(response);
            
            String logMessage = String.format(
                "[业务日志] 操作: %s, 请求: %s, 响应: %s",
                operation, requestStr, responseStr
            );
            logger.info(logMessage);
        } catch (Exception e) {
            logger.error("[业务日志] 操作: {} - 记录日志失败: {}", operation, e.getMessage());
        }
    }

    /**
     * 记录接口访问日志
     */
    public static void logAccess(Logger logger, String method, String uri, String ip, 
                               Object params, Object result, long duration) {
        try {
            String paramsStr = serializeSafely(params);
            String resultStr = serializeSafely(result);
            
            String logMessage = String.format(
                "[接口访问] %s %s, IP: %s, 参数: %s, 耗时: %dms, 结果: %s",
                method, uri, ip, paramsStr, duration, resultStr
            );
            logger.info(logMessage);
        } catch (Exception e) {
            logger.error("[接口访问] {} {}, IP: {}, 耗时: {}ms - 记录日志失败: {}", 
                       method, uri, ip, duration, e.getMessage());
        }
    }

    /**
     * 记录异常日志（包含堆栈信息）
     */
    public static void logExceptionWithStackTrace(Logger logger, String operation, Throwable throwable, Object... params) {
        try {
            String paramsStr = serializeSafely(params.length > 0 ? params : "无");
            String logMessage = String.format(
                "[异常日志] 操作: %s, 参数: %s, 异常类型: %s, 异常消息: %s",
                operation, paramsStr, throwable.getClass().getSimpleName(), throwable.getMessage()
            );
            logger.error(logMessage, throwable);
        } catch (Exception e) {
            logger.error("[异常日志] 操作: {} - 记录日志失败: {}", operation, e.getMessage());
        }
    }

    /**
     * 记录异常日志（不包含堆栈信息）
     */
    public static void logException(Logger logger, String operation, Throwable throwable, Object... params) {
        try {
            String paramsStr = serializeSafely(params.length > 0 ? params : "无");
            String logMessage = String.format(
                "[异常日志] 操作: %s, 参数: %s, 异常类型: %s, 异常消息: %s",
                operation, paramsStr, throwable.getClass().getSimpleName(), throwable.getMessage()
            );
            logger.error(logMessage);
        } catch (Exception e) {
            logger.error("[异常日志] 操作: {} - 记录日志失败: {}", operation, e.getMessage());
        }
    }

    /**
     * 记录性能日志
     */
    public static void logPerformance(Logger logger, String operation, long duration, Object... params) {
        try {
            String paramsStr = serializeSafely(params.length > 0 ? params : "无");
            String logMessage = String.format(
                "[性能日志] 操作: %s, 耗时: %dms, 参数: %s",
                operation, duration, paramsStr
            );
            logger.info(logMessage);
        } catch (Exception e) {
            logger.error("[性能日志] 操作: {} - 记录日志失败: {}", operation, e.getMessage());
        }
    }

    /**
     * 记录调试日志
     */
    public static void logDebug(Logger logger, String operation, Object... params) {
        if (logger.isDebugEnabled()) {
            try {
                String paramsStr = serializeSafely(params.length > 0 ? params : "无");
                String logMessage = String.format(
                    "[调试日志] 操作: %s, 参数: %s",
                    operation, paramsStr
                );
                logger.debug(logMessage);
            } catch (Exception e) {
                logger.error("[调试日志] 操作: {} - 记录日志失败: {}", operation, e.getMessage());
            }
        }
    }

    /**
     * 记录SQL日志
     */
    public static void logSql(Logger logger, String sql, Object... params) {
        try {
            String paramsStr = serializeSafely(params.length > 0 ? params : "无");
            String logMessage = String.format(
                "[SQL日志] SQL: %s, 参数: %s",
                sql, paramsStr
            );
            logger.debug(logMessage);
        } catch (Exception e) {
            logger.error("[SQL日志] SQL记录失败: {}", e.getMessage());
        }
    }

    /**
     * 记录用户操作日志
     */
    public static void logUserAction(Logger logger, String userId, String action, Object details) {
        try {
            String detailsStr = serializeSafely(details);
            String logMessage = String.format(
                "[用户操作] 用户ID: %s, 操作: %s, 详情: %s",
                userId, action, detailsStr
            );
            logger.info(logMessage);
        } catch (Exception e) {
            logger.error("[用户操作] 记录日志失败: {}", e.getMessage());
        }
    }

    /**
     * 记录安全日志
     */
    public static void logSecurity(Logger logger, String userId, String action, String ip, Object details) {
        try {
            String detailsStr = serializeSafely(details);
            String logMessage = String.format(
                "[安全日志] 用户ID: %s, 操作: %s, IP: %s, 详情: %s",
                userId, action, ip, detailsStr
            );
            logger.warn(logMessage);
        } catch (Exception e) {
            logger.error("[安全日志] 记录日志失败: {}", e.getMessage());
        }
    }

    /**
     * 安全序列化对象
     */
    private static String serializeSafely(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        try {
            // 避免序列化复杂对象
            if (isComplexObject(obj)) {
                return String.valueOf(obj);
            }
            
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[无法序列化: " + obj.getClass().getSimpleName() + "]";
        }
    }
    
    /**
     * 判断是否为复杂对象（避免序列化）
     */
    private static boolean isComplexObject(Object obj) {
        if (obj == null) return false;
        
        String className = obj.getClass().getName();
        return className.contains("HttpServletRequest") ||
               className.contains("HttpServletResponse") ||
               className.contains("HttpSession") ||
               className.contains("ServletContext") ||
               className.contains("Principal") ||
               className.contains("Session") ||
               className.contains("Request") ||
               className.contains("Response");
    }

    /**
     * 构建日志上下文
     */
    public static Map<String, Object> buildContext(String key, Object value) {
        Map<String, Object> context = new HashMap<>();
        context.put(key, value);
        return context;
    }

    /**
     * 构建日志上下文
     */
    public static Map<String, Object> buildContext(Map<String, Object> context) {
        return context;
    }
}