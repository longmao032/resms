package com.guang.resms.entity.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

@Data
public class TransactionAddDTO implements Serializable {
    @NotNull(message = "房源ID不能为空")
    private Integer houseId;

    @NotNull(message = "客户ID不能为空")
    private Integer customerId;

    @NotNull(message = "销售ID不能为空")
    private Integer salesId;

    @NotNull(message = "成交价格不能为空")
    private BigDecimal dealPrice;

    private BigDecimal deposit; // 定金
    private LocalDateTime depositTime; // 定金时间

    private BigDecimal downPayment; // 首付
    private LocalDateTime downPaymentTime; // 首付时间

    private BigDecimal loanAmount; // 贷款金额
    private Integer loanStatus; // 贷款状态
}
