package com.guang.resms.module.transaction.entity.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class CommissionPageDTO implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    
    private String transactionNo; // 交易编号
    private String salesName;     // 销售姓名
    private Integer salesId;      // 销售ID（数据权限过滤）
    private Integer status;       // 佣金状态
}