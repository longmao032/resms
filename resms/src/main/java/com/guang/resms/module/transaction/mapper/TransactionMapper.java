package com.guang.resms.module.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.module.transaction.entity.Transaction;
import com.guang.resms.module.transaction.entity.dto.TransactionPageDTO;
import com.guang.resms.module.transaction.entity.vo.TransactionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {

        IPage<TransactionVO> selectPageVO(Page<TransactionVO> page, @Param("dto") TransactionPageDTO dto);

        // 新增：根据ID查询详情 VO
        TransactionVO selectDetailVO(@Param("id") Integer id);
}