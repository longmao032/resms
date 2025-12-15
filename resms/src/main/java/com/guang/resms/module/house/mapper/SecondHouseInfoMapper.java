package com.guang.resms.module.house.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.module.house.entity.SecondHouseInfo;
import com.guang.resms.module.house.entity.vo.SecondHouseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 二手房信息扩展表Mapper
 * @author guang
 */

@Repository
public interface SecondHouseInfoMapper extends BaseMapper<SecondHouseInfo> {
    

    
    /**
     * 综合查询二手房信息
     * @param page 分页参数
     * @return 分页结果
     */
    IPage<SecondHouseVo> selectSecondHouseVoPage(Page<SecondHouseVo> page,
                                                 @Param("params") Map<String, Object> params);
}