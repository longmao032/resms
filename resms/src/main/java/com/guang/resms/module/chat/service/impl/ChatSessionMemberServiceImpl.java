package com.guang.resms.module.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.module.chat.entity.ChatSessionMember;
import com.guang.resms.module.chat.mapper.ChatSessionMemberMapper;
import com.guang.resms.module.chat.service.ChatSessionMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatSessionMemberServiceImpl extends ServiceImpl<ChatSessionMemberMapper, ChatSessionMember>
        implements ChatSessionMemberService {

    @Override
    public List<ChatSessionMember> getSessionMembers(Long sessionId) {
        return this.list(new LambdaQueryWrapper<ChatSessionMember>()
                .eq(ChatSessionMember::getSessionId, sessionId));
    }
}
