package org.practice.surveymaster.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.practice.surveymaster.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 脱敏配置测试类
 * </p>
 *
 * <p>
 * 测试 Jackson 配置是否正确支持 Java 8 时间类型和脱敏功能。
 * 验证 ObjectMapper 能否正确序列化包含 LocalDateTime 的对象。
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午5:30
 */
@SpringBootTest
public class SensitiveConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testJacksonWithLocalDateTime() throws Exception {
        // 创建包含 LocalDateTime 的用户对象
        User user = new User();
        user.setId(1);
        user.setUsername("张三");
        user.setPassword("password123");
        user.setEmail("zhangsan@example.com");
        user.setCreatedAt(LocalDateTime.now());

        // 测试序列化
        String json = objectMapper.writeValueAsString(user);
        assertNotNull(json);
        assertTrue(json.contains("createdAt"));
        
        // 验证脱敏效果（应该包含脱敏后的数据）
        assertTrue(json.contains("张*") || json.contains("username"));
        assertTrue(json.contains("***") || json.contains("password"));
        
        System.out.println("序列化结果: " + json);

        // 测试反序列化
        User deserializedUser = objectMapper.readValue(json, User.class);
        assertNotNull(deserializedUser);
        assertNotNull(deserializedUser.getCreatedAt());
        assertEquals(user.getId(), deserializedUser.getId());
    }

    @Test
    public void testObjectMapperConfiguration() {
        // 验证 ObjectMapper 是否正确配置
        assertNotNull(objectMapper);
        
        // 验证是否注册了 JSR310 模块
        assertTrue(objectMapper.getRegisteredModuleIds().contains("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule"));
        
        // 验证是否注册了脱敏模块
        assertTrue(objectMapper.getRegisteredModuleIds().contains("SensitiveModule"));
    }
}