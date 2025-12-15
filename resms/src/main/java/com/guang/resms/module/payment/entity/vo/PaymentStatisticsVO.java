package com.guang.resms.module.payment.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class PaymentStatisticsVO {
    // 1. 顶部卡片数据
    private BigDecimal totalAmount;       // 累计收款
    private BigDecimal todayAmount;       // 今日收款
    private BigDecimal monthAmount;       // 本月收款
    private BigDecimal pendingAmount;     // 待确认金额

    // 2. 图表数据
    // 趋势图 (Key: 日期/月份, Value: 金额)
    private List<String> trendDates;
    private List<BigDecimal> trendAmounts;

    // 类型占比 (Key: 类型名称, Value: 金额)
    private List<Map<String, Object>> typePieData; 
    
    // 销售回款排行
    private List<SalesRanking> salesRanking;

    @Data
    public static class SalesRanking {
        private String salesName;
        private BigDecimal totalAmount;
    }
}