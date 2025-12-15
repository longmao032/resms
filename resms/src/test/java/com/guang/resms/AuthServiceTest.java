package com.guang.resms;


import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.module.auth.entity.vo.LoginResponseVo;
import com.guang.resms.module.auth.service.AuthService;
import com.guang.resms.module.user.entity.User;
import com.guang.resms.module.user.entity.UserRole;
import com.guang.resms.module.user.mapper.UserMapper;
import com.guang.resms.module.user.mapper.UserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 登录功能测试
 *
 * @author guang
 */
@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    private String testUsername;

    /**
     * 测试成功的登录场景
     */
    @Test
    public void testSuccessfulLogin() {
        LoginResponseVo response = authService.login(testUsername, "123456");

        // 验证返回结果
        assertNotNull(response, "登录成功应该返回LoginResponseVo对象");

        // 验证基本用户信息
        assertNotNull(response.getUserId(), "用户ID不应为空");
        assertNotNull(response.getUsername(), "用户名不应为空");
        assertNotNull(response.getToken(), "JWT token不应为空");

        // 验证数据结构完整性
        assertNotNull(response.getMenus(), "菜单列表不应为空");
        assertNotNull(response.getPermissions(), "权限列表不应为空");
        assertNotNull(response.getRoleType(), "角色类型不应为空");

        // 验证token格式（JWT token应该以eyJ开头）
        assertTrue(response.getToken().startsWith("eyJ"), "JWT token应该以eyJ开头");

        System.out.println("✅ 登录成功测试通过");
        System.out.println("用户ID: " + response.getUserId());
        System.out.println("用户名: " + response.getUsername());
        System.out.println("Token长度: " + response.getToken().length());
        System.out.println("菜单数量: " + response.getMenus().size());
        System.out.println("权限数量: " + response.getPermissions().size());
    }

    /**
     * 测试用户名为空的场景
     */
    @Test
    public void testEmptyUsername() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            authService.login("", "password");
        });

        assertNotNull(exception.getMessage(), "应该抛出异常并包含错误信息");
        System.out.println("✅ 空用户名测试通过: " + exception.getMessage());
    }

    /**
     * 测试密码为空的场景
     */
    @Test
    public void testEmptyPassword() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            authService.login(testUsername, "");
        });

        assertNotNull(exception.getMessage(), "应该抛出异常并包含错误信息");
        System.out.println("✅ 空密码测试通过: " + exception.getMessage());
    }

    /**
     * 测试用户名不存在的场景
     */
    @Test
    public void testUserNotFound() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            authService.login("nonexistent", "password");
        });

        assertTrue(exception.getMessage().contains("用户名或密码"), "应该提示用户名或密码错误");
        System.out.println("✅ 用户不存在测试通过: " + exception.getMessage());
    }

    /**
     * 测试密码错误的场景
     */
    @Test
    public void testWrongPassword() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            authService.login(testUsername, "wrongpassword");
        });

        assertTrue(exception.getMessage().contains("用户名或密码"), "应该提示用户名或密码错误");
        System.out.println("✅ 密码错误测试通过: " + exception.getMessage());
    }

    /**
     * 测试输入验证 - 用户名包含危险字符
     */
    @Test
    public void testSqlInjectionAttempt() {
        // 测试SQL注入尝试
        User user = new User();
        user.setUsername("admin' OR '1'='1");
        user.setPassword("password");

        // 在Controller层验证，这里只是模拟
        String validationError = validateLoginInput(user.getUsername(), user.getPassword());
        assertNotNull(validationError, "应该检测到危险字符");
        assertTrue(validationError.contains("非法字符"), "应该提示包含非法字符");

        System.out.println("✅ SQL注入防护测试通过: " + validationError);
    }

    /**
     * 简单的输入验证方法（复制自Controller）
     */
    private String validateLoginInput(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }

        username = username.trim();
        if (username.length() < 3 || username.length() > 50) {
            return "用户名长度必须在3-50个字符之间";
        }

        // 检查用户名是否包含危险字符
        if (containsSqlInjectionChars(username)) {
            return "用户名包含非法字符";
        }

        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }

        if (password.length() < 6 || password.length() > 100) {
            return "密码长度必须在6-100个字符之间";
        }

        return null;
    }

    /**
     * 检查是否包含SQL注入相关字符
     */
    private boolean containsSqlInjectionChars(String input) {
        String[] dangerousPatterns = {
            ".*([';]+|(--)+).*",
            ".*(\\b(union|select|insert|delete|update|drop|create|alter)\\b).*",
            ".*(\\b(or|and)\\b.*(=|<|>)).*",
            ".*(\\b(exec|execute)\\b).*",
            ".*(\\b(script|javascript|vbscript|onload|onerror)\\b).*",
            ".*(<.*>).*",
        };

        for (String pattern : dangerousPatterns) {
            if (input.toLowerCase().matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    @BeforeEach
    public void setupTestUser() {
        testUsername = "test_admin_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String uuidDigits = UUID.randomUUID().toString().replace("-", "");

        User user = new User();
        user.setUsername(testUsername);
        user.setPassword("123456");
        user.setRealName("测试管理员");
        user.setPhone("199" + uuidDigits.substring(0, 8));
        user.setEmail(testUsername + "@example.com");
        user.setAvatar("/uploads/avatars/default.jpg");
        user.setStatus(1);

        int inserted = userMapper.insert(user);
        if (inserted <= 0 || user.getId() == null) {
            throw new RuntimeException("插入测试用户失败");
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(1);
        userRoleMapper.insert(userRole);
    }
}
