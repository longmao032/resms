package com.guang.resms.module.notice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.notice.entity.WorkNotice;
import com.guang.resms.module.notice.entity.dto.WorkNoticeQueryDTO;
import com.guang.resms.module.notice.entity.vo.WorkNoticeVO;
import com.guang.resms.module.notice.service.WorkNoticeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 工作通知控制器
 */
@RestController
@RequestMapping("/work-notice")
public class WorkNoticeController {

    @Value("${feature.notice.enabled:true}")
    private boolean noticeEnabled;

    @Autowired
    private WorkNoticeService workNoticeService;

    /**
     * 发送通知 (Admin only in frontend logic, verify here if needed)
     */
    @PostMapping("/send")
    public ResponseResult<?> send(@RequestBody WorkNotice notice) {
        if (!noticeEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "消息/通知功能已禁用");
        }
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
        if (!noticeEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "消息/通知功能已禁用");
        }
        Integer userId = SecurityUtils.getUserId();
        Integer roleId = SecurityUtils.getRoleType(); // Assuming RoleType acts as ID or mapped
        return ResponseResult.success(workNoticeService.getMyNoticePage(dto, userId, roleId));
    }

    /**
     * 标记已读
     */
    @PostMapping("/read/{id}")
    public ResponseResult<?> read(@PathVariable Integer id) {
        if (!noticeEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "消息/通知功能已禁用");
        }
        workNoticeService.markAsRead(SecurityUtils.getUserId(), id);
        return ResponseResult.success();
    }

    /**
     * 一键已读
     */
    @PostMapping("/read-all")
    public ResponseResult<?> readAll() {
        if (!noticeEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "消息/通知功能已禁用");
        }
        workNoticeService.markAllAsRead(SecurityUtils.getUserId());
        return ResponseResult.success("全部标记为已读");
    }

    /**
     * 撤回通知
     */
    @PostMapping("/withdraw/{id}")
    public ResponseResult<?> withdraw(@PathVariable Integer id) {
        if (!noticeEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "消息/通知功能已禁用");
        }
        workNoticeService.withdrawNotice(id);
        return ResponseResult.success("撤回成功");
    }

    /**
     * 删除通知 (逻辑删除 or 物理删除，这里简化为Service默认remove)
     */
    @DeleteMapping("/{id}")
    public ResponseResult<?> delete(@PathVariable Integer id) {
        if (!noticeEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "消息/通知功能已禁用");
        }
        workNoticeService.removeById(id);
        return ResponseResult.success("删除成功");
    }
}
