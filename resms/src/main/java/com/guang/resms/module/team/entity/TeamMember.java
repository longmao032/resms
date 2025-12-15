package com.guang.resms.module.team.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 团队成员关联表
 */
@Data
@TableName("tb_team_member")
public class TeamMember implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 团队ID（关联tb_team表）
     */
    private Integer teamId;

    /**
     * 用户ID（关联tb_user表，角色类型为2）
     */
    private Integer userId;

    /**
     * 加入团队时间
     */
    private LocalDate joinTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}