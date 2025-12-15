package com.guang.resms.module.chat.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatSessionVO {
    private Long id;
    private Integer sessionType;
    private String sessionName;
    private String lastMessageContent;
    private Integer lastMessageType;
    private LocalDateTime lastMessageTime;

    // Member specific info
    private Integer unreadCount;
    private Integer isTop;
    private Integer isDisturb;

    // Target user info (for private chat)
    private Integer targetUserId;
    private String targetUserName;
    private String targetAvatar;
}
