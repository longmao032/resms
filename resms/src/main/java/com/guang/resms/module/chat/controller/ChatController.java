package com.guang.resms.module.chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.utils.FileUploadUtils;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.chat.entity.ChatMessage;
import com.guang.resms.module.chat.entity.ChatSession;
import com.guang.resms.module.chat.entity.ChatSessionMember;
import com.guang.resms.module.chat.entity.vo.ChatSessionVO;
import com.guang.resms.module.chat.service.ChatMessageService;
import com.guang.resms.module.chat.service.ChatSessionMemberService;
import com.guang.resms.module.chat.service.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Value("${feature.chat.enabled:true}")
    private boolean chatEnabled;

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatSessionMemberService chatSessionMemberService;

    @Autowired
    private ChatMessageService chatMessageService;

    /**
     * 创建或获取私聊会话
     */
    @PostMapping("/session/private")
    public ResponseResult<ChatSession> createPrivateSession(@RequestParam Integer targetUserId) {
        if (!chatEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "聊天功能已禁用");
        }
        Integer userId = SecurityUtils.getUserId();
        if (userId == null) {
            return ResponseResult.fail("未登录");
        }
        if (userId.equals(targetUserId)) {
            return ResponseResult.fail("不能和自己聊天");
        }
        try {
            ChatSession session = chatSessionService.createPrivateSession(userId, targetUserId);
            return ResponseResult.success(session);
        } catch (Exception e) {
            log.error("创建会话失败", e);
            return ResponseResult.fail("创建会话失败");
        }
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/session/list")
    public ResponseResult<List<ChatSessionVO>> getSessionList() {
        if (!chatEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "聊天功能已禁用");
        }
        Integer userId = SecurityUtils.getUserId();
        if (userId == null) {
            return ResponseResult.fail("未登录");
        }
        return ResponseResult.success(chatSessionService.getUserSessions(userId));
    }

    /**
     * 获取消息历史
     */
    @GetMapping("/message/list")
    public ResponseResult<IPage<ChatMessage>> getMessageHistory(@RequestParam Long sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        if (!chatEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "聊天功能已禁用");
        }
        Integer userId = SecurityUtils.getUserId();
        // 校验用户是否在会话中
        long count = chatSessionMemberService.count(new LambdaQueryWrapper<ChatSessionMember>()
                .eq(ChatSessionMember::getSessionId, sessionId)
                .eq(ChatSessionMember::getUserId, userId));
        if (count == 0) {
            return ResponseResult.fail("您不在此会话中");
        }

        Page<ChatMessage> pageParams = new Page<>(page, size);
        IPage<ChatMessage> result = chatMessageService.page(pageParams,
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getSessionId, sessionId)
                        .orderByDesc(ChatMessage::getCreateTime)); // 倒序获取
        return ResponseResult.success(result);
    }

    /**
     * 发送消息
     */
    @PostMapping("/message/send")
    public ResponseResult<ChatMessage> sendMessage(@RequestBody ChatMessage message) {
        if (!chatEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "聊天功能已禁用");
        }
        Integer userId = SecurityUtils.getUserId();
        if (userId == null) {
            return ResponseResult.fail("未登录");
        }

        message.setSenderId(userId);
        message.setCreateTime(LocalDateTime.now());
        message.setIsRecalled(0);

        boolean saved = chatMessageService.save(message);
        if (saved) {
            // 更新会话最新消息
            ChatSession session = new ChatSession();
            session.setId(message.getSessionId());
            session.setLastMessageContent(message.getMsgType() == 1 ? message.getContent() : "[媒体消息]");
            session.setLastMessageType(message.getMsgType());
            session.setLastMessageTime(message.getCreateTime());
            chatSessionService.updateById(session);

            // 增加其他成员的未读数 (简单逻辑：除了发送者的其他所有成员+1)
            // 实际生产中可能需要更复杂的逻辑，比如排除当前在线的用户等
            chatSessionMemberService.update(new LambdaUpdateWrapper<ChatSessionMember>()
                    .eq(ChatSessionMember::getSessionId, message.getSessionId())
                    .ne(ChatSessionMember::getUserId, userId)
                    .setSql("unread_count = unread_count + 1"));

            return ResponseResult.success(message);
        }
        return ResponseResult.fail("发送失败");
    }

    /**
     * 标记会话已读
     */
    @PutMapping("/session/read")
    public ResponseResult<Boolean> markAsRead(@RequestParam Long sessionId) {
        if (!chatEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "聊天功能已禁用");
        }
        Integer userId = SecurityUtils.getUserId();
        boolean update = chatSessionMemberService.update(new LambdaUpdateWrapper<ChatSessionMember>()
                .eq(ChatSessionMember::getSessionId, sessionId)
                .eq(ChatSessionMember::getUserId, userId)
                .set(ChatSessionMember::getUnreadCount, 0));
        return ResponseResult.success(update);
    }

    /**
     * 退出/移除会话（仅从当前用户的会话列表中移除）
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseResult<Boolean> removeSession(@PathVariable Long sessionId) {
        if (!chatEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "聊天功能已禁用");
        }
        Integer userId = SecurityUtils.getUserId();
        if (userId == null) {
            return ResponseResult.fail("未登录");
        }

        long memberCount = chatSessionMemberService.count(new LambdaQueryWrapper<ChatSessionMember>()
                .eq(ChatSessionMember::getSessionId, sessionId)
                .eq(ChatSessionMember::getUserId, userId));
        if (memberCount == 0) {
            return ResponseResult.fail("您不在此会话中");
        }

        boolean removed = chatSessionMemberService.remove(new LambdaQueryWrapper<ChatSessionMember>()
                .eq(ChatSessionMember::getSessionId, sessionId)
                .eq(ChatSessionMember::getUserId, userId));

        if (removed) {
            long remain = chatSessionMemberService.count(new LambdaQueryWrapper<ChatSessionMember>()
                    .eq(ChatSessionMember::getSessionId, sessionId));
            if (remain == 0) {
                chatSessionService.removeById(sessionId);
            }
        }

        return ResponseResult.success(removed);
    }

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    /**
     * 上传聊天图片
     */
    @PostMapping("/upload/image")
    public ResponseResult<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (!chatEnabled) {
            return ResponseResult.fail(HttpEnums.FORBIDDEN.getCode(), "聊天功能已禁用");
        }
        Integer userId = SecurityUtils.getUserId();
        if (userId == null) {
            return ResponseResult.fail("未登录");
        }
        if (file.isEmpty()) {
            return ResponseResult.fail("文件不能为空");
        }
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String fileName = "chat_" + UUID.randomUUID().toString().replace("-", "") + ext;

            // 创建目录 - 使用绝对路径
            Path chatDir = Paths.get(FileUploadUtils.getUploadBasePath(), "chat");
            if (!Files.exists(chatDir)) {
                Files.createDirectories(chatDir);
            }

            // 保存文件 - 使用 Files.copy 替代 transferTo
            Path filePath = chatDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // 返回访问URL
            String url = "/uploads/chat/" + fileName;
            return ResponseResult.success(url);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return ResponseResult.fail("图片上传失败: " + e.getMessage());
        }
    }
}
