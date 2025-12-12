package com.guang.resms.entity.vo.statistics;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 交易统计 VO
 */
@Data
public class TransactionStatsVO {
    /**
     * 累计交易总额
     */
    private BigDecimal totalDealAmount;

    /**
     * 累计交易单数
     */
    private Long totalTransactionCount;

    /**
     * 待审核交易数
     */
    private Long pendingAuditCount;

    /**
     * 近期交易趋势 (用于折线图, 近7天或近12个月)
     * 包含 date, count, amount
     */
    private List<Map<String, Object>> recentTrend;
}
