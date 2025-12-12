package com.guang.resms.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Integer id;

    @NotNull(message = "关联交易不能为空")
    private Integer transactionId;

    @NotNull(message = "款项类型不能为空")
    private Integer paymentType;

    @NotNull(message = "资金流向不能为空")
    private Integer flowType;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;

    private LocalDateTime paymentTime;
    private String proofUrl;
    private String payerInfo;
    private String remark;
}