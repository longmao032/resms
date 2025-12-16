package com.guang.resms.module.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.customer.mapper.CustomerMapper;
import com.guang.resms.module.house.entity.House;
import com.guang.resms.module.house.mapper.HouseMapper;
import com.guang.resms.module.transaction.entity.Transaction;
import com.guang.resms.module.transaction.entity.dto.TransactionPageDTO;
import com.guang.resms.module.transaction.entity.dto.TransactionUpdateDTO;
import com.guang.resms.module.transaction.entity.vo.TransactionVO;
import com.guang.resms.module.transaction.mapper.TransactionMapper;
import com.guang.resms.module.transaction.service.TransactionService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author guang
 * @since 2020-08-06
 */
@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    @Autowired
    private com.guang.resms.module.chat.service.NotificationService notificationService;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public IPage<TransactionVO> getTransactionPage(TransactionPageDTO dto) {
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            Integer userId = SecurityUtils.getUserId();
            if (userId == null) {
                throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
            }
            dto.setSalesId(userId);
        }
        Page<TransactionVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return baseMapper.selectPageVO(page, dto);
    }

    @Override
    public TransactionVO getTransactionDetail(Integer id) {
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            Integer userId = SecurityUtils.getUserId();
            if (userId == null) {
                throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
            }
            Transaction tx = this.getById(id);
            if (tx == null) {
                return null;
            }
            if (tx.getSalesId() == null || !tx.getSalesId().equals(userId)) {
                throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
            }
        }
        return baseMapper.selectDetailVO(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTransaction(com.guang.resms.module.transaction.entity.dto.TransactionAddDTO dto) {
        if (dto == null) {
            throw new ServiceException("请求参数不能为空");
        }
        if (dto.getHouseId() == null) {
            throw new ServiceException("房源ID不能为空");
        }
        if (dto.getCustomerId() == null) {
            throw new ServiceException("客户ID不能为空");
        }
        if (dto.getSalesId() == null) {
            throw new ServiceException("销售ID不能为空");
        }

        // 校验房源存在且可交易
        House house = houseMapper.selectByIdForUpdate(dto.getHouseId());
        if (house == null) {
            throw new ServiceException("房源不存在");
        }
        // 状态：0=待审核，1=在售，2=已预订，3=已成交，4=下架
        if (house.getStatus() == null || house.getStatus() != 1) {
            throw new ServiceException("当前房源状态不允许创建交易");
        }

        // 校验客户存在
        if (customerMapper.selectById(dto.getCustomerId()) == null) {
            throw new ServiceException("客户不存在");
        }

        // 防止同一房源存在多笔未取消交易
        Long activeCnt = this.count(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getHouseId, dto.getHouseId())
                .ne(Transaction::getStatus, 5));
        if (activeCnt != null && activeCnt > 0) {
            throw new ServiceException("该房源已有进行中的交易，禁止重复创建");
        }

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
        transaction.setFinishAudit(0); // 待完成审核
        transaction.setLoanStatus(0); // 未申请

        boolean result = this.save(transaction);

        if (result) {
            // 发送新交易通知 (使用notifyTransactionStatusChanged或新建通知方法)
            notificationService.notifyTransactionStatusChanged(
                    transaction.getId(),
                    transaction.getTransactionNo(),
                    transaction.getSalesId(),
                    "新建交易");

            // 待付定金阶段 -> 通知销售经理审核
            notificationService.notifyTransactionPendingManagerAudit(
                    transaction.getId(),
                    transaction.getTransactionNo(),
                    transaction.getSalesId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransaction(TransactionUpdateDTO dto) {
        Transaction original = this.getById(dto.getId());
        if (original == null) {
            throw new ServiceException("交易不存在");
        }

        // ========== 角色权限与字段级权限控制 ==========
        Integer roleType = SecurityUtils.getRoleType();

        // 交易已完成且已通过完成审核后，禁止通过普通编辑接口修改（防止已结算数据被篡改）
        if (original.getStatus() != null && original.getStatus() == 4
                && original.getFinishAudit() != null && original.getFinishAudit() == 2) {
            if (hasChanged(dto.getStatus(), original.getStatus())
                    || hasChanged(dto.getManagerAudit(), original.getManagerAudit())
                    || hasChanged(dto.getLoanStatus(), original.getLoanStatus())
                    || hasChanged(dto.getDealPrice(), original.getDealPrice())
                    || hasChanged(dto.getDeposit(), original.getDeposit())
                    || hasChanged(dto.getDownPayment(), original.getDownPayment())
                    || hasChanged(dto.getLoanAmount(), original.getLoanAmount())
                    || hasChanged(dto.getDepositTime(), original.getDepositTime())
                    || hasChanged(dto.getDownPaymentTime(), original.getDownPaymentTime())
                    || hasChanged(dto.getTransferTime(), original.getTransferTime())) {
                throw new ServiceException("交易已完成且已通过完成审核，禁止修改");
            }
        }

        // 完成必须走管理员审核接口，禁止通过普通编辑接口直接标记完成
        if (roleType != null && roleType == 1
                && dto.getStatus() != null && dto.getStatus() == 4
                && original.getStatus() != null && (original.getStatus() == 2 || original.getStatus() == 3)) {
            throw new ServiceException("请通过完成审核操作完成交易");
        }

        // 记录客户端显式传入的字段（用于判断“手动改状态/审核”）
        Integer inputStatus = dto.getStatus();
        Integer inputManagerAudit = dto.getManagerAudit();
        Integer inputLoanStatus = dto.getLoanStatus();
        java.math.BigDecimal inputDealPrice = dto.getDealPrice();
        java.math.BigDecimal inputDeposit = dto.getDeposit();
        java.math.BigDecimal inputDownPayment = dto.getDownPayment();
        java.math.BigDecimal inputLoanAmount = dto.getLoanAmount();

        // roleType: 1=管理员, 2=销售顾问, 3=销售经理, 4=财务
        if (roleType != null) {
            // 销售顾问/销售经理：仅允许修改成交价
            if (roleType == 3 || roleType == 2) {
                if (hasChanged(inputStatus, original.getStatus())
                        || hasChanged(inputManagerAudit, original.getManagerAudit())
                        || hasChanged(inputLoanStatus, original.getLoanStatus())
                        || hasChanged(inputDeposit, original.getDeposit())
                        || hasChanged(inputDownPayment, original.getDownPayment())
                        || hasChanged(inputLoanAmount, original.getLoanAmount())
                        || hasChanged(dto.getDepositTime(), original.getDepositTime())
                        || hasChanged(dto.getDownPaymentTime(), original.getDownPaymentTime())
                        || hasChanged(dto.getTransferTime(), original.getTransferTime())) {
                    throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
                }

                // 允许成交价修改（不修改也允许提交）
            }

            // 财务：仅允许修改定金/首付/贷款字段，不允许手动改交易状态/经理审核/成交价
            if (roleType == 4) {
                if (hasChanged(inputDealPrice, original.getDealPrice())
                        || hasChanged(inputManagerAudit, original.getManagerAudit())
                        || (inputStatus != null && !inputStatus.equals(original.getStatus()))) {
                    throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
                }

                // 财务允许修改 loanStatus，但不允许“无业务驱动”的跳变由前端强塞 status
                // status 由系统自动推导或由 financeConfirm 接口推进
            }
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
            // 待定金阶段必须经理审核通过后才允许录入定金
            if (original.getManagerAudit() == null || original.getManagerAudit() != 1) {
                throw new ServiceException("待付定金阶段必须经理审核通过后才允许录入定金");
            }
            autoStatus = 1; // 已付定金
            needAuditReset = true;
            // 自动设置定金时间
            dto.setDepositTime(java.time.LocalDateTime.now());
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

        // 进入过户阶段时，重置完成审核状态
        if (dto.getStatus() != null && dto.getStatus() == 3
                && (original.getStatus() == null || original.getStatus() != 3)) {
            transaction.setFinishAudit(0);
        }

        boolean result = this.updateById(transaction);

        // 检查状态是否变更并发送通知
        Integer finalStatus = dto.getStatus();
        if (result && finalStatus != null && !finalStatus.equals(original.getStatus())) {
            // 同步房源状态
            syncHouseStatusOnTransactionStatusChanged(original.getHouseId(), original.getStatus(), finalStatus);
            notificationService.notifyTransactionStatusChanged(
                    original.getId(),
                    original.getTransactionNo(),
                    original.getSalesId(),
                    getStatusText(finalStatus));

            // ========== 任务通知流 ==========
            // 待付定金(0)且待审核 -> 通知销售经理
            if (finalStatus == 0 && (dto.getManagerAudit() != null && dto.getManagerAudit() == 0)) {
                notificationService.notifyTransactionPendingManagerAudit(
                        original.getId(),
                        original.getTransactionNo(),
                        original.getSalesId());
            }
            // 后续阶段 -> 通知财务
            if (finalStatus == 2 || finalStatus == 3) {
                notificationService.notifyTransactionPendingFinanceAudit(
                        original.getId(),
                        original.getTransactionNo(),
                        original.getSalesId(),
                        getStatusText(finalStatus));
            }
        }

        return result;
    }

    private void syncHouseStatusOnTransactionStatusChanged(Integer houseId, Integer oldTxStatus, Integer newTxStatus) {
        if (houseId == null || newTxStatus == null) {
            return;
        }

        House house = houseMapper.selectById(houseId);
        if (house == null) {
            return;
        }

        // 交易状态：0=待付定金，1=已付定金，2=已付首付，3=已过户，4=已完成，5=已取消
        // 房源状态：0=待审核，1=在售，2=已预订，3=已成交，4=下架
        if (newTxStatus == 1 || newTxStatus == 2 || newTxStatus == 3) {
            if (house.getStatus() != null && house.getStatus() == 1) {
                house.setStatus(2);
                houseMapper.updateById(house);
            }
            return;
        }

        if (newTxStatus == 4) {
            if (house.getStatus() != null && (house.getStatus() == 1 || house.getStatus() == 2)) {
                house.setStatus(3);
                houseMapper.updateById(house);
            }
            return;
        }

        if (newTxStatus == 5) {
            if (house.getStatus() != null && house.getStatus() == 2) {
                if (oldTxStatus != null && oldTxStatus >= 3) {
                    throw new ServiceException("交易已进入过户/完成阶段，禁止取消");
                }
                house.setStatus(1);
                houseMapper.updateById(house);
            }
        }
    }

    /**
     * 判断金额是否为正数
     */
    private boolean isPositive(java.math.BigDecimal value) {
        return value != null && value.compareTo(java.math.BigDecimal.ZERO) > 0;
    }

    private boolean hasChanged(Integer input, Integer original) {
        if (input == null) {
            return false;
        }
        if (original == null) {
            return true;
        }
        return !input.equals(original);
    }

    private boolean hasChanged(java.time.LocalDateTime input, java.time.LocalDateTime original) {
        if (input == null) {
            return false;
        }
        if (original == null) {
            return true;
        }
        return !input.equals(original);
    }

    private boolean hasChanged(java.math.BigDecimal input, java.math.BigDecimal original) {
        if (input == null) {
            return false;
        }
        if (original == null) {
            return input.compareTo(java.math.BigDecimal.ZERO) != 0;
        }
        return input.compareTo(original) != 0;
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
                throw new ServiceException("贷款审核中时，交易状态不能为'待付定金'，请先完成定金支付");
            }
            if (status == 4) {
                throw new ServiceException("贷款审核中时，交易不能标记为'已完成'，请等待贷款审批结果");
            }
            if (status == 5) {
                throw new ServiceException("贷款审核中时，交易不能标记为'已取消'，请先处理贷款申请");
            }
        }

        // 规则2：贷款已放款(2)时，交易状态应为：已过户(3)或已完成(4)
        if (loanStatus == 2) {
            if (status < 3) {
                throw new ServiceException("贷款已放款时，交易状态应至少为'已过户'，当前状态不符合业务逻辑");
            }
            if (status == 5) {
                throw new ServiceException("贷款已放款的交易无法取消");
            }
        }

        // 规则3：贷款未通过(3)时，交易状态应为：已付定金(1)、已付首付(2)、已取消(5)
        if (loanStatus == 3) {
            if (status == 0) {
                throw new ServiceException("贷款未通过时，交易状态不能为'待付定金'");
            }
            if (status == 3 || status == 4) {
                throw new ServiceException("贷款未通过的情况下，交易无法'过户'或'完成'，请改为全款或取消交易");
            }
        }

        // 规则4：交易已完成(4)时，贷款状态应为：未申请(0，全款)或已放款(2)
        if (status == 4) {
            if (loanStatus == 1) {
                throw new ServiceException("交易完成前，贷款必须完成审批流程");
            }
            if (loanStatus == 3) {
                throw new ServiceException("贷款未通过的交易无法标记为'已完成'");
            }
        }

        // 规则5：交易已取消(5)时，贷款状态不能为审核中(1)或已放款(2)
        if (status == 5) {
            if (loanStatus == 1) {
                throw new ServiceException("取消交易前，请先处理正在审核中的贷款申请");
            }
            if (loanStatus == 2) {
                throw new ServiceException("贷款已放款的交易无法取消");
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean managerApprove(Integer id, Boolean approved, String reason) {
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new ServiceException("交易不存在");
        }

        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null || (roleType != 1 && roleType != 3)) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }

        if (transaction.getStatus() == null || transaction.getStatus() != 0) {
            throw new ServiceException("只能审批待付定金状态的交易");
        }
        if (transaction.getManagerAudit() == null || transaction.getManagerAudit() != 0) {
            throw new ServiceException("该交易已审批过");
        }

        if (approved != null && approved) {
            transaction.setManagerAudit(1);
        } else {
            transaction.setManagerAudit(2);
        }

        boolean result = this.updateById(transaction);
        if (result) {
            String statusText = (approved != null && approved)
                    ? "经理审批通过" + (reason != null ? ":" + reason : "")
                    : "经理审批驳回" + (reason != null ? ":" + reason : "");

            notificationService.notifyTransactionStatusChanged(
                    transaction.getId(),
                    transaction.getTransactionNo(),
                    transaction.getSalesId(),
                    statusText);

            if (approved != null && approved) {
                notificationService.notifyTransactionPendingFinanceAudit(
                        transaction.getId(),
                        transaction.getTransactionNo(),
                        transaction.getSalesId(),
                        "定金审核通过");
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean financeConfirm(Integer id, Boolean approved, String reason) {
        if (approved == null || !approved) {
            throw new ServiceException("该操作仅支持确认");
        }
        return financeApplyFinish(id, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean financeApplyFinish(Integer id, String reason) {
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new ServiceException("交易不存在");
        }

        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null || (roleType != 1 && roleType != 4)) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }

        Integer status = transaction.getStatus();
        if (status == null || (status != 2 && status != 3)) {
            throw new ServiceException("只有已付首付/已过户状态的交易才能申请完成");
        }

        if (!isTransactionFullyPaid(transaction)) {
            throw new ServiceException("款项未结清，无法申请完成");
        }

        Integer finishAudit = transaction.getFinishAudit();
        if (finishAudit != null && finishAudit == 1) {
            throw new ServiceException("该交易已提交完成申请，请等待管理员审核");
        }
        if (finishAudit != null && finishAudit == 2) {
            throw new ServiceException("该交易已完成，无需再次申请");
        }

        transaction.setFinishAudit(1);
        boolean result = this.updateById(transaction);

        if (result) {
            notificationService.notifyTransactionPendingAdminFinishAudit(
                    transaction.getId(),
                    transaction.getTransactionNo(),
                    transaction.getSalesId());

            notificationService.notifyTransactionStatusChanged(
                    transaction.getId(),
                    transaction.getTransactionNo(),
                    transaction.getSalesId(),
                    "已提交完成申请" + (reason != null ? ":" + reason : ""));
        }

        return result;
    }

    private boolean isTransactionFullyPaid(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        java.math.BigDecimal dealPrice = transaction.getDealPrice();
        if (dealPrice == null) {
            return false;
        }
        java.math.BigDecimal deposit = transaction.getDeposit() == null ? java.math.BigDecimal.ZERO : transaction.getDeposit();
        java.math.BigDecimal downPayment = transaction.getDownPayment() == null ? java.math.BigDecimal.ZERO : transaction.getDownPayment();
        java.math.BigDecimal loanAmount = transaction.getLoanAmount() == null ? java.math.BigDecimal.ZERO : transaction.getLoanAmount();
        java.math.BigDecimal paid = deposit.add(downPayment).add(loanAmount);
        return paid.compareTo(dealPrice) >= 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adminFinishApprove(Integer id, Boolean approved, String reason) {
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new ServiceException("交易不存在");
        }

        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null || roleType != 1) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }

        if (transaction.getStatus() == null || (transaction.getStatus() != 2 && transaction.getStatus() != 3)) {
            throw new ServiceException("只有已付首付/已过户状态的交易才能进行完成审核");
        }
        if (transaction.getFinishAudit() == null || transaction.getFinishAudit() != 1) {
            throw new ServiceException("该交易未处于待完成审核状态");
        }

        Integer oldStatus = transaction.getStatus();

        if (approved != null && approved) {
            transaction.setFinishAudit(2); // 完成审核通过
            transaction.setStatus(4); // 已完成
        } else {
            transaction.setFinishAudit(3); // 完成审核驳回
        }

        boolean result = this.updateById(transaction);

        if (result && approved != null && approved) {
            syncHouseStatusOnTransactionStatusChanged(transaction.getHouseId(), oldStatus, transaction.getStatus());
        }

        if (result) {
            String statusText = (approved != null && approved)
                    ? "交易已完成" + (reason != null ? ":" + reason : "")
                    : "完成审核驳回" + (reason != null ? ":" + reason : "");

            notificationService.notifyTransactionStatusChanged(
                    transaction.getId(),
                    transaction.getTransactionNo(),
                    transaction.getSalesId(),
                    statusText);
        }

        return result;
    }
}