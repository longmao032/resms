package com.guang.resms.module.auth.service;

import com.guang.resms.module.auth.entity.vo.LoginResponseVo;
import org.apache.ibatis.annotations.Param;

public interface AuthService {
    /**
     * 登录验证
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户信息，失败返回null
     */
    LoginResponseVo login(@Param("username") String username, @Param("password") String password);
}
