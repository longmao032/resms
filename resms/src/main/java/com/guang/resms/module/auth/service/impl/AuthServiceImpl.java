package com.guang.resms.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guang.resms.module.user.entity.User;
import com.guang.resms.module.auth.entity.vo.LoginResponseVo;
import com.guang.resms.module.user.entity.vo.MenuVO;
import com.guang.resms.module.user.mapper.UserMapper;
import com.guang.resms.module.team.mapper.TeamMapper;
import com.guang.resms.module.team.mapper.TeamMemberMapper;
import com.guang.resms.module.user.mapper.UserRoleMapper;
import com.guang.resms.module.user.mapper.RoleMapper;
import com.guang.resms.module.auth.service.AuthService;
import com.guang.resms.module.user.service.PermissionService;
import com.guang.resms.common.utils.JwtUtil;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.ArrayList;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PermissionService permissionService;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public AuthServiceImpl(UserMapper userMapper, JwtUtil jwtUtil, PermissionService permissionService,
            TeamMapper teamMapper, TeamMemberMapper teamMemberMapper,
            UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.permissionService = permissionService;
        this.teamMapper = teamMapper;
        this.teamMemberMapper = teamMemberMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
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
                    .eq(User::getStatus, 1)); // 只查询启用状态的用户

            // 4. 检查用户是否存在
            if (user == null) {
                throw new ServiceException(HttpEnums.USERNAME_PASSWORD_ERROR.getMessage());
            }

            // 5. 验证密码（注意：当前是明文比较，建议后续使用BCrypt加密）
            if (!password.equals(user.getPassword())) {
                throw new ServiceException(HttpEnums.USERNAME_PASSWORD_ERROR.getMessage());
            }

            // 6. 查询用户的主角色ID
            Integer roleId = userRoleMapper.selectPrimaryRoleIdByUserId(user.getId());
            if (roleId == null) {
                throw new ServiceException("用户未分配角色,请联系管理员");
            }

            // 7. 查询团队信息 (根据 roleId 判断)
            Integer teamId = null;
            try {
                if (roleId == 2) {
                    // 销售顾问：从 tb_team_member 查询
                    teamId = teamMemberMapper.selectTeamIdByUserId(user.getId());
                } else if (roleId == 3) {
                    // 销售经理：从 tb_team 查询
                    teamId = teamMapper.selectTeamIdByManagerId(user.getId());
                }
                // 管理员和财务人员的 teamId 保持为 null
            } catch (Exception e) {
                // 团队信息查询失败不影响登录，记录日志即可
                System.err.println("查询团队信息失败: " + e.getMessage());
            }

            // 8. 生成JWT token
            String token = jwtUtil.generateToken(
                    user.getId(),
                    user.getUsername(),
                    roleId, // 使用从 tb_user_role 查询的角色ID
                    user.getRealName(),
                    teamId);

            // 9. 获取用户权限（添加异常处理）
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

            // 10. 构建响应对象
            LoginResponseVo response = new LoginResponseVo();
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setRealName(user.getRealName());
            response.setAvatar(user.getAvatar());
            response.setPhone(user.getPhone());
            response.setEmail(user.getEmail());
            response.setSex(user.getSex());
            response.setRemark(user.getRemark());
            response.setToken(token);
            response.setRoleType(roleId); // 使用从 tb_user_role 查询的角色ID
            response.setTeamId(teamId);
            response.setAdmin(roleId != null && roleId == 1);
            response.setTeamManager(teamMapper.selectTeamIdByManagerId(user.getId()) != null);
            response.setMenus(menus != null ? menus : List.of());
            response.setPermissions(permissions != null ? permissions : List.of());

            return response;

        } catch (ServiceException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 处理其他异常，如数据库连接错误等
            e.printStackTrace(); // 打印堆栈跟踪
            System.err.println("AuthService登录异常: " + e.getMessage());
            throw new ServiceException(HttpEnums.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

}
