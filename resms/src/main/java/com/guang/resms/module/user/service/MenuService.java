package com.guang.resms.module.user.service;

import com.guang.resms.module.user.entity.dto.MenuDTO;
import com.guang.resms.module.user.entity.vo.MenuTreeVO;

import java.util.List;
import java.util.Map;

/**
 * 菜单服务接口
 *
 * @author guang
 */
public interface MenuService {

    /**
     * 查询菜单树形结构
     *
     * @param params 查询参数
     * @return 菜单树列表
     */
    List<MenuTreeVO> getMenuTree(Map<String, Object> params);

    /**
     * 根据ID查询菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    MenuTreeVO getMenuById(Integer id);

    /**
     * 新增菜单
     *
     * @param menuDTO 菜单信息
     * @return 是否成功
     */
    boolean saveMenu(MenuDTO menuDTO);

    /**
     * 更新菜单
     *
     * @param menuDTO 菜单信息
     * @return 是否成功
     */
    boolean updateMenu(MenuDTO menuDTO);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否成功
     */
    boolean deleteMenu(Integer id);

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID列表
     * @return 是否成功
     */
    boolean batchDeleteMenus(List<Integer> ids);

    /**
     * 更新菜单状态
     *
     * @param ids    菜单ID列表
     * @param status 状态
     * @return 是否成功
     */
    boolean updateMenuStatus(List<Integer> ids, Integer status);

    /**
     * 批量更新菜单排序
     *
     * @param sortList 排序列表
     * @return 是否成功
     */
    boolean updateMenuSort(List<Map<String, Object>> sortList);

    /**
     * 检查菜单编码是否唯一
     *
     * @param menuCode  菜单编码
     * @param excludeId 排除的菜单ID
     * @return 是否唯一
     */
    boolean isMenuCodeUnique(String menuCode, Integer excludeId);

    /**
     * 检查菜单名称是否唯一（同父级下）
     *
     * @param menuName  菜单名称
     * @param parentId  父菜单ID
     * @param excludeId 排除的菜单ID
     * @return 是否唯一
     */
    boolean isMenuNameUnique(String menuName, Integer parentId, Integer excludeId);

    /**
     * 获取父菜单选择树（用于下拉选择）
     *
     * @param excludeId 排除的菜单ID（编辑时排除自己及子孙节点）
     * @return 菜单树列表
     */
    List<MenuTreeVO> getParentMenuTree(Integer excludeId);
}
