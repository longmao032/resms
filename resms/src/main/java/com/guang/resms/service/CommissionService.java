package com.guang.resms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.entity.Commission;
import com.guang.resms.entity.dto.CommissionPageDTO;
import com.guang.resms.entity.vo.CommissionVO;

public interface CommissionService extends IService<Commission> {
    IPage<CommissionVO> getCommissionPage(CommissionPageDTO dto);
    boolean calculateCommission(Integer id, Integer financeId); // 核算
    boolean issueCommission(Integer id, Integer financeId);     // 发放
}