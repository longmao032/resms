package com.guang.resms.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.entity.Payment;
import com.guang.resms.entity.dto.PaymentDTO;
import com.guang.resms.entity.dto.PaymentQueryDTO;
import com.guang.resms.entity.vo.PaymentStatisticsVO;
import com.guang.resms.entity.vo.PaymentVO;
import com.guang.resms.mapper.PaymentMapper;
import com.guang.resms.service.PaymentService;
import com.guang.resms.common.exception.ServiceException;
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

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.guang.resms.service.NotificationService notificationService;

    @org.springframework.beans.factory.annotation.Autowired
    private com.guang.resms.mapper.TransactionMapper transactionMapper;

    @Override
    public IPage<PaymentVO> getPaymentPage(PaymentQueryDTO dto) {
        return this.baseMapper.selectPaymentPage(new Page<>(dto.getPageNum(), dto.getPageSize()), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPayment(PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentDTO, payment);

        // 1. 生成收据编号: REC + yyyyMMddHHmmss + 6位随机数（确保唯一性）
        String timestamp = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String random = String.format("%06d", new java.util.Random().nextInt(1000000));
        String receiptNo = "REC" + timestamp + random;
        payment.setReceiptNo(receiptNo);

        // 2. 默认状态为待确认 (0)
        payment.setPaymentStatus(0);

        // 3. 设置当前经办财务 (模拟获取当前登录用户ID)
        // Integer currentUserId = SecurityUtils.getUserId();
        payment.setFinanceId(1); // 示例写死

        if (payment.getPaymentTime() == null) {
            payment.setPaymentTime(LocalDateTime.now());
        }

        return this.save(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmPayment(Integer id) {
        Payment payment = this.getById(id);
        if (payment == null)
            throw new ServiceException("记录不存在");
        if (payment.getPaymentStatus() != 0)
            throw new ServiceException("仅待确认状态可操作");

        payment.setPaymentStatus(1); // 变更为有效

        // ========== 交易状态流转逻辑 ==========
        com.guang.resms.entity.Transaction transaction = transactionMapper.selectById(payment.getTransactionId());
        if (transaction != null) {
            Integer paymentType = payment.getPaymentType();
            Integer currentStatus = transaction.getStatus();
            Integer newStatus = currentStatus; // 默认不变
            boolean needAuditReset = false; // 是否需要重置审核状态

            // 根据收款类型确定目标交易状态
            // paymentType: 1=定金, 2=首付款, 3=尾款, 4=中介费
            // status: 0=待付定金, 1=已付定金, 2=已付首付, 3=已过户, 4=已完成, 5=已取消
            switch (paymentType) {
                case 1: // 定金
                    if (currentStatus == 0) { // 仅当前状态为"待付定金"时才流转
                        newStatus = 1; // → 已付定金
                        needAuditReset = true;
                        // 同步更新定金支付时间
                        transaction.setDepositTime(payment.getPaymentTime());
                    }
                    break;
                case 2: // 首付款
                    if (currentStatus == 1) { // 仅当前状态为"已付定金"时才流转
                        newStatus = 2; // → 已付首付
                        needAuditReset = true;
                        // 同步更新首付支付时间
                        transaction.setDownPaymentTime(payment.getPaymentTime());
                    }
                    break;
                case 3: // 尾款 - 尾款确认可能表示交易接近完成
                    // 尾款确认不自动改变状态，因为可能需要等待过户
                    break;
                case 4: // 中介费
                    // 中介费不影响交易状态
                    break;
            }

            // 如果状态需要变更
            if (!newStatus.equals(currentStatus)) {
                transaction.setStatus(newStatus);

                // 状态变更后，重置审核状态为"待审核"
                if (needAuditReset) {
                    transaction.setManagerAudit(0); // 0=待审核
                }

                transactionMapper.updateById(transaction);

                // 发送状态变更通知
                String statusText = getStatusText(newStatus);
                notificationService.notifyTransactionStatusChanged(
                        transaction.getId(),
                        transaction.getTransactionNo(),
                        transaction.getSalesId(),
                        statusText);
            }
        }
        // ========== 状态流转逻辑结束 ==========

        boolean result = this.updateById(payment);

        // 发送确认收款通知
        if (result && transaction != null) {
            notificationService.notifyPaymentConfirmed(
                    id,
                    payment.getTransactionId(),
                    transaction.getSalesId(),
                    payment.getAmount());
        }

        return result;
    }

    /**
     * 获取交易状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null)
            return "未知";
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
    public boolean voidPayment(Integer id, String reason) {
        Payment payment = this.getById(id);
        if (payment == null)
            throw new ServiceException("记录不存在");

        // 记录作废原因 (建议追加到备注或单独日志表，此处追加备注)
        String newRemark = (payment.getRemark() == null ? "" : payment.getRemark()) + " [作废原因: " + reason + "]";
        payment.setRemark(newRemark);
        payment.setPaymentStatus(2); // 作废

        boolean result = this.updateById(payment);

        // 发送作废收款通知
        if (result) {
            com.guang.resms.entity.Transaction tx = transactionMapper.selectById(payment.getTransactionId());
            if (tx != null) {
                notificationService.notifyPaymentVoided(
                        id,
                        payment.getTransactionId(),
                        tx.getSalesId(),
                        reason);
            }
        }

        return result;
    }

    // PaymentServiceImpl.java

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePayment(PaymentDTO paymentDTO) {
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