package com.guang.resms.module.transaction.entity.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull; // Spring Boot 3.x 使用 jakarta，旧版用 javax

@Data
public class TransactionUpdateDTO implements Serializable {
    @NotNull(message = "交易ID不能为空")
    private Integer id;

    private BigDecimal dealPrice; // 成交价
    private BigDecimal deposit; // 定金
    private LocalDateTime depositTime; // 定金时间
    private BigDecimal downPayment; // 首付
    private LocalDateTime downPaymentTime; // 首付时间
    private BigDecimal loanAmount; // 贷款金额
    private LocalDateTime transferTime;// 过户时间

    private Integer loanStatus; // 贷款状态
    private Integer status; // 交易状态
    private Integer managerAudit; // 审核状态
}