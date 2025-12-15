package com.guang.resms.module.report.service.impl;

import com.guang.resms.module.report.entity.vo.report.CustomerReportVO;
import com.guang.resms.module.report.entity.vo.report.FinancialReportVO;
import com.guang.resms.module.report.entity.vo.report.SaleReportVO;
import com.guang.resms.module.report.mapper.ReportMapper;
import com.guang.resms.module.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    @Override
    public List<SaleReportVO> getSaleReport(String startDate, String endDate) {
        String s = normalizeStartDateTime(startDate);
        String e = normalizeEndDateTime(endDate);
        return reportMapper.selectSaleReport(s, e);
    }

    @Override
    public List<FinancialReportVO> getFinancialReport(String startDate, String endDate) {
        String s = normalizeStartDateTime(startDate);
        String e = normalizeFinancialEndDateTime(endDate);
        return reportMapper.selectFinancialReport(s, e);
    }

    @Override
    public List<CustomerReportVO> getCustomerReport(String startDate, String endDate) {
        String s = normalizeStartDateTime(startDate);
        String e = normalizeEndDateTime(endDate);
        List<CustomerReportVO> list = reportMapper.selectCustomerReport(s, e);
        // 计算转化率
        for (CustomerReportVO vo : list) {
            if (vo.getNewCustomerCount() != null && vo.getNewCustomerCount() > 0 && vo.getDealCount() != null) {
                BigDecimal rate = BigDecimal.valueOf(vo.getDealCount())
                        .divide(BigDecimal.valueOf(vo.getNewCustomerCount()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                vo.setConversionRate(rate.setScale(2, RoundingMode.HALF_UP));
            } else {
                vo.setConversionRate(BigDecimal.ZERO);
            }
        }
        return list;
    }

    private String normalizeStartDateTime(String date) {
        if (date == null) {
            return null;
        }
        String d = date.trim();
        if (d.isEmpty()) {
            return null;
        }
        if (d.length() == 10 && !d.contains(" ")) {
            return d + " 00:00:00";
        }
        return d;
    }

    private String normalizeEndDateTime(String date) {
        if (date == null) {
            return null;
        }
        String d = date.trim();
        if (d.isEmpty()) {
            return null;
        }
        if (d.length() == 10 && !d.contains(" ")) {
            return d + " 23:59:59";
        }
        return d;
    }

    private String normalizeFinancialEndDateTime(String date) {
        if (date == null) {
            return null;
        }
        String d = date.trim();
        if (d.isEmpty()) {
            return null;
        }
        if (d.length() == 10 && !d.contains(" ")) {
            LocalDate parsed = LocalDate.parse(d);
            YearMonth ym = YearMonth.of(parsed.getYear(), parsed.getMonth());
            return ym.atEndOfMonth() + " 23:59:59";
        }
        return d;
    }
}
