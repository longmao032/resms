package com.guang.resms.module.user.controller;

import com.guang.resms.module.user.entity.dto.MenuDTO;
import com.guang.resms.module.user.entity.vo.MenuTreeVO;
import com.guang.resms.module.user.service.MenuService;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.exception.ServiceException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单管理控制器
 *
 * @author guang
 */
@Slf4j
@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 获取菜单树形列表
     */
    @GetMapping("/tree")
    public ResponseResult<List<MenuTreeVO>> getMenuTree(
            @RequestParam(required = false) String menuName,
            @RequestParam(required = false) String menuCode,
            @RequestParam(required = false) Integer menuType,
            @RequestParam(required = false) Integer status) {
        try {
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (StringUtils.hasText(menuName)) {
                params.put("menuName", menuName);
            }
            if (StringUtils.hasText(menuCode)) {
                params.put("menuCode", menuCode);
            }
            if (menuType != null) {
                params.put("menuType", menuType);
            }
            if (status != null) {
                params.put("status", status);
            }

            List<MenuTreeVO> menuTree = menuService.getMenuTree(params);
            return ResponseResult.success("查询成功", menuTree);

        } catch (Exception e) {
            log.error("获取菜单树失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取菜单树失败：" + e.getMessage());
        }
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    public ResponseResult<MenuTreeVO> getMenuById(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "菜单ID不能为空");
            }

            MenuTreeVO menu = menuService.getMenuById(id);
            return ResponseResult.success("查询成功", menu);

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取菜单详情失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取菜单详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取父菜单选择树
     */
    @GetMapping("/parent-tree")
    public ResponseResult<List<MenuTreeVO>> getParentMenuTree(
            @RequestParam(required = false) Integer excludeId) {
        try {
            List<MenuTreeVO> menuTree = menuService.getParentMenuTree(excludeId);
            return ResponseResult.success("查询成功", menuTree);

        } catch (Exception e) {
            log.error("获取父菜单树失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "获取父菜单树失败：" + e.getMessage());
        }
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public ResponseResult<Void> saveMenu(@Valid @RequestBody MenuDTO menuDTO) {
        try {
            boolean success = menuService.saveMenu(menuDTO);
            if (success) {
                return ResponseResult.success("新增菜单成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "新增菜单失败");
            }

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("新增菜单失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "新增菜单失败：" + e.getMessage());
        }
    }

    /**
     * 更新菜单
     */
    @PutMapping
    public ResponseResult<Void> updateMenu(@Valid @RequestBody MenuDTO menuDTO) {
        try {
            boolean success = menuService.updateMenu(menuDTO);
            if (success) {
                return ResponseResult.success("更新菜单成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新菜单失败");
            }

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新菜单失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新菜单失败：" + e.getMessage());
        }
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteMenu(@PathVariable Integer id) {
        try {
            boolean success = menuService.deleteMenu(id);
            if (success) {
                return ResponseResult.success("删除菜单成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "删除菜单失败");
            }

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除菜单失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "删除菜单失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除菜单
     */
    @DeleteMapping("/batch")
    public ResponseResult<Void> batchDeleteMenus(@RequestBody Map<String, List<Integer>> params) {
        try {
            List<Integer> ids = params.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "菜单ID列表不能为空");
            }

            boolean success = menuService.batchDeleteMenus(ids);
            if (success) {
                return ResponseResult.success("批量删除菜单成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "批量删除菜单失败");
            }

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("批量删除菜单失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "批量删除菜单失败：" + e.getMessage());
        }
    }

    /**
     * 更新菜单状态
     */
    @PutMapping("/status")
    public ResponseResult<Void> updateMenuStatus(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) params.get("ids");
            Integer status = (Integer) params.get("status");

            if (ids == null || ids.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "菜单ID列表不能为空");
            }
            if (status == null || (status != 0 && status != 1)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "状态值无效");
            }

            boolean success = menuService.updateMenuStatus(ids, status);
            if (success) {
                return ResponseResult.success("更新菜单状态成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新菜单状态失败");
            }

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新菜单状态失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新菜单状态失败：" + e.getMessage());
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

            boolean success = menuService.updateMenuSort(sortList);
            if (success) {
                return ResponseResult.success("更新菜单排序成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新菜单排序失败");
            }

        } catch (ServiceException e) {
            return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新菜单排序失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新菜单排序失败：" + e.getMessage());
        }
    }

    /**
     * 检查菜单编码是否唯一
     */
    @GetMapping("/check-code")
    public ResponseResult<Map<String, Boolean>> checkMenuCode(
            @RequestParam String menuCode,
            @RequestParam(required = false) Integer excludeId) {
        try {
            if (!StringUtils.hasText(menuCode)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "菜单编码不能为空");
            }

            boolean unique = menuService.isMenuCodeUnique(menuCode, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("unique", unique);

            return ResponseResult.success("检查完成", result);

        } catch (Exception e) {
            log.error("检查菜单编码失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "检查菜单编码失败：" + e.getMessage());
        }
    }

    /**
     * 检查菜单名称是否唯一
     */
    @GetMapping("/check-name")
    public ResponseResult<Map<String, Boolean>> checkMenuName(
            @RequestParam String menuName,
            @RequestParam Integer parentId,
            @RequestParam(required = false) Integer excludeId) {
        try {
            if (!StringUtils.hasText(menuName)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "菜单名称不能为空");
            }

            boolean unique = menuService.isMenuNameUnique(menuName, parentId, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("unique", unique);

            return ResponseResult.success("检查完成", result);

        } catch (Exception e) {
            log.error("检查菜单名称失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "检查菜单名称失败：" + e.getMessage());
        }
    }
}
