package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 工作通知表
 */
@Data
@TableName("tb_work_notice")
public class WorkNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 通知标题
     */
    private String noticeTitle;

    /**
     * 通知内容
     */
    private String noticeContent;

    /**
     * 通知类型：1=系统通知，2=任务分配，3=交易提醒，4=佣金通知，5=团队通知
     */
    private Integer noticeType;

    /**
     * 发送人ID
     */
    private Integer senderId;

    /**
     * 发送人姓名
     */
    private String senderName;

    /**
     * 接收类型：1=指定用户，2=指定角色，3=指定团队，4=全部用户
     */
    private Integer receiverType;

    /**
     * 接收人ID列表（JSON数组格式）
     */
    @TableField("receiver_ids")
    private String receiverIds;

    /**
     * 优先级：1=紧急，2=普通，3=低
     */
    private Integer priority;

    /**
     * 已读人数
     */
    private Integer readCount;

    /**
     * 总接收人数
     */
    private Integer totalReceivers;

    /**
     * 状态：0=草稿，1=已发送，2=已撤回
     */
    private Integer status;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /**
     * 发送时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime sendTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}