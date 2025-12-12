package com.guang.resms.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ViewRecordQueryDTO extends QueryDTO {
    private Integer customerId;
    private Integer houseId;
    private Integer salesId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 状态：0=待确认, 1=已预约, 2=已完成, 3=已取消
     */
    private Integer status;

    /**
     * 预约方式：1=销售录入, 2=客户线上申请
     */
    private Integer appointType;
}