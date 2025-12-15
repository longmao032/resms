package com.guang.resms.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.module.user.entity.Permission;
import com.guang.resms.module.user.entity.vo.MenuTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 菜单Mapper接口
 *
 * @author guang
 */
@Mapper
public interface MenuMapper extends BaseMapper<Permission> {

    /**
     * 查询所有菜单（用于构建树形结构）
     *
     * @param params 查询参数
     * @return 菜单列表
     */
    List<MenuTreeVO> selectMenuList(@Param("params") Map<String, Object> params);

    /**
     * 根据ID查询菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    MenuTreeVO selectMenuById(@Param("id") Integer id);

    /**
     * 查询菜单的子菜单数量
     *
     * @param parentId 父菜单ID
     * @return 子菜单数量
     */
    Integer countChildrenByParentId(@Param("parentId") Integer parentId);

    /**
     * 检查菜单编码是否存在
     *
     * @param menuCode  菜单编码
     * @param excludeId 排除的菜单ID（更新时使用）
     * @return 数量
     */
    Integer checkMenuCodeUnique(@Param("menuCode") String menuCode, @Param("excludeId") Integer excludeId);

    /**
     * 检查菜单名称是否存在（同父级下）
     *
     * @param menuName  菜单名称
     * @param parentId  父菜单ID
     * @param excludeId 排除的菜单ID
     * @return 数量
     */
    Integer checkMenuNameUnique(@Param("menuName") String menuName, 
                                @Param("parentId") Integer parentId, 
                                @Param("excludeId") Integer excludeId);

    /**
     * 批量更新菜单排序
     *
     * @param sortList 排序列表
     * @return 影响行数
     */
    Integer batchUpdateSort(@Param("sortList") List<Map<String, Object>> sortList);

    /**
     * 更新菜单状态
     *
     * @param ids    菜单ID列表
     * @param status 状态
     * @return 影响行数
     */
    Integer updateMenuStatus(@Param("ids") List<Integer> ids, @Param("status") Integer status);

    /**
     * 查询菜单的所有子菜单ID（递归）
     *
     * @param parentId 父菜单ID
     * @return 子菜单ID列表
     */
    List<Integer> selectChildrenIds(@Param("parentId") Integer parentId);

    /**
     * 查询角色关联的菜单数量
     *
     * @param menuId 菜单ID
     * @return 关联数量
     */
    Integer countRoleMenuByMenuId(@Param("menuId") Integer menuId);
}
