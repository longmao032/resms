package com.guang.resms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 房源图片表
 */
@Data
@TableName("tb_house_image")
public class HouseImage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 图片ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 关联房源ID（对应tb_house表的id）
     */
    private Integer houseId;

    /**
     * 图片URL/本地路径（如"FC2024001/cover.jpg"或在线链接）
     */
    private String imageUrl;

    /**
     * 图片类型：1=封面图，2=室内图，3=户型图，4=环境图
     */
    private Integer imageType;

    /**
     * 排序序号（数字越小越靠前，用于前端展示顺序）
     */
    private Integer sortOrder;

    /**
     * 上传人ID（关联tb_user表，记录操作人）
     */
    private Integer uploadUserId;

    /**
     * 审核状态：0=待审核，1=已通过，2=已驳回
     */
    private Integer auditStatus;

    /**
     * 审核人ID（关联tb_user表，可为空）
     */
    private Integer auditUserId;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}