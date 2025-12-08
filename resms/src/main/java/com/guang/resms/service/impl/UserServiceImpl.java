package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.User;
import com.guang.resms.entity.UserRole;
import com.guang.resms.entity.vo.UserVO;
import com.guang.resms.mapper.UserMapper;
import com.guang.resms.mapper.UserRoleMapper;
import com.guang.resms.service.UserService;
import com.guang.resms.utils.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author guang
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    // 默认密码
    private static final String DEFAULT_PASSWORD = "123456";
    // 日期格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public Page<UserVO> getUserPage(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        // 创建分页对象
        Page<User> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 用户名查询
        if (params.containsKey("username") && StringUtils.hasText((String) params.get("username"))) {
            queryWrapper.like(User::getUsername, params.get("username"));
        }

        // 真实姓名查询
        if (params.containsKey("realName") && StringUtils.hasText((String) params.get("realName"))) {
            queryWrapper.like(User::getRealName, params.get("realName"));
        }

        // 手机号查询
        if (params.containsKey("phone") && StringUtils.hasText((String) params.get("phone"))) {
            queryWrapper.like(User::getPhone, params.get("phone"));
        }

        // 邮箱查询
        if (params.containsKey("email") && StringUtils.hasText((String) params.get("email"))) {
            queryWrapper.like(User::getEmail, params.get("email"));
        }

        // 角色类型查询
        if (params.containsKey("roleType") && params.get("roleType") != null) {
            queryWrapper.eq(User::getRoleType, params.get("roleType"));
        }

        // 状态查询
        if (params.containsKey("status") && params.get("status") != null) {
            queryWrapper.eq(User::getStatus, params.get("status"));
        }

        // 创建时间范围查询
        if (params.containsKey("createTimeBegin") && StringUtils.hasText((String) params.get("createTimeBegin"))) {
            queryWrapper.ge(User::getCreateTime, params.get("createTimeBegin"));
        }
        if (params.containsKey("createTimeEnd") && StringUtils.hasText((String) params.get("createTimeEnd"))) {
            queryWrapper.le(User::getCreateTime, params.get("createTimeEnd"));
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(User::getCreateTime);

        // 执行查询
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);

        // 转换为VO对象
        Page<UserVO> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOs = userPage.getRecords().stream()
            .map(this::convertToUserVO)
            .collect(Collectors.toList());

        resultPage.setRecords(userVOs);
        return resultPage;
    }

    @Override
    public UserVO getUserDetail(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return convertToUserVO(user);
    }

    @Override
    public List<UserVO> getAllUsers() {
        List<User> users = userMapper.selectList(
            new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1)  // 只查询启用状态的用户
                .orderByAsc(User::getUsername)
        );

        return users.stream()
            .map(this::convertToUserVO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        // 验证输入
        validateUserInput(user, null);


        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(user.getPassword());
        } else {
            // 如果没有提供密码，使用默认密码
            user.setPassword(DEFAULT_PASSWORD);
        }

        // 设置默认头像（如果没有设置）
        if (!StringUtils.hasText(user.getAvatar())) {
            user.setAvatar("/uploads/avatars/default.jpg");
        }

        // 保存用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new ServiceException("新增用户失败");
        }

        // 如果提供了角色类型，创建用户角色关联
        if (user.getRoleType() != null && user.getRoleType() > 0) {
            createUserRoleAssociation(user.getId(), user.getRoleType());
        }
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        // 验证输入
        validateUserInput(user, user.getId());

        // 如果提供了新密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(user.getPassword());
        } else {
            // 如果密码为空，不更新密码字段
            user.setPassword(null);
        }

        // 更新用户
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new ServiceException("更新用户失败");
        }

        // 更新用户角色关联
        if (user.getRoleType() != null && user.getRoleType() > 0) {
            updateUserRoleAssociation(user.getId(), user.getRoleType());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 不能删除系统管理员（假设ID为1的用户为系统管理员）
        if (userId == 1) {
            throw new ServiceException("不能删除系统管理员");
        }

        // 删除用户角色关联
        userRoleMapper.delete(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );

        // 删除用户
        int result = userMapper.deleteById(userId);
        if (result <= 0) {
            throw new ServiceException("删除用户失败");
        }
    }

    @Override
    @Transactional
    public void batchDeleteUsers(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        // 检查是否包含系统管理员
        if (userIds.contains(1)) {
            throw new ServiceException("不能删除系统管理员");
        }

        // 删除用户角色关联
        userRoleMapper.delete(
            new LambdaQueryWrapper<UserRole>()
                .in(UserRole::getUserId, userIds)
        );

        // 批量删除用户
        int result = userMapper.deleteBatchIds(userIds);
        if (result <= 0) {
            throw new ServiceException("批量删除用户失败");
        }
    }

    @Override
    @Transactional
    public void updateUserStatus(List<Integer> userIds, Integer status) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        // 检查是否包含系统管理员
        if (userIds.contains(1)) {
            throw new ServiceException("不能禁用系统管理员");
        }

        // 批量更新状态
        for (Integer userId : userIds) {
            User user = new User();
            user.setId(userId);
            user.setStatus(status);
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public void resetUserPassword(Integer userId) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 重置为默认密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(DEFAULT_PASSWORD);

        int result = userMapper.updateById(updateUser);
        if (result <= 0) {
            throw new ServiceException("重置密码失败");
        }
    }

    @Override
    @Transactional
    public void updateUserPassword(Integer userId, String oldPassword, String newPassword) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 验证原密码
        if (!oldPassword.equals(user.getPassword())) {
            throw new ServiceException("原密码错误");
        }

        // 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(newPassword);

        int result = userMapper.updateById(updateUser);
        if (result <= 0) {
            throw new ServiceException("密码修改失败");
        }
    }

    @Override
    public boolean isUsernameAvailable(String username, Integer excludeId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
            .eq(User::getUsername, username.trim());

        if (excludeId != null) {
            queryWrapper.ne(User::getId, excludeId);
        }

        return userMapper.selectCount(queryWrapper) == 0;
    }

    @Override
    public boolean isPhoneAvailable(String phone, Integer excludeId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
            .eq(User::getPhone, phone.trim());

        if (excludeId != null) {
            queryWrapper.ne(User::getId, excludeId);
        }

        return userMapper.selectCount(queryWrapper) == 0;
    }

    @Override
    public boolean isEmailAvailable(String email, Integer excludeId) {
        if (!StringUtils.hasText(email)) {
            return true; // 邮箱可以为空
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
            .eq(User::getEmail, email.trim().toLowerCase());

        if (excludeId != null) {
            queryWrapper.ne(User::getId, excludeId);
        }

        return userMapper.selectCount(queryWrapper) == 0;
    }

    /**
     * 创建用户角色关联
     */
    private void createUserRoleAssociation(Integer userId, Integer roleType) {
        // 这里简化处理，假设roleType直接对应角色ID
        // 实际项目中可能需要更复杂的映射逻辑
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleType);
        userRoleMapper.insert(userRole);
    }

    /**
     * 更新用户角色关联
     */
    private void updateUserRoleAssociation(Integer userId, Integer roleType) {
        // 删除现有关联
        userRoleMapper.delete(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );

        // 创建新关联
        createUserRoleAssociation(userId, roleType);
    }

    /**
     * 验证用户输入
     */
    private void validateUserInput(User user, Integer excludeId) {
        // 用户名验证
        if (!StringUtils.hasText(user.getUsername())) {
            throw new ServiceException("用户名不能为空");
        }
        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            throw new ServiceException("用户名长度必须在3-50个字符之间");
        }
        if (!user.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new ServiceException("用户名只能包含字母、数字和下划线");
        }
        if (!isUsernameAvailable(user.getUsername(), excludeId)) {
            throw new ServiceException("用户名已存在");
        }

        // 真实姓名验证
        if (!StringUtils.hasText(user.getRealName())) {
            throw new ServiceException("真实姓名不能为空");
        }
        if (user.getRealName().length() > 50) {
            throw new ServiceException("真实姓名长度不能超过50个字符");
        }

        // 手机号验证
        if (!StringUtils.hasText(user.getPhone())) {
            throw new ServiceException("手机号不能为空");
        }
        if (!user.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new ServiceException("请输入正确的手机号");
        }
        if (!isPhoneAvailable(user.getPhone(), excludeId)) {
            throw new ServiceException("手机号已存在");
        }

        // 邮箱验证
        if (StringUtils.hasText(user.getEmail())) {
            if (user.getEmail().length() > 255) {
                throw new ServiceException("邮箱长度不能超过255个字符");
            }
            if (!user.getEmail().matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
                throw new ServiceException("请输入正确的邮箱地址");
            }
            if (!isEmailAvailable(user.getEmail(), excludeId)) {
                throw new ServiceException("邮箱已存在");
            }
        }

        // 密码验证（新增时必需）
        if (excludeId == null && !StringUtils.hasText(user.getPassword())) {
            throw new ServiceException("密码不能为空");
        }

        // 角色类型验证
        if (user.getRoleType() == null || user.getRoleType() <= 0) {
            throw new ServiceException("请选择角色类型");
        }

        // 状态验证
        if (user.getStatus() == null || (user.getStatus() != 0 && user.getStatus() != 1)) {
            throw new ServiceException("状态值无效");
        }
    }

    /**
     * 将User实体转换为UserVO
     */
    private UserVO convertToUserVO(User user) {
        // 获取角色名称
        String roleName = getRoleNameByType(user.getRoleType());

        // 获取状态文本
        String statusText = user.getStatus() != null && user.getStatus() == 1 ? "正常" : "禁用";

        return UserVO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .phone(user.getPhone())
            .email(user.getEmail())
            .avatar(user.getAvatar())
            .roleType(user.getRoleType())
            .roleName(roleName)
            .status(user.getStatus())
            .statusText(statusText)
            .createTime(user.getCreateTime() != null ? user.getCreateTime().format(DATE_FORMATTER) : null)
            .updateTime(user.getUpdateTime() != null ? user.getUpdateTime().format(DATE_FORMATTER) : null)
            .lastLoginTime(null) // 这里可以根据需要添加最后登录时间字段
            .build();
    }

    /**
     * 根据角色类型获取角色名称
     */
    private String getRoleNameByType(Integer roleType) {
        if (roleType == null) {
            return "未知";
        }

        switch (roleType) {
            case 1: return "系统管理员";
            case 2: return "销售顾问";
            case 3: return "销售经理";
            case 4: return "财务人员";
            case 5: return "普通用户";
            default: return "未知角色";
        }
    }
}
