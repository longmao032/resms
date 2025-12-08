package com.guang.resms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Mapper接口
 *
 * @author guang
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
