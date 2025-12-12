package com.guang.resms.mapper;

import com.guang.resms.entity.vo.report.CustomerReportVO;
import com.guang.resms.entity.vo.report.FinancialReportVO;
import com.guang.resms.entity.vo.report.SaleReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 报表管理 Mapper
 */
@Mapper
public interface ReportMapper {

    /**
     * 查询销售报表
     */
    List<SaleReportVO> selectSaleReport(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询财务报表
     */
    List<FinancialReportVO> selectFinancialReport(@Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /**
     * 查询客户报表
     */
    List<CustomerReportVO> selectCustomerReport(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
