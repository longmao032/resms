package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 二手房小区信息表
 * @author guang
 */
@Data
@TableName("tb_second_house_community")
public class SecondHouseCommunity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 小区名称
     */
    private String communityName;
    
    /**
     * 地址
     */
    private String address;

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
     * 建成年代
     */
    private Integer buildYear;
    
    /**
     * 开发商
     */
    private String developer;

    /**
     * 总户数
     */
    private Integer totalHouseholds;
    
    /**
     * 物业费
     */
    private BigDecimal propertyFee;
    
    /**
     * 最近地铁站
     */
    private String metroStation;
    
    /**
     * 所属学区
     */
    private String schoolDistrict;
    
    /**
     * 创建时间
     */
    private Date createTime;
}