package com.guang.resms.entity.vo.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 财务报表 VO
 * 按月份统计
 */
@Data
public class FinancialReportVO {
    /**
     * 统计月份 (yyyy-MM)
     */
    private String period;

    /**
     * 总收入 (收款)
     */
    private BigDecimal totalIncome;

    /**
     * 总支出 (退款)
     */
    private BigDecimal totalRefund;

    /**
     * 净收入
     */
    private BigDecimal netIncome;

    /**
     * 交易笔数
     */
    private Long transactionCount;
}
