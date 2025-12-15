package com.guang.resms.module.transaction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.module.transaction.entity.Transaction;
import com.guang.resms.module.transaction.entity.dto.TransactionPageDTO;
import com.guang.resms.module.transaction.entity.dto.TransactionUpdateDTO;
import com.guang.resms.module.transaction.entity.vo.TransactionVO;

public interface TransactionService extends IService<Transaction> {
    IPage<TransactionVO> getTransactionPage(TransactionPageDTO dto);

    // 新增接口定义
    TransactionVO getTransactionDetail(Integer id);

    boolean updateTransaction(TransactionUpdateDTO dto);

    boolean addTransaction(com.guang.resms.module.transaction.entity.dto.TransactionAddDTO dto);

    /**
     * 经理审批交易(定金阶段)
     * 
     * @param id       交易ID
     * @param approved 是否通过
     * @param reason   审批原因
     * @return 是否成功
     */
    boolean managerApprove(Integer id, Boolean approved, String reason);

    /**
     * 财务确认交易完成(尾款阶段)
     * 
     * @param id       交易ID
     * @param approved 是否确认
     * @param reason   确认备注
     * @return 是否成功
     */
    boolean financeConfirm(Integer id, Boolean approved, String reason);

    boolean financeApplyFinish(Integer id, String reason);

    boolean adminFinishApprove(Integer id, Boolean approved, String reason);
}