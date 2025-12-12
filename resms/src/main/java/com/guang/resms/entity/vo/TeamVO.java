package com.guang.resms.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class TeamVO {
    private Integer id;
    private String teamName;
    private Integer managerId;
    private String managerName;
    private String managerPhone;
    private Integer teamSize; // 数据库存的字段
    private Integer memberCount; // 实时统计的字段
    private BigDecimal performanceTarget;
    private LocalDate establishTime;
    private String createTime;
    
    // 详情时返回的成员列表
    private List<TeamMemberVO> members;
}