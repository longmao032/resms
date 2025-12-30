package com.guang.resms.module.report.entity.vo.statistics;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 趋势统计 VO
 * 返回近N天的业务数据趋势
 */
@Data
public class TrendStatsVO {

    /**
     * 每日统计数据列表
     */
    private List<DailyStatsDTO> dailyStats;

    /**
     * 每日统计数据
     */
    @Data
    public static class DailyStatsDTO {
        /**
         * 日期 (yyyy-MM-dd)
         */
        private String date;

        /**
         * 新增客户数
         */
        private Integer newCustomers;

        /**
         * 新增交易数
         */
        private Integer newTransactions;

        /**
         * 收款金额
         */
        private BigDecimal paymentAmount;

        /**
         * 新增房源数
         */
        private Integer newHouses;
    }
}
