package com.guang.resms.module.user.controller;

import com.guang.resms.module.user.entity.vo.MenuTreeVO;
import com.guang.resms.module.user.service.MenuService;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.exception.ServiceException;
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
}
