package com.guang.resms.module.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("tb_chat_session")
@Data
public class ChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会话ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 会话类型：1=私聊(1:1)，2=群聊(Group) */
    @TableField("session_type")
    private Integer sessionType;

    /** 会话名称（群聊时显示） */
    @TableField("session_name")
    private String sessionName;

    /** 最后一条消息内容快照 */
    @TableField("last_message_content")
    private String lastMessageContent;

    /** 最后一条消息类型：1=文本，2=图片，3=文件 */
    @TableField("last_message_type")
    private Integer lastMessageType;

    /** 最后一条消息发送时间 */
    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
