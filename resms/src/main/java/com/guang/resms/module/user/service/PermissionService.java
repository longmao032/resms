package com.guang.resms.module.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.module.user.entity.Permission;
import com.guang.resms.module.user.entity.vo.MenuVO;
import com.guang.resms.module.user.entity.vo.PermissionVO;

import java.util.List;
import java.util.Map;

/**
 * 权限服务接口
 * 负责处理用户权限相关的业务逻辑
 *
 * @author guang
 */
public interface PermissionService {

    /**
     * 获取用户菜单权限
     *
     * @param userId 用户ID
     * @return 菜单权限列表
     */
    List<MenuVO> getUserMenus(Integer userId);

    /**
     * 获取用户按钮权限
     *
     * @param userId 用户ID
     * @return 按钮权限代码列表
     */
    List<String> getUserPermissions(Integer userId);

    /**
     * 检查用户是否拥有指定权限
     *
     * @param userId 用户ID
     * @param permission 权限代码
     * @return 是否拥有权限
     */
    boolean hasPermission(Integer userId, String permission);

    /**
     * 获取用户角色权限
     *
     * @param userId 用户ID
     * @return 角色权限列表
     */
    List<String> getUserRolePermissions(Integer userId);

    /**
     * 分页查询权限列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param params 查询参数
     * @return 分页结果
     */
    Page<PermissionVO> getPermissionPage(Integer pageNum, Integer pageSize, Map<String, Object> params);

    /**
     * 获取权限详情
     *
     * @param id 权限ID
     * @return 权限信息
     */
    PermissionVO getPermissionDetail(Integer id);

    /**
     * 获取权限树形结构
     *
     * @return 权限树
     */
    List<PermissionVO> getPermissionTree();

    /**
     * 获取菜单树形结构（只返回菜单类型的权限）
     *
     * @return 菜单树
     */
    List<PermissionVO> getMenuTree();

    /**
     * 新增权限
     *
     * @param permission 权限信息
     */
    void savePermission(Permission permission);

    /**
     * 更新权限
     *
     * @param permission 权限信息
     */
    void updatePermission(Permission permission);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void deletePermission(Integer id);

    /**
     * 批量删除权限
     *
     * @param permissionIds 权限ID列表
     */
    void batchDeletePermissions(List<Integer> permissionIds);

    /**
     * 更新权限状态
     *
     * @param permissionIds 权限ID列表
     * @param status 状态值
     */
    void updatePermissionStatus(List<Integer> permissionIds, Integer status);

    /**
     * 检查权限名称是否可用
     *
     * @param permissionName 权限名称
     * @param excludeId 排除的权限ID
     * @return 是否可用
     */
    boolean isPermissionNameAvailable(String permissionName, Integer excludeId);

    /**
     * 检查权限编码是否可用
     *
     * @param permissionCode 权限编码
     * @param excludeId 排除的权限ID
     * @return 是否可用
     */
    boolean isPermissionCodeAvailable(String permissionCode, Integer excludeId);

    /**
     * 更新菜单排序
     *
     * @param sortList 排序列表，包含id和sortOrder
     */
    void updateMenuSort(List<Map<String, Object>> sortList);
}
