package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 新房扩展信息表
 * @author guang
 */
@Data
@TableName("tb_new_house_info")
public class NewHouseInfo {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 房源ID
     */
    private Integer houseId;
    
    /**
     * 预售许可证号
     */
    private String preSaleLicenseNo;
    
    /**
     * 备案价
     */
    private BigDecimal recordPrice;
    
    /**
     * 产权年限
     */
    private Integer propertyRightYears;
    
    /**
     * 预计交房时间
     */
    private Date estimatedDeliveryTime;
    
    /**
     * 交付标准
     */
    private String deliveryStandard;
    
    /**
     * 梯户比
     */
    private String elevatorRatio;
    
    /**
     * 得房率(%)
     */
    private BigDecimal actualAreaRate;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}