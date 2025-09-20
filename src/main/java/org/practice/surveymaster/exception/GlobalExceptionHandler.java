package org.practice.surveymaster.exception;

import lombok.extern.slf4j.Slf4j;
import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.vo.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * @author ljn
 * @since 2025/9/11
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 - 错误码: {}, 消息: {}, 路径: {}", 
                e.getErrorCode().getCode(), e.getMessage(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getFormattedMessage())
                .path(request.getRequestURI())
                .build();
                
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(response);
    }

    /**
     * 处理HTTP消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("HTTP消息不可读 - 消息: {}, 路径: {}", e.getMessage(), request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.PARAM_ERROR, "请求参数格式错误");
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e, HttpServletRequest request) {
        
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("参数绑定失败 - 消息: {}, 路径: {}", errorMessage, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.BAD_REQUEST, errorMessage);
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理参数校验异常 - @Validated 校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("参数校验失败 - 消息: {}, 路径: {}", errorMessage, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.BAD_REQUEST, errorMessage);
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
                
        log.warn("参数验证异常 - 消息: {}, 路径: {}", message, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.PARAM_ERROR, message);
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理参数校验异常 - @RequestParam 缺少参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数 - 参数名: {}, 路径: {}", e.getParameterName(), request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.PARAM_ERROR, "缺少参数: " + e.getParameterName());
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        
        String errorMessage = "不支持的请求方法: " + e.getMethod();
        log.warn("HTTP方法不支持 - 方法: {}, 路径: {}", e.getMethod(), request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED, errorMessage);
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * 处理404错误
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(
            NoHandlerFoundException e, HttpServletRequest request) {
        
        String errorMessage = "请求的资源不存在: " + e.getHttpMethod() + " " + e.getRequestURL();
        log.warn("404错误 - 消息: {}, 路径: {}", errorMessage, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.NOT_FOUND, errorMessage);
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        
        String errorMessage = "参数类型错误: " + e.getName() + " 应该是 " + e.getRequiredType().getSimpleName();
        log.warn("参数类型不匹配 - 消息: {}, 路径: {}", errorMessage, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.BAD_REQUEST, errorMessage);
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 - 类型: {}, 消息: {}, 路径: {}", 
                e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, "系统繁忙，请稍后重试");
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常 - 类型: {}, 消息: {}, 路径: {}", 
                e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}