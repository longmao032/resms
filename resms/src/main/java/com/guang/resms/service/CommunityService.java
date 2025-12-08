package com.guang.resms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.entity.SecondHouseCommunity;
import com.guang.resms.entity.dto.CommunityQueryDTO;

import java.util.Map;

/**
 * 小区Service接口
 */
public interface CommunityService extends IService<SecondHouseCommunity> {
    
    /**
     * 分页查询小区列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Map<String, Object> getCommunityPage(CommunityQueryDTO queryDTO);

    /**
     * 新增小区
     * @param community 小区信息
     * @return 保存后的小区
     */
    SecondHouseCommunity addCommunity(SecondHouseCommunity community);

    /**
     * 更新小区
     * @param community 小区信息
     * @return 更新后的小区
     */
    SecondHouseCommunity updateCommunity(SecondHouseCommunity community);
}
