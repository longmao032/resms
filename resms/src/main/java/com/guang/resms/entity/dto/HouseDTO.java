package com.guang.resms.entity.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 房源ID（更新时需要）
     */
    private Integer id;

    /**
     * 房源编号（自动生成，更新时不变）
     */
    private String houseNo;

    /**
     * 建筑面积（㎡）
     */
    @NotNull(message = "建筑面积不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "建筑面积必须大于0")
    @DecimalMax(value = "99999.99", message = "建筑面积不能超过99999.99㎡")
    private BigDecimal area;

    /**
     * 户型名称（如"A户型"）
     */
    private String unitName;

    /**
     * 户型（如"3室2厅"）
     */
    @NotBlank(message = "户型不能为空")
    @Size(max = 20, message = "户型长度不能超过20个字符")
    private String layout;

    /**
     * 所在楼层
     */
    @NotNull(message = "楼层不能为空")
    @Min(value = -5, message = "楼层不能低于-5层")
    @Max(value = 200, message = "楼层不能超过200层")
    private Integer floor;

    /**
     * 总楼层
     */
    @NotNull(message = "总楼层不能为空")
    @Min(value = 1, message = "总楼层必须大于0")
    @Max(value = 200, message = "总楼层不能超过200层")
    private Integer totalFloor;

    /**
     * 朝向（如"南北通透"）
     */
    @Size(max = 20, message = "朝向长度不能超过20个字符")
    private String orientation;

    /**
     * 装修情况（如"精装修"）
     */
    @Size(max = 20, message = "装修情况长度不能超过20个字符")
    private String decoration;

    /**
     * 挂牌价（元）
     */
    @NotNull(message = "挂牌价不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "挂牌价必须大于0")
    @DecimalMax(value = "999999999.99", message = "挂牌价不能超过999999999.99元")
    private BigDecimal price;

    /**
     * 状态：0=待审核，1=在售，2=已预订，3=已成交，4=下架
     */
    private Integer status;

    /**
     * 负责销售ID（关联tb_user表）
     */
    private Integer salesId;

    /**
     * 房源描述（如"近地铁、学区房"）
     */
    @Size(max = 500, message = "房源描述不能超过500个字符")
    private String description;

    /**
     * 房源类型：1=二手房，2=新房，3=租房
     */
    @NotNull(message = "房源类型不能为空")
    @Min(value = 1, message = "房源类型必须在1-3之间")
    @Max(value = 3, message = "房源类型必须在1-3之间")
    private Integer houseType;

    /**
     * 楼栋号
     */
    private Integer buildingNo;

    /**
     * 所属项目ID
     */
    private Integer projectId;

    /**
     * 房号（如"101"）
     */
    @Size(max = 20, message = "房号长度不能超过20个字符")
    private String roomNo;

    /**
     * 新房扩展信息（当houseType=2时必填）
     */
    private NewHouseInfoDTO newHouseInfo;

    /**
     * 二手房扩展信息（当houseType=1时必填）
     */
    private SecondHouseInfoDTO secondHouseInfo;

    /**
     * 房源图片列表
     */
    private List<String> images;

    /**
     * 新房扩展信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewHouseInfoDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 预售许可证号 */
        private String preSaleLicenseNo;

        /** 备案价 */
        private BigDecimal recordPrice;

        /** 产权年限 */
        private Integer propertyRightYears;

        /** 预计交房时间 */
        private String estimatedDeliveryTime;

        /** 交付标准 */
        private String deliveryStandard;

        /** 梯户比 */
        private String elevatorRatio;

        /** 得房率(%) */
        private BigDecimal actualAreaRate;
    }

    /**
     * 二手房扩展信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecondHouseInfoDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 建筑年代 */
        private String buildYear;

        /** 装修时间 */
        private String decorationTime;

        /** 是否满二：0=否，1=是 */
        private Integer isOverTwo;

        /** 是否满五：0=否，1=是 */
        private Integer isOverFive;

        /** 是否唯一住房：0=否，1=是 */
        private Integer isOnlyHouse;

        /** 抵押情况：0=否，1=是 */
        private Integer mortgageStatus;

        /** 房屋用途 */
        private String houseUsage;

        /** 描述 */
        private String description;
    }
}
