package com.guang.resms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.entity.Team;
import com.guang.resms.entity.dto.TeamDTO;
import com.guang.resms.entity.dto.TeamPerformanceQueryDTO;
import com.guang.resms.entity.dto.TeamQueryDTO;
import com.guang.resms.entity.vo.TeamPerformanceVO;
import com.guang.resms.entity.vo.TeamVO;

import java.util.List;

public interface TeamService extends IService<Team> {
    IPage<TeamVO> getTeamPage(TeamQueryDTO queryDTO);
    TeamVO getTeamDetail(Integer id);
    boolean addTeam(TeamDTO teamDTO);
    boolean updateTeam(TeamDTO teamDTO);
    boolean deleteTeam(Integer id);
    public List<TeamPerformanceVO> getTeamPerformance(TeamPerformanceQueryDTO query);
}