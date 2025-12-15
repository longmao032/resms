package com.guang.resms.module.team.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.module.team.entity.Team;
import com.guang.resms.module.team.entity.dto.TeamDTO;
import com.guang.resms.module.team.entity.dto.TeamPerformanceQueryDTO;
import com.guang.resms.module.team.entity.dto.TeamQueryDTO;
import com.guang.resms.module.team.entity.vo.TeamPerformanceVO;
import com.guang.resms.module.team.entity.vo.TeamVO;

import java.util.List;

public interface TeamService extends IService<Team> {
    IPage<TeamVO> getTeamPage(TeamQueryDTO queryDTO);
    TeamVO getTeamDetail(Integer id);
    boolean addTeam(TeamDTO teamDTO);
    boolean updateTeam(TeamDTO teamDTO);
    boolean deleteTeam(Integer id);
    public List<TeamPerformanceVO> getTeamPerformance(TeamPerformanceQueryDTO query);
}