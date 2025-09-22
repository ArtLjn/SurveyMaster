package org.practice.surveymaster.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 提供JWT token的生成、解析、验证功能
 *
 * @author ljn
 * @since 2025/9/22
 */
@Component
public class JwtUtil {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:surveymaster-jwt-secret-key-for-authentication-very-secure}")
    private String secret;

    /**
     * JWT访问令牌过期时间（毫秒）- 默认2小时
     */
    @Value("${jwt.access-token-expiration:7200000}")
    private Long accessTokenExpiration;

    /**
     * JWT刷新令牌过期时间（毫秒）- 默认7天
     */
    @Value("${jwt.refresh-token-expiration:604800000}")
    private Long refreshTokenExpiration;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成访问令牌
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT访问令牌
     */
    public String generateAccessToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "access");
        
        return createToken(claims, username, accessTokenExpiration);
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "refresh");
        
        return createToken(claims, username, refreshTokenExpiration);
    }

    /**
     * 创建Token
     *
     * @param claims 声明
     * @param subject 主题
     * @param expiration 过期时间
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token中获取用户名
     *
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从token中获取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 从token中获取过期时间
     *
     * @param token JWT token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从token中获取令牌类型
     *
     * @param token JWT token
     * @return 令牌类型
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("tokenType");
    }

    /**
     * 从token中获取指定声明
     */
    public <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从token中获取所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * 检查token是否已过期
     *
     * @param token JWT token
     * @return true如果已过期，false如果未过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证token是否有效
     *
     * @param token JWT token
     * @param username 用户名
     * @return true如果有效，false如果无效
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证访问令牌
     *
     * @param token JWT token
     * @return true如果是有效的访问令牌
     */
    public Boolean validateAccessToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "access".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证刷新令牌
     *
     * @param token JWT token
     * @return true如果是有效的刷新令牌
     */
    public Boolean validateRefreshToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "refresh".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}