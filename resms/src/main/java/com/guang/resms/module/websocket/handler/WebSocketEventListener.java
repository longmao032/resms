package com.guang.resms.module.websocket.handler;

import com.guang.resms.module.websocket.manager.OnlineUserManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket 事件监听器
 * 处理用户连接和断开事件
 * 
 * @author guang
 */
@Slf4j
@Component
public class WebSocketEventListener {

    private final OnlineUserManager onlineUserManager;

    public WebSocketEventListener(OnlineUserManager onlineUserManager) {
        this.onlineUserManager = onlineUserManager;
    }

    /**
     * 监听 WebSocket 连接事件
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (userId != null) {
            log.info("WebSocket 连接建立: userId={}, username={}, sessionId={}",
                    userId, username, headerAccessor.getSessionId());
        }
    }

    /**
     * 监听 WebSocket 断开事件
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (userId != null) {
            log.info("WebSocket 连接断开: userId={}, username={}, sessionId={}",
                    userId, username, headerAccessor.getSessionId());
        }
    }
}
