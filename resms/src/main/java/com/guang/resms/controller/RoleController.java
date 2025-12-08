package com.guang.resms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.Role;
import com.guang.resms.entity.vo.RoleVO;
import com.guang.resms.service.RoleService;
import com.guang.resms.utils.exception.HttpEnums;
import com.guang.resms.utils.exception.ResponseResult;
import com.guang.resms.utils.exception.ServiceException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 *
 * @author guang
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 分页查询角色列表
     */
    @PostMapping("/page")
    public ResponseResult<Map<String, Object>> getRolePage(@RequestBody Map<String, Object> params) {
        try {
            // 构建分页参数
            Integer pageNum = (Integer) params.getOrDefault("pageNum", 1);
            Integer pageSize = (Integer) params.getOrDefault("pageSize", 10);

            // 构建查询参数
            Map<String, Object> queryParams = new HashMap<>();
            if (params.containsKey("roleName") && StringUtils.hasText((String) params.get("roleName"))) {
                queryParams.put("roleName", params.get("roleName"));
            }
            if (params.containsKey("roleCode") && StringUtils.hasText((String) params.get("roleCode"))) {
                queryParams.put("roleCode", params.get("roleCode"));
            }
            if (params.containsKey("status")) {
                queryParams.put("status", params.get("status"));
            }

            // 执行分页查询
            Page<RoleVO> page = roleService.getRolePage(pageNum, pageSize, queryParams);

            // 构建响应
            Map<String, Object> result = new HashMap<>();
            result.put("records", page.getRecords());
            result.put("total", page.getTotal());
            result.put("current", page.getCurrent());
            result.put("size", page.getSize());
            result.put("pages", page.getPages());

            return ResponseResult.success("查询成功", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "查询角色列表失败");
        }
    }

    /**
     * 获取角色信息详情
     */
    @GetMapping("/{id}")
    public ResponseResult<RoleVO> getRoleDetail(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色ID不能为空");
            }

            RoleVO roleVO = roleService.getRoleDetail(id);
            if (roleVO == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色不存在");
            }

            return ResponseResult.success("查询成功", roleVO);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取角色信息失败");
        }
    }

    /**
     * 获取所有角色（下拉框使用）
     */
    @GetMapping("/all")
    public ResponseResult<List<RoleVO>> getAllRoles() {
        try {
            List<RoleVO> roles = roleService.getAllRoles();
            return ResponseResult.success("查询成功", roles);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取角色列表失败");
        }
    }

    /**
     * 新增角色
     */
    @PostMapping
    public ResponseResult<Void> saveRole(@RequestBody Role role) {
        try {
            // 基础验证
            if (role == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色信息不能为空");
            }

            // 调用服务保存角色
            roleService.saveRole(role);
            return ResponseResult.success("新增角色成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "新增角色失败");
        }
    }

    /**
     * 更新角色
     */
    @PutMapping
    public ResponseResult<Void> updateRole(@RequestBody Role role) {
        try {
            // 基础验证
            if (role == null || role.getId() == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色信息不能为空");
            }

            // 调用服务更新角色
            roleService.updateRole(role);
            return ResponseResult.success("更新角色成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新角色失败");
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteRole(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色ID不能为空");
            }

            roleService.deleteRole(id);
            return ResponseResult.success("删除角色成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "删除角色失败");
        }
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    public ResponseResult<Void> batchDeleteRoles(@RequestBody Map<String, List<Integer>> params) {
        try {
            List<Integer> roleIds = params.get("roleIds");
            if (roleIds == null || roleIds.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色ID列表不能为空");
            }

            roleService.batchDeleteRoles(roleIds);
            return ResponseResult.success("批量删除角色成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "批量删除角色失败");
        }
    }

    /**
     * 配置角色权限
     */
    @PostMapping("/assign-permissions")
    public ResponseResult<Void> assignPermissions(@RequestBody Map<String, Object> params) {
        try {
            Integer roleId = (Integer) params.get("roleId");
            List<Integer> permissionIds = (List<Integer>) params.get("permissionIds");

            if (roleId == null || roleId <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色ID不能为空");
            }
            if (permissionIds == null) {
                permissionIds = List.of();
            }

            roleService.assignPermissions(roleId, permissionIds);
            return ResponseResult.success("权限配置成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "权限配置失败");
        }
    }

    /**
     * 获取角色的权限ID列表
     */
    @GetMapping("/{id}/permissions")
    public ResponseResult<List<Integer>> getRolePermissions(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色ID不能为空");
            }

            List<Integer> permissionIds = roleService.getRolePermissionIds(id);
            return ResponseResult.success("查询成功", permissionIds);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取角色权限失败");
        }
    }

    /**
     * 检查角色名称是否可用
     */
    @GetMapping("/check-role-name")
    public ResponseResult<Map<String, Boolean>> checkRoleName(@RequestParam String roleName,
                                                             @RequestParam(required = false) Integer excludeId) {
        try {
            if (!StringUtils.hasText(roleName)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色名称不能为空");
            }

            boolean available = roleService.isRoleNameAvailable(roleName, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", available);

            return ResponseResult.success("检查完成", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "检查角色名称失败");
        }
    }

    /**
     * 检查角色编码是否可用
     */
    @GetMapping("/check-role-code")
    public ResponseResult<Map<String, Boolean>> checkRoleCode(@RequestParam String roleCode,
                                                             @RequestParam(required = false) Integer excludeId) {
        try {
            if (!StringUtils.hasText(roleCode)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "角色编码不能为空");
            }

            boolean available = roleService.isRoleCodeAvailable(roleCode, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", available);

            return ResponseResult.success("检查完成", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "检查角色编码失败");
        }
    }
}
