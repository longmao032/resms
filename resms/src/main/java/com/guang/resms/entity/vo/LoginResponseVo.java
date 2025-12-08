package com.guang.resms.entity.vo;

import com.guang.resms.entity.Permission;
import lombok.Data;

import java.util.List;

/**
 * 登录响应VO，包含用户信息、JWT和权限信息
 *
 * @author guang
 */
@Data
public class LoginResponseVo {
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 角色类型
     */
    private Integer roleType;

    /**
     * 菜单权限列表
     */
    private List<MenuVO> menus;

    /**
     * 权限按钮列表
     */
    private List<String> permissions;
}