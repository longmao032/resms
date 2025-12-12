package com.guang.resms.controller;

import com.guang.resms.entity.vo.LoginResponseVo;
import com.guang.resms.service.AuthService;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseResult<LoginResponseVo> login(@RequestBody Map<String, Object> loginRequest,
            HttpServletRequest request) {
        try {
            // 1. 基础输入验证
            if (loginRequest == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "请求参数不能为空");
            }

            String username = (String) loginRequest.get("username");
            String password = (String) loginRequest.get("password");

            // 2. 验证用户名和密码格式
            String validationError = validateLoginInput(username, password);
            if (validationError != null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), validationError);
            }

            // 4. 执行登录逻辑
            LoginResponseVo loginResponse = authService.login(username, password);
            System.out.println(
                    "===================================================================" + loginResponse.getToken());
            // 5. 返回登录成功响应
            return ResponseResult.success("登录成功", loginResponse);

        } catch (ServiceException e) {
            // 处理业务异常（如密码错误、账户锁定等）
            String errorMessage = e.getMessage();
            if (errorMessage.contains("锁定")) {
                return ResponseResult.fail(HttpEnums.USER_ACCOUNT_LOCKED.getCode(), errorMessage);
            } else if (errorMessage.contains("用户名或密码")) {
                return ResponseResult.fail(HttpEnums.USERNAME_PASSWORD_ERROR.getCode(), errorMessage);
            } else {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), errorMessage);
            }
        } catch (Exception e) {
            // 处理系统异常
            return ResponseResult.<LoginResponseVo>fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "系统异常，请稍后重试");
        }
    }

    /**
     * 验证登录输入参数
     */
    private String validateLoginInput(String username, String password) {
        // 用户名验证
        if (!StringUtils.hasText(username)) {
            return "用户名不能为空";
        }

        username = username.trim();
        if (username.length() < 3 || username.length() > 50) {
            return "用户名长度必须在3-50个字符之间";
        }

        // 检查用户名是否包含危险字符（防SQL注入）
        if (containsSqlInjectionChars(username)) {
            return "用户名包含非法字符";
        }

        // 密码验证
        if (!StringUtils.hasText(password)) {
            return "密码不能为空";
        }

        return null; // 验证通过
    }

    /**
     * 检查是否包含SQL注入相关字符
     */
    private boolean containsSqlInjectionChars(String input) {
        // 常见的SQL注入字符模式
        String[] dangerousPatterns = {
                ".*([';]+|(--)+).*", // 单引号、分号、注释符
                ".*(\\b(union|select|insert|delete|update|drop|create|alter)\\b).*", // SQL关键字
                ".*(\\b(or|and)\\b.*(=|<|>)).*", // 逻辑运算符
                ".*(\\b(exec|execute)\\b).*", // 执行关键字
                ".*(\\b(script|javascript|vbscript|onload|onerror)\\b).*", // XSS关键字
                ".*(<.*>).*", // HTML标签
        };

        for (String pattern : dangerousPatterns) {
            if (Pattern.matches(pattern, input.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否包含危险字符
     */
    private boolean containsDangerousChars(String input) {
        // 检查是否包含控制字符或非常用字符
        for (char c : input.toCharArray()) {
            if (c < 32 || c == 127) { // 控制字符
                return true;
            }
        }
        return false;
    }

    private boolean isValidCsrfToken(String token, HttpServletRequest request) {
        String sessionToken = (String) request.getSession().getAttribute("csrfToken");
        return token != null && token.equals(sessionToken);
    }
}
