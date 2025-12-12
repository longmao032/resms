package com.guang.resms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.entity.WorkNotice;
import com.guang.resms.entity.dto.WorkNoticeQueryDTO;
import com.guang.resms.entity.vo.WorkNoticeVO;

public interface WorkNoticeService extends IService<WorkNotice> {

    /**
     * 发布通知
     */
    void publishNotice(WorkNotice notice);

    /**
     * 撤回通知
     */
    void withdrawNotice(Integer id);

    /**
     * 标记为已读
     */
    void markAsRead(Integer userId, Integer noticeId);

    /**
     * 获取我的通知列表
     */
    IPage<WorkNoticeVO> getMyNoticePage(WorkNoticeQueryDTO dto, Integer userId, Integer roleId);
}
