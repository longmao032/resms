package com.guang.resms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.User;
import com.guang.resms.entity.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 *
 * @author guang
 */
public interface UserService {

    /**
     * 分页查询用户列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param params 查询参数
     * @return 分页结果
     */
    Page<UserVO> getUserPage(Integer pageNum, Integer pageSize, Map<String, Object> params);

    /**
     * 获取用户信息详情
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserDetail(Integer userId);

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    List<UserVO> getAllUsers();

    /**
     * 新增用户
     *
     * @param user 用户信息
     */
    void saveUser(User user);

    /**
     * 更新用户
     *
     * @param user 用户信息
     */
    void updateUser(User user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Integer userId);

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID列表
     */
    void batchDeleteUsers(List<Integer> userIds);

    /**
     * 更新用户状态
     *
     * @param userIds 用户ID列表
     * @param status 状态
     */
    void updateUserStatus(List<Integer> userIds, Integer status);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     */
    void resetUserPassword(Integer userId);

    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void updateUserPassword(Integer userId, String oldPassword, String newPassword);

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @param excludeId 排除的用户ID（编辑时使用）
     * @return 是否可用
     */
    boolean isUsernameAvailable(String username, Integer excludeId);

    /**
     * 检查手机号是否可用
     *
     * @param phone 手机号
     * @param excludeId 排除的用户ID（编辑时使用）
     * @return 是否可用
     */
    boolean isPhoneAvailable(String phone, Integer excludeId);

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @param excludeId 排除的用户ID（编辑时使用）
     * @return 是否可用
     */
    boolean isEmailAvailable(String email, Integer excludeId);
}
