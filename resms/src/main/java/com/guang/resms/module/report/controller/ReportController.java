package com.guang.resms.module.report.controller;

import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.module.report.entity.vo.report.CustomerReportVO;
import com.guang.resms.module.report.entity.vo.report.FinancialReportVO;
import com.guang.resms.module.report.entity.vo.report.SaleReportVO;
import com.guang.resms.module.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 报表管理控制器
 */
@RestController
@RequestMapping("/report")
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

    @GetMapping("/sale/export")
    public void exportSaleReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response) throws IOException {
        List<SaleReportVO> list = reportService.getSaleReport(startDate, endDate);
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("销售报表");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("销售人员");
        header.createCell(1).setCellValue("所属团队");
        header.createCell(2).setCellValue("成交单数");
        header.createCell(3).setCellValue("成交总金额");
        header.createCell(4).setCellValue("佣金总金额");

        for (int i = 0; i < list.size(); i++) {
            SaleReportVO vo = list.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(vo.getStaffName() == null ? "" : vo.getStaffName());
            row.createCell(1).setCellValue(vo.getTeamName() == null ? "" : vo.getTeamName());
            row.createCell(2).setCellValue(vo.getDealCount() == null ? 0 : vo.getDealCount());
            row.createCell(3).setCellValue(vo.getTotalDealAmount() == null ? "0" : vo.getTotalDealAmount().toPlainString());
            row.createCell(4).setCellValue(vo.getTotalCommissionAmount() == null ? "0" : vo.getTotalCommissionAmount().toPlainString());
        }

        writeWorkbook(response, "销售报表.xlsx", wb);
    }

    @GetMapping("/financial/export")
    public void exportFinancialReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response) throws IOException {
        List<FinancialReportVO> list = reportService.getFinancialReport(startDate, endDate);
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("财务报表");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("统计月份");
        header.createCell(1).setCellValue("交易笔数");
        header.createCell(2).setCellValue("总收入");
        header.createCell(3).setCellValue("总退款");
        header.createCell(4).setCellValue("净收入");

        for (int i = 0; i < list.size(); i++) {
            FinancialReportVO vo = list.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(vo.getPeriod() == null ? "" : vo.getPeriod());
            row.createCell(1).setCellValue(vo.getTransactionCount() == null ? 0 : vo.getTransactionCount());
            row.createCell(2).setCellValue(vo.getTotalIncome() == null ? "0" : vo.getTotalIncome().toPlainString());
            row.createCell(3).setCellValue(vo.getTotalRefund() == null ? "0" : vo.getTotalRefund().toPlainString());
            row.createCell(4).setCellValue(vo.getNetIncome() == null ? "0" : vo.getNetIncome().toPlainString());
        }

        writeWorkbook(response, "财务报表.xlsx", wb);
    }

    @GetMapping("/customer/export")
    public void exportCustomerReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response) throws IOException {
        List<CustomerReportVO> list = reportService.getCustomerReport(startDate, endDate);
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("客户报表");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("销售人员");
        header.createCell(1).setCellValue("新增客户数");
        header.createCell(2).setCellValue("带看次数");
        header.createCell(3).setCellValue("成交客户数");
        header.createCell(4).setCellValue("成交转化率(%)");

        for (int i = 0; i < list.size(); i++) {
            CustomerReportVO vo = list.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(vo.getStaffName() == null ? "" : vo.getStaffName());
            row.createCell(1).setCellValue(vo.getNewCustomerCount() == null ? 0 : vo.getNewCustomerCount());
            row.createCell(2).setCellValue(vo.getViewCount() == null ? 0 : vo.getViewCount());
            row.createCell(3).setCellValue(vo.getDealCount() == null ? 0 : vo.getDealCount());
            row.createCell(4).setCellValue(vo.getConversionRate() == null ? "0" : vo.getConversionRate().toPlainString());
        }

        writeWorkbook(response, "客户报表.xlsx", wb);
    }

    private void writeWorkbook(HttpServletResponse response, String filename, XSSFWorkbook wb) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        try (ServletOutputStream os = response.getOutputStream()) {
            wb.write(os);
            os.flush();
        } finally {
            wb.close();
        }
    }
}
