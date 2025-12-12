package com.guang.resms.entity.vo.statistics;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 房源统计 VO
 */
@Data
public class HouseStatsVO {
    /**
     * 房源总数
     */
    private Long totalHouseCount;

    /**
     * 在售房源数
     */
    private Long onSaleCount;

    /**
     * 已成交房源数
     */
    private Long dealCount;

    /**
     * 待审核房源数
     */
    private Long auditCount;

    /**
     * 房源状态分布 (用于饼图)
     * key: 状态名称
     * value: 数量
     */
    private List<Map<String, Object>> statusStats;

    /**
     * 房源类型分布 (二手房/新房/租房)
     */
    private List<Map<String, Object>> typeStats;
}
