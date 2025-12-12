package com.guang.resms.controller;

import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.entity.vo.report.CustomerReportVO;
import com.guang.resms.entity.vo.report.FinancialReportVO;
import com.guang.resms.entity.vo.report.SaleReportVO;
import com.guang.resms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 报表管理控制器
 */
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sale")
    public ResponseResult<List<SaleReportVO>> getSaleReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseResult.success(reportService.getSaleReport(startDate, endDate));
    }

    @GetMapping("/financial")
    public ResponseResult<List<FinancialReportVO>> getFinancialReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseResult.success(reportService.getFinancialReport(startDate, endDate));
    }

    @GetMapping("/customer")
    public ResponseResult<List<CustomerReportVO>> getCustomerReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseResult.success(reportService.getCustomerReport(startDate, endDate));
    }
}
