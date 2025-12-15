package com.guang.resms.module.transaction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.module.transaction.entity.dto.CommissionPageDTO;
import com.guang.resms.module.transaction.entity.vo.CommissionVO;
import com.guang.resms.module.transaction.service.CommissionService;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.shared.annotation.OpLog;
import com.guang.resms.shared.enums.RiskDimension;
import com.guang.resms.shared.enums.RiskLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 
 */
@Slf4j
@RestController
@RequestMapping("/commission")
public class CommissionController {

    @Autowired
    private CommissionService commissionService;

    // 
    @GetMapping("/list")
    public ResponseResult<IPage<CommissionVO>> list(CommissionPageDTO dto) {
        return ResponseResult.success(commissionService.getCommissionPage(dto)); // 
    }

    // 
    @PostMapping("/calculate/{id}")
    @OpLog(module = "佣金管理", operationType = "佣金核算", level = RiskLevel.MEDIUM, dimensions = { RiskDimension.FINANCIAL })
    public ResponseResult<Boolean> calculate(@PathVariable Integer id) {
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null || (roleType != 1 && roleType != 4)) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }

        Integer currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }
        boolean result = commissionService.calculateCommission(id, currentUserId);
        return result ? ResponseResult.success("核算成功") : ResponseResult.fail("核算失败");
    }

    // 
    @PostMapping("/issue/{id}")
    @OpLog(module = "佣金管理", operationType = "佣金发放", level = RiskLevel.HIGH, dimensions = { RiskDimension.FINANCIAL }, notifyAdmin = true)
    public ResponseResult<Boolean> issue(@PathVariable Integer id) {
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null || (roleType != 1 && roleType != 4)) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }

        Integer currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }
        boolean result = commissionService.issueCommission(id, currentUserId);
        return result ? ResponseResult.success("发放成功") : ResponseResult.fail("发放失败，请检查状态");
    }

    @GetMapping("/by-transaction/{transactionId}")
    public ResponseResult<CommissionVO> getByTransaction(@PathVariable Integer transactionId) {
        Integer roleType = SecurityUtils.getRoleType();
        Integer currentUserId = SecurityUtils.getUserId();
        if (roleType == null || currentUserId == null) {
            throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }
        CommissionVO vo = commissionService.getByTransactionId(transactionId);
        return ResponseResult.success(vo);
    }

    @PostMapping("/create/{transactionId}")
    public ResponseResult<Boolean> create(@PathVariable Integer transactionId,
            @RequestParam BigDecimal rate) {
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null || (roleType != 1 && roleType != 4)) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }
        Integer currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }
        boolean result = commissionService.createByTransaction(transactionId, rate);
        return result ? ResponseResult.success("生成成功") : ResponseResult.fail("生成失败");
    }
}