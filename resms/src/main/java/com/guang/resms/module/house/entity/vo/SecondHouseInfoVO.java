package com.guang.resms.module.house.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 二手房扩展信息视图对象
 *
 * @author guang
 */
@Data
public class SecondHouseInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 二手房信息ID
     */
    private Integer id;

    /**
     * 房源ID
     */
    private Integer houseId;

    /**
     * 小区ID
     */
    private Integer communityId;

    /**
     * 小区名称
     */
    private String communityName;

    /**
     * 建筑年代
     */
    private Integer buildYear;

    /**
     * 装修时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate decorationTime;

    /**
     * 上次交易时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastTradeDate;

    /**
     * 是否满二：0=否，1=是
     */
    private Integer isOverTwo;

    /**
     * 是否满五：0=否，1=是
     */
    private Integer isOverFive;

    /**
     * 是否唯一住房：0=否，1=是
     */
    private Integer isOnlyHouse;

    /**
     * 抵押状态：0=无抵押，1=已抵押
     */
    private Integer mortgageStatus;

    /**
     * 抵押状态文本
     */
    private String mortgageStatusText;

    /**
     * 房本状态：0=无房本，1=有房本
     */
    private Integer certificateStatus;

    /**
     * 房本状态文本
     */
    private String certificateStatusText;

    /**
     * 房屋用途
     */
    private String houseUsage;

    /**
     * 房源描述
     */
    private String description;

    /**
     * 房龄（年）- 计算字段
     */
    private Integer houseAge;

    /**
     * 产权年限（年）- 计算字段
     */
    private Integer propertyRight;

    /**
     * 备注
     */
    private String remark;
}
