package org.practice.surveymaster.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.practice.surveymaster.annotation.Sensitive;
import org.practice.surveymaster.config.SensitiveConfig;
import org.practice.surveymaster.util.SensitiveUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 数据脱敏 AOP 切面处理器
 * </p>
 *
 * <p>
 * 基于 AOP 技术的数据脱敏处理器，能够在方法执行过程中自动对返回值中的敏感数据进行脱敏处理。
 * 支持对单个对象、列表对象以及复杂嵌套对象的脱敏处理。
 * 通过反射机制扫描对象中标注了 @Sensitive 注解的字段，并根据注解配置进行相应的脱敏处理。
 * 集成了完整的日志记录功能，便于调试和监控脱敏处理过程。
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午4:40
 */
@Aspect
@Component
public class SensitiveAspect {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveAspect.class);

    @Autowired
    private SensitiveConfig.SensitiveProperties sensitiveProperties;

    /**
     * 定义切点：拦截所有 Controller 层的方法
     */
    @Pointcut("execution(public * org.practice.surveymaster.controller..*(..))")
    public void controllerMethods() {}

    /**
     * 定义切点：拦截标注了 @Sensitive 注解的方法
     */
    @Pointcut("@annotation(org.practice.surveymaster.annotation.Sensitive)")
    public void sensitiveMethods() {}

    /**
     * 环绕通知：对方法返回值进行脱敏处理
     */
    @Around("controllerMethods()")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        // 如果脱敏功能未启用，直接执行原方法
        if (!sensitiveProperties.isEnabled()) {
            return joinPoint.proceed();
        }

        // 执行原方法
        Object result = joinPoint.proceed();

        // 对返回值进行脱敏处理
        return desensitizeResult(result);
    }

    /**
     * 对结果进行脱敏处理
     */
    private Object desensitizeResult(Object result) {
        if (result == null) {
            return null;
        }

        try {
            // 处理不同类型的返回值
            if (result instanceof List) {
                return desensitizeList((List<?>) result);
            } else if (result.getClass().getPackage() != null && 
                       result.getClass().getPackage().getName().startsWith("org.practice.surveymaster")) {
                // 只处理项目内的类
                return desensitizeObject(result);
            }
        } catch (Exception e) {
            logger.warn("脱敏处理失败: {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 对列表进行脱敏处理
     */
    @SuppressWarnings("unchecked")
    private List<Object> desensitizeList(List<?> list) {
        if (list == null || list.isEmpty()) {
            return (List<Object>) list;
        }

        List<Object> desensitizedList = new ArrayList<>();
        for (Object item : list) {
            desensitizedList.add(desensitizeResult(item));
        }

        return desensitizedList;
    }

    /**
     * 对单个对象进行脱敏处理
     */
    private Object desensitizeObject(Object obj) {
        if (obj == null) {
            return null;
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Sensitive sensitive = field.getAnnotation(Sensitive.class);
            if (sensitive != null && field.getType() == String.class) {
                try {
                    field.setAccessible(true);
                    String originalValue = (String) field.get(obj);
                    
                    // 根据注解配置进行脱敏
                    String desensitizedValue = desensitizeFieldValue(originalValue, sensitive);
                    field.set(obj, desensitizedValue);
                    
                    logger.debug("字段 {} 脱敏处理完成", field.getName());
                } catch (IllegalAccessException e) {
                    logger.warn("无法访问字段 {}: {}", field.getName(), e.getMessage());
                } catch (Exception e) {
                    logger.error("字段 {} 脱敏处理异常: {}", field.getName(), e.getMessage(), e);
                }
            }
        }

        return obj;
    }

    /**
     * 对字段值进行脱敏处理
     */
    private String desensitizeFieldValue(String value, Sensitive sensitive) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // 检查是否启用日志脱敏
        if (!sensitive.enableLog() && !sensitiveProperties.isEnableLog()) {
            return value;
        }

        // 根据脱敏类型进行处理
        switch (sensitive.value()) {
            case CUSTOM:
                return SensitiveUtil.desensitizeCustom(
                    value, 
                    sensitive.keepStart(), 
                    sensitive.keepEnd(), 
                    sensitive.maskChar()
                );
            default:
                return SensitiveUtil.desensitize(value, sensitive.value());
        }
    }
}