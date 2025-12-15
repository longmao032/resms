package com.guang.resms.shared.entity.dto;

import lombok.Data;

@Data
public class OperationLogQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private String module;
    private String operationType;
    private Integer riskLevel;
    private String riskDimension;
    private Integer status;
    private Integer userId;

    private String beginTime;
    private String endTime;
}
