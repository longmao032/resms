package com.guang.resms.module.report.entity.vo.statistics;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 收款统计 VO
 */
@Data
public class PaymentStatsVO {
    /**
     * 累计收款金额
     */
    private BigDecimal totalPaymentAmount;

    /**
     * 今日收款金额
     */
    private BigDecimal todayPaymentAmount;

    /**
     * 待确认收款笔数
     */
    private Long pendingConfirmCount;

    /**
     * 近期收款趋势 (用于折线图)
     */
    private List<Map<String, Object>> paymentTrend;
}
