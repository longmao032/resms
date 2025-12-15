package com.guang.resms.module.websocket.handler;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.guang.resms.module.chat.entity.ChatMessage;
import com.guang.resms.module.chat.entity.ChatSession;
import com.guang.resms.module.chat.entity.ChatSessionMember;
import com.guang.resms.module.chat.service.ChatMessageService;
import com.guang.resms.module.chat.service.ChatSessionMemberService;
import com.guang.resms.module.chat.service.ChatSessionService;
import com.guang.resms.module.websocket.message.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * 聊天消息 WebSocket 处理器
 * 
 * @author guang
 */
@Slf4j
@Controller
public class ChatWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatSessionService chatSessionService;
    private final ChatSessionMemberService chatSessionMemberService;

    public ChatWebSocketHandler(SimpMessagingTemplate messagingTemplate,
            ChatMessageService chatMessageService,
            ChatSessionService chatSessionService,
            ChatSessionMemberService chatSessionMemberService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.chatSessionService = chatSessionService;
        this.chatSessionMemberService = chatSessionMemberService;
    }

    /**
     * 处理聊天消息发送
     * 客户端发送到: /app/chat.send
     * 
     * @param message        聊天消息
     * @param headerAccessor 消息头访问器
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // 从 WebSocket Session 中获取发送者ID
            Integer senderId = (Integer) headerAccessor.getSessionAttributes().get("userId");
            if (senderId == null) {
                log.warn("发送消息失败: 未找到发送者ID");
                return;
            }

            // 设置消息属性
            message.setSenderId(senderId);
            message.setCreateTime(LocalDateTime.now());
            message.setIsRecalled(0);

            // 保存消息到数据库
            boolean saved = chatMessageService.save(message);
            if (!saved) {
                log.error("保存消息失败: sessionId={}", message.getSessionId());
                return;
            }

            // 更新会话最新消息
            ChatSession session = new ChatSession();
            session.setId(message.getSessionId());
            session.setLastMessageContent(message.getMsgType() == 1 ? message.getContent() : "[媒体消息]");
            session.setLastMessageType(message.getMsgType());
            session.setLastMessageTime(message.getCreateTime());
            chatSessionService.updateById(session);

            // 增加其他成员的未读数
            chatSessionMemberService.update(new LambdaUpdateWrapper<ChatSessionMember>()
                    .eq(ChatSessionMember::getSessionId, message.getSessionId())
                    .ne(ChatSessionMember::getUserId, senderId)
                    .setSql("unread_count = unread_count + 1"));

            // 获取会话成员,推送消息给其他成员
            chatSessionMemberService.list(new LambdaUpdateWrapper<ChatSessionMember>()
                    .eq(ChatSessionMember::getSessionId, message.getSessionId())
                    .ne(ChatSessionMember::getUserId, senderId))
                    .forEach(member -> {
                        // 推送消息给接收者
                        WebSocketMessage<ChatMessage> wsMessage = WebSocketMessage.chatMessage(
                                senderId, member.getUserId(), message);
                        messagingTemplate.convertAndSendToUser(
                                member.getUserId().toString(),
                                "/queue/messages",
                                wsMessage);
                        log.info("推送聊天消息: senderId={}, receiverId={}, sessionId={}",
                                senderId, member.getUserId(), message.getSessionId());
                    });

            // 发送确认给发送者
            WebSocketMessage<ChatMessage> confirmMessage = WebSocketMessage.chatMessage(
                    senderId, senderId, message);
            messagingTemplate.convertAndSendToUser(
                    senderId.toString(),
                    "/queue/messages",
                    confirmMessage);

        } catch (Exception e) {
            log.error("处理聊天消息失败", e);
        }
    }

    /**
     * 标记消息已读
     * 客户端发送到: /app/chat.read
     * 
     * @param sessionId      会话ID
     * @param headerAccessor 消息头访问器
     */
    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Long sessionId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
            if (userId == null) {
                return;
            }

            // 更新未读数为0
            chatSessionMemberService.update(new LambdaUpdateWrapper<ChatSessionMember>()
                    .eq(ChatSessionMember::getSessionId, sessionId)
                    .eq(ChatSessionMember::getUserId, userId)
                    .set(ChatSessionMember::getUnreadCount, 0));

            log.info("标记会话已读: userId={}, sessionId={}", userId, sessionId);

        } catch (Exception e) {
            log.error("标记已读失败", e);
        }
    }
}
