package com.guang.resms.shared.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.shared.entity.OperationLog;
import com.guang.resms.shared.entity.dto.OperationLogQueryDTO;
import com.guang.resms.shared.service.OperationLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operation-log")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping("/page")
    public ResponseResult<IPage<OperationLog>> page(OperationLogQueryDTO dto) {
        Integer roleType = SecurityUtils.getRoleType();
        Integer userId = SecurityUtils.getUserId();
        if (roleType == null || userId == null) {
            return ResponseResult.fail(HttpEnums.UNAUTHORIZED.getCode(), "未登录");
        }

        if (roleType != 1) {
            dto.setUserId(userId);
        }

        return ResponseResult.success(operationLogService.pageLogs(dto));
    }
}
