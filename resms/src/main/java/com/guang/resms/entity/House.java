package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房源信息表 实体类
 * 对应数据库表：tb_house
 */
@Data
@TableName("tb_house")
public class House {

    /**
     * 房源ID（自增主键）
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 房源编号（唯一，如"FC20240001"）
     */
    private String houseNo;

    /**
     * 建筑面积（㎡）
     */
    private BigDecimal area;

    /**
     * 户型名称（如"A户型"）
     */
    private String unitName;

    /**
     * 户型（如"3室2厅"）
     */
    private String layout;

    /**
     * 所在楼层
     */
    private Integer floor;

    /**
     * 总楼层
     */
    private Integer totalFloor;

    /**
     * 朝向（如"南北通透"）
     */
    private String orientation;

    /**
     * 装修情况（如"精装修"）
     */
    private String decoration;

    /**
     * 挂牌价（元）
     */
    private BigDecimal price;

    /**
     * 状态：0=待审核，1=在售，2=已预订，3=已成交，4=下架（默认0）
     */
    private Integer status;

    /**
     * 负责销售ID（关联tb_user表）
     */

    private Integer salesId;

    /**
     * 房源描述（如"近地铁、学区房"）
     */
    private String description;

    /**
     * 房源类型：1=二手房，2=新房，3=租房（默认1）
     */
    private Integer houseType;

    /**
     * 楼栋号
     */
    private Integer buildingNo;

    /**
     * 所属项目
     */
    private Integer projectId;

    /**
     * 房号（如"101"）
     */
    private String roomNo;

    /**
     * 创建时间（默认当前时间）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（默认当前时间，更新时自动刷新）
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}