package com.guang.resms.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.module.user.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Mapper接口
 *
 * @author guang
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
