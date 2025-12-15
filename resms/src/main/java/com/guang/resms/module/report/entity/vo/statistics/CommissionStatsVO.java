package com.guang.resms.module.report.entity.vo.statistics;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CommissionStatsVO {
    private BigDecimal totalCommissionAmount;
    private Long totalCommissionCount;

    private BigDecimal pendingAmount;
    private BigDecimal calculatedAmount;
    private BigDecimal issuedAmount;

    private Long pendingCount;
    private Long calculatedCount;
    private Long issuedCount;

    private List<Map<String, Object>> statusStats;
    private List<Map<String, Object>> recentTrend;
}
