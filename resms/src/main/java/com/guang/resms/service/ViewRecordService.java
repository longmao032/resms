package com.guang.resms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.entity.ViewRecord;
import com.guang.resms.entity.dto.ViewRecordQueryDTO;
import com.guang.resms.entity.vo.ViewRecordVO;

import com.guang.resms.entity.dto.ViewRecordDTO;

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
