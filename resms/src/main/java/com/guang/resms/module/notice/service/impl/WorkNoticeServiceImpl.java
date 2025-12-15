package com.guang.resms.module.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.notice.entity.NoticeRead;
import com.guang.resms.module.notice.entity.WorkNotice;
import com.guang.resms.module.notice.entity.dto.WorkNoticeQueryDTO;
import com.guang.resms.module.notice.entity.vo.WorkNoticeVO;
import com.guang.resms.module.notice.mapper.NoticeReadMapper;
import com.guang.resms.module.notice.mapper.WorkNoticeMapper;
import com.guang.resms.module.notice.service.WorkNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class WorkNoticeServiceImpl extends ServiceImpl<WorkNoticeMapper, WorkNotice> implements WorkNoticeService {

    @Autowired
    private WorkNoticeMapper workNoticeMapper;

    @Autowired
    private NoticeReadMapper noticeReadMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice(WorkNotice notice) {
        // [FIX] 自动通知（有业务类型）或系统通知（发送者ID为0）跳过权限校验
        boolean isAuto = (notice.getSenderId() != null && notice.getSenderId() == 0)
                || (notice.getBizType() != null && !notice.getBizType().trim().isEmpty());

        if (isAuto) {
            notice.setSendTime(LocalDateTime.now());
            notice.setStatus(1);
            this.save(notice);
            return;
        }

        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null) {
            throw new ServiceException("未登录");
        }

        // 权限校验
        if (roleType == 2 || roleType == 5) {
            throw new ServiceException("您没有权限发送通知");
        }

        // 销售经理限制
        if (roleType == 3) {
            // 只能发送给本部门 (ReceiverType=3 且 ID匹配 或 ReceiverType=1 且用户是下属)
            // 简化校验：必须是本部门相关
            if (notice.getReceiverType() == 4) { // 全体
                throw new ServiceException("销售经理不能发送全体通知");
            }
            // 更多细粒度校验可在此添加，如校验TeamID
        }

        // 财务限制
        if (roleType == 4) {
            // Relaxed: Allow Finance to send to any target (Users, Roles, Teams, All)
            // similar to Admin
            // Previously restricted to Sales Roles, now fully open as requested.
        }

        notice.setSendTime(LocalDateTime.now());
        notice.setStatus(1); // 1=已发送
        this.save(notice);
        // TODO: 这里可以异步触发消息推送（WebSocket）
    }

    @Override
    public void withdrawNotice(Integer id) {
        WorkNotice notice = this.getById(id);
        if (notice == null) {
            throw new ServiceException("通知不存在");
        }

        Integer userId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();

        boolean canWithdraw = false;

        if (roleType == 1) { // 管理员
            canWithdraw = true;
        } else if (notice.getSenderId().equals(userId)) { // 自己发送的
            canWithdraw = true;
        } else if (roleType == 3) { // 销售经理
            // 可撤回本部门通知 -> 理解为自己发的，或者本部门内的?
            // 鉴于只有经理能发部门通知，senderId=userId已覆盖。
            // 除非有其他经理发给同部门？
            // 暂时维持仅自己或Admin
        } else if (roleType == 4) { // 财务
            // 可撤回财务类通知 (Type=4)
            if (notice.getNoticeType() == 4) {
                canWithdraw = true;
            }
        }

        if (!canWithdraw) {
            throw new ServiceException("您没有权限撤回该通知");
        }

        notice.setStatus(2); // 2=已撤回
        this.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Integer userId, Integer noticeId) {
        // Double check lock ideally, but here simple check is enough for now
        Long count = noticeReadMapper.selectCount(new LambdaQueryWrapper<NoticeRead>()
                .eq(NoticeRead::getNoticeId, noticeId)
                .eq(NoticeRead::getUserId, userId));

        if (count == 0) {
            NoticeRead read = new NoticeRead();
            read.setNoticeId(noticeId);
            read.setUserId(userId);
            read.setReadTime(new Date());
            noticeReadMapper.insert(read);

            // 更新主表的已读计数（原子操作）
            workNoticeMapper.incrementReadCount(noticeId);
        }
    }

    @Override
    public IPage<WorkNoticeVO> getMyNoticePage(WorkNoticeQueryDTO dto, Integer userId, Integer roleId) {
        Page<WorkNoticeVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return workNoticeMapper.selectMyNoticePage(page, dto, userId, roleId);
    }
}
