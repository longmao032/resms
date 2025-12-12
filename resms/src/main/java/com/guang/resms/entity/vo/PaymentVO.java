package com.guang.resms.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentVO {
    private Integer id;
    private Integer transactionId;
    private String transactionNo; // 交易编号

    private String customerName;  // 客户姓名
    private String houseNo;       // 房源编号

    private Integer paymentType;
    private Integer flowType;
    private BigDecimal amount;
    private Integer paymentStatus;
    private LocalDateTime paymentTime;
    private String paymentMethod;
    private String receiptNo;
    private String proofUrl;
    private String payerInfo;

    private Integer financeId;
    private String financeName;   // 财务人员姓名

    private String remark;
    private LocalDateTime createTime;
}