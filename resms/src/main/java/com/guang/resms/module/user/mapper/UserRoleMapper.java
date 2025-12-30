package com.guang.resms.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.module.user.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 *
 * @author guang
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID查询角色ID列表
     * 
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Integer> selectRoleIdsByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户ID查询主角色ID (取第一个)
     * 
     * @param userId 用户ID
     * @return 主角色ID
     */
    Integer selectPrimaryRoleIdByUserId(@Param("userId") Integer userId);

    /**
     * 查询所有管理员用户ID列表 (roleId=1)
     * 
     * @return 管理员用户ID列表
     */
    @org.apache.ibatis.annotations.Select("SELECT user_id FROM tb_user_role WHERE role_id = 1")
    List<Integer> selectAdminUserIds();
}
