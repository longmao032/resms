package com.guang.resms.module.user.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 菜单数据传输对象
 *
 * @author guang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID（更新时需要）
     */
    private Integer id;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    /**
     * 菜单编码（唯一标识）
     */
    @NotBlank(message = "菜单编码不能为空")
    private String menuCode;

    /**
     * 父菜单ID（0表示顶级菜单）
     */
    @NotNull(message = "父菜单ID不能为空")
    private Integer parentId;

    /**
     * 菜单类型：1=目录，2=菜单，3=按钮
     */
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

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
     * 是否可见：0=隐藏，1=显示
     */
    private Integer visible;

    /**
     * 是否缓存：0=不缓存，1=缓存
     */
    private Integer isCache;

    /**
     * 权限标识（用于按钮权限）
     */
    private String perms;
}
