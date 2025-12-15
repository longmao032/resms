package com.guang.resms.module.payment.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.module.payment.entity.Payment;
import com.guang.resms.module.payment.entity.dto.PaymentDTO;
import com.guang.resms.module.payment.entity.dto.PaymentQueryDTO;
import com.guang.resms.module.payment.entity.vo.PaymentStatisticsVO;
import com.guang.resms.module.payment.entity.vo.PaymentVO;
import com.guang.resms.module.payment.mapper.PaymentMapper;
import com.guang.resms.module.payment.service.PaymentService;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.house.entity.House;
import com.guang.resms.module.house.mapper.HouseMapper;
import com.guang.resms.module.transaction.entity.Transaction;
import com.guang.resms.module.transaction.mapper.TransactionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * 
 * 
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.guang.resms.module.chat.service.NotificationService notificationService;

    @org.springframework.beans.factory.annotation.Autowired
    private TransactionMapper transactionMapper;

    @org.springframework.beans.factory.annotation.Autowired
    private HouseMapper houseMapper;

    @Override
    public IPage<PaymentVO> getPaymentPage(PaymentQueryDTO dto) {
        return this.baseMapper.selectPaymentPage(new Page<>(dto.getPageNum(), dto.getPageSize()), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPayment(PaymentDTO paymentDTO) {
        if (!SecurityUtils.isFinance() && !SecurityUtils.isAdmin()) {
            throw new ServiceException("无权限新增收款记录");
        }

        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentDTO, payment);

        if (payment.getTransactionId() == null) {
            throw new ServiceException("关联交易不能为空");
        }
        if (transactionMapper.selectById(payment.getTransactionId()) == null) {
            throw new ServiceException("关联交易不存在");
        }

        // 1. 
        String timestamp = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String random = String.format("%06d", new java.util.Random().nextInt(1000000));
        String receiptNo = "REC" + timestamp + random;
        payment.setReceiptNo(receiptNo);

        // 2. 
        payment.setPaymentStatus(0);

        // 3. 
        Integer currentUserId = SecurityUtils.requireUserId();
        payment.setFinanceId(currentUserId);

        if (payment.getPaymentTime() == null) {
            payment.setPaymentTime(LocalDateTime.now());
        }

        return this.save(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmPayment(Integer id) {
        if (!SecurityUtils.isFinance() && !SecurityUtils.isAdmin()) {
            throw new ServiceException("无权限确认收款");
        }

        Payment payment = this.getById(id);
        if (payment == null) {
            throw new ServiceException("记录不存在");
        }
        if (payment.getPaymentStatus() == null || payment.getPaymentStatus() != 0) {
            throw new ServiceException("仅待确认状态可操作");
        }

        Transaction transaction = transactionMapper.selectById(payment.getTransactionId());
        if (transaction == null) {
            throw new ServiceException("关联交易不存在");
        }

        // 
        if (payment.getFlowType() == null || payment.getFlowType() != 1) {
            throw new ServiceException("当前记录不是收款，无法执行确认");
        }

        Integer currentStatus = transaction.getStatus();
        Integer newStatus = currentStatus;
        boolean needAuditReset = false;

        // paymentType: 1=, 2=, 3=, 4=
        Integer paymentType = payment.getPaymentType();
        if (paymentType == null) {
            throw new ServiceException("款项类型不能为空");
        }

        if (paymentType == 1) {
            if (currentStatus == null || currentStatus != 0) {
                throw new ServiceException("交易状态不允许确认定金");
            }
            newStatus = 1;
            needAuditReset = true;
            transaction.setDepositTime(payment.getPaymentTime());
            if (transaction.getDeposit() == null || transaction.getDeposit().compareTo(BigDecimal.ZERO) <= 0) {
                transaction.setDeposit(payment.getAmount());
            }
        } else if (paymentType == 2) {
            if (currentStatus == null || currentStatus != 1) {
                throw new ServiceException("交易状态不允许确认首付款");
            }
            newStatus = 2;
            needAuditReset = true;
            transaction.setDownPaymentTime(payment.getPaymentTime());
            if (transaction.getDownPayment() == null || transaction.getDownPayment().compareTo(BigDecimal.ZERO) <= 0) {
                transaction.setDownPayment(payment.getAmount());
            }
        } else {
            // 
            newStatus = currentStatus;
        }

        if (needAuditReset) {
            transaction.setManagerAudit(0);
        }
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            transaction.setStatus(newStatus);
        }

        int txUpdated = transactionMapper.updateById(transaction);
        if (txUpdated <= 0) {
            throw new ServiceException("更新交易失败");
        }

        // 
        if (newStatus != null && newStatus == 1) {
            House house = houseMapper.selectById(transaction.getHouseId());
            if (house != null && house.getStatus() != null && house.getStatus() == 1) {
                house.setStatus(2);
                houseMapper.updateById(house);
            }
        }

        payment.setPaymentStatus(1);
        boolean result = this.updateById(payment);
        if (!result) {
            throw new ServiceException("更新收款状态失败");
        }

        // 
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            notificationService.notifyTransactionStatusChanged(
                    transaction.getId(),
                    transaction.getTransactionNo(),
                    transaction.getSalesId(),
                    getStatusText(newStatus));
        }
        notificationService.notifyPaymentConfirmed(
                id,
                payment.getTransactionId(),
                transaction.getSalesId(),
                payment.getAmount());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean voidPayment(Integer id, String reason) {
        if (!SecurityUtils.isFinance() && !SecurityUtils.isAdmin()) {
            throw new ServiceException("无权限作废收款");
        }

        Payment payment = this.getById(id);
        if (payment == null) {
            throw new ServiceException("记录不存在");
        }
        if (payment.getPaymentStatus() != null && payment.getPaymentStatus() == 2) {
            throw new ServiceException("该收款记录已作废");
        }

        Transaction tx = transactionMapper.selectById(payment.getTransactionId());
        if (tx == null) {
            throw new ServiceException("关联交易不存在");
        }

        // 
        if (payment.getPaymentStatus() != null && payment.getPaymentStatus() == 1) {
            Integer paymentType = payment.getPaymentType();
            Integer txStatus = tx.getStatus();

            if (paymentType != null && paymentType == 1) {
                if (txStatus == null || txStatus != 1) {
                    throw new ServiceException("交易已进入更后续阶段，禁止作废定金收款");
                }

                Long otherConfirmedCnt = this.count(new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getTransactionId, payment.getTransactionId())
                        .eq(Payment::getPaymentType, 1)
                        .eq(Payment::getPaymentStatus, 1)
                        .ne(Payment::getId, payment.getId()));
                if (otherConfirmedCnt == null || otherConfirmedCnt == 0) {
                    tx.setStatus(0);
                    tx.setDepositTime(null);
                    tx.setDeposit(null);
                    tx.setManagerAudit(0);
                    transactionMapper.updateById(tx);

                    House house = houseMapper.selectById(tx.getHouseId());
                    if (house != null && house.getStatus() != null && house.getStatus() == 2) {
                        house.setStatus(1);
                        houseMapper.updateById(house);
                    }
                }
            }

            if (paymentType != null && paymentType == 2) {
                if (txStatus == null || txStatus != 2) {
                    throw new ServiceException("交易已进入更后续阶段，禁止作废首付款收款");
                }

                Long otherConfirmedCnt = this.count(new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getTransactionId, payment.getTransactionId())
                        .eq(Payment::getPaymentType, 2)
                        .eq(Payment::getPaymentStatus, 1)
                        .ne(Payment::getId, payment.getId()));
                if (otherConfirmedCnt == null || otherConfirmedCnt == 0) {
                    tx.setStatus(1);
                    tx.setDownPaymentTime(null);
                    tx.setDownPayment(null);
                    tx.setManagerAudit(0);
                    transactionMapper.updateById(tx);
                }
            }
        }

        String newRemark = (payment.getRemark() == null ? "" : payment.getRemark()) + " [作废原因: " + reason + "]";
        payment.setRemark(newRemark);
        payment.setPaymentStatus(2);

        boolean result = this.updateById(payment);
        if (!result) {
            throw new ServiceException("作废失败");
        }

        notificationService.notifyPaymentVoided(
                id,
                payment.getTransactionId(),
                tx.getSalesId(),
                reason);

        return true;
    }

    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待付定金";
            case 1:
                return "已付定金";
            case 2:
                return "已付首付";
            case 3:
                return "已过户";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            default:
                return "未知";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePayment(PaymentDTO paymentDTO) {
        if (!SecurityUtils.isFinance() && !SecurityUtils.isAdmin()) {
            throw new ServiceException("无权限修改收款记录");
        }

        if (paymentDTO.getId() == null) {
            throw new ServiceException("ID不能为空");
        }

        Payment oldPayment = this.getById(paymentDTO.getId());
        if (oldPayment == null) {
            throw new ServiceException("记录不存在");
        }

        // 关键安全校验：只有【待确认】状态允许编辑
        if (oldPayment.getPaymentStatus() != 0) {
            throw new ServiceException("只有【待确认】状态的收款记录可以修改，已确认或作废记录禁止编辑！");
        }

        // 复制属性，但在修改时通常不允许修改 关联交易、收据号 等核心归属信息
        // 这里根据业务需求，允许修改金额、方式、时间、备注等
        Payment updateEntity = new Payment();
        BeanUtils.copyProperties(paymentDTO, updateEntity);

        // 保护核心字段不被修改
        updateEntity.setReceiptNo(null); // 收据号不可改
        updateEntity.setTransactionId(null); // 建议关联交易也不可改，如果错了建议作废重录
        updateEntity.setPaymentStatus(null); // 状态流转走专门接口

        return this.updateById(updateEntity);
    }

    @Override
    public PaymentStatisticsVO getStatistics() {
        PaymentStatisticsVO vo = new PaymentStatisticsVO();

        // 1. 获取总览数据
        List<Map<String, Object>> overviewList = baseMapper.selectOverviewStats();
        if (!overviewList.isEmpty()) {
            Map<String, Object> map = overviewList.get(0);
            vo.setTotalAmount((BigDecimal) map.getOrDefault("total_amount", BigDecimal.ZERO));
            vo.setTodayAmount((BigDecimal) map.getOrDefault("today_amount", BigDecimal.ZERO));
            vo.setMonthAmount((BigDecimal) map.getOrDefault("month_amount", BigDecimal.ZERO));
            vo.setPendingAmount((BigDecimal) map.getOrDefault("pending_amount", BigDecimal.ZERO));
        }

        // 2. 处理趋势图数据
        List<Map<String, Object>> trendList = baseMapper.selectTrendStats();
        List<String> dates = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        trendList.forEach(m -> {
            dates.add((String) m.get("month"));
            amounts.add((BigDecimal) m.get("amount"));
        });
        vo.setTrendDates(dates);
        vo.setTrendAmounts(amounts);

        // 3. 类型占比
        vo.setTypePieData(baseMapper.selectTypeStats());

        // 4. 销售排行
        List<Map<String, Object>> rankingList = baseMapper.selectSalesRanking();
        List<PaymentStatisticsVO.SalesRanking> rankings = rankingList.stream().map(m -> {
            PaymentStatisticsVO.SalesRanking r = new PaymentStatisticsVO.SalesRanking();
            r.setSalesName((String) m.get("sales_name"));
            r.setTotalAmount((BigDecimal) m.get("total_amount"));
            return r;
        }).collect(Collectors.toList());
        vo.setSalesRanking(rankings);

        return vo;
    }
}