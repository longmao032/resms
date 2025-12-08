package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 销售佣金表
 */
@TableName("tb_commission")
@Data
public class Commission implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 佣金ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 交易ID（关联tb_transaction表）
     */
    private Integer transactionId;

    /**
     * 销售ID（关联tb_user表）
     */
    private Integer salesId;

    /**
     * 提成比例（%）
     */
    private BigDecimal commissionRate;

    /**
     * 佣金金额（元）
     */
    private BigDecimal amount;

    /**
     * 状态：0=待核算，1=已核算，2=已发放
     */
    private Integer status;

    /**
     * 核算时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date calculateTime;

    /**
     * 发放时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date issueTime;

    /**
     * 核算财务ID（关联tb_user表）
     */
    private Integer financeId;

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