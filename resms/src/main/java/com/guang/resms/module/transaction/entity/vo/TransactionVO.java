package com.guang.resms.module.transaction.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionVO {

    private Integer id;
    private String transactionNo;

    // 关联信息
    private String houseNo; // 房源编号
    private String unitName; // 户型名称
    private String customerName; // 客户姓名
    private String customerPhone; // 客户电话
    private Integer salesId; // 销售ID
    private String salesName; // 销售姓名

    // 金额信息
    private BigDecimal dealPrice; // 成交价格
    private BigDecimal deposit; // 定金
    private BigDecimal downPayment; // 首付款
    private BigDecimal loanAmount; // 贷款金额
    private BigDecimal tailAmount; // 尾款金额（已确认收款汇总）
    private BigDecimal paidAmount; // 已确认总额（定金+首付+尾款+贷款）
    private BigDecimal finalPayment; // 尾款（计算字段）

    // 状态信息
    private Integer loanStatus;
    private Integer status;
    private Integer managerAudit;
    private Integer finishAudit;
    private Boolean canApplyFinish;
    private LocalDateTime createTime;
}