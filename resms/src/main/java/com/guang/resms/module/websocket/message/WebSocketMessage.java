package com.guang.resms.module.websocket.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket 消息封装类
 * 
 * @author guang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage<T> {

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息内容
     */
    private T data;

    /**
     * 发送者ID
     */
    private Integer senderId;

    /**
     * 接收者ID (点对点消息时使用)
     */
    private Integer receiverId;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 消息ID (用于消息去重和确认)
     */
    private String messageId;

    /**
     * 创建聊天消息
     */
    public static <T> WebSocketMessage<T> chatMessage(Integer senderId, Integer receiverId, T data) {
        return WebSocketMessage.<T>builder()
                .type("CHAT")
                .senderId(senderId)
                .receiverId(receiverId)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建通知消息
     */
    public static <T> WebSocketMessage<T> noticeMessage(Integer senderId, T data) {
        return WebSocketMessage.<T>builder()
                .type("NOTICE")
                .senderId(senderId)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建系统消息
     */
    public static <T> WebSocketMessage<T> systemMessage(T data) {
        return WebSocketMessage.<T>builder()
                .type("SYSTEM")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
