package com.guang.resms.module.transaction.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易信息表
 */
@Data
@TableName("tb_transaction")
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 交易ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 交易编号（唯一，如"JY20240001"）
     */
    private String transactionNo;

    /**
     * 房源ID（关联tb_house表）
     */
    private Integer houseId;

    /**
     * 客户ID（关联tb_customer表）
     */
    private Integer customerId;

    /**
     * 销售ID（关联tb_user表）
     */
    private Integer salesId;

    /**
     * 成交价格（元）
     */
    private BigDecimal dealPrice;

    /**
     * 定金金额（元）
     */
    private BigDecimal deposit;

    /**
     * 定金支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime depositTime;

    /**
     * 首付款金额（元）
     */
    private BigDecimal downPayment;

    /**
     * 首付款支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime downPaymentTime;

    /**
     * 贷款金额（元）
     */
    private BigDecimal loanAmount;

    /**
     * 贷款状态：0=未申请，1=审核中，2=已放款，3=未通过
     */
    private Integer loanStatus;

    /**
     * 过户时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transferTime;

    /**
     * 交易状态：0=待付定金，1=已付定金，2=已付首付，3=已过户，4=已完成，5=已取消
     */
    private Integer status;

    /**
     * 经理审核：0=待审核，1=已通过，2=已驳回
     */
    private Integer managerAudit;

    /**
     * 完结审核：0=待审核，1=已通过，2=已驳回
     */
    private Integer finishAudit;

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