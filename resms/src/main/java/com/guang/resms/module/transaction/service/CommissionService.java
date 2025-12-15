package com.guang.resms.module.transaction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.module.transaction.entity.Commission;
import com.guang.resms.module.transaction.entity.dto.CommissionPageDTO;
import com.guang.resms.module.transaction.entity.vo.CommissionVO;

import java.math.BigDecimal;

public interface CommissionService extends IService<Commission> {
    IPage<CommissionVO> getCommissionPage(CommissionPageDTO dto);
    boolean calculateCommission(Integer id, Integer financeId); // 核算
    boolean issueCommission(Integer id, Integer financeId);     // 发放

    CommissionVO getByTransactionId(Integer transactionId);

    boolean createByTransaction(Integer transactionId, BigDecimal commissionRate);
}