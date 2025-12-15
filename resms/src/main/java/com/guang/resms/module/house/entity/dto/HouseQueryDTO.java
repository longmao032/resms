package com.guang.resms.module.house.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房源查询数据传输对象
 *
 * @author guang
 */
@Data
public class HouseQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 房源编号（模糊查询）
     */
    private String houseNo;

    /**
     * 户型（模糊查询）
     */
    private String layout;

    /**
     * 房源类型：1=二手房，2=新房，3=租房
     */
    private Integer houseType;

    /**
     * 状态：0=待审核，1=在售，2=已预订，3=已成交，4=下架
     */
    private Integer status;

    /**
     * 负责销售ID
     */
    private Integer salesId;

    /**
     * 所属项目ID
     */
    private Integer projectId;

    /**
     * 最小面积（㎡）
     */
    private BigDecimal minArea;

    /**
     * 最大面积（㎡）
     */
    private BigDecimal maxArea;

    /**
     * 最小价格（元）
     */
    private BigDecimal minPrice;

    /**
     * 最大价格（元）
     */
    private BigDecimal maxPrice;

    /**
     * 最小楼层
     */
    private Integer minFloor;

    /**
     * 最大楼层
     */
    private Integer maxFloor;

    /**
     * 朝向
     */
    private String orientation;

    /**
     * 装修情况
     */
    private String decoration;

    /**
     * 当前用户ID（用于数据权限控制）
     */
    private Integer currentUserId;

    /**
     * 数据权限范围：1=全部数据，2=本部门数据，3=本部门及以下数据，4=仅本人数据
     */
    private Integer dataScope;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy = "create_time";

    /**
     * 排序方式：asc/desc
     */
    private String order = "desc";
}
