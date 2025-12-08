package com.guang.resms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 *
 * @author guang
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
