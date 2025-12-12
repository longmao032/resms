package com.guang.resms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.entity.dto.TransactionPageDTO;
import com.guang.resms.entity.dto.TransactionUpdateDTO;
import com.guang.resms.entity.vo.TransactionVO;
import com.guang.resms.service.TransactionService;
import com.guang.resms.common.exception.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/list")
    public ResponseResult<IPage<TransactionVO>> list(TransactionPageDTO dto) {
        try {
            IPage<TransactionVO> result = transactionService.getTransactionPage(dto);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("查询交易列表失败", e);
            return ResponseResult.fail("查询失败：" + e.getMessage());
        }
    }

    // 1. 获取详情接口
    @GetMapping("/{id}")
    public ResponseResult<TransactionVO> getDetail(@PathVariable Integer id) {
        TransactionVO vo = transactionService.getTransactionDetail(id);
        if (vo == null) {
            return ResponseResult.fail("交易记录不存在");
        }
        return ResponseResult.success(vo);
    }

    // 2. 编辑更新接口
    @PutMapping
    public ResponseResult<Boolean> update(@RequestBody @Validated TransactionUpdateDTO dto) {
        boolean result = transactionService.updateTransaction(dto);
        if (result) {
            return ResponseResult.success(true);
        }
        return ResponseResult.fail("更新失败");
    }

    // 3. 新增交易接口
    @PostMapping
    public ResponseResult<Boolean> add(@RequestBody @Validated com.guang.resms.entity.dto.TransactionAddDTO dto) {
        boolean result = transactionService.addTransaction(dto);
        if (result) {
            return ResponseResult.success(true);
        }
        return ResponseResult.fail("新增失败");
    }
}