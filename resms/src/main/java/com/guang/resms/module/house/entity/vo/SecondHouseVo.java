package com.guang.resms.module.house.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class SecondHouseVo {
    /**
     * 房屋户型
     */
    private String layout;

    /**
     * 房屋面积
     */
    private BigDecimal area;

    /**
     * 所在楼层
     */
    private Integer floor;

    /**
     * 总楼层
     */
    private Integer totalFloor;

    /**
     * 房屋朝向
     */
    private String orientation;

    /**
     * 总价（元）
     */
    private String totalPriceYuan;

    /**
     * 总价（万元）
     */
    private String totalPrice10kYuan;

    /**
     * 单价（元/㎡）
     */
    private Integer unitPriceYuanPerSqm;

    /**
     * 小区/项目名称
     */
    private String communityName;

    /**
     * 详细地址
     */
    private String detailedAddress;

    /**
     * 税务状态
     */
    private String taxStatus;

    /**
     * 房屋性质
     */
    private String housePropertyType;

    /**
     * 房屋描述
     */
    private String houseDescription;

    /**
     * 详细房屋描述
     */
    private String detailedHouseDescription;

    /**
     * 建成年份
     */
    private Integer buildYear;

    /**
     * 装修情况
     */
    private String decoration;

    /**
     * 房号
     */
    private String houseNo;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}