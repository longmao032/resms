package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_work_notice")
public class WorkNotice {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String noticeTitle;
    private String noticeContent;
    private Integer noticeType; // 1=系统, 2=任务, 3=交易, 4=佣金, 5=团队
    private Integer priority; // 1=紧急, 2=普通, 3=低
    private String bizType; // 关联业务类型
    private Integer bizId; // 关联业务ID
    private String routerPath; // 前端跳转路径
    private Integer senderId;
    private String senderName;
    private Integer receiverType; // 1=个人, 2=角色, 3=团队, 4=全体
    private String receiverIds; // JSON字符串
    private Integer status; // 1=已发送
    private LocalDateTime sendTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}