package org.practice.surveymaster.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.practice.surveymaster.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 日志切面
 * 记录方法调用日志
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
//    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 定义切点：所有Controller、Service、Mapper方法
     */
    @Pointcut("within(@org.springframework.stereotype.Controller *) || " +
              "within(@org.springframework.web.bind.annotation.RestController *) || " +
              "within(@org.springframework.stereotype.Service *) || " +
              "within(@org.springframework.stereotype.Repository *)")
    public void applicationPackagePointcut() {
        // 切点定义
    }

    /**
     * 环绕通知：记录方法调用日志
     */
    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String operation = className + "." + methodName;
        
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception thrownException = null;
        
        try {
            // 记录方法调用开始
            LogUtil.logDebug(logger, "方法调用开始", operation, joinPoint.getArgs());
            
            // 执行方法
            result = joinPoint.proceed();
            
            return result;
            
        } catch (Exception e) {
            thrownException = e;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            if (thrownException != null) {
                // 优化：避免记录404等正常错误
                if (!isNormalError(thrownException)) {
                    LogUtil.logException(logger, operation, thrownException, joinPoint.getArgs());
                }
            } else {
                // 记录性能
                LogUtil.logPerformance(logger, operation, duration, joinPoint.getArgs(), result);
            }
        }
    }
    
    private boolean isNormalError(Throwable throwable) {
        String message = throwable.getMessage();
        return message != null && (
            message.contains("404") || 
            message.contains("Not Found") ||
            message.contains("No handler found")
        );
    }

    /**
     * 记录业务方法日志的切点
     */
    @Pointcut("@annotation(org.practice.surveymaster.annotation.LogBusiness)")
    public void logBusinessPointcut() {
        // 切点定义
    }

    /**
     * 记录业务方法日志
     */
    @Around("logBusinessPointcut()")
    public Object logBusinessAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String operation = className + "." + methodName;
        
        Object result = null;
        Exception thrownException = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            thrownException = e;
            throw e;
        } finally {
            if (thrownException == null) {
                LogUtil.logBusiness(logger, operation, joinPoint.getArgs(), result);
            }
        }
    }
}