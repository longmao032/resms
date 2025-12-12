package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.entity.SecondHouseCommunity;
import com.guang.resms.entity.dto.CommunityQueryDTO;
import com.guang.resms.mapper.SecondHouseCommunityMapper;
import com.guang.resms.service.CommunityService;
import com.guang.resms.service.NotificationService;
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
}
