package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.entity.Team;
import com.guang.resms.entity.TeamMember;
import com.guang.resms.entity.dto.TeamDTO;
import com.guang.resms.entity.dto.TeamPerformanceQueryDTO;
import com.guang.resms.entity.dto.TeamQueryDTO;
import com.guang.resms.entity.vo.TeamPerformanceVO;
import com.guang.resms.entity.vo.TeamVO;
import com.guang.resms.mapper.TeamMapper;
import com.guang.resms.mapper.TeamMemberMapper;
import com.guang.resms.service.TeamService;
import com.guang.resms.common.exception.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Autowired
    private com.guang.resms.service.NotificationService notificationService;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Override
    public IPage<TeamVO> getTeamPage(TeamQueryDTO queryDTO) {
        Page<TeamVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return teamMapper.selectTeamPage(page, queryDTO);
    }

    @Override
    public TeamVO getTeamDetail(Integer id) {
        TeamVO teamVO = teamMapper.selectTeamDetailById(id);
        if (teamVO != null) {
            teamVO.setMembers(teamMemberMapper.selectMembersByTeamId(id));
        }
        return teamVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTeam(TeamDTO teamDTO) {
        // 1. 校验名称唯一性
        Long count = this.count(new LambdaQueryWrapper<Team>().eq(Team::getTeamName, teamDTO.getTeamName()));
        if (count > 0) {
            throw new ServiceException("团队名称已存在");
        }

        // 2. 保存团队
        Team team = new Team();
        BeanUtils.copyProperties(teamDTO, team);
        team.setEstablishTime(LocalDate.now());
        // 初始人数
        int size = (teamDTO.getMemberIds() != null) ? teamDTO.getMemberIds().size() : 0;
        team.setTeamSize(size);
        this.save(team);

        // 3. 保存成员
        if (size > 0) {
            saveMembers(team.getId(), teamDTO.getMemberIds(), team.getTeamName());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeam(TeamDTO teamDTO) {
        Team existingTeam = this.getById(teamDTO.getId());
        if (existingTeam == null)
            throw new ServiceException("团队不存在");

        // 1. 更新基本信息
        Team team = new Team();
        BeanUtils.copyProperties(teamDTO, team);
        // 更新人数
        int size = (teamDTO.getMemberIds() != null) ? teamDTO.getMemberIds().size() : 0;
        team.setTeamSize(size);
        this.updateById(team);

        // 2. 更新成员列表 (差异更新)
        String teamName = (teamDTO.getTeamName() != null) ? teamDTO.getTeamName() : existingTeam.getTeamName();
        updateMembers(teamDTO.getId(), teamDTO.getMemberIds(), teamName);

        return true;
    }

    private void saveMembers(Integer teamId, List<Integer> memberIds, String teamName) {
        List<TeamMember> list = new ArrayList<>();
        for (Integer userId : memberIds) {
            TeamMember member = new TeamMember();
            member.setTeamId(teamId);
            member.setUserId(userId);
            member.setJoinTime(LocalDate.now());
            list.add(member);

            // 发送入队通知
            notificationService.notifyTeamChanged(userId, teamName, true);
        }
        // 这里可以使用 TeamMemberService 的 saveBatch，或者循环插入
        for (TeamMember tm : list) {
            teamMemberMapper.insert(tm);
        }
    }

    private void updateMembers(Integer teamId, List<Integer> newMemberIds, String teamName) {
        if (newMemberIds == null)
            newMemberIds = new ArrayList<>();

        // 获取现有成员ID
        List<TeamMember> currentMembers = teamMemberMapper.selectList(
                new LambdaQueryWrapper<TeamMember>().eq(TeamMember::getTeamId, teamId));
        List<Integer> currentIds = currentMembers.stream().map(TeamMember::getUserId).collect(Collectors.toList());

        // 找出需要删除的 (在目前列表中，但不在新列表中)
        List<Integer> finalNewMemberIds = newMemberIds;
        List<Integer> toDelete = currentIds.stream()
                .filter(id -> !finalNewMemberIds.contains(id))
                .collect(Collectors.toList());

        // 找出需要新增的 (在新列表中，但不在目前列表中)
        List<Integer> toAdd = newMemberIds.stream()
                .filter(id -> !currentIds.contains(id))
                .collect(Collectors.toList());

        // 执行删除
        if (!toDelete.isEmpty()) {
            teamMemberMapper.delete(
                    new LambdaQueryWrapper<TeamMember>()
                            .eq(TeamMember::getTeamId, teamId)
                            .in(TeamMember::getUserId, toDelete));

            // 发送离队通知
            for (Integer userId : toDelete) {
                notificationService.notifyTeamChanged(userId, teamName, false);
            }
        }

        // 执行新增
        if (!toAdd.isEmpty()) {
            saveMembers(teamId, toAdd, teamName);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Integer id) {
        // 1. 安全检查：验证团队是否还有成员
        Long memberCount = teamMemberMapper.selectCount(
                new LambdaQueryWrapper<TeamMember>().eq(TeamMember::getTeamId, id));

        if (memberCount > 0) {
            throw new ServiceException("该团队尚有成员，请先移除所有成员后再解散");
        }

        // 2. 执行删除
        return this.removeById(id);
    }

    @Override
    public List<TeamPerformanceVO> getTeamPerformance(TeamPerformanceQueryDTO query) {
        List<TeamPerformanceVO> list = teamMapper.selectTeamPerformance(query);

        // 计算完成率
        for (TeamPerformanceVO vo : list) {
            if (vo.getPerformanceTarget() != null && vo.getPerformanceTarget().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = vo.getActualPerformance()
                        .divide(vo.getPerformanceTarget(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100));
                vo.setCompletionRate(rate);
            } else {
                vo.setCompletionRate(BigDecimal.ZERO);
            }
        }
        return list;
    }
}