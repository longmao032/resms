package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.entity.SecondHouseInfo;
import com.guang.resms.entity.dto.QueryDTO;
import com.guang.resms.entity.vo.SecondHouseVo;
import com.guang.resms.mapper.SecondHouseInfoMapper;
import com.guang.resms.service.SecondHouseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 二手房信息扩展表Service实现类
 * @author guang
 */
@Service
public class SecondHouseInfoServiceImpl extends ServiceImpl<SecondHouseInfoMapper, SecondHouseInfo> implements SecondHouseInfoService {
    
    @Autowired
    private SecondHouseInfoMapper secondHouseInfoMapper;
    
    @Override
    public IPage<SecondHouseVo> selectSecondHouseVoPage(QueryDTO queryDTO) {
        // 构建分页参数
        Page<SecondHouseVo> page = new Page<>(queryDTO.getPageNum(), 10);

        // 构建查询参数Map
        Map<String, Object> params = new HashMap<>();
        params.put("city", queryDTO.getCity());
        params.put("district", queryDTO.getDistrict());
        params.put("searchKeyword", queryDTO.getKeyword());
        params.put("minPrice", queryDTO.getMinPrice());
        params.put("maxPrice", queryDTO.getMaxPrice());
        params.put("layout", queryDTO.getLayout());
        params.put("minArea", queryDTO.getMinArea());
        params.put("maxArea", queryDTO.getMaxArea());
        params.put("sortField", queryDTO.getSortField());
        params.put("sortOrder", queryDTO.getSortOrder());

        return secondHouseInfoMapper.selectSecondHouseVoPage(page, params);
    }
}