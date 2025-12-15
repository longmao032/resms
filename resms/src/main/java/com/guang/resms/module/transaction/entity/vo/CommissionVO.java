package com.guang.resms.module.transaction.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CommissionVO {
    private Integer id;
    private String transactionNo; // 关联交易编号
    private BigDecimal dealPrice; // 关联成交价
    private String salesName;     // 销售姓名
    private BigDecimal commissionRate;
    private BigDecimal amount;
    private Integer status;
    private String financeName;   // 财务姓名
    private LocalDateTime calculateTime;
    private LocalDateTime issueTime;
    private LocalDateTime updateTime;
}