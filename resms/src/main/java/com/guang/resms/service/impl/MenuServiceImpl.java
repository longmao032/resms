package com.guang.resms.service.impl;

import com.guang.resms.entity.Permission;
import com.guang.resms.entity.RolePermission;
import com.guang.resms.entity.dto.MenuDTO;
import com.guang.resms.entity.vo.MenuTreeVO;
import com.guang.resms.mapper.MenuMapper;
import com.guang.resms.mapper.RolePermissionMapper;
import com.guang.resms.service.MenuService;
import com.guang.resms.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 *
 * @author guang
 */
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public MenuServiceImpl(MenuMapper menuMapper, RolePermissionMapper rolePermissionMapper) {
        this.menuMapper = menuMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public List<MenuTreeVO> getMenuTree(Map<String, Object> params) {
        // 查询所有菜单
        List<MenuTreeVO> menuList = menuMapper.selectMenuList(params);

        // 构建树形结构
        return buildMenuTree(menuList, 0);
    }

    @Override
    public MenuTreeVO getMenuById(Integer id) {
        if (id == null || id <= 0) {
            throw new ServiceException("菜单ID不能为空");
        }

        MenuTreeVO menu = menuMapper.selectMenuById(id);
        if (menu == null) {
            throw new ServiceException("菜单不存在");
        }

        // 设置类型和状态文本
        menu.setMenuTypeText(getMenuTypeText(menu.getMenuType()));
        menu.setStatusText(getStatusText(menu.getStatus()));

        return menu;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveMenu(MenuDTO menuDTO) {
        // 数据验证
        validateMenuData(menuDTO, false);

        // 检查菜单编码唯一性
        if (!isMenuCodeUnique(menuDTO.getMenuCode(), null)) {
            throw new ServiceException("菜单编码已存在");
        }

        // 检查菜单名称唯一性（同父级下）
        if (!isMenuNameUnique(menuDTO.getMenuName(), menuDTO.getParentId(), null)) {
            throw new ServiceException("同级菜单名称已存在");
        }

        // 转换为实体类
        Permission permission = new Permission();
        permission.setPermissionName(menuDTO.getMenuName());
        permission.setPermissionCode(menuDTO.getMenuCode());
        permission.setParentId(menuDTO.getParentId() != null ? menuDTO.getParentId() : 0);
        permission.setPermissionType(menuDTO.getMenuType());
        permission.setPath(menuDTO.getPath());
        permission.setComponent(menuDTO.getComponent());
        permission.setIcon(menuDTO.getIcon());
        permission.setSortOrder(menuDTO.getSortOrder() != null ? menuDTO.getSortOrder() : 0);
        permission.setDescription(menuDTO.getDescription());
        permission.setStatus(menuDTO.getStatus() != null ? menuDTO.getStatus() : 1);

        int result = menuMapper.insert(permission);

        if (result > 0) {
            // 自动给管理员角色(id=1)分配权限
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(1); // 系统管理员ID
            rolePermission.setPermissionId(permission.getId());
            rolePermissionMapper.insert(rolePermission);
        }

        log.info("新增菜单：{}", menuDTO.getMenuName());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(MenuDTO menuDTO) {
        if (menuDTO.getId() == null || menuDTO.getId() <= 0) {
            throw new ServiceException("菜单ID不能为空");
        }

        // 检查菜单是否存在
        Permission existMenu = menuMapper.selectById(menuDTO.getId());
        if (existMenu == null) {
            throw new ServiceException("菜单不存在");
        }

        // 数据验证
        validateMenuData(menuDTO, true);

        // 不能将父菜单设置为自己
        if (menuDTO.getParentId() != null && menuDTO.getId().equals(menuDTO.getParentId())) {
            throw new ServiceException("不能将父菜单设置为自己");
        }

        // 不能将父菜单设置为自己的子孙菜单
        if (menuDTO.getParentId() != null && !menuDTO.getParentId().equals(0)) {
            List<Integer> childrenIds = menuMapper.selectChildrenIds(menuDTO.getId());
            if (childrenIds.contains(menuDTO.getParentId())) {
                throw new ServiceException("不能将父菜单设置为自己的子菜单");
            }
        }

        // 检查菜单编码唯一性
        if (!isMenuCodeUnique(menuDTO.getMenuCode(), menuDTO.getId())) {
            throw new ServiceException("菜单编码已存在");
        }

        // 检查菜单名称唯一性（同父级下）
        if (!isMenuNameUnique(menuDTO.getMenuName(), menuDTO.getParentId(), menuDTO.getId())) {
            throw new ServiceException("同级菜单名称已存在");
        }

        // 更新数据
        Permission permission = new Permission();
        permission.setId(menuDTO.getId());
        permission.setPermissionName(menuDTO.getMenuName());
        permission.setPermissionCode(menuDTO.getMenuCode());
        permission.setParentId(menuDTO.getParentId() != null ? menuDTO.getParentId() : 0);
        permission.setPermissionType(menuDTO.getMenuType());
        permission.setPath(menuDTO.getPath());
        permission.setComponent(menuDTO.getComponent());
        permission.setIcon(menuDTO.getIcon());
        permission.setSortOrder(menuDTO.getSortOrder() != null ? menuDTO.getSortOrder() : 0);
        permission.setDescription(menuDTO.getDescription());
        permission.setStatus(menuDTO.getStatus() != null ? menuDTO.getStatus() : 1);

        int result = menuMapper.updateById(permission);

        log.info("更新菜单：{}", menuDTO.getMenuName());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenu(Integer id) {
        if (id == null || id <= 0) {
            throw new ServiceException("菜单ID不能为空");
        }

        // 检查菜单是否存在
        Permission menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new ServiceException("菜单不存在");
        }

        // 检查是否有子菜单
        Integer childCount = menuMapper.countChildrenByParentId(id);
        if (childCount > 0) {
            throw new ServiceException("该菜单存在子菜单，无法删除");
        }

        // 检查是否有角色关联
        Integer roleCount = menuMapper.countRoleMenuByMenuId(id);
        if (roleCount > 0) {
            throw new ServiceException("该菜单已分配给角色，无法删除");
        }

        int result = menuMapper.deleteById(id);

        log.info("删除菜单：{}", menu.getPermissionName());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteMenus(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("菜单ID列表不能为空");
        }

        // 逐个删除，确保校验规则生效
        for (Integer id : ids) {
            deleteMenu(id);
        }

        log.info("批量删除菜单：{}", ids);
        return true;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenuSort(List<Map<String, Object>> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            throw new ServiceException("排序列表不能为空");
        }

        int result = menuMapper.batchUpdateSort(sortList);

        log.info("更新菜单排序：count={}", sortList.size());
        return result > 0;
    }

    @Override
    public boolean isMenuCodeUnique(String menuCode, Integer excludeId) {
        if (!StringUtils.hasText(menuCode)) {
            return false;
        }

        Integer count = menuMapper.checkMenuCodeUnique(menuCode, excludeId);
        return count == 0;
    }

    @Override
    public boolean isMenuNameUnique(String menuName, Integer parentId, Integer excludeId) {
        if (!StringUtils.hasText(menuName)) {
            return false;
        }

        Integer count = menuMapper.checkMenuNameUnique(menuName, parentId, excludeId);
        return count == 0;
    }

    @Override
    public List<MenuTreeVO> getParentMenuTree(Integer excludeId) {
        // 查询所有菜单类型的权限（排除按钮权限）
        Map<String, Object> params = new HashMap<>();
        params.put("menuType", 1); // 只查询菜单类型

        List<MenuTreeVO> menuList = menuMapper.selectMenuList(params);

        // 如果是编辑，需要排除自己及子孙节点
        if (excludeId != null && excludeId > 0) {
            List<Integer> excludeIds = new ArrayList<>();
            excludeIds.add(excludeId);

            // 获取所有子孙节点ID
            List<Integer> childrenIds = menuMapper.selectChildrenIds(excludeId);
            excludeIds.addAll(childrenIds);

            // 过滤掉排除的节点
            menuList = menuList.stream()
                    .filter(menu -> !excludeIds.contains(menu.getId()))
                    .collect(Collectors.toList());
        }

        // 添加顶级菜单选项
        MenuTreeVO rootMenu = MenuTreeVO.builder()
                .id(0)
                .menuName("顶级菜单")
                .menuCode("root")
                .parentId(-1)
                .build();

        // 构建树形结构
        List<MenuTreeVO> tree = buildMenuTree(menuList, 0);
        tree.add(0, rootMenu);

        return tree;
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
     * 数据验证
     */
    private void validateMenuData(MenuDTO menuDTO, boolean isUpdate) {
        if (!StringUtils.hasText(menuDTO.getMenuName())) {
            throw new ServiceException("菜单名称不能为空");
        }

        if (!StringUtils.hasText(menuDTO.getMenuCode())) {
            throw new ServiceException("菜单编码不能为空");
        }

        if (menuDTO.getMenuType() == null) {
            throw new ServiceException("菜单类型不能为空");
        }

        if (menuDTO.getMenuType() < 1 || menuDTO.getMenuType() > 3) {
            throw new ServiceException("菜单类型无效");
        }

        // 如果是菜单类型，路由路径不能为空
        if (menuDTO.getMenuType() == 1 && !StringUtils.hasText(menuDTO.getPath())) {
            throw new ServiceException("菜单路由路径不能为空");
        }
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
