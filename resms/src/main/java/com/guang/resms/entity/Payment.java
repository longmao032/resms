package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 收款记录表
 */
@Data
@TableName("tb_payment")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 收款ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 交易ID（关联tb_transaction表）
     */
    private Integer transactionId;

    /**
     * 款项类型：1=定金，2=首付款，3=尾款，4=中介费
     */
    private Integer paymentType;

    /**
     * 收款金额（元）
     */
    private BigDecimal amount;

    /**
     * 收款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    /**
     * 支付方式（如"微信""银行卡"）
     */
    private String paymentMethod;

    /**
     * 收据/发票编号
     */
    private String receiptNo;

    /**
     * 财务人员ID（关联tb_user表）
     */
    private Integer financeId;

    /**
     * 备注（如"客户刷卡支付"）
     */
    private String remark;

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