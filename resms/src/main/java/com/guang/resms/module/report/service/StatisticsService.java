package com.guang.resms.module.report.service;

import com.guang.resms.module.report.entity.vo.statistics.CustomerStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.HouseStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.CommissionStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.PaymentStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.TodoStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.TrendStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.TransactionStatsVO;
import com.guang.resms.module.report.entity.dto.CommissionStatsDTO;

/**
 * 统计分析服务接口
 */
public interface StatisticsService {

    /**
     * 获取房源统计数据
     */
    HouseStatsVO getHouseStats();

    /**
     * 获取客户统计数据
     */
    CustomerStatsVO getCustomerStats();

    /**
     * 获取交易统计数据
     */
    TransactionStatsVO getTransactionStats();

    /**
     * 获取收款统计数据
     */
    PaymentStatsVO getPaymentStats();

    CommissionStatsVO getCommissionStats(CommissionStatsDTO dto);

    /**
     * 获取待办事项统计（根据角色返回不同数据）
     */
    TodoStatsVO getTodoStats();

    /**
     * 获取趋势统计数据
     * 
     * @param days 统计天数，默认7天
     */
    TrendStatsVO getTrendStats(Integer days);
}
