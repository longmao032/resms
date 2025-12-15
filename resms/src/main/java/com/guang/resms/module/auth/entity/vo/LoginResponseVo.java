package com.guang.resms.module.auth.entity.vo;

import com.guang.resms.module.user.entity.vo.MenuVO;
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

    private String phone;

    private String email;

    private Integer sex;

    private String remark;

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 角色类型
     */
    private Integer roleType;

    private Integer teamId;

    private Boolean admin;

    private Boolean teamManager;

    /**
     * 菜单权限列表
     */
    private List<MenuVO> menus;

    /**
     * 权限按钮列表
     */
    private List<String> permissions;
}