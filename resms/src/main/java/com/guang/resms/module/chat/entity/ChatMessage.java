package com.guang.resms.module.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("tb_chat_message")
@Data
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 消息ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 所属会话ID */
    @TableField("session_id")
    private Long sessionId;

    /** 发送者ID */
    @TableField("sender_id")
    private Integer senderId;

    /** 消息内容 */
    @TableField("content")
    private String content;

    /** 消息类型：1=文本，2=图片，3=文件，4=系统提示 */
    @TableField("msg_type")
    private Integer msgType;

    /** 文件/图片地址 */
    @TableField("file_url")
    private String fileUrl;

    /** 文件大小 */
    @TableField("file_size")
    private Long fileSize;

    /** 原始文件名 */
    @TableField("file_name")
    private String fileName;

    /** 发送时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 是否撤回：0=否，1=是 */
    @TableField("is_recalled")
    private Integer isRecalled;
}
