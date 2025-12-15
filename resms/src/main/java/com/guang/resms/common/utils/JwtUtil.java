package com.guang.resms.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * JWT工具类，用于生成和验证JWT
 *
 * @author guang
 */

@Component
public class JwtUtil {
    // 使用固定密钥，方便测试
    private static final String SECRET_STRING = "your-secret-key-for-jwt-authentication-in-resms-system-2025";

    // 生成密钥
    private final SecretKey SECRET_KEY = io.jsonwebtoken.security.Keys
            .hmacShaKeyFor(SECRET_STRING.getBytes(java.nio.charset.StandardCharsets.UTF_8));

    // 过期时间，设置为24小时
    private final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    /**
     * 生成JWT
     * 
     * @param userId   用户ID
     * @param username 用户名
     * @param roleType 角色类型
     * @param realName 真实姓名
     * @param teamId   团队ID (可能为null)
     * @return JWT字符串
     */
    public String generateToken(Integer userId, String username, Integer roleType,
            String realName, Integer teamId) {
        // 设置JWT声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roleType", roleType);
        claims.put("realName", realName);
        if (teamId != null) {
            claims.put("teamId", teamId);
        }

        // 生成JWT - 注意：0.12.x 版本API有变化
        return Jwts.builder()
                .claims(claims) // 使用.claims()方法设置声明
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY) // 使用SecretKey对象，不直接指定算法
                .compact();
    }

    /**
     * 解析JWT
     * 
     * @param token JWT字符串
     * @return JWT声明
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY) // 使用verifyWith()而不是setSigningKey()
                .build()
                .parseSignedClaims(token) // 使用parseSignedClaims()而不是parseClaimsJws()
                .getPayload();
    }

    /**
     * 验证JWT是否有效
     * 
     * @param token JWT字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从token中获取用户名
     * 
     * @param token JWT字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从token中获取用户ID
     * 
     * @param token JWT字符串
     * @return 用户ID
     */
    public Integer getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Integer.class);
    }

    /**
     * 从token中获取角色类型
     * 
     * @param token JWT字符串
     * @return 角色类型
     */
    public Integer getRoleTypeFromToken(String token) {
        return parseToken(token).get("roleType", Integer.class);
    }

    /**
     * 检查token是否过期
     * 
     * @param token JWT字符串
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 从token中获取真实姓名
     * 
     * @param token JWT字符串
     * @return 真实姓名
     */
    public String getRealNameFromToken(String token) {
        return parseToken(token).get("realName", String.class);
    }

    /**
     * 从token中获取团队ID
     * 
     * @param token JWT字符串
     * @return 团队ID (可能为null)
     */
    public Integer getTeamIdFromToken(String token) {
        return parseToken(token).get("teamId", Integer.class);
    }
}