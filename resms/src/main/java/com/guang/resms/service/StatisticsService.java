package com.guang.resms.service;

import com.guang.resms.entity.vo.statistics.CustomerStatsVO;
import com.guang.resms.entity.vo.statistics.HouseStatsVO;
import com.guang.resms.entity.vo.statistics.PaymentStatsVO;
import com.guang.resms.entity.vo.statistics.TransactionStatsVO;

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
}
