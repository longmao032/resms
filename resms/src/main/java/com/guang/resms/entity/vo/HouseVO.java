package com.guang.resms.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 房源视图对象
 *
 * @author guang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 房源ID
     */
    private Integer id;

    /**
     * 房源编号
     */
    private String houseNo;

    /**
     * 建筑面积（㎡）
     */
    private BigDecimal area;

    /**
     * 户型名称
     */
    private String unitName;

    /**
     * 户型
     */
    private String layout;

    /**
     * 所在楼层
     */
    private Integer floor;

    /**
     * 总楼层
     */
    private Integer totalFloor;

    /**
     * 朝向
     */
    private String orientation;

    /**
     * 装修情况
     */
    private String decoration;

    /**
     * 挂牌价（元）
     */
    private BigDecimal price;

    /**
     * 状态：0=待审核，1=在售，2=已预订，3=已成交，4=下架
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 负责销售ID
     */
    private Integer salesId;

    /**
     * 负责销售姓名
     */
    private String salesName;

    /**
     * 房源描述
     */
    private String description;

    /**
     * 房源类型：1=二手房，2=新房，3=租房
     */
    private Integer houseType;

    /**
     * 房源类型文本
     */
    private String houseTypeText;

    /**
     * 楼栋号
     */
    private Integer buildingNo;

    /**
     * 所属项目ID
     */
    private Integer projectId;

    /**
     * 所属项目名称
     */
    private String projectName;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区域
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 完整地址（省市区+详细地址+楼栋号+房号）
     */
    private String fullAddress;

    /**
     * 房号
     */
    private String roomNo;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 图片列表
     */
    private List<String> images;

    /**
     * 项目详细信息
     */
    private ProjectVO projectInfo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 扩展信息：新房信息
     */
    private NewHouseInfoVO newHouseInfo;

    /**
     * 扩展信息：二手房信息
     */
    private SecondHouseInfoVO secondHouseInfo;
}
