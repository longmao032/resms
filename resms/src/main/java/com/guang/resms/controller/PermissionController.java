package com.guang.resms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.Permission;
import com.guang.resms.entity.vo.PermissionVO;
import com.guang.resms.service.PermissionService;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.exception.ServiceException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限管理控制器
 *
 * @author guang
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 分页查询权限列表
     */
    @PostMapping("/page")
    public ResponseResult<Map<String, Object>> getPermissionPage(@RequestBody Map<String, Object> params) {
        try {
            // 构建分页参数
            Integer pageNum = (Integer) params.getOrDefault("pageNum", 1);
            Integer pageSize = (Integer) params.getOrDefault("pageSize", 10);

            // 构建查询参数
            Map<String, Object> queryParams = new HashMap<>();
            if (params.containsKey("permissionName") && StringUtils.hasText((String) params.get("permissionName"))) {
                queryParams.put("permissionName", params.get("permissionName"));
            }
            if (params.containsKey("permissionCode") && StringUtils.hasText((String) params.get("permissionCode"))) {
                queryParams.put("permissionCode", params.get("permissionCode"));
            }
            if (params.containsKey("permissionType")) {
                queryParams.put("permissionType", params.get("permissionType"));
            }
            if (params.containsKey("status")) {
                queryParams.put("status", params.get("status"));
            }

            // 执行分页查询
            Page<PermissionVO> page = permissionService.getPermissionPage(pageNum, pageSize, queryParams);

            // 构建响应
            Map<String, Object> result = new HashMap<>();
            result.put("records", page.getRecords());
            result.put("total", page.getTotal());
            result.put("current", page.getCurrent());
            result.put("size", page.getSize());
            result.put("pages", page.getPages());

            return ResponseResult.success("查询成功", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "查询权限列表失败");
        }
    }

    /**
     * 获取权限信息详情
     */
    @GetMapping("/{id}")
    public ResponseResult<PermissionVO> getPermissionDetail(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "权限ID不能为空");
            }

            PermissionVO permissionVO = permissionService.getPermissionDetail(id);
            if (permissionVO == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "权限不存在");
            }

            return ResponseResult.success("查询成功", permissionVO);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取权限信息失败");
        }
    }

    /**
     * 获取所有权限（树形结构）
     */
    @GetMapping("/tree")
    public ResponseResult<List<PermissionVO>> getPermissionTree() {
        try {
            List<PermissionVO> permissions = permissionService.getPermissionTree();
            return ResponseResult.success("查询成功", permissions);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取权限树失败");
        }
    }

    /**
     * 获取菜单树（只返回菜单类型的权限）
     */
    @GetMapping("/menu/tree")
    public ResponseResult<List<PermissionVO>> getMenuTree() {
        try {
            List<PermissionVO> menus = permissionService.getMenuTree();
            return ResponseResult.success("查询成功", menus);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取菜单树失败");
        }
    }

    /**
     * 新增权限
     */
    @PostMapping
    public ResponseResult<Void> savePermission(@RequestBody Permission permission) {
        try {
            // 基础验证
            if (permission == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "权限信息不能为空");
            }

            // 调用服务保存权限
            permissionService.savePermission(permission);
            return ResponseResult.success("新增权限成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "新增权限失败");
        }
    }

    /**
     * 更新权限
     */
    @PutMapping
    public ResponseResult<Void> updatePermission(@RequestBody Permission permission) {
        try {
            // 基础验证
            if (permission == null || permission.getId() == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "权限信息不能为空");
            }

            // 调用服务更新权限
            permissionService.updatePermission(permission);
            return ResponseResult.success("更新权限成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新权限失败");
        }
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deletePermission(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "权限ID不能为空");
            }

            permissionService.deletePermission(id);
            return ResponseResult.success("删除权限成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "删除权限失败");
        }
    }

    /**
     * 批量删除权限
     */
    @DeleteMapping("/batch")
    public ResponseResult<Void> batchDeletePermissions(@RequestBody Map<String, List<Integer>> params) {
        try {
            List<Integer> permissionIds = params.get("permissionIds");
            if (permissionIds == null || permissionIds.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "权限ID列表不能为空");
            }

            permissionService.batchDeletePermissions(permissionIds);
            return ResponseResult.success("批量删除权限成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "批量删除权限失败");
        }
    }

    /**
     * 更新权限状态
     */
    @PutMapping("/status")
    public ResponseResult<Void> updatePermissionStatus(@RequestBody Map<String, Object> params) {
        try {
            List<Integer> permissionIds = (List<Integer>) params.get("permissionIds");
            Integer status = (Integer) params.get("status");

            if (permissionIds == null || permissionIds.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "权限ID列表不能为空");
            }
            if (status == null || (status != 0 && status != 1)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "状态值无效");
            }

            permissionService.updatePermissionStatus(permissionIds, status);
            return ResponseResult.success("更新权限状态成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新权限状态失败");
        }
    }

    /**
     * 检查权限名称是否可用
     */
    @GetMapping("/check-permission-name")
    public ResponseResult<Map<String, Boolean>> checkPermissionName(@RequestParam String permissionName,
                                                                   @RequestParam(required = false) Integer excludeId) {
        try {
            if (!StringUtils.hasText(permissionName)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "权限名称不能为空");
            }

            boolean available = permissionService.isPermissionNameAvailable(permissionName, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", available);

            return ResponseResult.success("检查完成", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "检查权限名称失败");
        }
    }

    /**
     * 检查权限编码是否可用
     */
    @GetMapping("/check-permission-code")
    public ResponseResult<Map<String, Boolean>> checkPermissionCode(@RequestParam String permissionCode,
                                                                   @RequestParam(required = false) Integer excludeId) {
        try {
            if (!StringUtils.hasText(permissionCode)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "权限编码不能为空");
            }

            boolean available = permissionService.isPermissionCodeAvailable(permissionCode, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", available);

            return ResponseResult.success("检查完成", result);

        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "检查权限编码失败");
        }
    }

    /**
     * 更新菜单排序
     */
    @PutMapping("/sort")
    public ResponseResult<Void> updateMenuSort(@RequestBody Map<String, List<Map<String, Object>>> params) {
        try {
            List<Map<String, Object>> sortList = params.get("sortList");
            if (sortList == null || sortList.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "排序列表不能为空");
            }

            permissionService.updateMenuSort(sortList);
            return ResponseResult.success("更新菜单排序成功");

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新菜单排序失败");
        }
    }
}
