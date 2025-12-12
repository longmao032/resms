-- ----------------------------
-- 在线聊天模块表结构设计
-- 包含：会话表、会话成员表、消息表
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 会话主表 (tb_chat_session)
-- 管理所有的聊天会话（包括私聊和群聊）
-- ----------------------------
DROP TABLE IF EXISTS `tb_chat_session`;
CREATE TABLE `tb_chat_session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_type` tinyint NOT NULL DEFAULT 1 COMMENT '会话类型：1=私聊(1:1)，2=群聊(Group)',
  `session_name` varchar(50) DEFAULT NULL COMMENT '会话名称（群聊时显示，私聊可为空或自动生成）',
  `last_message_content` varchar(500) DEFAULT NULL COMMENT '最后一条消息内容快照（用于列表展示）',
  `last_message_type` tinyint DEFAULT 1 COMMENT '最后一条消息类型：1=文本，2=图片，3=文件',
  `last_message_time` datetime DEFAULT NULL COMMENT '最后一条消息发送时间（用于排序）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_last_message_time`(`last_message_time` DESC) USING BTREE
) ENGINE=InnoDB COMMENT='聊天会话主表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 2. 会话成员表 (tb_chat_session_member)
-- 关联用户和会话，存储用户在会话中的个性化设置（如未读数）
-- ----------------------------
DROP TABLE IF EXISTS `tb_chat_session_member`;
CREATE TABLE `tb_chat_session_member` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名称快照（可选）',
  `unread_count` int NOT NULL DEFAULT 0 COMMENT '未读消息数',
  `is_top` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否，1=是',
  `is_disturb` tinyint NOT NULL DEFAULT 0 COMMENT '消息免打扰：0=否，1=是',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_session_user`(`session_id`, `user_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `fk_member_session` FOREIGN KEY (`session_id`) REFERENCES `tb_chat_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='会话成员关联表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 3. 聊天消息表 (tb_chat_message)
-- 存储实际的聊天记录
-- ----------------------------
DROP TABLE IF EXISTS `tb_chat_message`;
CREATE TABLE `tb_chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` bigint NOT NULL COMMENT '所属会话ID',
  `sender_id` int NOT NULL COMMENT '发送者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `msg_type` tinyint NOT NULL DEFAULT 1 COMMENT '消息类型：1=文本，2=图片，3=文件，4=系统提示',
  `file_url` varchar(500) DEFAULT NULL COMMENT '文件/图片地址（当类型不为1时使用）',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_name` varchar(255) DEFAULT NULL COMMENT '原始文件名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_recalled` tinyint NOT NULL DEFAULT 0 COMMENT '是否撤回：0=否，1=是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_time`(`session_id`, `create_time`) USING BTREE,
  CONSTRAINT `fk_message_session` FOREIGN KEY (`session_id`) REFERENCES `tb_chat_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='聊天消息表' ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
