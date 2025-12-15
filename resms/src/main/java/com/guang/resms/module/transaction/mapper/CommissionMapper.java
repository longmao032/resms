package com.guang.resms.module.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.module.transaction.entity.Commission;
import com.guang.resms.module.transaction.entity.dto.CommissionPageDTO;
import com.guang.resms.module.transaction.entity.vo.CommissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommissionMapper extends BaseMapper<Commission> {

    IPage<CommissionVO> selectPageVO(Page<CommissionVO> page, @Param("dto") CommissionPageDTO dto);

    CommissionVO selectByTransactionId(@Param("transactionId") Integer transactionId);
}