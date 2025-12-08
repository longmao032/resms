package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guang.resms.entity.User;
import com.guang.resms.entity.vo.LoginResponseVo;
import com.guang.resms.entity.vo.MenuVO;
import com.guang.resms.mapper.UserMapper;
import com.guang.resms.service.AuthService;
import com.guang.resms.service.PermissionService;
import com.guang.resms.utils.JwtUtil;
import com.guang.resms.utils.exception.HttpEnums;
import com.guang.resms.utils.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.ArrayList;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PermissionService permissionService;


    public AuthServiceImpl(UserMapper userMapper, JwtUtil jwtUtil, PermissionService permissionService) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.permissionService = permissionService;
    }


    @Override
    public LoginResponseVo login(String username, String password) {
        // 1. 验证输入参数
        if (!StringUtils.hasText(username)) {
            throw new ServiceException(HttpEnums.BAD_REQUEST.getMessage());
        }
        if (!StringUtils.hasText(password)) {
            throw new ServiceException(HttpEnums.BAD_REQUEST.getMessage());
        }


        try {
            // 3. 查询用户（只查询启用状态的用户）
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username.trim())
                    .eq(User::getStatus, 1));  // 只查询启用状态的用户

            // 4. 检查用户是否存在
            if (user == null) {
                throw new ServiceException(HttpEnums.USERNAME_PASSWORD_ERROR.getMessage());
            }

            // 5. 验证密码（注意：当前是明文比较，建议后续使用BCrypt加密）
            if (!password.equals(user.getPassword())) {
                throw new ServiceException(HttpEnums.USERNAME_PASSWORD_ERROR.getMessage());
            }

            // 7. 生成JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleType());

            // 8. 获取用户权限（添加异常处理）
            List<MenuVO> menus = new ArrayList<>();
            List<String> permissions = new ArrayList<>();
            try {
                menus = permissionService.getUserMenus(user.getId());
                permissions = permissionService.getUserPermissions(user.getId());
            } catch (Exception e) {
                // 权限获取失败时记录日志，但不影响登录
                System.err.println("获取用户权限失败: " + e.getMessage());
                // 使用空列表作为默认值
            }

            // 9. 构建响应对象
            LoginResponseVo response = new LoginResponseVo();
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setRealName(user.getRealName());
            response.setAvatar(user.getAvatar());
            response.setToken(token);
            response.setRoleType(user.getRoleType());
            response.setMenus(menus != null ? menus : List.of());
            response.setPermissions(permissions != null ? permissions : List.of());

            return response;

        } catch (ServiceException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 处理其他异常，如数据库连接错误等
            throw new ServiceException(HttpEnums.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

}
