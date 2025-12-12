package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.entity.Commission;
import com.guang.resms.entity.dto.CommissionPageDTO;
import com.guang.resms.entity.vo.CommissionVO;
import com.guang.resms.mapper.CommissionMapper;
import com.guang.resms.service.CommissionService;
import com.guang.resms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class CommissionServiceImpl extends ServiceImpl<CommissionMapper, Commission> implements CommissionService {

    @Autowired
    private NotificationService notificationService;

    @Override
    public IPage<CommissionVO> getCommissionPage(CommissionPageDTO dto) {
        Page<CommissionVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return baseMapper.selectPageVO(page, dto);
    }

    @Override
    @Transactional
    public boolean calculateCommission(Integer id, Integer financeId) {
        Commission commission = this.getById(id);
        if (commission == null)
            return false;

        commission.setStatus(1); // 已核算
        commission.setCalculateTime(LocalDateTime.now());
        commission.setFinanceId(financeId);
        boolean result = this.updateById(commission);

        if (result) {
            notificationService.notifyCommissionCalculated(
                    commission.getId(),
                    commission.getSalesId(),
                    commission.getAmount());
        }
        return result;
    }

    @Override
    @Transactional
    public boolean issueCommission(Integer id, Integer financeId) {
        Commission commission = this.getById(id);
        if (commission == null || commission.getStatus() != 1)
            return false; // 必须先核算

        commission.setStatus(2); // 已发放
        commission.setIssueTime(LocalDateTime.now());
        commission.setFinanceId(financeId); // 记录发放人
        boolean result = this.updateById(commission);

        if (result) {
            notificationService.notifyCommissionIssued(
                    commission.getId(),
                    commission.getSalesId(),
                    commission.getAmount());
        }
        return result;
    }
}