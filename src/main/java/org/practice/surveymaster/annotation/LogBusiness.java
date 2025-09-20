package org.practice.surveymaster.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 业务日志注解
 * 标记需要记录业务日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogBusiness {
    
    /**
     * 业务操作描述
     */
    String value() default "";
    
    /**
     * 是否记录参数
     */
    boolean logParams() default true;
    
    /**
     * 是否记录返回值
     */
    boolean logResult() default true;
}