package com.guang.resms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.User;
import com.guang.resms.entity.vo.UserVO;
import com.guang.resms.service.UserService;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.exception.ServiceException;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户列表
     */
    @PostMapping("/page")
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
    public ResponseResult<UserVO> getUserInfo() {
        try {
            Integer userId = com.guang.resms.common.utils.SecurityUtils.getUserId();
            if (userId == null) {
                return ResponseResult.fail(HttpEnums.UNAUTHORIZED.getCode(), "未登录");
            }
            return getUserDetail(userId);
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取当前用户信息失败");
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
     * @param roleType 角色类型：2=销售顾问, 3=销售经理
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
    public ResponseResult<Void> saveUser(@RequestBody User user) {
        try {
            // 基础验证
            if (user == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户信息不能为空");
            }

            // 调用服务保存用户
            userService.saveUser(user);
            return ResponseResult.success("新增用户成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "新增用户失败");
        }
    }

    /**
     * 更新用户
     */
    @PutMapping
    public ResponseResult<Void> updateUser(@RequestBody User user) {
        try {
            // 基础验证
            if (user == null || user.getId() == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户信息不能为空");
            }

            // 调用服务更新用户
            userService.updateUser(user);
            return ResponseResult.success("更新用户成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新用户失败");
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
    public ResponseResult<Void> updateUserPassword(@RequestBody Map<String, Object> params,
            HttpServletRequest request) {
        try {
            Integer userId = (Integer) params.get("userId");
            String oldPassword = (String) params.get("oldPassword");
            String newPassword = (String) params.get("newPassword");
            String confirmPassword = (String) params.get("confirmNewPassword");

            if (userId == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "用户ID不能为空");
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
