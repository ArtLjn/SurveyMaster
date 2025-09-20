package org.practice.surveymaster.controller;

import lombok.extern.slf4j.Slf4j;
import org.practice.surveymaster.constant.ErrorCode;
import org.practice.surveymaster.exception.BusinessException;
import org.practice.surveymaster.util.AssertUtil;
import org.practice.surveymaster.vo.ApiResponse;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 错误码使用示例控制器
 * 
 * @author ljn
 * @since 2025/9/11
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 示例1：使用断言工具类
     */
    @GetMapping("/assert")
    public ApiResponse<String> testAssert(@RequestParam String username) {
        // 使用断言工具类进行参数校验
        AssertUtil.notBlank(username, ErrorCode.INVALID_USERNAME, "用户名不能为空");
        AssertUtil.lengthBetween(username, 3, 20, ErrorCode.INVALID_USERNAME);
        
        // 模拟业务逻辑
        if ("admin".equals(username)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        
        return ApiResponse.success("用户名可用：" + username);
    }

    /**
     * 示例2：使用业务异常
     */
    @GetMapping("/business")
    public ApiResponse<String> testBusinessException(@RequestParam String email) {
        // 模拟业务校验
        if (email == null || !email.contains("@")) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL, "邮箱格式不正确");
        }
        
        // 模拟业务逻辑
        if (email.endsWith("@test.com")) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "该邮箱已被注册");
        }
        
        return ApiResponse.success("邮箱验证通过：" + email);
    }

    /**
     * 示例3：使用统一响应
     */
    @GetMapping("/response")
    public ApiResponse<UserVO> testResponse() {
        UserVO user = new UserVO(1L, "testuser", "test@example.com");
        return ApiResponse.success("用户信息获取成功", user);
    }

    /**
     * 示例4：使用参数校验
     */
    @PostMapping("/validate")
    public ApiResponse<String> testValidation(@Valid @RequestBody UserDTO userDTO) {
        log.info("接收到用户注册请求：{}", userDTO);
        
        // 模拟业务逻辑
        if (userDTO.getUsername().length() < 3) {
            throw new BusinessException(ErrorCode.INVALID_USERNAME, "用户名长度至少3位");
        }

        return ApiResponse.success("用户注册成功");
    }

    /**
     * 用户VO类
     */
    public static class UserVO {
        private Long id;
        private String username;
        private String email;

        public UserVO(Long id, String username, String email) {
            this.id = id;
            this.username = username;
            this.email = email;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    /**
     * 用户DTO类
     */
    public static class UserDTO {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
        
        @NotBlank(message = "邮箱不能为空")
        private String email;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @Override
        public String toString() {
            return "UserDTO{" +
                    "username='" + username + '\'' +
                    ", password='***'" +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}