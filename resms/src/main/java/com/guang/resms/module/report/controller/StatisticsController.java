package com.guang.resms.module.report.controller;

import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.module.report.entity.dto.CommissionStatsDTO;
import com.guang.resms.module.report.entity.vo.statistics.CustomerStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.CommissionStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.HouseStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.PaymentStatsVO;
import com.guang.resms.module.report.entity.vo.statistics.TransactionStatsVO;
import com.guang.resms.module.report.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计分析控制器
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取房源统计数据
     */
    @GetMapping("/house")
    public ResponseResult<HouseStatsVO> getHouseStats() {
        return ResponseResult.success(statisticsService.getHouseStats());
    }

    /**
     * 获取客户统计数据
     */
    @GetMapping("/customer")
    public ResponseResult<CustomerStatsVO> getCustomerStats() {
        return ResponseResult.success(statisticsService.getCustomerStats());
    }

    /**
     * 获取交易统计数据
     */
    @GetMapping("/transaction")
    public ResponseResult<TransactionStatsVO> getTransactionStats() {
        return ResponseResult.success(statisticsService.getTransactionStats());
    }

    /**
     * 获取收款统计数据
     */
    @GetMapping("/payment")
    public ResponseResult<PaymentStatsVO> getPaymentStats() {
        return ResponseResult.success(statisticsService.getPaymentStats());
    }

    @GetMapping("/commission")
    public ResponseResult<CommissionStatsVO> getCommissionStats(CommissionStatsDTO dto) {
        return ResponseResult.success(statisticsService.getCommissionStats(dto));
    }
}
