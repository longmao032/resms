package com.guang.resms.module.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.module.chat.entity.ChatSession;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

    @org.apache.ibatis.annotations.Select("SELECT s.* FROM tb_chat_session s " +
            "JOIN tb_chat_session_member m1 ON s.id = m1.session_id " +
            "JOIN tb_chat_session_member m2 ON s.id = m2.session_id " +
            "WHERE s.session_type = 1 " +
            "AND m1.user_id = #{userId1} AND m2.user_id = #{userId2} LIMIT 1")
    ChatSession selectPrivateSession(@org.apache.ibatis.annotations.Param("userId1") Integer userId1,
            @org.apache.ibatis.annotations.Param("userId2") Integer userId2);

    @org.apache.ibatis.annotations.Select("SELECT " +
            "s.id, s.session_type, s.session_name, s.last_message_content, s.last_message_type, s.last_message_time, " +
            "m.unread_count, m.is_top, m.is_disturb, " +
            "t.user_id as targetUserId, " +
            "u.real_name as targetUserName, " +
            "u.avatar as targetAvatar " +
            "FROM tb_chat_session s " +
            "JOIN tb_chat_session_member m ON s.id = m.session_id " +
            "LEFT JOIN tb_chat_session_member t ON s.id = t.session_id AND t.user_id != m.user_id AND s.session_type = 1 "
            +
            "LEFT JOIN tb_user u ON t.user_id = u.id " +
            "WHERE m.user_id = #{userId} " +
            "ORDER BY s.last_message_time DESC")
    java.util.List<com.guang.resms.module.chat.entity.vo.ChatSessionVO> selectUserSessions(
            @org.apache.ibatis.annotations.Param("userId") Integer userId);
}
