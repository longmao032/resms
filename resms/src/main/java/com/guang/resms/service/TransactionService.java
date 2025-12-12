package com.guang.resms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.entity.Transaction;
import com.guang.resms.entity.dto.TransactionPageDTO;
import com.guang.resms.entity.dto.TransactionUpdateDTO;
import com.guang.resms.entity.vo.TransactionVO;

public interface TransactionService extends IService<Transaction> {
    IPage<TransactionVO> getTransactionPage(TransactionPageDTO dto);

    // 新增接口定义
    TransactionVO getTransactionDetail(Integer id);

    boolean updateTransaction(TransactionUpdateDTO dto);

    boolean addTransaction(com.guang.resms.entity.dto.TransactionAddDTO dto);
}