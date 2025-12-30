package com.guang.resms.module.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.house.entity.SecondHouseCommunity;
import com.guang.resms.module.house.entity.SecondHouseInfo;
import com.guang.resms.module.house.entity.dto.CommunityQueryDTO;
import com.guang.resms.module.house.mapper.SecondHouseCommunityMapper;
import com.guang.resms.module.house.mapper.SecondHouseInfoMapper;
import com.guang.resms.module.house.service.CommunityService;
import com.guang.resms.module.chat.service.NotificationService;
import com.guang.resms.module.team.mapper.TeamMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 小区Service实现类
 */
@Service
public class CommunityServiceImpl extends ServiceImpl<SecondHouseCommunityMapper, SecondHouseCommunity>
        implements CommunityService {

    @Autowired
    private SecondHouseCommunityMapper communityMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Override
    public Map<String, Object> getCommunityPage(CommunityQueryDTO queryDTO) {
        // 参数验证
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }

        // 构建查询条件
        LambdaQueryWrapper<SecondHouseCommunity> queryWrapper = new LambdaQueryWrapper<>();

        // ========== 数据权限过滤 ==========
        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();

        if (roleType != null) {
            if (roleType == 2) {
                // 销售顾问: 看所属团队经理创建的小区
                Integer managerId = getManagerId(currentUserId);
                if (managerId != null) {
                    queryWrapper.eq(SecondHouseCommunity::getCreatorId, managerId);
                } else {
                    // 无团队,看不到任何小区
                    queryWrapper.eq(SecondHouseCommunity::getId, -1);
                }
            } else if (roleType == 3) {
                // 销售经理: 看自己创建的
                queryWrapper.eq(SecondHouseCommunity::getCreatorId, currentUserId);
            }
            // roleType == 1 (管理员) 或 roleType == 4 (财务): 看全部
        }

        // 小区名称模糊查询
        if (StringUtils.isNotBlank(queryDTO.getCommunityName())) {
            queryWrapper.like(SecondHouseCommunity::getCommunityName, queryDTO.getCommunityName());
        }

        // 城市
        if (StringUtils.isNotBlank(queryDTO.getCity())) {
            queryWrapper.eq(SecondHouseCommunity::getCity, queryDTO.getCity());
        }

        // 区县
        if (StringUtils.isNotBlank(queryDTO.getDistrict())) {
            queryWrapper.eq(SecondHouseCommunity::getDistrict, queryDTO.getDistrict());
        }

        // 开发商
        if (StringUtils.isNotBlank(queryDTO.getDeveloper())) {
            queryWrapper.like(SecondHouseCommunity::getDeveloper, queryDTO.getDeveloper());
        }

        // 建成年代范围
        if (queryDTO.getMinBuildYear() != null) {
            queryWrapper.ge(SecondHouseCommunity::getBuildYear, queryDTO.getMinBuildYear());
        }
        if (queryDTO.getMaxBuildYear() != null) {
            queryWrapper.le(SecondHouseCommunity::getBuildYear, queryDTO.getMaxBuildYear());
        }

        // 排序
        if (StringUtils.isNotBlank(queryDTO.getSortField())) {
            boolean isAsc = "ASC".equalsIgnoreCase(queryDTO.getSortOrder());
            switch (queryDTO.getSortField()) {
                case "buildYear":
                    queryWrapper.orderBy(true, isAsc, SecondHouseCommunity::getBuildYear);
                    break;
                case "createTime":
                    queryWrapper.orderBy(true, isAsc, SecondHouseCommunity::getCreateTime);
                    break;
                default:
                    queryWrapper.orderByDesc(SecondHouseCommunity::getCreateTime);
            }
        } else {
            // 默认按创建时间降序
            queryWrapper.orderByDesc(SecondHouseCommunity::getCreateTime);
        }

        // 分页查询
        Page<SecondHouseCommunity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<SecondHouseCommunity> result = communityMapper.selectPage(page, queryWrapper);

        // 构建返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", result.getTotal());
        resultMap.put("list", result.getRecords());
        resultMap.put("pageNum", result.getCurrent());
        resultMap.put("pageSize", result.getSize());
        resultMap.put("pages", result.getPages());

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SecondHouseCommunity addCommunity(SecondHouseCommunity community) {
        // 获取当前用户ID作为创建人
        Integer creatorId = SecurityUtils.getUserId();
        community.setCreatorId(creatorId);
        community.setStatus(1); // 默认待审核

        // 保存小区
        communityMapper.insert(community);

        // 发送自动通知给管理员
        notificationService.notifyCommunityCreated(
                community.getId(),
                community.getCommunityName(),
                creatorId);

        return community;
    }

    @Override
    public SecondHouseCommunity updateCommunity(SecondHouseCommunity community) {
        // 验证小区ID是否存在
        if (community.getId() == null) {
            throw new RuntimeException("小区ID不能为空");
        }

        SecondHouseCommunity existingCommunity = communityMapper.selectById(community.getId());
        if (existingCommunity == null) {
            throw new RuntimeException("小区不存在");
        }

        // 更新小区
        communityMapper.updateById(community);
        return communityMapper.selectById(community.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditCommunity(Integer id, Integer status, String reason) {
        SecondHouseCommunity community = communityMapper.selectById(id);
        if (community == null) {
            throw new ServiceException("小区不存在");
        }

        if (community.getStatus() != 1) {
            throw new ServiceException("该小区不是待审核状态");
        }

        // 更新状态
        community.setStatus(status);
        communityMapper.updateById(community);

        // 发送审核结果通知给创建人
        boolean approved = (status == 0);
        notificationService.notifyCommunityAudited(
                id,
                community.getCommunityName(),
                community.getCreatorId(),
                approved,
                reason);
    }

    @Autowired
    private SecondHouseInfoMapper secondHouseInfoMapper;

    @Autowired
    private com.guang.resms.module.user.mapper.UserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        // 检查小区下是否有关联的二手房信息
        LambdaQueryWrapper<SecondHouseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecondHouseInfo::getCommunityId, id);
        Long count = secondHouseInfoMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new ServiceException("该小区下包含有房源，请检查后在删除");
        }

        return super.removeById(id);
    }

    /**
     * 获取销售顾问所属团队的经理ID
     */
    private Integer getManagerId(Integer userId) {
        return teamMemberMapper.selectManagerIdByUserId(userId);
    }

    @Override
    public java.util.List<SecondHouseCommunity> getAllCommunities() {
        // 获取当前用户信息
        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();
        Integer teamId = SecurityUtils.getTeamId();

        // 构建查询条件:只查询审核通过(status=0)的小区
        LambdaQueryWrapper<SecondHouseCommunity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecondHouseCommunity::getStatus, 0);

        // 权限过滤:只能看到本团队成员和管理员创建的小区
        java.util.List<Integer> allowedCreatorIds = new java.util.ArrayList<>();

        // 1. 添加所有管理员ID
        java.util.List<Integer> adminIds = userRoleMapper.selectAdminUserIds();
        if (adminIds != null && !adminIds.isEmpty()) {
            allowedCreatorIds.addAll(adminIds);
        }

        // 2. 添加团队成员ID
        if (teamId != null) {
            // 如果用户有团队,查询团队所有成员ID
            if (roleType != null && roleType == 3) {
                // 销售经理:查询自己团队的所有成员ID
                java.util.List<Integer> teamMemberIds = teamMemberMapper.selectUserIdsByManagerId(currentUserId);
                if (teamMemberIds != null && !teamMemberIds.isEmpty()) {
                    allowedCreatorIds.addAll(teamMemberIds);
                }
            } else if (roleType != null && roleType == 2) {
                // 销售顾问:查询经理ID,然后查询团队所有成员ID
                Integer managerId = teamMemberMapper.selectManagerIdByUserId(currentUserId);
                if (managerId != null) {
                    java.util.List<Integer> teamMemberIds = teamMemberMapper.selectUserIdsByManagerId(managerId);
                    if (teamMemberIds != null && !teamMemberIds.isEmpty()) {
                        allowedCreatorIds.addAll(teamMemberIds);
                    }
                }
            }
        }

        // 应用权限过滤
        if (!allowedCreatorIds.isEmpty()) {
            queryWrapper.in(SecondHouseCommunity::getCreatorId, allowedCreatorIds);
        } else {
            // 如果没有任何允许的创建者ID,返回空列表
            queryWrapper.eq(SecondHouseCommunity::getId, -1);
        }

        queryWrapper.orderBy(true, true, SecondHouseCommunity::getId);
        return communityMapper.selectList(queryWrapper);
    }
}
