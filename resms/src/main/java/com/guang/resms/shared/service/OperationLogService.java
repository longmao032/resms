package com.guang.resms.shared.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.shared.entity.OperationLog;
import com.guang.resms.shared.entity.dto.OperationLogQueryDTO;

public interface OperationLogService extends IService<OperationLog> {

    void saveLog(OperationLog log);

    IPage<OperationLog> pageLogs(OperationLogQueryDTO dto);
}
