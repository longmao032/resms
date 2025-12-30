package com.guang.resms.module.report.service.impl;

import com.guang.resms.module.report.entity.vo.statistics.CustomerStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.HouseStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.CommissionStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.PaymentStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.TodoStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.TrendStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.TrendStatsVO.DailyStatsDTO;
import com.guang.resms.module.report.entity.vo.statistics.TransactionStatsVO;
import com.guang.resms.module.report.entity.dto.CommissionStatsDTO;
import com.guang.resms.module.report.mapper.StatisticsMapper;
import com.guang.resms.module.report.service.StatisticsService;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
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
            Object statusObj = map.get("status");
            Integer status = statusObj instanceof Number ? ((Number) statusObj).intValue() : null;
            Long count = map.get("count") instanceof Number ? ((Number) map.get("count")).longValue() : 0L;
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
            Object typeObj = map.get("type");
            Integer type = typeObj instanceof Number ? ((Number) typeObj).intValue() : null;
            Long count = map.get("count") instanceof Number ? ((Number) map.get("count")).longValue() : 0L;
            String name = getHouseTypeName(type);
            item.put("name", name);
            item.put("value", count);
            typeStats.add(item);
        }
        vo.setTypeStats(typeStats);

        return vo;
    }

    @Override
    public CommissionStatsVO getCommissionStats(CommissionStatsDTO dto) {
        if (dto == null) {
            dto = new CommissionStatsDTO();
        }

        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            Integer userId = SecurityUtils.getUserId();
            if (userId == null) {
                throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
            }
            dto.setSalesId(userId);
        }

        boolean hasStart = dto.getStartDate() != null && !dto.getStartDate().isEmpty();
        boolean hasEnd = dto.getEndDate() != null && !dto.getEndDate().isEmpty();
        if (!hasStart && !hasEnd) {
            if (dto.getDays() == null || dto.getDays() <= 0) {
                dto.setDays(30);
            }
        }

        CommissionStatsVO vo = new CommissionStatsVO();
        vo.setTotalCommissionAmount(statisticsMapper.sumCommissionAmount(dto));
        vo.setTotalCommissionCount(statisticsMapper.countCommission(dto));

        List<Map<String, Object>> statusRaw = statisticsMapper.groupCommissionByStatus(dto);
        List<Map<String, Object>> statusStats = new ArrayList<>();

        java.math.BigDecimal pendingAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal calculatedAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal issuedAmount = java.math.BigDecimal.ZERO;
        long pendingCount = 0L;
        long calculatedCount = 0L;
        long issuedCount = 0L;

        for (Map<String, Object> map : statusRaw) {
            Object statusObj = map.get("status");
            Integer status = statusObj instanceof Number ? ((Number) statusObj).intValue() : null;
            Long count = map.get("count") instanceof Number ? ((Number) map.get("count")).longValue() : 0L;
            java.math.BigDecimal amount = map.get("amount") instanceof java.math.BigDecimal
                    ? (java.math.BigDecimal) map.get("amount")
                    : (map.get("amount") instanceof Number
                            ? new java.math.BigDecimal(((Number) map.get("amount")).toString())
                            : java.math.BigDecimal.ZERO);

            if (status != null) {
                if (status == 0) {
                    pendingAmount = pendingAmount.add(amount);
                    pendingCount += count;
                } else if (status == 1) {
                    calculatedAmount = calculatedAmount.add(amount);
                    calculatedCount += count;
                } else if (status == 2) {
                    issuedAmount = issuedAmount.add(amount);
                    issuedCount += count;
                }
            }

            Map<String, Object> item = new HashMap<>();
            item.put("name", getCommissionStatusName(status));
            item.put("value", count);
            statusStats.add(item);
        }

        vo.setPendingAmount(pendingAmount);
        vo.setCalculatedAmount(calculatedAmount);
        vo.setIssuedAmount(issuedAmount);
        vo.setPendingCount(pendingCount);
        vo.setCalculatedCount(calculatedCount);
        vo.setIssuedCount(issuedCount);
        vo.setStatusStats(statusStats);

        vo.setRecentTrend(statisticsMapper.selectRecentCommissionTrend(dto));
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

    private String getCommissionStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待核算";
            case 1 -> "已核算";
            case 2 -> "已发放";
            default -> "其他";
        };
    }

    @Override
    public TodoStatsVO getTodoStats() {
        TodoStatsVO vo = new TodoStatsVO();

        Integer roleType = SecurityUtils.getRoleType();
        Integer userId = SecurityUtils.getUserId();

        if (roleType == null || userId == null) {
            return vo; // 返回空数据
        }

        // 管理员待办
        if (roleType == 1) {
            // 待审核交易（managerAudit=0）
            vo.setPendingTransactionAudits(statisticsMapper.countTransactionsByManagerAudit(0));
            // 待审核房源（status=0）
            Long houseCount = statisticsMapper.countHousesByStatus(0);
            vo.setPendingHouseAudits(houseCount != null ? houseCount.intValue() : 0);
            // 待审核项目（audit=0）
            vo.setPendingProjectAudits(statisticsMapper.countProjectsByAudit(0));
        }

        // 销售经理待办
        if (roleType == 3) {
            // 团队待审核交易
            vo.setTeamPendingTransactions(statisticsMapper.countTeamPendingTransactions(userId));
        }

        // 销售顾问待办
        if (roleType == 2) {
            // 我的待审核交易
            vo.setMyPendingTransactions(statisticsMapper.countMyPendingTransactions(userId));
            // 我的待跟进客户（假设需要跟进的客户）
            vo.setMyFollowUpCustomers(statisticsMapper.countMyCustomers(userId));
        }

        // 财务待办
        if (roleType == 4) {
            // 待确认收款
            Long paymentCount = statisticsMapper.countPendingConfirmPayments();
            vo.setPendingPaymentConfirms(paymentCount != null ? paymentCount.intValue() : 0);
            // 待发放佣金（status=1已核算）
            vo.setPendingCommissions(statisticsMapper.countCommissionsByStatus(1));
            // 待处理交易完成申请（finishAudit=0）
            vo.setPendingFinanceConfirms(statisticsMapper.countTransactionsByFinishAudit(0));
        }

        return vo;
    }

    @Override
    public TrendStatsVO getTrendStats(Integer days) {
        // 默认查询近7天
        if (days == null || days <= 0) {
            days = 7;
        }

        TrendStatsVO vo = new TrendStatsVO();
        List<DailyStatsDTO> dailyStats = new ArrayList<>();

        // 生成近N天的日期范围
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.toString();

            DailyStatsDTO dailyDTO = new DailyStatsDTO();
            dailyDTO.setDate(dateStr);

            // 查询当日新增客户数
            Long newCustomers = statisticsMapper.countNewCustomersLineDate(dateStr);
            dailyDTO.setNewCustomers(newCustomers != null ? newCustomers.intValue() : 0);

            // 查询当日新增交易数（从selectRecentTransactionTrend获取）
            // 这里简化处理，直接计算
            dailyDTO.setNewTransactions(0); // 需要添加mapper方法

            // 查询当日收款金额
            dailyDTO.setPaymentAmount(statisticsMapper.sumPaymentAmountByDate(dateStr));

            // 查询当日新增房源数
            dailyDTO.setNewHouses(0); // 需要添加mapper方法

            dailyStats.add(dailyDTO);
        }

        vo.setDailyStats(dailyStats);
        return vo;
    }
}
