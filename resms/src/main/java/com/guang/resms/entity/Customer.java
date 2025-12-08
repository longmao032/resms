package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户信息表
 */
@TableName("tb_customer")
@Data
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 客户ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 客户编号（唯一，如"KH20240001"）
     */
    private String customerNo;

    /**
     * 客户姓名
     */
    private String realName;

    /**
     * 客户电话（唯一）
     */
    private String phone;

    /**
     * 身份证号（脱敏存储）
     */
    private String idCard;

    /**
     * 意向面积（㎡）
     */
    private BigDecimal demandArea;

    /**
     * 意向价格（元）
     */
    private BigDecimal demandPrice;

    /**
     * 意向户型
     */
    private String demandLayout;

    /**
     * 意向区域
     */
    private String demandAreaRegion;

    /**
     * 意向等级：1=高，2=中，3=低
     */
    private Integer intentionLevel;

    /**
     * 对接销售ID（关联tb_user表）
     */
    private Integer salesId;

    /**
     * 客户来源（如"门店接待""线上咨询"）
     */
    private String source;

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
}