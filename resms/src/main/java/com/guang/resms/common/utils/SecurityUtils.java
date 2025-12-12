package com.guang.resms.common.utils;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 安全工具类，用于获取当前登录用户信息
 */
@Component
public class SecurityUtils {

    @Autowired
    private JwtUtil jwtUtil;

    private static JwtUtil staticJwtUtil;

    @PostConstruct
    public void init() {
        staticJwtUtil = this.jwtUtil;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Integer getUserId() {
        String token = getToken();
        if (token != null && staticJwtUtil.validateToken(token)) {
            return staticJwtUtil.getUserIdFromToken(token);
        }
        // 如果没有token或非法，返回null或抛出异常，视业务需求。这里返回null
        return null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        String token = getToken();
        if (token != null && staticJwtUtil.validateToken(token)) {
            return staticJwtUtil.getUsernameFromToken(token);
        }
        return null;
    }

    /**
     * 获取当前登录用户角色类型
     */
    public static Integer getRoleType() {
        String token = getToken();
        if (token != null && staticJwtUtil.validateToken(token)) {
            return staticJwtUtil.getRoleTypeFromToken(token);
        }
        return null;
    }

    /**
     * 获取请求中的Token
     */
    private static String getToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            HttpServletRequest request = attributes.getRequest();
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
