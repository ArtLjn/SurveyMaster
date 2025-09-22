package org.practice.surveymaster.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 *
 * @author ljn
 * @since 2025/9/22
 */
@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {

    @Test
    public void testJwtTokenGeneration() {
        JwtUtil jwtUtil = new JwtUtil();
        
        // 测试数据
        Long userId = 1L;
        String username = "testuser";
        
        // 生成访问令牌
        String accessToken = jwtUtil.generateAccessToken(userId, username);
        assertNotNull(accessToken);
        assertTrue(accessToken.length() > 0);
        
        // 生成刷新令牌
        String refreshToken = jwtUtil.generateRefreshToken(userId, username);
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
        
        // 验证访问令牌
        assertTrue(jwtUtil.validateAccessToken(accessToken));
        assertFalse(jwtUtil.validateRefreshToken(accessToken)); // 访问令牌不应该被识别为刷新令牌
        
        // 验证刷新令牌
        assertTrue(jwtUtil.validateRefreshToken(refreshToken));
        assertFalse(jwtUtil.validateAccessToken(refreshToken)); // 刷新令牌不应该被识别为访问令牌
        
        // 从令牌中提取信息
        assertEquals(username, jwtUtil.getUsernameFromToken(accessToken));
        assertEquals(userId, jwtUtil.getUserIdFromToken(accessToken));
        assertEquals("access", jwtUtil.getTokenTypeFromToken(accessToken));
        
        assertEquals(username, jwtUtil.getUsernameFromToken(refreshToken));
        assertEquals(userId, jwtUtil.getUserIdFromToken(refreshToken));
        assertEquals("refresh", jwtUtil.getTokenTypeFromToken(refreshToken));
        
        System.out.println("JWT功能测试通过！");
        System.out.println("访问令牌: " + accessToken);
        System.out.println("刷新令牌: " + refreshToken);
    }
}