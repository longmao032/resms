package com.guang.resms.shared.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 系统操作日志表
 */
@Data
@TableName("tb_operation_log")
public class OperationLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    private Integer riskLevel;

    private String riskDimension;

    /**
     * 操作人ID
     */
    private Integer userId;

    /**
     * 操作人姓名
     */
    private String userRealName;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * 操作状态：0=失败，1=成功
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行时间（毫秒）
     */
    private Long executeTime;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
}