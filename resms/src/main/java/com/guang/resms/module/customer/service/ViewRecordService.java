package com.guang.resms.module.customer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.module.customer.entity.ViewRecord;
import com.guang.resms.module.customer.entity.dto.ViewRecordQueryDTO;
import com.guang.resms.module.customer.entity.vo.ViewRecordVO;

import com.guang.resms.module.customer.entity.dto.ViewRecordDTO;

public interface ViewRecordService extends IService<ViewRecord> {
    public Page<ViewRecordVO> getViewRecordPage(ViewRecordQueryDTO queryDTO);

    boolean addViewRecord(ViewRecord viewRecord);

    boolean updateViewRecord(ViewRecord viewRecord);

    /**
     * 新增预约
     */
    boolean addAppointment(ViewRecordDTO dto);

    /**
     * 确认预约
     */
    boolean confirmAppointment(Integer id);

    /**
     * 完成带看
     */
    boolean completeAppointment(Integer id, String feedback);

    /**
     * 取消预约
     */
    boolean cancelAppointment(Integer id, String reason);
}
