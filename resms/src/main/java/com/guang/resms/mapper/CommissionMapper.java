package com.guang.resms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.Commission;
import com.guang.resms.entity.dto.CommissionPageDTO;
import com.guang.resms.entity.vo.CommissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommissionMapper extends BaseMapper<Commission> {

    IPage<CommissionVO> selectPageVO(Page<CommissionVO> page, @Param("dto") CommissionPageDTO dto);
}