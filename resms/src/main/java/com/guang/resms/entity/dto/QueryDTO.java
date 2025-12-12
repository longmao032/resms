package com.guang.resms.entity.dto;

import lombok.Data;
import java.math.BigDecimal;


@Data
public class QueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String city;
    private String district;
    private String keyword;
    private Integer minPrice;
    private Integer maxPrice;
    private BigDecimal minArea;
    private BigDecimal maxArea;
    private BigDecimal minUnitPrice;
    private BigDecimal maxUnitPrice;
    private String propertyType;
    private String layout;
    private String sortField;
    private String sortOrder;

    private Integer noticeType; // 筛选类型
    private Integer isRead;     // 筛选状态: 0=未读, 1=已读
}