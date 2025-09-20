package org.practice.surveymaster.exception;

import lombok.Getter;
import org.practice.surveymaster.constant.ErrorCode;

/**
 * 业务异常类
 * 
 * @author ljn
 * @since 2025/9/11
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    /**
     * 获取格式化的错误消息
     */
    public String getFormattedMessage() {
        if (args == null || args.length == 0) {
            return getMessage();
        }
        return String.format(getMessage(), args);
    }
}