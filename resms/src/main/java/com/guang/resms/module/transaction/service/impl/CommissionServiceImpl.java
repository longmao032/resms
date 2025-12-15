package com.guang.resms.module.transaction.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.transaction.entity.Commission;
import com.guang.resms.module.transaction.entity.dto.CommissionPageDTO;
import com.guang.resms.module.transaction.entity.vo.CommissionVO;
import com.guang.resms.module.transaction.mapper.CommissionMapper;
import com.guang.resms.module.transaction.mapper.TransactionMapper;
import com.guang.resms.module.transaction.entity.Transaction;
import com.guang.resms.module.transaction.service.CommissionService;
import com.guang.resms.module.chat.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CommissionServiceImpl extends ServiceImpl<CommissionMapper, Commission> implements CommissionService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public IPage<CommissionVO> getCommissionPage(CommissionPageDTO dto) {
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            Integer userId = SecurityUtils.getUserId();
            if (userId == null) {
                throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
            }
            dto.setSalesId(userId);
        }
        Page<CommissionVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return baseMapper.selectPageVO(page, dto);
    }

    @Override
    public CommissionVO getByTransactionId(Integer transactionId) {
        if (transactionId == null) {
            return null;
        }
        Commission commission = this.getOne(new LambdaQueryWrapper<Commission>()
                .eq(Commission::getTransactionId, transactionId)
                .orderByDesc(Commission::getCreateTime)
                .last("LIMIT 1"));
        if (commission == null) {
            return null;
        }

        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            Integer userId = SecurityUtils.getUserId();
            if (userId == null) {
                throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
            }
            if (commission.getSalesId() == null || !commission.getSalesId().equals(userId)) {
                throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
            }
        }

        return baseMapper.selectByTransactionId(transactionId);
    }

    @Override
    @Transactional
    public boolean createByTransaction(Integer transactionId, BigDecimal commissionRate) {
        if (transactionId == null) {
            throw new ServiceException("交易ID不能为空");
        }
        if (commissionRate == null || commissionRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("提成比例必须大于0");
        }

        Transaction tx = transactionMapper.selectById(transactionId);
        if (tx == null) {
            throw new ServiceException("交易不存在");
        }
        if (tx.getStatus() == null || tx.getStatus() != 4) {
            throw new ServiceException("只有已完成的交易才能生成佣金");
        }

        Integer finishAudit = tx.getFinishAudit();
        if (finishAudit == null || finishAudit != 2) {
            throw new ServiceException("该交易未通过完成审核，无法生成佣金");
        }

        Long exist = this.count(new LambdaQueryWrapper<Commission>()
                .eq(Commission::getTransactionId, transactionId));
        if (exist != null && exist > 0) {
            throw new ServiceException("该交易已生成佣金记录");
        }

        if (tx.getDealPrice() == null) {
            throw new ServiceException("成交价为空，无法生成佣金");
        }
        if (tx.getSalesId() == null) {
            throw new ServiceException("销售为空，无法生成佣金");
        }

        BigDecimal amount = tx.getDealPrice()
                .multiply(commissionRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        Commission commission = new Commission();
        commission.setTransactionId(transactionId);
        commission.setSalesId(tx.getSalesId());
        commission.setCommissionRate(commissionRate);
        commission.setAmount(amount);
        commission.setStatus(0);

        return this.save(commission);
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