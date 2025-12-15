package com.guang.resms.module.report.mapper;

import com.guang.resms.module.report.entity.dto.CommissionStatsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 统计分析 Mapper
 */
@Mapper
public interface StatisticsMapper {

    // --- 房源统计 ---
    Long countTotalHouses();

    Long countHousesByStatus(@Param("status") Integer status);

    List<Map<String, Object>> countHousesGroupByStatus();

    List<Map<String, Object>> countHousesGroupByType();

    // --- 客户统计 ---
    Long countTotalCustomers();

    Long countNewCustomersLineDate(@Param("date") String date); // 统计某天之后或当天

    List<Map<String, Object>> countCustomersGroupByIntention();

    List<Map<String, Object>> countCustomersGroupBySource();

    // --- 交易统计 ---
    BigDecimal sumTotalTransactionAmount();

    Long countTotalTransactions();

    Long countPendingAuditTransactions(); // manager_audit = 0

    List<Map<String, Object>> selectRecentTransactionTrend(@Param("days") int days);

    // --- 收款统计 ---
    BigDecimal sumTotalPaymentAmount();

    BigDecimal sumPaymentAmountByDate(@Param("date") String date);

    Long countPendingConfirmPayments(); // payment_status = 0

    List<Map<String, Object>> selectRecentPaymentTrend(@Param("days") int days);

    BigDecimal sumCommissionAmount(@Param("dto") CommissionStatsDTO dto);

    Long countCommission(@Param("dto") CommissionStatsDTO dto);

    List<Map<String, Object>> groupCommissionByStatus(@Param("dto") CommissionStatsDTO dto);

    List<Map<String, Object>> selectRecentCommissionTrend(@Param("dto") CommissionStatsDTO dto);
}
