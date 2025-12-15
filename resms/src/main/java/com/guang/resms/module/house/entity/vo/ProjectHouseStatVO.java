package com.guang.resms.module.house.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 南宁在售新房项目统计分页VO
 */
@Data
public class ProjectHouseStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 项目基础信息（对应tb_project表）
    private Integer id;                  // 项目ID（对应p.id）
    private String projectNo;           // 项目编号（对应p.project_no）
    private String projectName;          // 项目名称（对应p.project_name）
    private String developer;            // 开发商（对应p.developer）
    private String propertyCompany;      // 物业公司（对应p.property_company）
    private String address;              // 项目地址（对应p.address）
    private BigDecimal totalArea;        // 总建筑面积（对应p.total_area）
    private Integer priceAvg;            // 项目均价（对应p.price_avg，实体类为Integer，保持一致）
    private String description;          // 项目描述（对应p.description）
    private String city;                 // 城市（对应p.city，固定为"南宁"）
    private String district;             // 区县（对应p.district）
    private String businessDistrict;     // 所属商圈（对应p.business_district）
    private String coverImg;     // 封面图片URL（对应p.cover_url）
    private LocalDate openingTime;      // 开盘时间（对应p.opening_time）

    // 新房统计信息（聚合查询结果）
    private Integer houseCount;          // 新房数量（对应COUNT(h.id)）
    private BigDecimal minArea;          // 最小面积（对应MIN(h.area)）
    private BigDecimal maxArea;          // 最大面积（对应MAX(h.area)）
    private BigDecimal minPrice;         // 最低总价（对应MIN(h.price)）
    private BigDecimal maxPrice;         // 最高总价（对应MAX(h.price)）
    private BigDecimal minUnitPrice;     // 最低单价（对应MIN(ROUND(h.price / h.area, 2))）
    private BigDecimal maxUnitPrice;     // 最高单价（对应MAX(ROUND(h.price / h.area, 2))）
    private String layoutConcat;         // 在售户型（对应GROUP_CONCAT结果）
}