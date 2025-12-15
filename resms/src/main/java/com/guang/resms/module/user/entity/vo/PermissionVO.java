package com.guang.resms.module.user.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限VO
 *
 * @author guang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    private Integer id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 父权限ID
     */
    private Integer parentId;

    /**
     * 权限类型：1=菜单 2=按钮/接口
     */
    private Integer permissionType;

    /**
     * 类型文本
     */
    private String typeText;

    /**
     * 路由地址（菜单时使用）
     */
    private String path;

    /**
     * 组件路径（菜单时使用）
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 状态：0=禁用，1=正常
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子权限列表（树形结构使用）
     */
    private List<PermissionVO> children;
}
