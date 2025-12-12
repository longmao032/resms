package com.guang.resms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.entity.dto.CommissionPageDTO;
import com.guang.resms.entity.vo.CommissionVO;
import com.guang.resms.service.CommissionService;
import com.guang.resms.common.exception.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/commission")
public class CommissionController {

    @Autowired
    private CommissionService commissionService;

    // 分页列表
    @GetMapping("/list")
    public ResponseResult<IPage<CommissionVO>> list(CommissionPageDTO dto) {
        return ResponseResult.success(commissionService.getCommissionPage(dto)); // 引用 [cite: 21]
    }

    // 核算操作
    @PostMapping("/calculate/{id}")
    public ResponseResult<Boolean> calculate(@PathVariable Integer id) {
        // 假设从SecurityContext获取当前登录用户ID作为financeId，这里模拟为1
        Integer currentUserId = 1; 
        boolean result = commissionService.calculateCommission(id, currentUserId);
        return result ? ResponseResult.success("核算成功") : ResponseResult.fail("核算失败"); // 引用 [cite: 22, 30]
    }

    // 发放操作
    @PostMapping("/issue/{id}")
    public ResponseResult<Boolean> issue(@PathVariable Integer id) {
        Integer currentUserId = 1;
        boolean result = commissionService.issueCommission(id, currentUserId);
        return result ? ResponseResult.success("发放成功") : ResponseResult.fail("发放失败，请检查状态");
    }
}