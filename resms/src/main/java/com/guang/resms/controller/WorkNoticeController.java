package com.guang.resms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.entity.WorkNotice;
import com.guang.resms.entity.dto.WorkNoticeQueryDTO;
import com.guang.resms.entity.vo.WorkNoticeVO;
import com.guang.resms.service.WorkNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 工作通知控制器
 */
@RestController
@RequestMapping("/work-notice")
public class WorkNoticeController {

    @Autowired
    private WorkNoticeService workNoticeService;

    /**
     * 发送通知 (Admin only in frontend logic, verify here if needed)
     */
    @PostMapping("/send")
    public ResponseResult<?> send(@RequestBody WorkNotice notice) {
        notice.setSenderId(SecurityUtils.getUserId());
        // Assume username available in context or fetch via service. Here simple set.
        String currentUsername = SecurityUtils.getUsername();
        notice.setSenderName(currentUsername != null ? currentUsername : "系统管理员");

        workNoticeService.publishNotice(notice);
        return ResponseResult.success("发送成功");
    }

    /**
     * 获取我的通知列表
     */
    @GetMapping("/list")
    public ResponseResult<IPage<WorkNoticeVO>> list(WorkNoticeQueryDTO dto) {
        Integer userId = SecurityUtils.getUserId();
        Integer roleId = SecurityUtils.getRoleType(); // Assuming RoleType acts as ID or mapped
        return ResponseResult.success(workNoticeService.getMyNoticePage(dto, userId, roleId));
    }

    /**
     * 标记已读
     */
    @PostMapping("/read/{id}")
    public ResponseResult<?> read(@PathVariable Integer id) {
        workNoticeService.markAsRead(SecurityUtils.getUserId(), id);
        return ResponseResult.success();
    }

    /**
     * 撤回通知
     */
    @PostMapping("/withdraw/{id}")
    public ResponseResult<?> withdraw(@PathVariable Integer id) {
        workNoticeService.withdrawNotice(id);
        return ResponseResult.success("撤回成功");
    }

    /**
     * 删除通知 (逻辑删除 or 物理删除，这里简化为Service默认remove)
     */
    @DeleteMapping("/{id}")
    public ResponseResult<?> delete(@PathVariable Integer id) {
        workNoticeService.removeById(id);
        return ResponseResult.success("删除成功");
    }
}
