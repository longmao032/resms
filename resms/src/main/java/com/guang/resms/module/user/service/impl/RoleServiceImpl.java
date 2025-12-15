package com.guang.resms.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.module.user.entity.Role;
import com.guang.resms.module.user.entity.RolePermission;
import com.guang.resms.module.user.entity.UserRole;
import com.guang.resms.module.user.entity.vo.RoleVO;
import com.guang.resms.module.user.mapper.PermissionMapper;
import com.guang.resms.module.user.mapper.RoleMapper;
import com.guang.resms.module.user.mapper.RolePermissionMapper;
import com.guang.resms.module.user.mapper.UserRoleMapper;
import com.guang.resms.module.user.service.RoleService;
import com.guang.resms.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author guang
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    // 日期格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RoleServiceImpl(RoleMapper roleMapper, UserRoleMapper userRoleMapper,
            RolePermissionMapper rolePermissionMapper, PermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public Page<RoleVO> getRolePage(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        // 创建分页对象
        Page<Role> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();

        // 角色名称查询
        if (params.containsKey("roleName") && StringUtils.hasText((String) params.get("roleName"))) {
            queryWrapper.like(Role::getRoleName, params.get("roleName"));
        }

        // 角色编码查询
        if (params.containsKey("roleCode") && StringUtils.hasText((String) params.get("roleCode"))) {
            queryWrapper.like(Role::getRoleCode, params.get("roleCode"));
        }

        // 状态查询
        if (params.containsKey("status") && params.get("status") != null) {
            queryWrapper.eq(Role::getStatus, params.get("status"));
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Role::getCreateTime);

        // 执行查询
        IPage<Role> rolePage = roleMapper.selectPage(page, queryWrapper);

        // 转换为VO对象
        Page<RoleVO> resultPage = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        List<RoleVO> roleVOs = rolePage.getRecords().stream()
                .map(this::convertToRoleVO)
                .collect(Collectors.toList());

        resultPage.setRecords(roleVOs);
        return resultPage;
    }

    @Override
    public RoleVO getRoleDetail(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return null;
        }
        return convertToRoleVO(role);
    }

    @Override
    public List<RoleVO> getAllRoles() {
        List<Role> roles = roleMapper.selectList(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getStatus, 1) // 只查询启用状态的角色
                        .orderByAsc(Role::getRoleName));

        return roles.stream()
                .map(this::convertToRoleVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveRole(Role role) {
        // 验证输入
        validateRoleInput(role, null);

        // 保存角色
        int result = roleMapper.insert(role);
        if (result <= 0) {
            throw new ServiceException("新增角色失败");
        }
    }

    @Override
    @Transactional
    public void updateRole(Role role) {
        // 验证输入
        validateRoleInput(role, role.getId());

        // 如果是系统管理员角色，不能禁用
        if (role.getId() == 1 && role.getStatus() != null && role.getStatus() == 0) {
            throw new ServiceException("不能禁用系统管理员角色");
        }

        // 更新角色
        int result = roleMapper.updateById(role);
        if (result <= 0) {
            throw new ServiceException("更新角色失败");
        }
    }

    @Override
    @Transactional
    public void deleteRole(Integer roleId) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new ServiceException("角色不存在");
        }

        // 不能删除系统管理员角色
        if (roleId == 1) {
            throw new ServiceException("不能删除系统管理员角色");
        }

        // 检查是否被用户使用
        Long userCount = userRoleMapper.selectCount(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getRoleId, roleId));

        // 使用 userCount
        if (userCount > 0) {
            // 有用户使用该角色
            System.out.println("该角色下有 " + userCount + " 个用户");
        }

        if (userCount > 0) {
            throw new ServiceException("该角色正在被用户使用，无法删除");
        }

        // 删除角色权限关联
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId));

        // 删除角色
        int result = roleMapper.deleteById(roleId);
        if (result <= 0) {
            throw new ServiceException("删除角色失败");
        }
    }

    @Override
    @Transactional
    public void batchDeleteRoles(List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        // 检查是否包含系统管理员角色
        if (roleIds.contains(1)) {
            throw new ServiceException("不能删除系统管理员角色");
        }

        Long userCount = userRoleMapper.selectCount(
                new LambdaQueryWrapper<UserRole>()
                        .in(UserRole::getRoleId, roleIds));

        // 使用 userCount
        if (userCount > 0) {
            // 有用户使用该角色
            System.out.println("该角色下有 " + userCount + " 个用户");
        }
        if (userCount > 0) {
            throw new ServiceException("选中的角色中存在正在被用户使用的角色，无法删除");
        }

        // 删除角色权限关联
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>()
                        .in(RolePermission::getRoleId, roleIds));

        // 批量删除角色
        int result = roleMapper.deleteBatchIds(roleIds);
        if (result <= 0) {
            throw new ServiceException("批量删除角色失败");
        }
    }

    @Override
    @Transactional
    public void assignPermissions(Integer roleId, List<Integer> permissionIds) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new ServiceException("角色不存在");
        }

        // 不能修改系统管理员角色的权限
        if (roleId == 1) {
            throw new ServiceException("不能修改系统管理员角色的权限");
        }

        // 删除现有的权限关联
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId));

        // 添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Integer permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }

        // 触发角色 updateTime 更新：用于让已登录用户的 token 在权限变更后失效
        Role roleToTouch = new Role();
        roleToTouch.setId(roleId);
        roleToTouch.setUpdateTime(LocalDateTime.now());
        int touched = roleMapper.updateById(roleToTouch);
        if (touched <= 0) {
            throw new ServiceException("权限配置成功，但刷新角色版本失败");
        }
    }

    @Override
    public List<Integer> getRolePermissionIds(Integer roleId) {
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId));

        return rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isRoleNameAvailable(String roleName, Integer excludeId) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleName, roleName.trim());

        if (excludeId != null) {
            queryWrapper.ne(Role::getId, excludeId);
        }

        return roleMapper.selectCount(queryWrapper) == 0;
    }

    @Override
    public boolean isRoleCodeAvailable(String roleCode, Integer excludeId) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, roleCode.trim());

        if (excludeId != null) {
            queryWrapper.ne(Role::getId, excludeId);
        }

        return roleMapper.selectCount(queryWrapper) == 0;
    }

    /**
     * 验证角色输入
     */
    private void validateRoleInput(Role role, Integer excludeId) {
        // 角色名称验证
        if (!StringUtils.hasText(role.getRoleName())) {
            throw new ServiceException("角色名称不能为空");
        }
        if (role.getRoleName().length() > 50) {
            throw new ServiceException("角色名称长度不能超过50个字符");
        }
        if (!isRoleNameAvailable(role.getRoleName(), excludeId)) {
            throw new ServiceException("角色名称已存在");
        }

        // 角色编码验证
        if (!StringUtils.hasText(role.getRoleCode())) {
            throw new ServiceException("角色编码不能为空");
        }
        if (role.getRoleCode().length() > 50) {
            throw new ServiceException("角色编码长度不能超过50个字符");
        }
        if (!role.getRoleCode().matches("^[a-zA-Z0-9_]+$")) {
            throw new ServiceException("角色编码只能包含字母、数字和下划线");
        }
        if (!isRoleCodeAvailable(role.getRoleCode(), excludeId)) {
            throw new ServiceException("角色编码已存在");
        }

        // 角色描述验证
        if (StringUtils.hasText(role.getDescription()) && role.getDescription().length() > 200) {
            throw new ServiceException("角色描述长度不能超过200个字符");
        }

        // 数据权限范围验证
        if (role.getDataScope() == null || role.getDataScope() < 1 || role.getDataScope() > 4) {
            throw new ServiceException("请选择有效的数据权限范围");
        }

        // 状态验证
        if (role.getStatus() == null || (role.getStatus() != 0 && role.getStatus() != 1)) {
            throw new ServiceException("状态值无效");
        }
    }

    /**
     * 将Role实体转换为RoleVO
     */
    private RoleVO convertToRoleVO(Role role) {
        // 获取数据权限范围文本
        String dataScopeText = getDataScopeText(role.getDataScope());

        // 获取状态文本
        String statusText = role.getStatus() != null && role.getStatus() == 1 ? "启用" : "禁用";

        // 获取用户数量
        int userCount = Math.toIntExact(userRoleMapper.selectCount(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getRoleId, role.getId())));

        return RoleVO.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .description(role.getDescription())
                .dataScope(role.getDataScope())
                .dataScopeText(dataScopeText)
                .status(role.getStatus())
                .statusText(statusText)
                .createTime(role.getCreateTime() != null ? role.getCreateTime().format(DATE_FORMATTER) : null)
                .updateTime(role.getUpdateTime() != null ? role.getUpdateTime().format(DATE_FORMATTER) : null)
                .userCount(userCount)
                .build();
    }

    /**
     * 根据数据权限范围获取文本
     */
    private String getDataScopeText(Integer dataScope) {
        if (dataScope == null) {
            return "未知";
        }

        switch (dataScope) {
            case 1:
                return "全部数据";
            case 2:
                return "本部门数据";
            case 3:
                return "本部门及以下数据";
            case 4:
                return "仅本人数据";
            default:
                return "未知范围";
        }
    }
}
