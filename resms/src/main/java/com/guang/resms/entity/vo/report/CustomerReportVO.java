package com.guang.resms.entity.vo.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 客户报表 VO
 * 按销售人员统计客户转化情况
 */
@Data
public class CustomerReportVO {
    /**
     * 员工姓名
     */
    private String staffName;

    /**
     * 新增客户数
     */
    private Long newCustomerCount;

    /**
     * 带看次数
     */
    private Long viewCount;

    /**
     * 成交客户数
     */
    private Long dealCount;

    /**
     * 转化率 (成交/新增)
     */
    private BigDecimal conversionRate;
}
