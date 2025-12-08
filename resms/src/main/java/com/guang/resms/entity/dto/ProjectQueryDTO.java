package com.guang.resms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目查询DTO
 */
@Data
public class ProjectQueryDTO {
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 项目名称（模糊查询）
     */
    private String projectName;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 开发商
     */
    private String developer;

    /**
     * 状态：1=在售，2=售罄，3=待售
     */
    private Integer status;

    /**
     * 最低均价
     */
    private Integer minPrice;

    /**
     * 最高均价
     */
    private Integer maxPrice;

    /**
     * 物业类型
     */
    private String propertyType;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式：ASC/DESC
     */
    private String sortOrder;
}
