package com.guang.resms.module.team.entity.vo;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TeamMemberVO {
    private Integer userId;
    private String realName;
    private String phone;
    private LocalDate joinTime;
}