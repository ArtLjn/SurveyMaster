package org.practice.surveymaster.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.practice.surveymaster.constant.SensitiveType;
import org.practice.surveymaster.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * <p>
 * 数据脱敏功能测试运行器
 * </p>
 *
 * <p>
 * 应用启动后自动运行的测试器，用于快速验证脱敏功能是否正常工作。
 * 测试各种脱敏类型的效果，并展示 JSON 序列化的结果。
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午5:35
 */
@Component
public class SensitiveTestRunner implements CommandLineRunner {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 开始数据脱敏功能测试 ===");
        
        // 测试基本脱敏功能
        testBasicSensitiveUtils();
        
        // 测试 JSON 序列化脱敏
        testJsonSerialization();
        
        System.out.println("=== 数据脱敏功能测试完成 ===");
    }

    private void testBasicSensitiveUtils() {
        System.out.println("\n--- 基本脱敏功能测试 ---");
        
        // 测试手机号脱敏
        String mobile = "13812345678";
        String maskedMobile = SensitiveUtil.desensitize(mobile, SensitiveType.MOBILE);
        System.out.println("手机号脱敏: " + mobile + " -> " + maskedMobile);
        
        // 测试姓名脱敏
        String name = "张三丰";
        String maskedName = SensitiveUtil.desensitize(name, SensitiveType.NAME);
        System.out.println("姓名脱敏: " + name + " -> " + maskedName);
        
        // 测试邮箱脱敏
        String email = "zhangsan@example.com";
        String maskedEmail = SensitiveUtil.desensitize(email, SensitiveType.EMAIL);
        System.out.println("邮箱脱敏: " + email + " -> " + maskedEmail);
        
        // 测试密码脱敏
        String password = "mySecretPassword";
        String maskedPassword = SensitiveUtil.desensitize(password, SensitiveType.PASSWORD);
        System.out.println("密码脱敏: " + password + " -> " + maskedPassword);
        
        // 测试身份证脱敏
        String idCard = "110101199001011234";
        String maskedIdCard = SensitiveUtil.desensitize(idCard, SensitiveType.ID_CARD);
        System.out.println("身份证脱敏: " + idCard + " -> " + maskedIdCard);
        
        // 测试自定义脱敏
        String custom = "CustomSensitiveData";
        String maskedCustom = SensitiveUtil.desensitizeCustom(custom, 3, 3, "#");
        System.out.println("自定义脱敏: " + custom + " -> " + maskedCustom);
    }

    private void testJsonSerialization() {
        System.out.println("\n--- JSON 序列化脱敏测试 ---");
        
        try {
            // 创建用户对象
            User user = new User();
            user.setId(1L);
            user.setUsername("李四");
            user.setPassword("password123");
            user.setEmail("lisi@test.com");
            user.setCreatedAt(LocalDateTime.now());
            
            // 序列化为 JSON
            String json = objectMapper.writeValueAsString(user);
            System.out.println("用户对象 JSON 序列化结果:");
            System.out.println(json);
            
            // 验证时间字段是否正确处理
            if (json.contains("createdAt")) {
                System.out.println("✓ LocalDateTime 序列化成功");
            } else {
                System.out.println("✗ LocalDateTime 序列化失败");
            }
            
            // 反序列化测试
            User deserializedUser = objectMapper.readValue(json, User.class);
            System.out.println("反序列化成功，用户ID: " + deserializedUser.getId());
            
        } catch (Exception e) {
            System.err.println("JSON 序列化测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}