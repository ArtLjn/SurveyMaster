package org.practice.surveymaster.constant;

import lombok.Getter;

/**
 * 统一错误码枚举
 * 
 * 错误码设计规则：
 * 1. 错误码格式：ABCD-EFGH
 *    - AB：系统模块标识（如USER、AUTH、SURVEY等）
 *    - CD：具体业务场景
 *    - EF：错误级别（01-信息，02-警告，03-错误，04-严重错误）
 *    - GH：具体错误序号
 * 
 * 2. HTTP状态码映射：
 *    - 4xx：客户端错误
 *    - 5xx：服务器错误
 * 
 * @author ljn
 * @since 2025/9/11
 */
@Getter
public enum ErrorCode {

    // ========== 成功 ==========
    SUCCESS("0000-0000", "操作成功", 200),

    // ========== 通用错误 ==========
    BAD_REQUEST("COMM-0101", "请求参数错误", 400),
    UNAUTHORIZED("COMM-0201", "未授权访问", 401),
    FORBIDDEN("COMM-0301", "权限不足", 403),
    NOT_FOUND("COMM-0401", "资源不存在", 404),
    METHOD_NOT_ALLOWED("COMM-0501", "请求方法不允许", 405),
    TOO_MANY_REQUESTS("COMM-0601", "请求过于频繁", 429),
    INTERNAL_SERVER_ERROR("COMM-0701", "服务器内部错误", 500),
    SERVICE_UNAVAILABLE("COMM-0801", "服务暂不可用", 503),

    // ========== 用户模块 ==========
    USER_NOT_FOUND("USER-0103", "用户不存在", 404),
    USER_ALREADY_EXISTS("USER-0203", "用户已存在", 409),
    USERNAME_ALREADY_EXISTS("USER-0303", "用户名已存在", 409),
    EMAIL_ALREADY_EXISTS("USER-0403", "邮箱已存在", 409),
    INVALID_USERNAME("USER-0502", "用户名格式错误", 400),
    INVALID_EMAIL("USER-0602", "邮箱格式错误", 400),
    INVALID_PASSWORD("USER-0702", "密码格式错误", 400),
    PASSWORD_TOO_SIMPLE("USER-0802", "密码过于简单", 400),
    PASSWORD_MISMATCH("USER-0903", "密码不匹配", 400),
    USER_STATUS_ABNORMAL("USER-1003", "用户状态异常", 403),
    USER_PASSWORD_ERROR("USER-0901", "用户名或密码错误", 401),

    // ========== 认证授权模块 ==========
    AUTH_FAILURE("AUTH-0103", "认证失败", 401),
    LOGIN_FAILURE("AUTH-0203", "登录失败", 401),
    TOKEN_EXPIRED("AUTH-0303", "令牌已过期", 401),
    TOKEN_INVALID("AUTH-0403", "令牌无效", 401),
    PERMISSION_DENIED("AUTH-0503", "权限不足", 403),
    ROLE_NOT_FOUND("AUTH-0604", "角色不存在", 404),

    // ========== 问卷模块 ==========
    SURVEY_NOT_FOUND("SURV-0103", "问卷不存在", 404),
    SURVEY_ALREADY_DELETED("SURV-0203", "问卷已被删除", 410),
    SURVEY_ALREADY_PUBLISHED("SURV-0303", "问卷已发布，无法修改", 409),
    SURVEY_NOT_PUBLISHED("SURV-0403", "问卷未发布", 409),
    INVALID_SURVEY_DATA("SURV-0502", "问卷数据无效", 400),
    SURVEY_EXPIRED("SURV-0603", "问卷已过期", 410),
    SURVEY_QUOTA_FULL("SURV-0703", "问卷配额已满", 409),

    // ========== 题目模块 ==========
    QUESTION_NOT_FOUND("QUES-0103", "题目不存在", 404),
    INVALID_QUESTION_TYPE("QUES-0202", "题目类型无效", 400),
    INVALID_QUESTION_DATA("QUES-0302", "题目数据无效", 400),

    // ========== 选项模块 ==========
    OPTION_NOT_FOUND("OPTI-0103", "选项不存在", 404),
    OPERATION_FAILED("OPTI-0203", "操作失败", 500),
    INVALID_OPTION_DATA("OPTI-0302", "选项数据无效", 400),

    // ========== 答案模块 ==========
    ANSWER_NOT_FOUND("ANSW-0103", "答案不存在", 404),
    INVALID_ANSWER_DATA("ANSW-0202", "答案数据无效", 400),
    ANSWER_ALREADY_SUBMITTED("ANSW-0303", "答案已提交", 409),
    // ========== 数据模块 ==========
    DATA_NOT_FOUND("DATA-0103", "数据不存在", 404),
    DATA_ALREADY_EXISTS("DATA-0203", "数据已存在", 409),
    DATA_VALIDATION_FAILED("DATA-0302", "数据验证失败", 400),
    DATA_PROCESSING_ERROR("DATA-0403", "数据处理错误", 500),
    PARAM_ERROR("PARAM-0103","缺少参数",400),

    // ========== 文件模块 ==========
    FILE_NOT_FOUND("FILE-0103", "文件不存在", 404),
    FILE_UPLOAD_FAILED("FILE-0203", "文件上传失败", 500),
    FILE_DOWNLOAD_FAILED("FILE-0303", "文件下载失败", 500),
    FILE_TYPE_NOT_SUPPORTED("FILE-0402", "不支持的文件类型", 415),
    FILE_SIZE_EXCEEDED("FILE-0502", "文件大小超出限制", 413),

    // ========== 网络模块 ==========
    NETWORK_ERROR("NETW-0103", "网络错误", 500),
    TIMEOUT_ERROR("NETW-0203", "请求超时", 504),
    CONNECTION_REFUSED("NETW-0303", "连接被拒绝", 503),

    // ========== 缓存模块 ==========
    CACHE_ERROR("CACH-0103", "缓存错误", 500),
    CACHE_NOT_AVAILABLE("CACH-0203", "缓存不可用", 503),

    // ========== 数据库模块 ==========
    DATABASE_ERROR("DB-0103", "数据库错误", 500),
    DATABASE_CONNECTION_FAILED("DB-0203", "数据库连接失败", 503),
    DATABASE_TRANSACTION_FAILED("DB-0303", "数据库事务失败", 500),
    DUPLICATE_KEY("DB-0403", "主键冲突", 409),

    // ========== 第三方服务模块 ==========
    THIRD_PARTY_SERVICE_ERROR("THIRD-0103", "第三方服务错误", 500),
    THIRD_PARTY_SERVICE_TIMEOUT("THIRD-0203", "第三方服务超时", 504),
    THIRD_PARTY_SERVICE_UNAVAILABLE("THIRD-0303", "第三方服务不可用", 503);

    private final String code;
    private final String message;
    private final int httpStatus;

    ErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /**
     * 根据错误码获取错误枚举
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }

    /**
     * 获取HTTP状态码
     */
    public static int getHttpStatus(String code) {
        return fromCode(code).getHttpStatus();
    }

    /**
     * 获取错误信息
     */
    public static String getMessage(String code) {
        return fromCode(code).getMessage();
    }
}