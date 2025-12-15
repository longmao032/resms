package com.guang.resms.module.house.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新房扩展信息视图对象
 *
 * @author guang
 */
@Data
public class NewHouseInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 新房信息ID
     */
    private Integer id;

    /**
     * 房源ID
     */
    private Integer houseId;

    /**
     * 预售许可证号
     */
    private String preSaleLicenseNo;

    /**
     * 备案价
     */
    private BigDecimal recordPrice;

    /**
     * 产权年限
     */
    private Integer propertyRightYears;

    /**
     * 预计交房时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate estimatedDeliveryTime;

    /**
     * 交付标准
     */
    private String deliveryStandard;

    /**
     * 梯户比
     */
    private String elevatorRatio;

    /**
     * 得房率(%)
     */
    private BigDecimal actualAreaRate;
}
