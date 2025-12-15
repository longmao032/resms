package com.guang.resms.module.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("tb_chat_session_member")
@Data
public class ChatSessionMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 会话ID */
    @TableField("session_id")
    private Long sessionId;

    /** 用户ID */
    @TableField("user_id")
    private Integer userId;

    /** 用户名称快照 */
    @TableField("user_name")
    private String userName;

    /** 未读消息数 */
    @TableField("unread_count")
    private Integer unreadCount;

    /** 是否置顶：0=否，1=是 */
    @TableField("is_top")
    private Integer isTop;

    /** 消息免打扰：0=否，1=是 */
    @TableField("is_disturb")
    private Integer isDisturb;

    /** 加入时间 */
    @TableField(value = "join_time", fill = FieldFill.INSERT)
    private LocalDateTime joinTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
