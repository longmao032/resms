package com.guang.resms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.Role;
import com.guang.resms.entity.vo.RoleVO;

import java.util.List;
import java.util.Map;

/**
 * 角色服务接口
 *
 * @author guang
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param params 查询参数
     * @return 分页结果
     */
    Page<RoleVO> getRolePage(Integer pageNum, Integer pageSize, Map<String, Object> params);

    /**
     * 获取角色信息详情
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    RoleVO getRoleDetail(Integer roleId);

    /**
     * 获取所有角色
     *
     * @return 角色列表
     */
    List<RoleVO> getAllRoles();

    /**
     * 新增角色
     *
     * @param role 角色信息
     */
    void saveRole(Role role);

    /**
     * 更新角色
     *
     * @param role 角色信息
     */
    void updateRole(Role role);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    void deleteRole(Integer roleId);

    /**
     * 批量删除角色
     *
     * @param roleIds 角色ID列表
     */
    void batchDeleteRoles(List<Integer> roleIds);

    /**
     * 配置角色权限
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Integer roleId, List<Integer> permissionIds);

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Integer> getRolePermissionIds(Integer roleId);

    /**
     * 检查角色名称是否可用
     *
     * @param roleName 角色名称
     * @param excludeId 排除的角色ID（编辑时使用）
     * @return 是否可用
     */
    boolean isRoleNameAvailable(String roleName, Integer excludeId);

    /**
     * 检查角色编码是否可用
     *
     * @param roleCode 角色编码
     * @param excludeId 排除的角色ID（编辑时使用）
     * @return 是否可用
     */
    boolean isRoleCodeAvailable(String roleCode, Integer excludeId);
}
