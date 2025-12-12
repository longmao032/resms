package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 销售团队表
 */
@Data
@TableName("tb_team")
public class Team implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 团队ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 团队名称（唯一）
     */
    private String teamName;

    /**
     * 团队经理ID（关联tb_user表，角色类型为3）
     */
    private Integer managerId;

    /**
     * 成立时间
     */

    private LocalDate establishTime;

    /**
     * 团队人数
     */
    private Integer teamSize;

    /**
     * 月度业绩目标（元）
     */
    private BigDecimal performanceTarget;

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