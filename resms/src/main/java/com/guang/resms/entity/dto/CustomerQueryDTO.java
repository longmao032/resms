// src/main/java/com/guang/resms/entity/dto/CustomerQueryDTO.java
package com.guang.resms.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerQueryDTO extends QueryDTO {
    /**
     * 客户姓名（模糊查询）
     */
    private String realName;

    /**
     * 客户电话（模糊查询）
     */
    private String phone;

    /**
     * 意向等级：1=高，2=中，3=低
     */
    private Integer intentionLevel;

    /**
     * 归属销售ID
     */
    private Integer salesId;
}