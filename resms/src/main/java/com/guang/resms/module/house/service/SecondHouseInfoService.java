package com.guang.resms.module.house.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.module.house.entity.SecondHouseInfo;
import com.guang.resms.module.user.entity.dto.QueryDTO;
import com.guang.resms.module.house.entity.vo.SecondHouseVo;

import java.util.Map;

/**
 * 二手房信息扩展表Service
 * @author guang
 */
public interface SecondHouseInfoService extends IService<SecondHouseInfo> {
    

    
    /**
     * 综合查询二手房信息（返回Vo）
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<SecondHouseVo> selectSecondHouseVoPage(QueryDTO queryDTO);
}