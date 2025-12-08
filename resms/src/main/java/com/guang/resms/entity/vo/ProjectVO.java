package com.guang.resms.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目视图对象
 *
 * @author guang
 */
@Data
public class ProjectVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Integer id;

    /**
     * 项目编号
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
     * 占地面积（㎡）
     */
    private BigDecimal landArea;

    /**
     * 容积率
     */
    private BigDecimal plotRatio;

    /**
     * 绿化率（%）
     */
    private BigDecimal greeningRate;

    /**
     * 车位配比
     */
    private String parkingRatio;

    /**
     * 总户数
     */
    private Integer totalHouses;

    /**
     * 物业费（元/㎡/月）
     */
    private BigDecimal propertyFee;

    /**
     * 建筑类型
     */
    private String buildingType;

    /**
     * 开盘时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate openingDate;

    /**
     * 交房时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

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
     * 区域
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
     * 距离地铁（米）
     */
    private Integer metroDistance;

    /**
     * 最近地铁站
     */
    private String nearestMetro;

    /**
     * 学区信息
     */
    private String schoolDistrict;

    /**
     * 商圈信息
     */
    private String businessCircle;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 状态：0=规划中，1=在售，2=售罄
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
