package com.guang.resms.module.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.module.customer.entity.ViewRecord;
import com.guang.resms.module.customer.entity.dto.ViewRecordQueryDTO;
import com.guang.resms.module.customer.entity.vo.ViewRecordVO;
import com.guang.resms.module.customer.service.ViewRecordService;
import com.guang.resms.common.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/view-record")
public class ViewRecordController {

    @Autowired
    private ViewRecordService viewRecordService;

    // 分页查询带看记录（包含关联信息）
    @PostMapping("/page")
    public ResponseResult<Page<ViewRecordVO>> getPage(@RequestBody ViewRecordQueryDTO queryDTO) {
        return ResponseResult.success(viewRecordService.getViewRecordPage(queryDTO));
    }

    // 新增记录接口已删除 - 跟进记录由预约看房完成后自动生成

    // 更新记录
    @PutMapping
    public ResponseResult<Boolean> update(@RequestBody ViewRecord viewRecord) {
        return ResponseResult.success(viewRecordService.updateViewRecord(viewRecord));
    }

    // 删除记录
    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> delete(@PathVariable Integer id) {
        return ResponseResult.success(viewRecordService.removeById(id));
    }

    // 新增预约
    @PostMapping("/appointment")
    public ResponseResult<Boolean> addAppointment(
            @RequestBody com.guang.resms.module.customer.entity.dto.ViewRecordDTO dto) {
        return ResponseResult.success(viewRecordService.addAppointment(dto));
    }

    // 确认预约
    @PutMapping("/appointment/confirm/{id}")
    public ResponseResult<Boolean> confirmAppointment(@PathVariable Integer id) {
        return ResponseResult.success(viewRecordService.confirmAppointment(id));
    }

    // 完成带看
    @PutMapping("/appointment/complete/{id}")
    public ResponseResult<Boolean> completeAppointment(@PathVariable Integer id,
            @RequestParam(required = false) String feedback) {
        return ResponseResult.success(viewRecordService.completeAppointment(id, feedback));
    }

    // 取消预约
    @PutMapping("/appointment/cancel/{id}")
    public ResponseResult<Boolean> cancelAppointment(@PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        return ResponseResult.success(viewRecordService.cancelAppointment(id, reason));
    }
}