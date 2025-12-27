package com.guang.resms.module.transaction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.module.transaction.entity.dto.TransactionPageDTO;
import com.guang.resms.module.transaction.entity.dto.TransactionUpdateDTO;
import com.guang.resms.module.transaction.entity.vo.TransactionVO;
import com.guang.resms.module.transaction.service.TransactionService;
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
    public ResponseResult<Boolean> add(
            @RequestBody @Validated com.guang.resms.module.transaction.entity.dto.TransactionAddDTO dto) {
        boolean result = transactionService.addTransaction(dto);
        if (result) {
            return ResponseResult.success(true);
        }
        return ResponseResult.fail("新增失败");
    }

    @PutMapping("/manager-approve/{id}")
    public ResponseResult<Boolean> managerApprove(@PathVariable Integer id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String reason) {
        return ResponseResult.success(transactionService.managerApprove(id, approved, reason));
    }

    @PutMapping("/finance-confirm/{id}")
    public ResponseResult<Boolean> financeConfirm(@PathVariable Integer id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String reason) {
        return ResponseResult.success(transactionService.financeConfirm(id, approved, reason));
    }

    @PutMapping("/finance-apply-finish/{id}")
    public ResponseResult<Boolean> financeApplyFinish(@PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        return ResponseResult.success(transactionService.financeApplyFinish(id, reason));
    }

    @PutMapping("/admin-finish-approve/{id}")
    public ResponseResult<Boolean> adminFinishApprove(@PathVariable Integer id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String reason) {
        return ResponseResult.success(transactionService.adminFinishApprove(id, approved, reason));
    }

    @PutMapping("/resubmit/{id}")
    public ResponseResult<Void> resubmitAudit(@PathVariable Integer id) {
        transactionService.resubmitAudit(id);
        return ResponseResult.success("重新提交成功");
    }

    @PutMapping("/void/{id}")
    public ResponseResult<Void> voidTransaction(@PathVariable Integer id, @RequestParam String reason) {
        transactionService.voidTransaction(id, reason);
        return ResponseResult.success("作废成功");
    }
}