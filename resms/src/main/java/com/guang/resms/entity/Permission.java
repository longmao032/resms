package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限表
 */
@Data
@TableName("tb_permission")
public class Permission implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 权限名称 */
    @TableField("permission_name")
    private String permissionName;

    /** 权限编码（前端判断关键） 如：house:add、commission:issue */
    @TableField("permission_code")
    private String permissionCode;

    /** 父权限ID（支持树形结构） */
    @TableField("parent_id")
    private Integer parentId;

    /** 权限类型：1=菜单 2=按钮/接口 */
    @TableField("permission_type")
    private Integer permissionType;

    /** 路由地址（菜单时使用） */
    @TableField("path")
    private String path;

    /** 组件路径（菜单时使用） */
    @TableField("component")
    private String component;

    /** 图标 */
    @TableField("icon")
    private String icon;

    @TableField("sort_order")
    private Integer sortOrder;

    /** 权限描述 */
    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status; // 0禁用 1启用

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}