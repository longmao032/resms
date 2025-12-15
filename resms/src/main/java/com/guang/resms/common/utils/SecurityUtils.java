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
     * 获取当前登录用户真实姓名
     */
    public static String getRealName() {
        String token = getToken();
        if (token != null && staticJwtUtil.validateToken(token)) {
            return staticJwtUtil.getRealNameFromToken(token);
        }
        return null;
    }

    /**
     * 获取当前登录用户团队ID
     */
    public static Integer getTeamId() {
        String token = getToken();
        if (token != null && staticJwtUtil.validateToken(token)) {
            return staticJwtUtil.getTeamIdFromToken(token);
        }
        return null;
    }

    /**
     * 获取当前登录用户ID (必需版本，如果未登录则抛出异常)
     * 
     * @return 用户ID
     * @throws RuntimeException 如果未登录或Token无效
     */
    public static Integer requireUserId() {
        Integer userId = getUserId();
        if (userId == null) {
            throw new RuntimeException("未登录或登录已过期，请重新登录");
        }
        return userId;
    }

    /**
     * 获取当前登录用户角色类型 (必需版本，如果未登录则抛出异常)
     * 
     * @return 角色类型
     * @throws RuntimeException 如果未登录或Token无效
     */
    public static Integer requireRoleType() {
        Integer roleType = getRoleType();
        if (roleType == null) {
            throw new RuntimeException("未登录或登录已过期，请重新登录");
        }
        return roleType;
    }

    /**
     * 判断当前用户是否为系统管理员
     * 
     * @return true=管理员, false=非管理员或未登录
     */
    public static boolean isAdmin() {
        Integer roleType = getRoleType();
        return roleType != null && roleType == 1;
    }

    /**
     * 判断当前用户是否为销售顾问
     * 
     * @return true=销售顾问, false=非销售顾问或未登录
     */
    public static boolean isSalesConsultant() {
        Integer roleType = getRoleType();
        return roleType != null && roleType == 2;
    }

    /**
     * 判断当前用户是否为销售经理
     * 
     * @return true=销售经理, false=非销售经理或未登录
     */
    public static boolean isSalesManager() {
        Integer roleType = getRoleType();
        return roleType != null && roleType == 3;
    }

    /**
     * 判断当前用户是否为财务人员
     * 
     * @return true=财务人员, false=非财务人员或未登录
     */
    public static boolean isFinance() {
        Integer roleType = getRoleType();
        return roleType != null && roleType == 4;
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
