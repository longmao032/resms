package com.guang.resms;

import com.guang.resms.common.utils.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 密码加密功能测试
 */
@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testEncode() {
        String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("明文密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$"));
    }

    @Test
    public void testMatches() {
        String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 正确密码应该匹配
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));

        // 错误密码不应该匹配
        assertFalse(passwordEncoder.matches("wrong", encodedPassword));
    }

    @Test
    public void testMultipleEncodings() {
        String rawPassword = "123456";
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);

        // 同一密码每次加密结果应该不同（因为盐值不同）
        assertNotEquals(encoded1, encoded2);

        // 但都应该能验证成功
        assertTrue(passwordEncoder.matches(rawPassword, encoded1));
        assertTrue(passwordEncoder.matches(rawPassword, encoded2));
    }
}
