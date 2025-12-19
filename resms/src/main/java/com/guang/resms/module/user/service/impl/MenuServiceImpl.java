package com.guang.resms.module.user.service.impl;

import com.guang.resms.module.user.entity.vo.MenuTreeVO;
import com.guang.resms.module.user.mapper.MenuMapper;
import com.guang.resms.module.user.service.MenuService;
import com.guang.resms.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 菜单服务实现类
 *
 * @author guang
 */
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    public MenuServiceImpl(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    @Override
    public List<MenuTreeVO> getMenuTree(Map<String, Object> params) {
        // 查询所有菜单
        List<MenuTreeVO> menuList = menuMapper.selectMenuList(params);

        // 构建树形结构
        return buildMenuTree(menuList, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenuStatus(List<Integer> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("菜单ID列表不能为空");
        }

        if (status == null || (status != 0 && status != 1)) {
            throw new ServiceException("状态值无效");
        }

        int result = menuMapper.updateMenuStatus(ids, status);

        log.info("更新菜单状态：ids={}, status={}", ids, status);
        return result > 0;
    }

    /**
     * 构建菜单树形结构
     *
     * @param menuList 菜单列表
     * @param parentId 父菜单ID
     * @return 树形菜单列表
     */
    private List<MenuTreeVO> buildMenuTree(List<MenuTreeVO> menuList, Integer parentId) {
        List<MenuTreeVO> treeList = new ArrayList<>();

        for (MenuTreeVO menu : menuList) {
            if (menu.getParentId().equals(parentId)) {
                // 设置类型和状态文本
                menu.setMenuTypeText(getMenuTypeText(menu.getMenuType()));
                menu.setStatusText(getStatusText(menu.getStatus()));

                // 递归查找子菜单
                List<MenuTreeVO> children = buildMenuTree(menuList, menu.getId());
                menu.setChildren(children);
                menu.setHasChildren(!children.isEmpty());

                treeList.add(menu);
            }
        }

        // 按排序号排序
        treeList.sort(Comparator.comparing(MenuTreeVO::getSortOrder,
                Comparator.nullsLast(Comparator.naturalOrder())));

        return treeList;
    }

    /**
     * 获取菜单类型文本
     */
    private String getMenuTypeText(Integer type) {
        if (type == null) {
            return "未知";
        }
        switch (type) {
            case 1:
                return "菜单";
            case 2:
                return "按钮";
            case 3:
                return "数据";
            default:
                return "未知";
        }
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "启用" : "禁用";
    }
}
