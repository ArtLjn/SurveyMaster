package org.practice.surveymaster.util;

import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.exception.BusinessException;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言工具类
 * 
 * @author ljn
 * @since 2025/9/11
 */
public class AssertUtil {

    /**
     * 断言为真
     */
    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode, String message) {
        if (!expression) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode, Object... args) {
        if (!expression) {
            throw new BusinessException(errorCode, args);
        }
    }

    /**
     * 断言为假
     */
    public static void isFalse(boolean expression, ErrorCode errorCode) {
        if (expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isFalse(boolean expression, ErrorCode errorCode, String message) {
        if (expression) {
            throw new BusinessException(errorCode, message);
        }
    }

    /**
     * 断言对象为空
     */
    public static void isNull(Object object, ErrorCode errorCode) {
        if (Objects.nonNull(object)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isNull(Object object, ErrorCode errorCode, String message) {
        if (Objects.nonNull(object)) {
            throw new BusinessException(errorCode, message);
        }
    }

    /**
     * 断言对象不为空
     */
    public static void notNull(Object object, ErrorCode errorCode) {
        if (Objects.isNull(object)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notNull(Object object, ErrorCode errorCode, String message) {
        if (Objects.isNull(object)) {
            throw new BusinessException(errorCode, message);
        }
    }

    /**
     * 断言字符串不为空
     */
    public static void notBlank(String str, ErrorCode errorCode) {
        if (str == null || str.trim().isEmpty()) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notBlank(String str, ErrorCode errorCode, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new BusinessException(errorCode, message);
        }
    }

    /**
     * 断言集合不为空
     */
    public static void notEmpty(Collection<?> collection, ErrorCode errorCode) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(errorCode, message);
        }
    }

    /**
     * 断言Map不为空
     */
    public static void notEmpty(Map<?, ?> map, ErrorCode errorCode) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(Map<?, ?> map, ErrorCode errorCode, String message) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(errorCode, message);
        }
    }

    /**
     * 断言数组不为空
     */
    public static void notEmpty(Object[] array, ErrorCode errorCode) {
        if (array == null || array.length == 0) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(Object[] array, ErrorCode errorCode, String message) {
        if (array == null || array.length == 0) {
            throw new BusinessException(errorCode, message);
        }
    }

    /**
     * 断言两个对象相等
     */
    public static void equals(Object expected, Object actual, ErrorCode errorCode) {
        if (!Objects.equals(expected, actual)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void equals(Object expected, Object actual, ErrorCode errorCode, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new BusinessException(errorCode, message);
        }
    }

    /**
     * 断言两个对象不相等
     */
    public static void notEquals(Object expected, Object actual, ErrorCode errorCode) {
        if (Objects.equals(expected, actual)) {
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 断言字符串长度在指定范围内
     */
    public static void lengthBetween(String str, int min, int max, ErrorCode errorCode) {
        if (str == null || str.length() < min || str.length() > max) {
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 断言数字在指定范围内
     */
    public static void between(Number number, Number min, Number max, ErrorCode errorCode) {
        if (number == null || number.doubleValue() < min.doubleValue() || number.doubleValue() > max.doubleValue()) {
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 断言状态为期望值
     */
    public static void state(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void state(boolean expression, ErrorCode errorCode, String message) {
        if (!expression) {
            throw new BusinessException(errorCode, message);
        }
    }
}