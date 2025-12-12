package com.guang.resms.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WorkNoticeVO {
    private Integer id;
    private String noticeTitle;
    private String noticeContent;
    private Integer noticeType; // 1=系统, 2=任务, 3=交易, 4=佣金, 5=团队
    private Integer priority; // 1=紧急, 2=普通, 3=低
    private Integer contentType; // 1=文本, 2=HTML

    // 发送信息
    private Integer senderId;
    private String senderName;
    private LocalDateTime sendTime;

    // 业务跳转
    private String bizType;
    private Integer bizId;
    private String routerPath;

    // 接收状态
    private Integer readCount; // 已读人数（管理员可见）
    private Integer totalReceivers; // 总接收人数（管理员可见）

    // 当前用户状态
    private Boolean isRead; // 当前用户是否已读
}