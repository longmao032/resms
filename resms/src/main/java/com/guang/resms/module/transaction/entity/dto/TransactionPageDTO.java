package com.guang.resms.module.transaction.entity.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class TransactionPageDTO implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    
    // 搜索条件
    private String transactionNo; // 交易编号
    private String customerName;  // 客户姓名（关联查询）
    private Integer status;       // 交易状态
    private Integer salesId;      // 销售人员ID（数据权限控制用）
}