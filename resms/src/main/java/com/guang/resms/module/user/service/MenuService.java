package com.guang.resms.module.user.service;

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
     * 更新菜单状态
     *
     * @param ids    菜单ID列表
     * @param status 状态
     * @return 是否成功
     */
    boolean updateMenuStatus(List<Integer> ids, Integer status);
}
