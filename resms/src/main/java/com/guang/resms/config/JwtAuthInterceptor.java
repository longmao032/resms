package com.guang.resms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.utils.JwtUtil;
import com.guang.resms.module.user.entity.Role;
import com.guang.resms.module.user.mapper.RoleMapper;
import com.guang.resms.module.user.mapper.UserRoleMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public JwtAuthInterceptor(JwtUtil jwtUtil,
                              ObjectMapper objectMapper,
                              UserRoleMapper userRoleMapper,
                              RoleMapper roleMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        if ("/auth/login".equals(path)) {
            return true;
        }

        String token = resolveToken(request);
        if (token != null && jwtUtil.validateToken(token)) {
            // 额外校验：token 里的角色与数据库当前主角色是否一致；若不一致则强制失效
            Integer tokenUserId = jwtUtil.getUserIdFromToken(token);
            Integer tokenRoleId = jwtUtil.getRoleTypeFromToken(token);

            if (tokenUserId == null || tokenRoleId == null) {
                return writeUnauthorized(response, "未登录或登录已过期");
            }

            Integer currentRoleId = userRoleMapper.selectPrimaryRoleIdByUserId(tokenUserId);
            if (currentRoleId == null) {
                return writeUnauthorized(response, "用户未分配角色，请重新登录");
            }

            if (!tokenRoleId.equals(currentRoleId)) {
                return writeUnauthorized(response, "角色已变更，请重新登录");
            }

            // 角色权限变更后立即生效：若角色 updateTime 晚于 token 签发时间，则要求重新登录
            Role role = roleMapper.selectById(currentRoleId);
            if (role != null && role.getUpdateTime() != null) {
                Date issuedAt = jwtUtil.parseToken(token).getIssuedAt();
                if (issuedAt != null) {
                    Date roleUpdateAt = Date.from(role.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant());
                    // DB datetime 通常只有“秒”精度，可能出现“同一秒内 roleUpdateAt 被截断导致比较不出变更”的情况
                    Date roleUpdateAtWithTolerance = new Date(roleUpdateAt.getTime() + 999);
                    if (issuedAt.before(roleUpdateAtWithTolerance)) {
                        return writeUnauthorized(response, "角色权限已变更，请重新登录");
                    }
                }
            }

            return true;
        }

        return writeUnauthorized(response, "未登录或登录已过期");
    }

    private boolean writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");

        ResponseResult<Void> body = ResponseResult.fail(HttpEnums.UNAUTHORIZED.getCode(), message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
        return false;
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String bearer = authHeader.substring(7);
            if (!bearer.isBlank()) {
                return bearer;
            }
        }

        String token = request.getParameter("token");
        if (token != null && !token.isBlank()) {
            return token;
        }

        return null;
    }
}
