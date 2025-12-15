package com.guang.resms.module.house.entity.dto;

import lombok.Data;

/**
 * 小区查询DTO
 */
@Data
public class CommunityQueryDTO {
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 小区名称（模糊查询）
     */
    private String communityName;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 建成年代（起始）
     */
    private Integer minBuildYear;

    /**
     * 建成年代（结束）
     */
    private Integer maxBuildYear;

    /**
     * 开发商
     */
    private String developer;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式：ASC/DESC
     */
    private String sortOrder;
}
