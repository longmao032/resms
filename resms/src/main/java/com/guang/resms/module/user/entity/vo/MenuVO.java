package com.guang.resms.module.user.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuVO {
    private Integer id;
    private String name;           // 权限名称
    private String code;           // 权限代码
    private Integer type;          // 权限类型：1=菜单，2=按钮，3=数据
    private Integer parentId;
    private String path;           // 路由路径
    private String component;      // 组件路径
    private String icon;           // 图标
    private Integer sortOrder;     // 排序
    private String description;    // 描述
    private List<MenuVO> children; // 按钮权限
}