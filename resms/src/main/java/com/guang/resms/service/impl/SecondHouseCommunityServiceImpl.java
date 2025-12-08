package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.entity.SecondHouseCommunity;
import com.guang.resms.mapper.SecondHouseCommunityMapper;
import com.guang.resms.service.SecondHouseCommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 二手房小区信息表Service实现类
 * @author guang
 */
@Service
public class SecondHouseCommunityServiceImpl extends ServiceImpl<SecondHouseCommunityMapper, SecondHouseCommunity> implements SecondHouseCommunityService {
    
    @Autowired
    private SecondHouseCommunityMapper secondHouseCommunityMapper;

}