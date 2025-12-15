package com.guang.resms.module.websocket.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线用户管理器
 * 维护在线用户的 WebSocket Session
 * 
 * @author guang
 */
@Slf4j
@Component
public class OnlineUserManager {

    /**
     * 在线用户映射: userId -> WebSocketSession
     */
    private final Map<Integer, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();

    /**
     * 用户上线
     * 
     * @param userId  用户ID
     * @param session WebSocket Session
     */
    public void userOnline(Integer userId, WebSocketSession session) {
        onlineUsers.put(userId, session);
        log.info("用户上线: userId={}, 当前在线人数={}", userId, onlineUsers.size());
    }

    /**
     * 用户下线
     * 
     * @param userId 用户ID
     */
    public void userOffline(Integer userId) {
        onlineUsers.remove(userId);
        log.info("用户下线: userId={}, 当前在线人数={}", userId, onlineUsers.size());
    }

    /**
     * 获取用户的 WebSocket Session
     * 
     * @param userId 用户ID
     * @return WebSocket Session,如果用户不在线则返回 null
     */
    public WebSocketSession getUserSession(Integer userId) {
        return onlineUsers.get(userId);
    }

    /**
     * 判断用户是否在线
     * 
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isOnline(Integer userId) {
        return onlineUsers.containsKey(userId);
    }

    /**
     * 获取在线用户数量
     * 
     * @return 在线用户数量
     */
    public int getOnlineCount() {
        return onlineUsers.size();
    }

    /**
     * 获取所有在线用户ID
     * 
     * @return 在线用户ID集合
     */
    public java.util.Set<Integer> getOnlineUserIds() {
        return onlineUsers.keySet();
    }
}
