package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.entity.Transaction;
import com.guang.resms.entity.dto.TransactionPageDTO;
import com.guang.resms.entity.dto.TransactionUpdateDTO;
import com.guang.resms.entity.vo.TransactionVO;
import com.guang.resms.mapper.TransactionMapper;
import com.guang.resms.service.TransactionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    @Autowired
    private com.guang.resms.service.NotificationService notificationService;

    @Override
    public IPage<TransactionVO> getTransactionPage(TransactionPageDTO dto) {
        Page<TransactionVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return baseMapper.selectPageVO(page, dto);
    }

    @Override
    public TransactionVO getTransactionDetail(Integer id) {
        return baseMapper.selectDetailVO(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTransaction(com.guang.resms.entity.dto.TransactionAddDTO dto) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(dto, transaction);

        // 生成交易编号 JY + 时间戳 + 3位随机数
        String timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(java.time.LocalDateTime.now());
        String random = String.format("%03d", new java.util.Random().nextInt(1000));
        transaction.setTransactionNo("JY" + timestamp + random);

        // 初始化状态
        transaction.setStatus(0); // 待付定金
        transaction.setManagerAudit(0); // 待审核
        transaction.setLoanStatus(0); // 未申请

        boolean result = this.save(transaction);

        if (result) {
            // 发送新交易通知 (使用notifyTransactionStatusChanged或新建通知方法)
            notificationService.notifyTransactionStatusChanged(
                    transaction.getId(),
                    transaction.getTransactionNo(),
                    transaction.getSalesId(),
                    "新建交易");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransaction(TransactionUpdateDTO dto) {
        Transaction original = this.getById(dto.getId());
        if (original == null) {
            throw new RuntimeException("交易不存在");
        }

        // ========== 自动状态流转逻辑 ==========
        Integer autoStatus = null; // 自动计算的状态
        boolean needAuditReset = false; // 是否需要重置审核状态

        // 获取原始值和新值
        java.math.BigDecimal originalDeposit = original.getDeposit();
        java.math.BigDecimal newDeposit = dto.getDeposit();
        java.math.BigDecimal originalDownPayment = original.getDownPayment();
        java.math.BigDecimal newDownPayment = dto.getDownPayment();
        java.math.BigDecimal originalLoanAmount = original.getLoanAmount();
        java.math.BigDecimal newLoanAmount = dto.getLoanAmount();
        Integer originalStatus = original.getStatus();
        Integer originalLoanStatus = original.getLoanStatus();
        Integer newLoanStatusInput = dto.getLoanStatus();

        // ========== 规则1：输入定金金额 → 已付定金 ==========
        // 条件：原来没有定金(null或0)，现在有定金(>0)，且当前状态为"待付定金"(0)
        if (isPositive(newDeposit) && !isPositive(originalDeposit) && originalStatus == 0) {
            autoStatus = 1; // 已付定金
            needAuditReset = true;
            // 自动设置定金时间
            if (dto.getDepositTime() == null) {
                dto.setDepositTime(java.time.LocalDateTime.now());
            }
        }

        // ========== 规则2：输入首付金额 → 已付首付 ==========
        // 条件：原来没有首付(null或0)，现在有首付(>0)，且当前状态为"已付定金"(1)
        if (isPositive(newDownPayment) && !isPositive(originalDownPayment) && originalStatus == 1) {
            autoStatus = 2; // 已付首付
            needAuditReset = true;
            // 自动设置首付时间
            if (dto.getDownPaymentTime() == null) {
                dto.setDownPaymentTime(java.time.LocalDateTime.now());
            }
        }

        // ========== 规则3：贷款状态变为"已放款" → 已过户 ==========
        // 条件：贷款状态从非"已放款"变为"已放款"(2)，且当前状态为"已付首付"(2)
        if (newLoanStatusInput != null && newLoanStatusInput == 2 && originalLoanStatus != 2 && originalStatus == 2) {
            autoStatus = 3; // 已过户
            needAuditReset = true;
            // 自动设置过户时间
            dto.setTransferTime(java.time.LocalDateTime.now());
        }

        // ========== 规则4：输入贷款金额+贷款已放款 → 已过户（一步到位场景）==========
        // 条件：同时输入贷款金额和设置贷款状态为已放款
        if (isPositive(newLoanAmount) && !isPositive(originalLoanAmount)
                && newLoanStatusInput != null && newLoanStatusInput == 2
                && originalStatus == 2) {
            autoStatus = 3; // 已过户
            needAuditReset = true;
            dto.setTransferTime(java.time.LocalDateTime.now());
        }

        // ========== 规则5：手动标记为"已完成"时校验 ==========
        // 如果用户主动将状态改为"已完成"(4)，自动跳过后续检查
        // （已在validateStatusConsistency中处理）

        // 应用自动状态变更
        if (autoStatus != null) {
            dto.setStatus(autoStatus);
        }
        // 如果需要重置审核状态
        if (needAuditReset) {
            dto.setManagerAudit(0); // 待审核
        }
        // ========== 状态流转逻辑结束 ==========

        // 获取最终的状态值（如果未提供则使用原值）
        Integer newStatus = dto.getStatus() != null ? dto.getStatus() : original.getStatus();
        Integer newLoanStatus = dto.getLoanStatus() != null ? dto.getLoanStatus() : original.getLoanStatus();

        // 校验贷款状态与交易状态的一致性
        validateStatusConsistency(newStatus, newLoanStatus);

        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(dto, transaction);

        boolean result = this.updateById(transaction);

        // 检查状态是否变更并发送通知
        Integer finalStatus = dto.getStatus();
        if (result && finalStatus != null && !finalStatus.equals(original.getStatus())) {
            notificationService.notifyTransactionStatusChanged(
                    original.getId(),
                    original.getTransactionNo(),
                    original.getSalesId(),
                    getStatusText(finalStatus));
        }

        return result;
    }

    /**
     * 判断金额是否为正数
     */
    private boolean isPositive(java.math.BigDecimal value) {
        return value != null && value.compareTo(java.math.BigDecimal.ZERO) > 0;
    }

    /**
     * 校验贷款状态与交易状态的一致性
     * 
     * @param status     交易状态：0=待付定金，1=已付定金，2=已付首付，3=已过户，4=已完成，5=已取消
     * @param loanStatus 贷款状态：0=未申请，1=审核中，2=已放款，3=未通过
     */
    private void validateStatusConsistency(Integer status, Integer loanStatus) {
        if (status == null || loanStatus == null) {
            return; // 如果状态为空，跳过校验
        }

        // 规则1：贷款审核中(1)时，交易状态应为：已付定金(1)、已付首付(2)、已过户(3)
        if (loanStatus == 1) {
            if (status == 0) {
                throw new RuntimeException("贷款审核中时，交易状态不能为'待付定金'，请先完成定金支付");
            }
            if (status == 4) {
                throw new RuntimeException("贷款审核中时，交易不能标记为'已完成'，请等待贷款审批结果");
            }
            if (status == 5) {
                throw new RuntimeException("贷款审核中时，交易不能标记为'已取消'，请先处理贷款申请");
            }
        }

        // 规则2：贷款已放款(2)时，交易状态应为：已过户(3)或已完成(4)
        if (loanStatus == 2) {
            if (status < 3) {
                throw new RuntimeException("贷款已放款时，交易状态应至少为'已过户'，当前状态不符合业务逻辑");
            }
            if (status == 5) {
                throw new RuntimeException("贷款已放款的交易无法取消");
            }
        }

        // 规则3：贷款未通过(3)时，交易状态应为：已付定金(1)、已付首付(2)、已取消(5)
        if (loanStatus == 3) {
            if (status == 0) {
                throw new RuntimeException("贷款未通过时，交易状态不能为'待付定金'");
            }
            if (status == 3 || status == 4) {
                throw new RuntimeException("贷款未通过的情况下，交易无法'过户'或'完成'，请改为全款或取消交易");
            }
        }

        // 规则4：交易已完成(4)时，贷款状态应为：未申请(0，全款)或已放款(2)
        if (status == 4) {
            if (loanStatus == 1) {
                throw new RuntimeException("交易完成前，贷款必须完成审批流程");
            }
            if (loanStatus == 3) {
                throw new RuntimeException("贷款未通过的交易无法标记为'已完成'");
            }
        }

        // 规则5：交易已取消(5)时，贷款状态不能为审核中(1)或已放款(2)
        if (status == 5) {
            if (loanStatus == 1) {
                throw new RuntimeException("取消交易前，请先处理正在审核中的贷款申请");
            }
            if (loanStatus == 2) {
                throw new RuntimeException("贷款已放款的交易无法取消");
            }
        }
    }

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
}