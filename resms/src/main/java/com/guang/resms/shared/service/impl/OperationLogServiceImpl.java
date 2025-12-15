package com.guang.resms.shared.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.shared.entity.OperationLog;
import com.guang.resms.shared.entity.dto.OperationLogQueryDTO;
import com.guang.resms.shared.mapper.OperationLogMapper;
import com.guang.resms.shared.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public void saveLog(OperationLog log) {
        this.save(log);
    }

    @Override
    public IPage<OperationLog> pageLogs(OperationLogQueryDTO dto) {
        Page<OperationLog> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<OperationLog> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getModule())) {
            qw.like(OperationLog::getModule, dto.getModule());
        }
        if (StringUtils.hasText(dto.getOperationType())) {
            qw.like(OperationLog::getOperationType, dto.getOperationType());
        }
        if (dto.getRiskLevel() != null) {
            qw.eq(OperationLog::getRiskLevel, dto.getRiskLevel());
        }
        if (StringUtils.hasText(dto.getRiskDimension())) {
            qw.like(OperationLog::getRiskDimension, dto.getRiskDimension());
        }
        if (dto.getStatus() != null) {
            qw.eq(OperationLog::getStatus, dto.getStatus());
        }
        if (dto.getUserId() != null) {
            qw.eq(OperationLog::getUserId, dto.getUserId());
        }
        if (StringUtils.hasText(dto.getBeginTime())) {
            qw.ge(OperationLog::getOperationTime, dto.getBeginTime());
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            qw.le(OperationLog::getOperationTime, dto.getEndTime());
        }

        qw.orderByDesc(OperationLog::getOperationTime);
        return this.page(page, qw);
    }
}
