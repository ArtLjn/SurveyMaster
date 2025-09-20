package org.practice.surveymaster.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.practice.surveymaster.constant.ErrorCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一API响应结果
 *
 * @author ljn
 * @since 2025/9/11
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // Getters and Setters
    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 请求ID（用于链路追踪）
     */
    private String requestId;

    /**
     * 路径（请求路径）
     */
    private String path;

    /**
     * 私有化构造方法
     */
    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(message != null ? message : errorCode.getMessage());
        return response;
    }

    /**
     * 快速构建器
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private final ApiResponse<T> response;

        public Builder() {
            this.response = new ApiResponse<>();
        }

        public Builder<T> code(String code) {
            response.setCode(code);
            return this;
        }

        public Builder<T> message(String message) {
            response.setMessage(message);
            return this;
        }

        public Builder<T> data(T data) {
            response.setData(data);
            return this;
        }

        public Builder<T> requestId(String requestId) {
            response.setRequestId(requestId);
            return this;
        }

        public Builder<T> path(String path) {
            response.setPath(path);
            return this;
        }

        public ApiResponse<T> build() {
            return response;
        }
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", requestId='" + requestId + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}