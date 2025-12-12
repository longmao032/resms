package com.guang.resms.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 带看记录/预约 DTO
 */
@Data
public class ViewRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID (更新时必填)
     */
    private Integer id;

    /**
     * 客户ID
     */
    private Integer customerId;

    /**
     * 房源ID
     */
    private Integer houseId;

    /**
     * 销售ID
     */
    private Integer salesId;

    /**
     * 预约时间/带看时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date viewTime;

    /**
     * 预约方式：1=销售录入, 2=客户线上申请
     */
    private Integer appointType;

    /**
     * 客户反馈
     */
    private String customerFeedback;

    /**
     * 跟进建议
     */
    private String followAdvice;
}
