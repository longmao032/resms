package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 新房项目表实体类
 */
@Data
@TableName("tb_project")
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 项目编号（唯一，如"XM20240001"）
     */
    private String projectNo;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 开发商
     */
    private String developer;

    /**
     * 物业公司
     */
    private String propertyCompany;

    /**
     * 项目地址
     */
    private String address;

    /**
     * 总建筑面积（㎡）
     */
    private BigDecimal totalArea;

    /**
     * 容积率
     */
    private BigDecimal plotRatio;

    /**
     * 绿化率（%）
     */
    private BigDecimal greeningRate;

    /**
     * 车位比
     */
    private String parkingRatio;

    /**
     * 总户数
     */
    private String totalHouseholds ;

    /**
     * 平均价格（元/㎡）
     */
    private Integer priceAvg;

    /**
     * 物业费（元/㎡/月）
     */
    private BigDecimal propertyFee;

    /**
     * 物业类型：住宅/公寓/别墅等
     */
    private String propertyType;

    /**
     * 开盘时间
     */
    @TableField("opening_time")
    private LocalDate openingTime;

    /**
     * 交房时间
     */
    @TableField("completion_time")
    private LocalDateTime completionTime;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 最近地铁站距离（米）
     */
    private Integer metroDistance;

    /**
     * 最近地铁站名称
     */
    private String metroStation;

    /**
     * 所属学区
     */
    private String schoolDistrict;

    /**
     * 所属商圈
     */
    private String businessDistrict;

    /**
     * 默认封面图片URL
     */
     private String coverUrl;

    /**
     * 状态：1=在售，2=售罄，3=待售
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    // 关联的新房列表
    @TableField(exist = false)
    private List<House> newHouses;
}