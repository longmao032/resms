package com.guang.resms.module.user.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色VO
 *
 * @author guang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 数据权限范围：1=全部数据，2=本部门数据，3=本部门及以下数据，4=仅本人数据
     */
    private Integer dataScope;

    /**
     * 数据权限范围文本
     */
    private String dataScopeText;

    /**
     * 状态：0=禁用，1=启用
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 用户数量
     */
    private Integer userCount;
}
