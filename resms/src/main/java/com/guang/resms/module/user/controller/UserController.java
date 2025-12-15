package com.guang.resms.module.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.auth.entity.vo.LoginResponseVo;
import com.guang.resms.module.user.entity.User;
import com.guang.resms.module.user.entity.vo.UserVO;
import com.guang.resms.module.user.mapper.UserRoleMapper;
import com.guang.resms.module.user.service.UserService;
import com.guang.resms.module.user.service.PermissionService;
import com.guang.resms.module.team.mapper.TeamMapper;
import com.guang.resms.module.team.mapper.TeamMemberMapper;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.shared.annotation.OpLog;
import com.guang.resms.shared.enums.RiskDimension;
import com.guang.resms.shared.enums.RiskLevel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 *
 * @author guang
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PermissionService permissionService;
    private final UserRoleMapper userRoleMapper;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;

    public UserController(UserService userService,
            PermissionService permissionService,
            UserRoleMapper userRoleMapper,
            TeamMapper teamMapper,
            TeamMemberMapper teamMemberMapper) {
        this.userService = userService;
        this.permissionService = permissionService;
        this.userRoleMapper = userRoleMapper;
        this.teamMapper = teamMapper;
        this.teamMemberMapper = teamMemberMapper;
    }

    /**
     * 分页查询用户列表
     */
    @PostMapping("/page")
    @OpLog(module = "用户管理", operationType = "查询用户", level = RiskLevel.NORMAL, dimensions = { RiskDimension.SYSTEM })
    public ResponseResult<Map<String, Object>> getUserPage(@RequestBody Map<String, Object> params) {
        try {
            // 构建分页参数
            Integer pageNum = (Integer) params.getOrDefault("pageNum", 1);
            Integer pageSize = (Integer) params.getOrDefault("pageSize", 10);

            // 构建查询参数
            Map<String, Object> queryParams = new HashMap<>();
            if (params.containsKey("username") && StringUtils.hasText((String) params.get("username"))) {
                queryParams.put("username", params.get("username"));
            }
            if (params.containsKey("realName") && StringUtils.hasText((String) params.get("realName"))) {
                queryParams.put("realName", params.get("realName"));
            }
            if (params.containsKey("phone") && StringUtils.hasText((String) params.get("phone"))) {
                queryParams.put("phone", params.get("phone"));
            }
            if (params.containsKey("email") && StringUtils.hasText((String) params.get("email"))) {
                queryParams.put("email", params.get("email"));
            }
            if (params.containsKey("roleType")) {
                queryParams.put("roleType", params.get("roleType"));
            }
            if (params.containsKey("status")) {
                queryParams.put("status", params.get("status"));
            }
            if (params.containsKey("createTimeBegin") && StringUtils.hasText((String) params.get("createTimeBegin"))) {
                queryParams.put("createTimeBegin", params.get("createTimeBegin"));
            }
            if (params.containsKey("createTimeEnd") && StringUtils.hasText((String) params.get("createTimeEnd"))) {
                queryParams.put("createTimeEnd", params.get("createTimeEnd"));
            }

            // 执行分页查询
            Page<UserVO> page = userService.getUserPage(pageNum, pageSize, queryParams);

            // 构建响应
            Map<String, Object> result = new HashMap<>();
            result.put("records", page.getRecords());
            result.put("total", page.getTotal());
            result.put("current", page.getCurrent());
            result.put("size", page.getSize());
            result.put("pages", page.getPages());

            return ResponseResult.success("查询成功", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "查询用户列表失败");
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    @OpLog(module = "个人中心", operationType = "获取用户信息", level = RiskLevel.NORMAL, dimensions = { RiskDimension.DATA })
    public ResponseResult<LoginResponseVo> getUserInfo() {
        try {
            Integer userId = com.guang.resms.common.utils.SecurityUtils.getUserId();
            if (userId == null) {
                return ResponseResult.fail(HttpEnums.UNAUTHORIZED.getCode(), "未登录");
            }

            UserVO userVO = userService.getUserDetail(userId);
            if (userVO == null) {
                return ResponseResult.fail(HttpEnums.USER_NOT_EXIST.getCode(), "用户不存在");
            }

            Integer roleId = userRoleMapper.selectPrimaryRoleIdByUserId(userId);
            Integer teamId = null;
            if (roleId != null && roleId == 3) {
                teamId = teamMapper.selectTeamIdByManagerId(userId);
            } else if (roleId != null && roleId == 2) {
                teamId = teamMemberMapper.selectTeamIdByUserId(userId);
            }

            LoginResponseVo vo = new LoginResponseVo();
            vo.setUserId(userVO.getId());
            vo.setUsername(userVO.getUsername());
            vo.setRealName(userVO.getRealName());
            vo.setAvatar(userVO.getAvatar());
            vo.setPhone(userVO.getPhone());
            vo.setEmail(userVO.getEmail());
            vo.setSex(userVO.getSex());
            vo.setRemark(userVO.getRemark());
            vo.setToken(null);
            vo.setRoleType(roleId);
            vo.setTeamId(teamId);
            vo.setAdmin(roleId != null && roleId == 1);
            vo.setTeamManager(teamMapper.selectTeamIdByManagerId(userId) != null);
            vo.setMenus(permissionService.getUserMenus(userId));
            vo.setPermissions(permissionService.getUserPermissions(userId));

            return ResponseResult.success("查询成功", vo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取当前用户信息失败");
        }
    }

    @PutMapping("/profile")
    @OpLog(module = "个人中心", operationType = "修改个人资料", level = RiskLevel.NORMAL, dimensions = { RiskDimension.DATA })
    public ResponseResult<Void> updateProfile(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = SecurityUtils.getUserId();
            if (userId == null) {
                return ResponseResult.fail(HttpEnums.UNAUTHORIZED.getCode(), "未登录");
            }

            UserVO exist = userService.getUserDetail(userId);
            if (exist == null) {
                return ResponseResult.fail(HttpEnums.USER_NOT_EXIST.getCode(), "用户不存在");
            }

            User user = new User();
            user.setId(userId);
            user.setUsername(exist.getUsername());
            user.setStatus(exist.getStatus());
            user.setPassword(null);

            user.setRealName((String) params.get("realName"));
            user.setPhone((String) params.get("phone"));
            user.setEmail((String) params.get("email"));
            user.setSex(params.get("sex") == null ? null : ((Number) params.get("sex")).intValue());
            user.setRemark((String) params.get("remark"));
            user.setAvatar((String) params.get("avatar"));

            userService.updateUser(user);
            return ResponseResult.success("个人资料更新成功");
        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新失败");
        }
    }

    /**
     * 获取用户信息详情
     */
    @GetMapping("/{id}")
    public ResponseResult<UserVO> getUserDetail(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户ID不能为空");
            }

            UserVO userVO = userService.getUserDetail(id);
            if (userVO == null) {
                return ResponseResult.fail(HttpEnums.USER_NOT_EXIST.getCode(), "用户不存在");
            }

            return ResponseResult.success("查询成功", userVO);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取用户信息失败");
        }
    }

    /**
     * 获取所有用户（下拉框使用）
     */
    @GetMapping("/all")
    public ResponseResult<List<UserVO>> getAllUsers() {
        try {
            List<UserVO> users = userService.getAllUsers();
            return ResponseResult.success("查询成功", users);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取用户列表失败");
        }
    }

    /**
     * 根据角色类型获取用户列表（用于团队管理中选择经理/成员）
     * 
     * @param roleType 角色类型：2=销售经理, 3=销售顾问
     */
    @GetMapping("/listByRole/{roleType}")
    public ResponseResult<List<UserVO>> getUsersByRole(@PathVariable Integer roleType) {
        try {
            if (roleType == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色类型不能为空");
            }

            List<UserVO> users = userService.getUsersByRoleType(roleType);
            return ResponseResult.success("查询成功", users);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取用户列表失败");
        }
    }

    /**
     * 新增用户
     */
    @PostMapping
    public ResponseResult<Void> saveUser(@RequestBody Map<String, Object> params) {
        try {
            // 基础验证
            if (params == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户信息不能为空");
            }

            // 获取角色ID (前端使用 roleType 字段传递)
            Integer roleId = null;
            Object roleIdObj = params.get("roleType");
            if (roleIdObj instanceof Number) {
                roleId = ((Number) roleIdObj).intValue();
            } else if (roleIdObj instanceof String && StringUtils.hasText((String) roleIdObj)) {
                roleId = Integer.parseInt(((String) roleIdObj).trim());
            }
            if (roleId == null || roleId <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "请选择角色");
            }

            // 构建 User 对象
            User user = new User();
            user.setUsername((String) params.get("username"));
            user.setPassword((String) params.get("password"));
            user.setRealName((String) params.get("realName"));
            user.setPhone((String) params.get("phone"));
            user.setEmail((String) params.get("email"));
            user.setAvatar((String) params.get("avatar"));
            user.setStatus((Integer) params.get("status"));

            // 调用服务保存用户
            userService.saveUser(user);

            // 创建用户角色关联
            userService.createUserRoleAssociation(user.getId(), roleId);

            return ResponseResult.success("新增用户成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "新增用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PutMapping
    public ResponseResult<Void> updateUser(@RequestBody Map<String, Object> params) {
        try {
            // 基础验证
            if (params == null || params.get("id") == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户信息不能为空");
            }

            // 获取角色ID (前端使用 roleType 字段传递)
            Integer roleId = null;
            Object roleIdObj = params.get("roleType");
            if (roleIdObj instanceof Number) {
                roleId = ((Number) roleIdObj).intValue();
            } else if (roleIdObj instanceof String && StringUtils.hasText((String) roleIdObj)) {
                roleId = Integer.parseInt(((String) roleIdObj).trim());
            }
            if (roleId == null || roleId <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "请选择角色");
            }

            // 构建 User 对象
            User user = new User();
            user.setId((Integer) params.get("id"));
            user.setUsername((String) params.get("username"));
            user.setPassword((String) params.get("password"));
            user.setRealName((String) params.get("realName"));
            user.setPhone((String) params.get("phone"));
            user.setEmail((String) params.get("email"));
            user.setAvatar((String) params.get("avatar"));
            user.setStatus((Integer) params.get("status"));

            // 调用服务更新用户
            userService.updateUser(user);

            // 更新用户角色关联
            userService.updateUserRoleAssociation(user.getId(), roleId);

            return ResponseResult.success("更新用户成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteUser(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户ID不能为空");
            }

            userService.deleteUser(id);
            return ResponseResult.success("删除用户成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "删除用户失败");
        }
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public ResponseResult<Void> batchDeleteUsers(@RequestBody Map<String, List<Integer>> params) {
        try {
            List<Integer> userIds = params.get("userIds");
            if (userIds == null || userIds.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户ID列表不能为空");
            }

            userService.batchDeleteUsers(userIds);
            return ResponseResult.success("批量删除用户成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "批量删除用户失败");
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/status")
    @OpLog(module = "用户管理", operationType = "更新用户状态", level = RiskLevel.HIGH, dimensions = { RiskDimension.SYSTEM }, notifyAdmin = true)
    public ResponseResult<Void> updateUserStatus(@RequestBody Map<String, Object> params) {
        try {
            List<Integer> userIds = (List<Integer>) params.get("userIds");
            Integer status = (Integer) params.get("status");

            if (userIds == null || userIds.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户ID列表不能为空");
            }
            if (status == null || (status != 0 && status != 1)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "状态值无效");
            }

            userService.updateUserStatus(userIds, status);
            return ResponseResult.success("更新用户状态成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新用户状态失败");
        }
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/reset-password/{id}")
    @OpLog(module = "用户管理", operationType = "重置密码", level = RiskLevel.HIGH, dimensions = { RiskDimension.SYSTEM }, notifyAdmin = true)
    public ResponseResult<Void> resetUserPassword(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户ID不能为空");
            }

            userService.resetUserPassword(id);
            return ResponseResult.success("重置密码成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "重置密码失败");
        }
    }

    /**
     * 更新用户密码
     */
    @PutMapping("/password")
    @OpLog(module = "个人中心", operationType = "修改密码", level = RiskLevel.HIGH, dimensions = { RiskDimension.SYSTEM }, notifyAdmin = true)
    public ResponseResult<Void> updateUserPassword(@RequestBody Map<String, Object> params,
            HttpServletRequest request) {
        try {
            Integer userId = SecurityUtils.getUserId();
            String oldPassword = (String) params.get("oldPassword");
            String newPassword = (String) params.get("newPassword");
            String confirmPassword = (String) params.get("confirmNewPassword");

            if (userId == null) {
                return ResponseResult.fail(HttpEnums.UNAUTHORIZED.getCode(), "未登录");
            }
            if (!StringUtils.hasText(oldPassword)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "原密码不能为空");
            }
            if (!StringUtils.hasText(newPassword)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "新密码不能为空");
            }
            if (!newPassword.equals(confirmPassword)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "两次输入的新密码不一致");
            }

            userService.updateUserPassword(userId, oldPassword, newPassword);
            return ResponseResult.success("密码修改成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "密码修改失败");
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public ResponseResult<Map<String, Boolean>> checkUsername(@RequestParam String username,
            @RequestParam(required = false) Integer excludeId) {
        try {
            if (!StringUtils.hasText(username)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户名不能为空");
            }

            boolean available = userService.isUsernameAvailable(username, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", available);

            return ResponseResult.success("检查完成", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "检查用户名失败");
        }
    }

    /**
     * 检查手机号是否可用
     */
    @GetMapping("/check-phone")
    public ResponseResult<Map<String, Boolean>> checkPhone(@RequestParam String phone,
            @RequestParam(required = false) Integer excludeId) {
        try {
            if (!StringUtils.hasText(phone)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "手机号不能为空");
            }

            boolean available = userService.isPhoneAvailable(phone, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", available);

            return ResponseResult.success("检查完成", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "检查手机号失败");
        }
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public ResponseResult<Map<String, Boolean>> checkEmail(@RequestParam String email,
            @RequestParam(required = false) Integer excludeId) {
        try {
            if (!StringUtils.hasText(email)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "邮箱不能为空");
            }

            boolean available = userService.isEmailAvailable(email, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", available);

            return ResponseResult.success("检查完成", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "检查邮箱失败");
        }
    }

    /**
     * 根据角色获取启用状态的用户列表（用于下拉框）
     * * @param roleType 角色ID
     * 
     * @return {@link ResponseResult}
     */
    @GetMapping("/teamListByRole/{roleType}")
    public ResponseResult<List<UserVO>> getListByRole(@PathVariable Integer roleType) {
        List<UserVO> list = userService.getEnableUsersByRole(roleType);
        return ResponseResult.success(list);
    }
}
