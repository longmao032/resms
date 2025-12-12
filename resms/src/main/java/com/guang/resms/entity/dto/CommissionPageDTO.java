package com.guang.resms.entity.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class CommissionPageDTO implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    
    private String transactionNo; // 交易编号
    private String salesName;     // 销售姓名
    private Integer status;       // 佣金状态
}