package com.guang.resms.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TeamDTO implements Serializable {
    
    private Integer id;

    @NotBlank(message = "团队名称不能为空")
    private String teamName;

    @NotNull(message = "请选择团队经理")
    private Integer managerId;

    private BigDecimal performanceTarget;

    /**
     * 团队成员ID列表
     */
    private List<Integer> memberIds;
}