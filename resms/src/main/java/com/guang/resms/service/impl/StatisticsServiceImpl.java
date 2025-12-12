package com.guang.resms.service.impl;

import com.guang.resms.entity.vo.statistics.CustomerStatsVO;
import com.guang.resms.entity.vo.statistics.HouseStatsVO;
import com.guang.resms.entity.vo.statistics.PaymentStatsVO;
import com.guang.resms.entity.vo.statistics.TransactionStatsVO;
import com.guang.resms.mapper.StatisticsMapper;
import com.guang.resms.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计分析服务实现类
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    @Override
    public HouseStatsVO getHouseStats() {
        HouseStatsVO vo = new HouseStatsVO();
        vo.setTotalHouseCount(statisticsMapper.countTotalHouses());
        vo.setOnSaleCount(statisticsMapper.countHousesByStatus(1)); // 1=在售
        vo.setDealCount(statisticsMapper.countHousesByStatus(3)); // 3=已成交
        vo.setAuditCount(statisticsMapper.countHousesByStatus(0)); // 0=待审核

        // 处理状态分布
        List<Map<String, Object>> statusRaw = statisticsMapper.countHousesGroupByStatus();
        List<Map<String, Object>> statusStats = new ArrayList<>();
        for (Map<String, Object> map : statusRaw) {
            Map<String, Object> item = new HashMap<>();
            Integer status = (Integer) map.get("status");
            Long count = ((Number) map.get("count")).longValue();
            String name = getHouseStatusName(status);
            item.put("name", name);
            item.put("value", count);
            statusStats.add(item);
        }
        vo.setStatusStats(statusStats);

        // 处理类型分布
        List<Map<String, Object>> typeRaw = statisticsMapper.countHousesGroupByType();
        List<Map<String, Object>> typeStats = new ArrayList<>();
        for (Map<String, Object> map : typeRaw) {
            Map<String, Object> item = new HashMap<>();
            Integer type = (Integer) map.get("type");
            Long count = ((Number) map.get("count")).longValue();
            String name = getHouseTypeName(type);
            item.put("name", name);
            item.put("value", count);
            typeStats.add(item);
        }
        vo.setTypeStats(typeStats);

        return vo;
    }

    @Override
    public CustomerStatsVO getCustomerStats() {
        CustomerStatsVO vo = new CustomerStatsVO();
        vo.setTotalCustomerCount(statisticsMapper.countTotalCustomers());

        String today = LocalDate.now().toString();
        String monthStart = LocalDate.now().withDayOfMonth(1).toString();

        vo.setTodayNewCount(statisticsMapper.countNewCustomersLineDate(today));
        vo.setMonthNewCount(statisticsMapper.countNewCustomersLineDate(monthStart));

        // 意向等级分布
        List<Map<String, Object>> intentionRaw = statisticsMapper.countCustomersGroupByIntention();
        List<Map<String, Object>> intentionStats = new ArrayList<>();
        for (Map<String, Object> map : intentionRaw) {
            Map<String, Object> item = new HashMap<>();
            Integer level = (Integer) map.get("level");
            Long count = ((Number) map.get("count")).longValue();
            item.put("name", getIntentionLevelName(level));
            item.put("value", count);
            intentionStats.add(item);
        }
        vo.setIntentionLevelStats(intentionStats);

        // 来源分布
        List<Map<String, Object>> sourceRaw = statisticsMapper.countCustomersGroupBySource();
        List<Map<String, Object>> sourceStats = new ArrayList<>();
        for (Map<String, Object> map : sourceRaw) {
            Map<String, Object> item = new HashMap<>();
            String source = (String) map.get("source");
            Long count = ((Number) map.get("count")).longValue();
            item.put("name", source == null ? "未知" : source);
            item.put("value", count);
            sourceStats.add(item);
        }
        vo.setSourceStats(sourceStats);

        return vo;
    }

    @Override
    public TransactionStatsVO getTransactionStats() {
        TransactionStatsVO vo = new TransactionStatsVO();
        vo.setTotalTransactionCount(statisticsMapper.countTotalTransactions());
        vo.setTotalDealAmount(statisticsMapper.sumTotalTransactionAmount());
        vo.setPendingAuditCount(statisticsMapper.countPendingAuditTransactions());

        // 近30天趋势
        vo.setRecentTrend(statisticsMapper.selectRecentTransactionTrend(30));
        return vo;
    }

    @Override
    public PaymentStatsVO getPaymentStats() {
        PaymentStatsVO vo = new PaymentStatsVO();
        vo.setTotalPaymentAmount(statisticsMapper.sumTotalPaymentAmount());
        vo.setTodayPaymentAmount(statisticsMapper.sumPaymentAmountByDate(LocalDate.now().toString()));
        vo.setPendingConfirmCount(statisticsMapper.countPendingConfirmPayments());

        // 近30天收款趋势
        vo.setPaymentTrend(statisticsMapper.selectRecentPaymentTrend(30));
        return vo;
    }

    // --- Helper Methods ---

    private String getHouseStatusName(Integer status) {
        if (status == null)
            return "未知";
        return switch (status) {
            case 0 -> "待审核";
            case 1 -> "在售";
            case 2 -> "已预订";
            case 3 -> "已成交";
            case 4 -> "已下架";
            default -> "其他";
        };
    }

    private String getHouseTypeName(Integer type) {
        if (type == null)
            return "未知";
        return switch (type) {
            case 1 -> "二手房";
            case 2 -> "新房";
            case 3 -> "租房";
            default -> "其他";
        };
    }

    private String getIntentionLevelName(Integer level) {
        if (level == null)
            return "未知";
        return switch (level) {
            case 1 -> "高";
            case 2 -> "中";
            case 3 -> "低";
            default -> "其他";
        };
    }
}
