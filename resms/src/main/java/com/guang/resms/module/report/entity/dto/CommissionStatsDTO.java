package com.guang.resms.module.report.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommissionStatsDTO implements Serializable {
    private String startDate;
    private String endDate;

    private Integer salesId;
    private Integer status;

    private Integer days = 30;
}
