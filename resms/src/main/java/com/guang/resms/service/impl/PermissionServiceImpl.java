package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.Permission;
import com.guang.resms.entity.RolePermission;
import com.guang.resms.entity.UserRole;
import com.guang.resms.entity.vo.MenuVO;
import com.guang.resms.entity.vo.PermissionVO;
import com.guang.resms.mapper.PermissionMapper;
import com.guang.resms.mapper.RolePermissionMapper;
import com.guang.resms.mapper.UserRoleMapper;
import com.guang.resms.service.PermissionService;
import com.guang.resms.utils.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 *
 * @author guang
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(UserRoleMapper userRoleMapper,
                                RolePermissionMapper rolePermissionMapper,
                                PermissionMapper permissionMapper) {
        this.userRoleMapper = userRoleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<MenuVO> getUserMenus(Integer userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        // 1. 获取用户角色
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> roleIds = userRoles.stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toList());

        // 2. 获取角色权限关联
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<RolePermission>()
                .in(RolePermission::getRoleId, roleIds)
        );

        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> permissionIds = rolePermissions.stream()
            .map(RolePermission::getPermissionId)
            .distinct()
            .collect(Collectors.toList());

        // 3. 获取权限信息并转换为菜单结构
        List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);

        return buildMenuTree(permissions);
    }

    @Override
    public List<String> getUserPermissions(Integer userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        // 1. 获取用户角色
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> roleIds = userRoles.stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toList());

        // 2. 获取角色权限关联
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<RolePermission>()
                .in(RolePermission::getRoleId, roleIds)
        );

        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> permissionIds = rolePermissions.stream()
            .map(RolePermission::getPermissionId)
            .distinct()
            .collect(Collectors.toList());

        // 3. 获取所有权限（包括菜单权限和按钮权限）
        List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);

        // 4. 提取所有权限码（不仅仅是菜单权限）
        return permissions.stream()
            .filter(p -> StringUtils.hasText(p.getPermissionCode()))
            .map(Permission::getPermissionCode)
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(Integer userId, String permission) {
        if (userId == null || !StringUtils.hasText(permission)) {
            return false;
        }

        List<String> userPermissions = getUserPermissions(userId);
        return userPermissions.contains(permission);
    }

    @Override
    public List<String> getUserRolePermissions(Integer userId) {
        return getUserPermissions(userId);
    }

    /**
     * 构建菜单树结构
     */
    private List<MenuVO> buildMenuTree(List<Permission> permissions) {
        // 只处理菜单类型的权限（type=1）
        List<Permission> menuPermissions = permissions.stream()
            .filter(p -> p.getPermissionType() != null && p.getPermissionType() == 1)
            .collect(Collectors.toList());

        // 获取根菜单（parentId为null或0）
        List<MenuVO> rootMenus = menuPermissions.stream()
            .filter(p -> p.getParentId() == null || p.getParentId() == 0)
            .map(this::convertToMenuVO)
            .collect(Collectors.toList());

        // 递归构建子菜单
        rootMenus.forEach(menu -> buildChildren(menu, menuPermissions));

        return rootMenus;
    }

    /**
     * 递归构建子菜单
     */
    private void buildChildren(MenuVO parent, List<Permission> allPermissions) {
        List<MenuVO> children = allPermissions.stream()
            .filter(p -> parent.getId().equals(p.getParentId()))
            .map(this::convertToMenuVO)
            .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parent.setChildren(children);
            children.forEach(child -> buildChildren(child, allPermissions));
        }
    }

    /**
     * 将Permission实体转换为MenuVO
     */
    private MenuVO convertToMenuVO(Permission permission) {
        return MenuVO.builder()
            .id(permission.getId())
            .name(permission.getPermissionName())
            .code(permission.getPermissionCode())
            .type(permission.getPermissionType())
            .parentId(permission.getParentId())
            .path(permission.getPath())
            .component(permission.getComponent())
            .icon(permission.getIcon())
            .sortOrder(permission.getSortOrder())
            .description(null) // Permission表中没有description字段
            .build();
    }

    /**
     * 递归收集所有权限代码
     */
    private void collectPermissionCodes(List<MenuVO> menus, List<String> permissions) {
        for (MenuVO menu : menus) {
            if (StringUtils.hasText(menu.getCode())) {
                permissions.add(menu.getCode());
            }
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                collectPermissionCodes(menu.getChildren(), permissions);
            }
        }
    }

    @Override
    public Page<PermissionVO> getPermissionPage(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        Page<Permission> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();

        // 条件查询
        if (params.containsKey("permissionName") && StringUtils.hasText((String) params.get("permissionName"))) {
            wrapper.like(Permission::getPermissionName, params.get("permissionName"));
        }
        if (params.containsKey("permissionCode") && StringUtils.hasText((String) params.get("permissionCode"))) {
            wrapper.like(Permission::getPermissionCode, params.get("permissionCode"));
        }
        if (params.containsKey("type")) {
            wrapper.eq(Permission::getPermissionType, params.get("type"));
        }
        if (params.containsKey("status")) {
            wrapper.eq(Permission::getStatus, params.get("status"));
        }

        // 排序
        wrapper.orderByAsc(Permission::getSortOrder).orderByAsc(Permission::getId);

        Page<Permission> permissionPage = permissionMapper.selectPage(page, wrapper);

        // 转换为VO
        List<PermissionVO> permissionVOs = permissionPage.getRecords().stream()
            .map(this::convertToPermissionVO)
            .collect(Collectors.toList());

        Page<PermissionVO> resultPage = new Page<>(permissionPage.getCurrent(),
                                                  permissionPage.getSize(),
                                                  permissionPage.getTotal());
        resultPage.setRecords(permissionVOs);

        return resultPage;
    }

    @Override
    public PermissionVO getPermissionDetail(Integer id) {
        Permission permission = permissionMapper.selectById(id);
        return permission != null ? convertToPermissionVO(permission) : null;
    }

    @Override
    public List<PermissionVO> getPermissionTree() {
        // 获取所有权限
        List<Permission> permissions = permissionMapper.selectList(
            new LambdaQueryWrapper<Permission>()
                .eq(Permission::getStatus, 1) // 只获取启用的权限
                .orderByAsc(Permission::getSortOrder)
        );

        // 转换为VO
        List<PermissionVO> permissionVOs = permissions.stream()
            .map(this::convertToPermissionVO)
            .collect(Collectors.toList());

        // 构建树形结构
        return buildPermissionTree(permissionVOs);
    }

    @Override
    public List<PermissionVO> getMenuTree() {
        // 只获取菜单类型的权限（permission_type = 1）
        List<Permission> permissions = permissionMapper.selectList(
            new LambdaQueryWrapper<Permission>()
                .eq(Permission::getPermissionType, 1) // 只获取菜单类型
                .orderByAsc(Permission::getSortOrder)
        );

        // 转换为VO
        List<PermissionVO> permissionVOs = permissions.stream()
            .map(this::convertToPermissionVO)
            .collect(Collectors.toList());

        // 构建树形结构
        return buildPermissionTree(permissionVOs);
    }

    @Override
    public void savePermission(Permission permission) {
        // 验证权限编码唯一性
        if (!isPermissionCodeAvailable(permission.getPermissionCode(), null)) {
            throw new ServiceException("权限编码已存在");
        }

        // 设置默认值
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }

        permissionMapper.insert(permission);
    }

    @Override
    public void updatePermission(Permission permission) {
        // 验证权限编码唯一性
        if (!isPermissionCodeAvailable(permission.getPermissionCode(), permission.getId())) {
            throw new ServiceException("权限编码已存在");
        }

        permissionMapper.updateById(permission);
    }

    @Override
    public void deletePermission(Integer id) {
        // 检查是否有子权限
        List<Permission> children = permissionMapper.selectList(
            new LambdaQueryWrapper<Permission>()
                .eq(Permission::getParentId, id)
        );

        if (!children.isEmpty()) {
            throw new ServiceException("存在子权限，无法删除");
        }

        // 检查是否被角色使用
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getPermissionId, id)
        );

        if (!rolePermissions.isEmpty()) {
            throw new ServiceException("权限正在被角色使用，无法删除");
        }

        permissionMapper.deleteById(id);
    }

    @Override
    public void batchDeletePermissions(List<Integer> permissionIds) {
        for (Integer permissionId : permissionIds) {
            deletePermission(permissionId);
        }
    }

    @Override
    public void updatePermissionStatus(List<Integer> permissionIds, Integer status) {
        LambdaUpdateWrapper<Permission> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Permission::getId, permissionIds)
               .set(Permission::getStatus, status);

        permissionMapper.update(null, wrapper);
    }

    @Override
    public boolean isPermissionNameAvailable(String permissionName, Integer excludeId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionName, permissionName);

        if (excludeId != null) {
            wrapper.ne(Permission::getId, excludeId);
        }

        return permissionMapper.selectCount(wrapper) == 0;
    }

    @Override
    public boolean isPermissionCodeAvailable(String permissionCode, Integer excludeId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permissionCode);

        if (excludeId != null) {
            wrapper.ne(Permission::getId, excludeId);
        }

        return permissionMapper.selectCount(wrapper) == 0;
    }

    /**
     * 将Permission实体转换为PermissionVO
     */
    private PermissionVO convertToPermissionVO(Permission permission) {
        String typeText = "";
        switch (permission.getPermissionType()) {
            case 1:
                typeText = "菜单";
                break;
            case 2:
                typeText = "按钮/接口";
                break;
            default:
                typeText = "未知";
        }

        String statusText = permission.getStatus() != null && permission.getStatus() == 1 ? "正常" : "禁用";

        return PermissionVO.builder()
            .id(permission.getId())
            .permissionName(permission.getPermissionName())
            .permissionCode(permission.getPermissionCode())
            .parentId(permission.getParentId())
            .permissionType(permission.getPermissionType())
            .typeText(typeText)
            .path(permission.getPath())
            .component(permission.getComponent())
            .icon(permission.getIcon())
            .sortOrder(permission.getSortOrder())
            .description(permission.getDescription())
            .status(permission.getStatus())
            .statusText(statusText)
            .createTime(permission.getCreateTime())
            .updateTime(permission.getUpdateTime())
            .children(new ArrayList<>())
            .build();
    }

    /**
     * 构建权限树形结构
     */
    private List<PermissionVO> buildPermissionTree(List<PermissionVO> permissions) {
        Map<Integer, PermissionVO> permissionMap = new HashMap<>();
        List<PermissionVO> rootPermissions = new ArrayList<>();

        // 将所有权限放入Map中
        for (PermissionVO permission : permissions) {
            permissionMap.put(permission.getId(), permission);
        }

        // 构建树形结构
        for (PermissionVO permission : permissions) {
            if (permission.getParentId() == null || permission.getParentId() == 0) {
                // 根权限
                rootPermissions.add(permission);
            } else {
                // 子权限
                PermissionVO parent = permissionMap.get(permission.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(permission);
                }
            }
        }

        // 对子权限进行排序
        sortPermissionTree(rootPermissions);

        return rootPermissions;
    }

    /**
     * 对权限树进行排序
     */
    private void sortPermissionTree(List<PermissionVO> permissions) {
        permissions.sort(Comparator.comparing(PermissionVO::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                              .thenComparing(PermissionVO::getId));

        for (PermissionVO permission : permissions) {
            if (permission.getChildren() != null && !permission.getChildren().isEmpty()) {
                sortPermissionTree(permission.getChildren());
            }
        }
    }

    @Override
    public void updateMenuSort(List<Map<String, Object>> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            throw new ServiceException("排序列表不能为空");
        }

        for (Map<String, Object> item : sortList) {
            Integer id = (Integer) item.get("id");
            Integer sortOrder = (Integer) item.get("sortOrder");

            if (id == null || id <= 0) {
                throw new ServiceException("菜单ID无效");
            }

            if (sortOrder == null || sortOrder < 0) {
                throw new ServiceException("排序号无效");
            }

            Permission permission = permissionMapper.selectById(id);
            if (permission == null) {
                throw new ServiceException("菜单不存在，ID: " + id);
            }

            permission.setSortOrder(sortOrder);
            permissionMapper.updateById(permission);
        }
    }
}
