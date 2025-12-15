package com.guang.resms.module.team.entity.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TeamPerformanceVO {
    private Integer teamId;
    private String teamName;
    private String managerName;
    private Integer memberCount;
    
    // 业绩指标
    private BigDecimal performanceTarget; // 目标
    private BigDecimal actualPerformance; // 实际
    private BigDecimal completionRate;    // 完成率 (%)
    private Integer transactionCount;     // 成交单数
    private BigDecimal commissionTotal;   // 佣金总额 (可选)
    
    // 排名 (可选)
    private Integer rank;
}