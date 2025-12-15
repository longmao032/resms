package com.guang.resms.module.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.module.chat.entity.ChatSession;
import com.guang.resms.module.chat.entity.ChatSessionMember;
import com.guang.resms.module.chat.mapper.ChatSessionMapper;
import com.guang.resms.module.chat.service.ChatSessionMemberService;
import com.guang.resms.module.chat.service.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatSessionService {

    @Autowired
    private ChatSessionMemberService chatSessionMemberService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatSession createPrivateSession(Integer userId, Integer targetUserId) {
        // 1. Check if session exists
        ChatSession existingSession = baseMapper.selectPrivateSession(userId, targetUserId);
        if (existingSession != null) {
            return existingSession;
        }

        // 2. Create new session
        ChatSession session = new ChatSession();
        session.setSessionType(1); // Private
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        this.save(session);

        // 3. Create members
        ChatSessionMember member1 = new ChatSessionMember();
        member1.setSessionId(session.getId());
        member1.setUserId(userId);
        member1.setJoinTime(LocalDateTime.now());

        ChatSessionMember member2 = new ChatSessionMember();
        member2.setSessionId(session.getId());
        member2.setUserId(targetUserId);
        member2.setJoinTime(LocalDateTime.now());

        chatSessionMemberService.save(member1);
        chatSessionMemberService.save(member2);

        return session;
    }

    @Override
    public java.util.List<com.guang.resms.module.chat.entity.vo.ChatSessionVO> getUserSessions(Integer userId) {
        return baseMapper.selectUserSessions(userId);
    }
}
