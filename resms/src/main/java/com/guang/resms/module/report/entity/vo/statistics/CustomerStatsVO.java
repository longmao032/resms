package com.guang.resms.module.report.entity.vo.statistics;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 客户统计 VO
 */
@Data
public class CustomerStatsVO {
    /**
     * 客户总数
     */
    private Long totalCustomerCount;

    /**
     * 今日新增客户数
     */
    private Long todayNewCount;

    /**
     * 本月新增客户数
     */
    private Long monthNewCount;

    /**
     * 意向等级分布 (用于饼图)
     */
    private List<Map<String, Object>> intentionLevelStats;

    /**
     * 客户来源分布 (用于柱状图)
     */
    private List<Map<String, Object>> sourceStats;
}
