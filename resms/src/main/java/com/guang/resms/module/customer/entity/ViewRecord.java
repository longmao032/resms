package com.guang.resms.module.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 带看记录表
 */
@Data
@TableName("tb_view_record")
public class ViewRecord implements Serializable {
    // Force recompile
    private static final long serialVersionUID = 1L;

    /**
     * 带看ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 客户ID（关联tb_customer表）
     */
    private Integer customerId;

    /**
     * 房源ID（关联tb_house表）
     */
    private Integer houseId;

    /**
     * 销售ID（关联tb_user表）
     */
    private Integer salesId;

    /**
     * 带看时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date viewTime;

    /**
     * 客户反馈（如"价格偏高，户型满意"）
     */
    private String customerFeedback;

    /**
     * 跟进建议（如"下周推送同小区低价房源"）
     */
    private String followAdvice;

    /**
     * 状态：0=待确认, 1=已预约, 2=已完成, 3=已取消
     */
    private Integer status;

    /**
     * 预约方式：1=销售录入, 2=客户线上申请
     */
    private Integer appointType;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}