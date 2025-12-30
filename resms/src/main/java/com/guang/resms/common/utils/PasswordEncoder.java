package com.guang.resms.common.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 * 使用BCrypt算法进行密码加密和验证
 * 
 * @author guang
 */
@Component
public class PasswordEncoder {

    /**
     * BCrypt加密强度（工作因子），取值范围4-31
     * 推荐值：10-12
     * 值越大，加密越安全，但耗时越长
     */
    private static final int BCRYPT_ROUNDS = 10;

    /**
     * 加密明文密码
     * 
     * @param rawPassword 明文密码
     * @return 加密后的密码（包含盐值）
     */
    public String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * 验证明文密码是否与加密密码匹配
     * 
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return true=匹配，false=不匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            return false;
        }
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(rawPassword, encodedPassword);
        } catch (IllegalArgumentException e) {
            // 如果encodedPassword格式不正确，返回false
            return false;
        }
    }
}
