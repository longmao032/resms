package com.guang.resms.module.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 收退款记录表
 * </p>
 *
 * @author
 * @since 2024
 */
@Data
@TableName("tb_payment")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收款ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 交易ID
     */
    @TableField("transaction_id")
    private Integer transactionId;

    /**
     * 款项类型：1=定金，2=首付款，3=尾款，4=中介费
     */
    @TableField("payment_type")
    private Integer paymentType;

    /**
     * 资金流向：1=收款，2=退款
     */
    @TableField("flow_type")
    private Integer flowType;

    /**
     * 金额（元）
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 状态：0=待确认，1=有效，2=已作废
     */
    @TableField("payment_status")
    private Integer paymentStatus;

    /**
     * 变动时间
     */
    @TableField("payment_time")
    private LocalDateTime paymentTime;

    /**
     * 支付方式
     */
    @TableField("payment_method")
    private String paymentMethod;

    /**
     * 收据/发票编号
     */
    @TableField("receipt_no")
    private String receiptNo;

    /**
     * 凭证图片路径
     */
    @TableField("proof_url")
    private String proofUrl;

    /**
     * 付款人备注（如：张三代付）
     */
    @TableField("payer_info")
    private String payerInfo;

    /**
     * 经办财务ID
     */
    @TableField("finance_id")
    private Integer financeId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}