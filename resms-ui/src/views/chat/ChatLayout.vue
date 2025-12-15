<template>
  <div class="chat-container">
    <!-- 左侧会话列表 -->
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <el-input v-model="searchText" placeholder="搜索联系人/会话" prefix-icon="Search" />
      </div>
      <div class="session-list" v-loading="loading">
        <div v-for="session in filteredSessions" :key="session.id" class="session-item"
          :class="{ active: currentSession && currentSession.id === session.id }" @click="handleSelectSession(session)">
          <div class="avatar-wrapper">
            <el-avatar :size="40" :src="session.targetAvatar ? getImageUrl(session.targetAvatar) : defaultAvatar" />
            <div v-if="session.unreadCount > 0" class="unread-dot">{{ session.unreadCount > 99 ? '99+' :
              session.unreadCount }}</div>
          </div>
          <div class="session-info">
            <div class="session-top">
              <span class="session-name">{{ session.sessionName || session.targetUserName || '未知用户' }}</span>
              <span class="session-time">{{ formatTime(session.lastMessageTime) }}</span>
            </div>
            <div class="session-bottom">
              <span class="session-preview">{{ session.lastMessageContent || '暂无消息' }}</span>
            </div>
          </div>

          <div class="session-actions" @click.stop>
            <el-button circle type="danger" size="small" plain @click.stop="handleRemoveSession(session)">
              <el-icon>
                <Delete />
              </el-icon>
            </el-button>
          </div>
        </div>
        <el-empty v-if="filteredSessions.length === 0" description="暂无会话" :image-size="60" />
      </div>
    </div>

    <!-- 右侧聊天窗口 -->
    <div class="chat-main" v-if="currentSession">
      <div class="chat-header">
        <span class="header-title">{{ currentSession.sessionName || currentSession.targetUserName }}</span>
        <!-- 可以在这里加更多操作按钮 -->
      </div>

      <div class="chat-content" ref="msgListRef">
        <div v-for="msg in messageList" :key="msg.id" class="message-row" :class="{ 'message-self': isSelf(msg) }">
          <el-avatar :size="36" :src="getMessageAvatar(msg)" class="message-avatar" />
          <div class="message-bubble">
            <template v-if="msg.msgType === 2 || msg.type === 'image'">
              <img :src="msg.content" class="message-image" @load="scrollToBottom" />
            </template>
            <template v-else>
              <div class="message-text">{{ msg.content }}</div>
            </template>
          </div>
        </div>
      </div>

      <div class="chat-footer">
        <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleImageSelected" />
        <div class="footer-bar">
          <el-input
            v-model="inputContent"
            type="textarea"
            :rows="2"
            placeholder="Enter 发送，Shift+Enter 换行"
            resize="none"
            class="footer-input"
            @keydown="handleInputKeydown"
          />
          <div class="footer-actions">
            <el-button type="info" size="small" @click="openFilePicker">
              <el-icon>
                <Picture />
              </el-icon>
              图片
            </el-button>
            <el-button type="primary" size="small" @click="handleSend" :disabled="!inputContent.trim()">发送</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="chat-empty" v-else>
      <el-empty description="选择一个会话开始聊天" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useChatStore } from '@/stores/chatStore'
import { useUserStore } from '@/stores/userStore'
import { storeToRefs } from 'pinia'
import { Search, Picture, Delete } from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/image'
// Format time logic could be extracted
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'

const chatStore = useChatStore()
const userStore = useUserStore()
const { sessionList, currentSession, messageList, loading } = storeToRefs(chatStore)

const searchText = ref('')
const inputContent = ref('')
const msgListRef = ref<HTMLElement | null>(null)
const fileInput = ref<HTMLInputElement | null>(null)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// Current User ID
const currentUserId = computed(() => userStore.userInfo?.userId || userStore.userInfo?.id)

// Filtered sessions
const filteredSessions = computed(() => {
  if (!searchText.value) return sessionList.value
  const text = searchText.value.toLowerCase()
  return sessionList.value.filter(s =>
    (s.sessionName && s.sessionName.toLowerCase().includes(text)) ||
    (s.targetUserName && s.targetUserName.toLowerCase().includes(text))
  )
})

// 使用通用图片工具获取可访问 URL（兼容开发代理与生产地址）

// Get message avatar
const getMessageAvatar = (msg: any) => {
  // If it's self
  if (msg.senderId == currentUserId.value) {
    return getImageUrl(userStore.userInfo?.avatar) || defaultAvatar
  }
  // If it's the other person (in private chat)
  // Note: For group chat, we would need sender info in the message object or a member map
  if (currentSession.value && currentSession.value.targetUserId) {
    return getImageUrl(currentSession.value.targetAvatar) || defaultAvatar
  }
  return defaultAvatar
}

// Time formatter
const formatTime = (time: string) => {
  if (!time) return ''
  const date = dayjs(time)
  const now = dayjs()
  if (date.isSame(now, 'day')) {
    return date.format('HH:mm')
  } else if (date.isSame(now.subtract(1, 'day'), 'day')) {
    return '昨天'
  } else {
    return date.format('MM-DD')
  }
}

// Select session
const handleSelectSession = (session: any) => {
  chatStore.selectSession(session)
  scrollToBottom()
}

const handleRemoveSession = async (session: any) => {
  try {
    const name = session.sessionName || session.targetUserName || '该会话'
    await ElMessageBox.confirm(`确定移除 ${name} 吗？移除后将从会话列表消失。`, '提示', { type: 'warning' })
    await chatStore.removeChatSession(session.id)
    ElMessage.success('已移除')
  } catch (e) {
    // cancelled or failed
  }
}

// Check if message is from self (using loose equality for safety)
const isSelf = (msg: any) => {
  return msg.senderId == currentUserId.value
}

// Send message
const handleSend = async () => {
  const content = inputContent.value.trim()
  if (!content) return

  await chatStore.sendMsg(content)
  inputContent.value = ''
  scrollToBottom()
}

const handleInputKeydown = async (e: KeyboardEvent) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    await handleSend()
  }
}

// Open file picker
const openFilePicker = () => {
  fileInput.value?.click()
}

// Handle image selection and send as image message (msgType = 2)
const handleImageSelected = async (e: Event) => {
  const input = e.target as HTMLInputElement
  if (!input || !input.files || input.files.length === 0) return
  const file = input.files[0]
  console.log('Uploading file:', file.name, file.size, file.type)

  try {
    // 先上传图片获取URL
    const { uploadChatImage } = await import('@/api/chat')
    const uploadRes = await uploadChatImage(file)
    console.log('Upload response:', uploadRes)
    // 后端返回的 URL 可能在 data、message 或 data.url 字段
    let imageUrl = ''
    if (!uploadRes) imageUrl = ''
    else if (typeof uploadRes === 'string') imageUrl = uploadRes
    else if (uploadRes.data && typeof uploadRes.data === 'string') imageUrl = uploadRes.data
    else if (uploadRes.data && typeof uploadRes.data === 'object' && uploadRes.data.url) imageUrl = uploadRes.data.url
    else if ((uploadRes as any).message && typeof (uploadRes as any).message === 'string') imageUrl = (uploadRes as any).message

    if (imageUrl) {
      const fullImageUrl = getImageUrl(imageUrl)
      if (fullImageUrl) {
        await chatStore.sendMsg(fullImageUrl, 2)
        scrollToBottom()
        return
      }
    }

    console.error('Upload failed, no valid URL in response:', uploadRes)
    ElMessage.error('图片上传失败')
  } catch (err: any) {
    console.error('发送图片失败', err)
    const errMsg = err?.response?.data?.message || err?.message || '发送图片失败'
    ElMessage.error(errMsg)
  }

  // clear input so same file can be selected again
  input.value = ''
}

// Auto scroll to bottom
const scrollToBottom = () => {
  nextTick(() => {
    if (msgListRef.value) {
      msgListRef.value.scrollTop = msgListRef.value.scrollHeight
    }
  })
}

// Watch message list changes to scroll
watch(() => messageList.value.length, () => {
  scrollToBottom()
})

onMounted(() => {
  // 滚动到底部
  scrollToBottom()
  // 进入页面时拉取一次会话列表
  chatStore.fetchSessionList()
  // 如果有当前会话，确保消息轮询开启
  if (currentSession.value) {
    chatStore.startMessagePolling(currentSession.value.id)
  }
})

onUnmounted(() => {
  chatStore.stopMessagePolling()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 100px);
  /* Adjust based on layout header/padding */
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chat-sidebar {
  width: 280px;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
  background-color: #f7f7f7;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.session-list {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  display: flex;
  padding: 12px 15px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.session-actions {
  margin-left: 8px;
  flex-shrink: 0;
}

.session-item:hover {
  background-color: #eee;
}

.session-item.active {
  background-color: #e6f1fc;
}

.avatar-wrapper {
  position: relative;
  margin-right: 12px;
}

.unread-dot {
  position: absolute;
  top: -2px;
  right: -2px;
  background-color: #f56c6c;
  color: #fff;
  font-size: 10px;
  padding: 0 4px;
  border-radius: 10px;
  line-height: 14px;
  border: 1px solid #fff;
}

.session-info {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.session-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.session-name {
  font-weight: 500;
  color: #333;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-time {
  font-size: 12px;
  color: #999;
}

.session-bottom {
  display: flex;
  justify-content: space-between;
}

.session-preview {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.chat-empty {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.chat-header {
  height: 60px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
  font-size: 16px;
  font-weight: 500;
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}

.message-row {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}

.message-self {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 10px;
}

.message-bubble {
  max-width: 60%;
  padding: 10px 14px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  font-size: 14px;
  line-height: 1.5;
  word-break: break-all;
  position: relative;
  text-align: left;
}

.message-self .message-bubble {
  background-color: #95ec69;
  /* WeChat green */
  color: #000;
  text-align: right;
  /* Text align for self messages */
}

.message-image {
  max-width: 100%;
  border-radius: 6px;
  display: block;
}

.chat-footer {
  padding: 15px;
  border-top: 1px solid #e6e6e6;
}

.footer-bar {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.footer-input {
  flex: 1;
}

.footer-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
  justify-content: flex-end;
}
</style>
