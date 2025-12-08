package com.guang.resms.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
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
     * 楼盘名称
     */
    private String estateName;

    /**
     * 楼栋号
     */
    private String building;

    /**
     * 单元号
     */
    private String unit;

    /**
     * 房号
     */
    private String room;

    /**
     * 开盘时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate openingDate;

    /**
     * 交房时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    /**
     * 销售状态：0=待售，1=在售，2=售罄
     */
    private Integer saleStatus;

    /**
     * 销售状态文本
     */
    private String saleStatusText;

    /**
     * 备注
     */
    private String remark;
}
