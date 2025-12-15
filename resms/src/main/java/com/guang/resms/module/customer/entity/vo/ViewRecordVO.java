package com.guang.resms.module.customer.entity.vo;

import com.guang.resms.module.customer.entity.ViewRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ViewRecordVO extends ViewRecord {
    // 客户姓名
    private String customerName;
    // 客户电话
    private String customerPhone;
    // 房源编号
    private String houseNo;
    // 房源描述或小区名
    private String houseDesc;
    // 销售姓名
    private String salesName;
    // 户型
    private String layout;
    // 房源类型
    private Integer houseType;
}