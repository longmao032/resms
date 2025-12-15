package com.guang.resms.module.user.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单树形结构VO
 *
 * @author guang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Integer id;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 父菜单ID
     */
    private Integer parentId;

    /**
     * 父菜单名称
     */
    private String parentName;

    /**
     * 菜单类型：1=菜单权限，2=按钮权限，3=数据权限
     */
    private Integer menuType;

    /**
     * 菜单类型文本
     */
    private String menuTypeText;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 显示排序
     */
    private Integer sortOrder;

    /**
     * 菜单描述
     */
    private String description;

    /**
     * 菜单状态：0=禁用，1=启用
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 是否可见：0=隐藏，1=显示
     */
    private Integer visible;

    /**
     * 是否缓存：0=不缓存，1=缓存
     */
    private Integer isCache;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 子菜单列表
     */
    private List<MenuTreeVO> children;

    /**
     * 是否有子节点
     */
    private Boolean hasChildren;

    /**
     * 层级深度（用于前端展示缩进）
     */
    private Integer level;
}
