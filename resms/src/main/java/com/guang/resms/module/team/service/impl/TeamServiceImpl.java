package com.guang.resms.module.team.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.module.team.entity.Team;
import com.guang.resms.module.team.entity.TeamMember;
import com.guang.resms.module.team.entity.dto.TeamDTO;
import com.guang.resms.module.team.entity.dto.TeamPerformanceQueryDTO;
import com.guang.resms.module.team.entity.dto.TeamQueryDTO;
import com.guang.resms.module.team.entity.vo.TeamPerformanceVO;
import com.guang.resms.module.team.entity.vo.TeamVO;
import com.guang.resms.module.team.mapper.TeamMapper;
import com.guang.resms.module.team.mapper.TeamMemberMapper;
import com.guang.resms.module.team.service.TeamService;
import com.guang.resms.module.user.mapper.UserRoleMapper;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.utils.SecurityUtils;
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
    private com.guang.resms.module.chat.service.NotificationService notificationService;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public IPage<TeamVO> getTeamPage(TeamQueryDTO queryDTO) {
        applyTeamScope(queryDTO);
        Page<TeamVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return teamMapper.selectTeamPage(page, queryDTO);
    }

    @Override
    public TeamVO getTeamDetail(Integer id) {
        enforceTeamAccess(id);
        TeamVO teamVO = teamMapper.selectTeamDetailById(id);
        if (teamVO != null) {
            teamVO.setMembers(teamMemberMapper.selectMembersByTeamId(id));
        }
        return teamVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTeam(TeamDTO teamDTO) {
        enforceAdmin();

        validateManager(teamDTO.getManagerId(), null);
        validateMembers(teamDTO.getMemberIds(), null);

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
        Integer roleType = SecurityUtils.getRoleType();
        Integer userId = SecurityUtils.getUserId();
        if (roleType == null || userId == null) {
            throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }

        Team existingTeam = this.getById(teamDTO.getId());
        if (existingTeam == null)
            throw new ServiceException("团队不存在");

        boolean isAdmin = roleType == 1;
        boolean isManager = roleType == 3;
        if (!isAdmin && !isManager) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }

        if (isManager) {
            if (existingTeam.getManagerId() == null || !existingTeam.getManagerId().equals(userId)) {
                throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
            }
            if (teamDTO.getTeamName() != null && !teamDTO.getTeamName().equals(existingTeam.getTeamName())) {
                throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
            }
            if (teamDTO.getManagerId() != null && !teamDTO.getManagerId().equals(existingTeam.getManagerId())) {
                throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
            }
            teamDTO.setTeamName(existingTeam.getTeamName());
            teamDTO.setManagerId(existingTeam.getManagerId());
        }

        if (teamDTO.getTeamName() != null && !teamDTO.getTeamName().equals(existingTeam.getTeamName())) {
            Long nameCnt = this.count(new LambdaQueryWrapper<Team>()
                    .eq(Team::getTeamName, teamDTO.getTeamName())
                    .ne(Team::getId, teamDTO.getId()));
            if (nameCnt != null && nameCnt > 0) {
                throw new ServiceException("团队名称已存在");
            }
        }

        validateManager(teamDTO.getManagerId(), teamDTO.getId());
        validateMembers(teamDTO.getMemberIds(), teamDTO.getId());

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
        enforceAdmin();

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
        applyTeamScope(query);
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

    private void enforceAdmin() {
        Integer roleType = SecurityUtils.getRoleType();
        Integer userId = SecurityUtils.getUserId();
        if (roleType == null || userId == null) {
            throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }
        if (roleType != 1) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }
    }

    private void enforceTeamAccess(Integer teamId) {
        Integer roleType = SecurityUtils.getRoleType();
        Integer userId = SecurityUtils.getUserId();
        if (roleType == null || userId == null) {
            throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }
        if (roleType == 1) {
            return;
        }

        Integer scopedTeamId = resolveTeamIdByRole(roleType, userId);
        if (scopedTeamId == null || !scopedTeamId.equals(teamId)) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }
    }

    private void applyTeamScope(TeamQueryDTO queryDTO) {
        if (queryDTO == null) {
            return;
        }
        Integer roleType = SecurityUtils.getRoleType();
        Integer userId = SecurityUtils.getUserId();
        if (roleType == null || userId == null) {
            throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }
        if (roleType == 1) {
            return;
        }
        if (roleType != 2 && roleType != 3) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }
        Integer teamId = resolveTeamIdByRole(roleType, userId);
        queryDTO.setTeamId(teamId == null ? -1 : teamId);
    }

    private void applyTeamScope(TeamPerformanceQueryDTO queryDTO) {
        if (queryDTO == null) {
            return;
        }
        Integer roleType = SecurityUtils.getRoleType();
        Integer userId = SecurityUtils.getUserId();
        if (roleType == null || userId == null) {
            throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }
        if (roleType == 1) {
            return;
        }
        if (roleType != 2 && roleType != 3) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }
        Integer teamId = resolveTeamIdByRole(roleType, userId);
        queryDTO.setTeamId(teamId == null ? -1 : teamId);
    }

    private Integer resolveTeamIdByRole(Integer roleType, Integer userId) {
        if (roleType == null || userId == null) {
            return null;
        }
        if (roleType == 3) {
            return teamMapper.selectTeamIdByManagerId(userId);
        }
        if (roleType == 2) {
            return teamMemberMapper.selectTeamIdByUserId(userId);
        }
        return null;
    }

    private void validateManager(Integer managerId, Integer currentTeamId) {
        if (managerId == null) {
            return;
        }
        List<Integer> roleIds = userRoleMapper.selectRoleIdsByUserId(managerId);
        if (roleIds == null || !roleIds.contains(3)) {
            throw new ServiceException("团队经理角色不正确");
        }
        Integer existTeamId = teamMapper.selectTeamIdByManagerId(managerId);
        if (existTeamId != null && (currentTeamId == null || !existTeamId.equals(currentTeamId))) {
            throw new ServiceException("该经理已绑定其他团队");
        }
    }

    private void validateMembers(List<Integer> memberIds, Integer currentTeamId) {
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }
        for (Integer userId : memberIds) {
            if (userId == null) {
                continue;
            }
            List<Integer> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
            if (roleIds == null || !roleIds.contains(2)) {
                throw new ServiceException("团队成员角色不正确");
            }
            Integer existTeamId = teamMemberMapper.selectTeamIdByUserId(userId);
            if (existTeamId != null && (currentTeamId == null || !existTeamId.equals(currentTeamId))) {
                throw new ServiceException("团队成员已在其他团队中");
            }
        }
    }
}