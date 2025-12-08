package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 二手房扩展信息表
 */
@Data
@TableName("tb_second_house_info")
public class SecondHouseInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 关联房源ID
     */
    private Integer houseId;

    /**
     * 建筑年代（如2010）
     */
    private Integer buildYear;

    /**
     * 装修时间
     */
    private LocalDateTime decorationTime;

    /**
     * 上次交易时间
     */
    private LocalDateTime lastTransactionTime;

    /**
     * 房源描述（如"满五唯一，税费低"）
     */
    private String description;

    /**
     * 小区ID
     */
    private Integer communityId;

    /**
     * 抵押情况 0=否，1=是
     */
    private Integer mortgageStatus;

    /**
     * 是否唯一住房：0=否，1=是
     */
    private Integer isOnlyHouse;

    /**
     * 是否满二：0=否，1=是
     */
    private Integer isOverTwo;

    /**
     * 是否满五：0=否，1=是
     */
    private Integer isOverFive;


    /**
     * 房屋用途：住宅/商住等
     */
    private String houseUsage;

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

    /**
     * 关联的房源信息（非数据库字段）
     */
    @TableField(exist = false)
    private House house;

    /**
     * 判断是否满二（便捷方法）
     */
    public boolean isOverTwoHouse() {
        return isOverTwo != null && isOverTwo == 1;
    }

    /**
     * 判断是否满五（便捷方法）
     */
    public boolean isOverFiveHouse() {
        return isOverFive != null && isOverFive == 1;
    }

    /**
     * 判断是否唯一住房（便捷方法）
     */
    public boolean isOnlyHouseProperty() {
        return isOnlyHouse != null && isOnlyHouse == 1;
    }


    /**
     * 计算房龄（便捷方法）
     */
    public Integer getHouseAge() {
        if (buildYear != null) {
            return LocalDateTime.now().getYear() - buildYear;
        }
        return null;
    }
}