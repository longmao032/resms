package com.guang.resms.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.module.user.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper接口
 *
 * @author guang
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
