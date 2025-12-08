package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

// 1. 用户表 tb_user
@TableName("tb_user")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 登录用户名（唯一） */
    @TableField("username")
    private String username;

    /** 密码（已加密） */
    @TableField("password")
    private String password;

    /** 真实姓名 */
    @TableField("real_name")
    private String realName;

    /** 联系电话（唯一） */
    @TableField("phone")
    private String phone;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 头像地址 */
    @TableField("avatar")
    private String avatar;

    /** 角色类型：1=系统管理员 2=销售顾问 3=销售经理 4=财务人员 5=普通用户 */
    @TableField("role_type")
    private Integer roleType;

    /** 账号状态：0=禁用 1=正常 */
    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}