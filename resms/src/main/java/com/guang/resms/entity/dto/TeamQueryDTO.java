package com.guang.resms.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQueryDTO extends QueryDTO {
    /**
     * 团队名称（模糊查询）
     */
    private String teamName;
}