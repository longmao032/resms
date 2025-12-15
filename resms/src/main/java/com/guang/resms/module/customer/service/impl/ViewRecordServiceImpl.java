package com.guang.resms.module.customer.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.module.customer.entity.ViewRecord;
import com.guang.resms.module.customer.entity.dto.ViewRecordQueryDTO;
import com.guang.resms.module.customer.entity.vo.ViewRecordVO;
import com.guang.resms.module.customer.mapper.ViewRecordMapper;
import com.guang.resms.module.customer.service.ViewRecordService;
import com.guang.resms.module.customer.entity.dto.ViewRecordDTO;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 
 *
 * @author guang
 */
@Service
public class ViewRecordServiceImpl extends ServiceImpl<ViewRecordMapper, ViewRecord> implements ViewRecordService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.guang.resms.module.chat.service.NotificationService notificationService;

    @org.springframework.beans.factory.annotation.Autowired
    private com.guang.resms.module.customer.mapper.CustomerMapper customerMapper;

    @Override
    public Page<ViewRecordVO> getViewRecordPage(ViewRecordQueryDTO queryDTO) {
        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            queryDTO.setSalesId(currentUserId);
        }
        Page<ViewRecordVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return baseMapper.selectPageVO(page, queryDTO);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean addViewRecord(ViewRecord viewRecord) {
        boolean result = this.save(viewRecord);

        // 
        if (result && viewRecord.getSalesId() != null) {
            String customerName = "未知客户";
            if (viewRecord.getCustomerId() != null) {
                com.guang.resms.module.customer.entity.Customer c = customerMapper
                        .selectById(viewRecord.getCustomerId());
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
        assertOperateAllowed(viewRecord == null ? null : viewRecord.getId());
        return this.updateById(viewRecord);
    }

    @Override
    public boolean removeById(Serializable id) {
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }
        return super.removeById(id);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean addAppointment(ViewRecordDTO dto) {
        ViewRecord record = new ViewRecord();
        org.springframework.beans.BeanUtils.copyProperties(dto, record);

        // ========== ==========
        Integer currentUserId = com.guang.resms.common.utils.SecurityUtils.getUserId();
        Integer roleType = com.guang.resms.common.utils.SecurityUtils.getRoleType();

        // (roleType=3): salesId
        if (roleType != null && roleType == 2) {
            record.setSalesId(currentUserId);
        } else if (record.getSalesId() == null) {
            // / : salesId
            record.setSalesId(currentUserId);
        }

        // : (0)
        // (1), (1)
        if (record.getAppointType() != null && record.getAppointType() == 1) {
            record.setStatus(1); // 
        } else {
            record.setStatus(0); // 
        }

        // 
        if (dto.getViewTime() != null) {
            record.setViewTime(dto.getViewTime());
        }

        return this.save(record);
    }

    @Override
    public boolean confirmAppointment(Integer id) {
        ViewRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("");
        }
        assertOperateAllowed(record.getId());
        // (0)
        if (record.getStatus() != 0) {
            throw new RuntimeException("");
        }

        record.setStatus(1); // 
        return this.updateById(record);
    }

    @Override
    public boolean completeAppointment(Integer id, String feedback) {
        ViewRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("");
        }
        assertOperateAllowed(record.getId());
        // (1)
        if (record.getStatus() != 1) {
            throw new RuntimeException("");
        }

        record.setStatus(2); // 
        record.setCustomerFeedback(feedback);
        return this.updateById(record);
    }

    @Override
    public boolean cancelAppointment(Integer id, String reason) {
        ViewRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("");
        }
        assertOperateAllowed(record.getId());
        // (2)(3)
        if (record.getStatus() >= 2) {
            throw new RuntimeException("");
        }

        record.setStatus(3); // 
        record.setCancelReason(reason);
        return this.updateById(record);
    }

    private void assertOperateAllowed(Integer viewRecordId) {
        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null || roleType != 2) {
            return;
        }
        if (viewRecordId == null) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }

        ViewRecord record = this.getById(viewRecordId);
        if (record == null) {
            throw new ServiceException(HttpEnums.FAIL.getCode(), "记录不存在");
        }
        if (record.getSalesId() == null || !record.getSalesId().equals(currentUserId)) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }
    }
}