package com.guang.resms.module.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.module.chat.entity.ChatMessage;
import com.guang.resms.module.chat.mapper.ChatMessageMapper;
import com.guang.resms.module.chat.service.ChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
}
