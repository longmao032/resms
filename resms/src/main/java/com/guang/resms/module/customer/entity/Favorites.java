package com.guang.resms.module.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏表 实体类
 * 对应数据库表：tb_favorites
 */
@Data
@TableName("tb_favorites")
public class Favorites {


    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 收藏类型（枚举映射：project=项目，building=楼栋，layout=户型，unit=房源）
     */
    private TargetType targetType;

    /**
     * 收藏对象ID（关联对应类型的主键ID）
     */
    private Integer targetId;

    /**
     * 收藏时间（默认当前时间）
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 收藏类型枚举（与数据库 enum 完全对应，强类型约束）
     */
    public enum TargetType {
        project,   // 项目
        building,  // 楼栋
        layout,    // 户型
        unit       // 房源
    }

    /**
     * 唯一索引说明：user_id + target_type + target_id 联合唯一
     * （对应数据库 unique_favorite 索引，避免同一用户重复收藏同一对象）
     */
}