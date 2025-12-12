package com.guang.resms.common.exception;

import lombok.Getter;

@Getter
public enum HttpEnums {
    // 成功状态
    SUCCESS(200, "操作成功"),
    // 失败状态
    FAIL(-1, "操作失败"),

    // HTTP状态码
    CREATED(201, "资源创建成功"),
    NO_CONTENT(204, "请求成功但无内容"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "拒绝访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    // 业务状态码 - 用户相关
    USER_NOT_EXIST(1001, "用户不存在"),
    USERNAME_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_ACCOUNT_LOCKED(1003, "用户账号已锁定"),
    USER_ACCOUNT_EXPIRED(1004, "用户账号已过期"),
    USER_PERMISSION_DENIED(1005, "用户权限不足"),

    // 业务状态码 - 客户相关
    CUSTOMER_NOT_EXIST(2001, "客户不存在"),
    CUSTOMER_ALREADY_EXIST(2002, "客户已存在"),

    // 业务状态码 - 房源相关
    HOUSE_NOT_EXIST(3001, "房源不存在"),
    HOUSE_ALREADY_EXIST(3002, "房源已存在"),
    HOUSE_STATUS_ERROR(3003, "房源状态错误"),

    // 业务状态码 - 交易相关
    TRANSACTION_NOT_EXIST(4001, "交易不存在"),
    TRANSACTION_STATUS_ERROR(4002, "交易状态错误"),
    TRANSACTION_AMOUNT_ERROR(4003, "交易金额错误"),

    // 业务状态码 - 系统相关
    SYSTEM_BUSY(9001, "系统繁忙，请稍后再试"),
    PARAMETER_VALIDATION_ERROR(9002, "参数校验失败"),
    DATA_ACCESS_ERROR(9003, "数据访问错误"),
    NETWORK_ERROR(9004, "网络连接错误"),

    // 文件相关状态码
    FILE_UPLOAD_SUCCESS(5001, "文件上传成功"),
    FILE_UPLOAD_FAIL(5002, "文件上传失败"),
    FILE_NOT_FOUND(5003, "文件不存在"),
    FILE_SIZE_EXCEED(5004, "文件大小超出限制");

    private final Integer code;
    private final String message;

    HttpEnums(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
