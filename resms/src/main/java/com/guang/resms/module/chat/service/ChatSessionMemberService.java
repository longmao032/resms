package com.guang.resms.module.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.module.chat.entity.ChatSessionMember;

import java.util.List;

public interface ChatSessionMemberService extends IService<ChatSessionMember> {

    /**
     * 获取会话的所有成员
     * 
     * @param sessionId 会话ID
     * @return 成员列表
     */
    List<ChatSessionMember> getSessionMembers(Long sessionId);
}
