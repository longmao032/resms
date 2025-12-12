package com.guang.resms.entity.vo.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 销售报表 VO
 * 按销售人员统计
 */
@Data
public class SaleReportVO {
    /**
     * 员工姓名
     */
    private String staffName;

    /**
     * 所属团队
     */
    private String teamName;

    /**
     * 成交单数
     */
    private Long dealCount;

    /**
     * 成交总金额
     */
    private BigDecimal totalDealAmount;

    /**
     * 佣金总金额
     */
    private BigDecimal totalCommissionAmount;
}
