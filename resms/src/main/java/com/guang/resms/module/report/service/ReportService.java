package com.guang.resms.module.report.service;

import com.guang.resms.module.report.entity.vo.report.CustomerReportVO;
import com.guang.resms.module.report.entity.vo.report.FinancialReportVO;
import com.guang.resms.module.report.entity.vo.report.SaleReportVO;
import java.util.List;

/**
 * 报表服务接口
 */
public interface ReportService {

    /**
     * 获取销售报表
     * 
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate   结束日期 yyyy-MM-dd
     */
    List<SaleReportVO> getSaleReport(String startDate, String endDate);

    /**
     * 获取财务报表
     */
    List<FinancialReportVO> getFinancialReport(String startDate, String endDate);

    /**
     * 获取客户报表
     */
    List<CustomerReportVO> getCustomerReport(String startDate, String endDate);
}
