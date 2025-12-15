package com.guang.resms.module.team.entity.dto;

import com.guang.resms.module.user.entity.dto.QueryDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQueryDTO extends QueryDTO {
    /**
     * 团队名称（模糊查询）
     */
    private String teamName;

    private Integer teamId;
}