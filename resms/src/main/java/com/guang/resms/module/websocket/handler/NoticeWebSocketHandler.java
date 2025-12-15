package com.guang.resms.module.websocket.handler;

import com.guang.resms.module.notice.entity.NoticeRead;
import com.guang.resms.module.notice.entity.WorkNotice;
import com.guang.resms.module.user.mapper.UserRoleMapper;
import com.guang.resms.module.notice.service.WorkNoticeService;
import com.guang.resms.module.websocket.message.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 工作通知 WebSocket 处理器
 * 
 * @author guang
 */
@Slf4j
@Controller
public class NoticeWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final WorkNoticeService workNoticeService;
    private final UserRoleMapper userRoleMapper;

    public NoticeWebSocketHandler(SimpMessagingTemplate messagingTemplate,
            WorkNoticeService workNoticeService,
            UserRoleMapper userRoleMapper) {
        this.messagingTemplate = messagingTemplate;
        this.workNoticeService = workNoticeService;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * 发送工作通知
     * 客户端发送到: /app/notice.send
     * 
     * @param notice         通知内容
     * @param headerAccessor 消息头访问器
     */
    @MessageMapping("/notice.send")
    public void sendNotice(@Payload WorkNotice notice, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // 从 WebSocket Session 中获取发送者信息
            Integer senderId = (Integer) headerAccessor.getSessionAttributes().get("userId");
            String username = (String) headerAccessor.getSessionAttributes().get("username");

            if (senderId == null) {
                log.warn("发送通知失败: 未找到发送者ID");
                return;
            }

            // 设置发送者信息
            notice.setSenderId(senderId);
            notice.setSenderName(username != null ? username : "系统管理员");

            // 保存通知到数据库
            workNoticeService.publishNotice(notice);

            // 根据通知类型推送
            WebSocketMessage<WorkNotice> wsMessage = WebSocketMessage.noticeMessage(senderId, notice);

            // 根据接收者类型推送
            if (notice.getReceiverType() == 2) {
                // 角色通知 - 广播给指定角色
                // receiverIds 包含角色ID列表,这里简化处理,假设只有一个角色ID
                messagingTemplate.convertAndSend(
                        "/topic/notice." + notice.getReceiverIds(),
                        wsMessage);
                log.info("发送角色通知: senderId={}, receiverIds={}, title={}",
                        senderId, notice.getReceiverIds(), notice.getNoticeTitle());

            } else if (notice.getReceiverType() == 1) {
                // 个人通知 - 点对点发送
                // receiverIds 包含用户ID列表
                if (notice.getReceiverIds() != null && !notice.getReceiverIds().isEmpty()) {
                    // 简化处理,假设 receiverIds 是单个用户ID
                    messagingTemplate.convertAndSendToUser(
                            notice.getReceiverIds(),
                            "/queue/notices",
                            wsMessage);
                    log.info("发送个人通知: senderId={}, receiverId={}, title={}",
                            senderId, notice.getReceiverIds(), notice.getNoticeTitle());
                }

            } else if (notice.getReceiverType() == 4) {
                // 全体通知 - 广播给所有人
                messagingTemplate.convertAndSend(
                        "/topic/notice.system",
                        wsMessage);
                log.info("发送系统通知: senderId={}, title={}", senderId, notice.getNoticeTitle());
            }

        } catch (Exception e) {
            log.error("发送通知失败", e);
        }
    }

    /**
     * 标记通知已读
     * 客户端发送到: /app/notice.read
     * 
     * @param noticeId       通知ID
     * @param headerAccessor 消息头访问器
     */
    @MessageMapping("/notice.read")
    public void markNoticeAsRead(@Payload Integer noticeId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
            if (userId == null) {
                return;
            }

            // 标记已读
            workNoticeService.markAsRead(userId, noticeId);
            log.info("标记通知已读: userId={}, noticeId={}", userId, noticeId);

        } catch (Exception e) {
            log.error("标记通知已读失败", e);
        }
    }
}
