package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.entity.ViewRecord;
import com.guang.resms.entity.dto.ViewRecordQueryDTO;
import com.guang.resms.entity.vo.ViewRecordVO;
import com.guang.resms.mapper.ViewRecordMapper;
import com.guang.resms.service.ViewRecordService;
import com.guang.resms.entity.dto.ViewRecordDTO;
import org.springframework.stereotype.Service;

@Service
public class ViewRecordServiceImpl extends ServiceImpl<ViewRecordMapper, ViewRecord> implements ViewRecordService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.guang.resms.service.NotificationService notificationService;

    @org.springframework.beans.factory.annotation.Autowired
    private com.guang.resms.mapper.CustomerMapper customerMapper;

    @Override
    public Page<ViewRecordVO> getViewRecordPage(ViewRecordQueryDTO queryDTO) {
        Page<ViewRecordVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return baseMapper.selectPageVO(page, queryDTO);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean addViewRecord(ViewRecord viewRecord) {
        boolean result = this.save(viewRecord);

        // 发送带看任务通知
        if (result && viewRecord.getSalesId() != null) {
            String customerName = "未知客户";
            if (viewRecord.getCustomerId() != null) {
                com.guang.resms.entity.Customer c = customerMapper.selectById(viewRecord.getCustomerId());
                if (c != null) {
                    customerName = c.getRealName();
                }
            }

            notificationService.notifyVisitCreated(
                    viewRecord.getId(),
                    customerName,
                    viewRecord.getSalesId(),
                    viewRecord.getViewTime());
        }
        return result;
    }

    @Override
    public boolean updateViewRecord(ViewRecord viewRecord) {
        return this.updateById(viewRecord);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean addAppointment(ViewRecordDTO dto) {
        ViewRecord record = new ViewRecord();
        org.springframework.beans.BeanUtils.copyProperties(dto, record);

        // 默认状态：待确认 (0)
        // 如果是销售录入 (1)，则自动确认为已预约 (1)
        if (record.getAppointType() != null && record.getAppointType() == 1) {
            record.setStatus(1); // 已预约
        } else {
            record.setStatus(0); // 待确认
        }

        // 设置默认预约时间为传入的时间，如果没有则不设置（预约必须有时间）
        if (dto.getViewTime() != null) {
            record.setViewTime(dto.getViewTime());
        }

        return this.save(record);
    }

    @Override
    public boolean confirmAppointment(Integer id) {
        ViewRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("预约记录不存在");
        }
        // 只有待确认(0)状态可以确认
        if (record.getStatus() != 0) {
            throw new RuntimeException("当前状态无法确认");
        }

        record.setStatus(1); // 已预约
        return this.updateById(record);
    }

    @Override
    public boolean completeAppointment(Integer id, String feedback) {
        ViewRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("预约记录不存在");
        }
        // 只有已预约(1)状态可以完成
        if (record.getStatus() != 1) {
            throw new RuntimeException("当前状态无法完成带看");
        }

        record.setStatus(2); // 已完成
        record.setCustomerFeedback(feedback);
        return this.updateById(record);
    }

    @Override
    public boolean cancelAppointment(Integer id, String reason) {
        ViewRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("预约记录不存在");
        }
        // 已完成(2)或已取消(3)的状态无法取消
        if (record.getStatus() >= 2) {
            throw new RuntimeException("当前状态无法取消");
        }

        record.setStatus(3); // 已取消
        record.setCancelReason(reason);
        return this.updateById(record);
    }
}