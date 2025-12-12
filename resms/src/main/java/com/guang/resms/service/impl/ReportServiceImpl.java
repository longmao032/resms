package com.guang.resms.service.impl;

import com.guang.resms.entity.vo.report.CustomerReportVO;
import com.guang.resms.entity.vo.report.FinancialReportVO;
import com.guang.resms.entity.vo.report.SaleReportVO;
import com.guang.resms.mapper.ReportMapper;
import com.guang.resms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    @Override
    public List<SaleReportVO> getSaleReport(String startDate, String endDate) {
        return reportMapper.selectSaleReport(startDate, endDate);
    }

    @Override
    public List<FinancialReportVO> getFinancialReport(String startDate, String endDate) {
        return reportMapper.selectFinancialReport(startDate, endDate);
    }

    @Override
    public List<CustomerReportVO> getCustomerReport(String startDate, String endDate) {
        List<CustomerReportVO> list = reportMapper.selectCustomerReport(startDate, endDate);
        // 计算转化率
        for (CustomerReportVO vo : list) {
            if (vo.getNewCustomerCount() != null && vo.getNewCustomerCount() > 0 && vo.getDealCount() != null) {
                BigDecimal rate = BigDecimal.valueOf(vo.getDealCount())
                        .divide(BigDecimal.valueOf(vo.getNewCustomerCount()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                vo.setConversionRate(rate);
            } else {
                vo.setConversionRate(BigDecimal.ZERO);
            }
        }
        return list;
    }
}
