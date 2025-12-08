package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知阅读记录表
 */
@Data
@TableName("tb_notice_read")
public class NoticeRead implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 阅读记录ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 通知ID
     */
    private Integer noticeId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 阅读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;
}