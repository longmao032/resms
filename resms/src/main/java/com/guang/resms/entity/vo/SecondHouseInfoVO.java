package com.guang.resms.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 二手房扩展信息视图对象
 *
 * @author guang
 */
@Data
public class SecondHouseInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 二手房信息ID
     */
    private Integer id;

    /**
     * 房源ID
     */
    private Integer houseId;

    /**
     * 小区ID
     */
    private Integer communityId;

    /**
     * 小区名称
     */
    private String communityName;

    /**
     * 房龄（年）
     */
    private Integer houseAge;

    /**
     * 产权年限（年）
     */
    private Integer propertyRight;

    /**
     * 上次交易时间
     */
    private LocalDate lastTradeDate;

    /**
     * 抵押状态：0=无抵押，1=已抵押
     */
    private Integer mortgageStatus;

    /**
     * 抵押状态文本
     */
    private String mortgageStatusText;

    /**
     * 房本状态：0=无房本，1=有房本
     */
    private Integer certificateStatus;

    /**
     * 房本状态文本
     */
    private String certificateStatusText;

    /**
     * 备注
     */
    private String remark;
}
