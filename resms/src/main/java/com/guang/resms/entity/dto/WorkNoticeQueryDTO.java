package com.guang.resms.entity.dto;

import lombok.Data;

@Data
public class WorkNoticeQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    // 基础筛选
    private String keyword; // 搜索标题
    private Integer noticeType; // 通知类型
    private Integer isRead; // 状态: 0=未读, 1=已读

    // 高级筛选（可选，预留）
    private Integer priority; // 优先级
    private String startTime; // 发送开始时间
    private String endTime; // 发送结束时间
}
