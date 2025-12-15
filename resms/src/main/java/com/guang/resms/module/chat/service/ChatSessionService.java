package com.guang.resms.module.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.module.chat.entity.ChatSession;

public interface ChatSessionService extends IService<ChatSession> {

    /**
     * 创建或获取私聊会话
     * 
     * @param userId       当前用户ID
     * @param targetUserId 目标用户ID
     * @return 会话对象
     */
    ChatSession createPrivateSession(Integer userId, Integer targetUserId);

    /**
     * 获取用户的会话列表
     * 
     * @param userId 用户ID
     * @return 会话VO列表
     */
    java.util.List<com.guang.resms.module.chat.entity.vo.ChatSessionVO> getUserSessions(Integer userId);
}
