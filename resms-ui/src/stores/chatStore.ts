import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getSessionList, getMessageList, sendMessage, markAsRead, createPrivateSession, removeSession } from '@/api/chat'

export const useChatStore = defineStore('chat', () => {
    const sessionList = ref<any[]>([])
    const currentSession = ref<any>(null)
    const messageList = ref<any[]>([])
    const loading = ref(false)
    const sessionTimer = ref<any>(null)
    const messageTimer = ref<any>(null)

    // 计算总未读数
    const unreadTotal = computed(() => {
        return sessionList.value.reduce((sum, s) => sum + (s.unreadCount || 0), 0)
    })

    // 获取会话列表
    const fetchSessionList = async () => {
        try {
            const res = await getSessionList()
            if (res.data) {
                sessionList.value = res.data
                // 如果当前有选中的会话，更新其信息（如最新消息）
                if (currentSession.value) {
                    const current = sessionList.value.find(s => s.id === currentSession.value.id)
                    if (current) {
                        // 只更新必要字段，不完全替换以免丢失焦点等状态
                        currentSession.value.unreadCount = current.unreadCount
                        currentSession.value.lastMessageContent = current.lastMessageContent
                        currentSession.value.lastMessageTime = current.lastMessageTime
                    }
                }
            }
        } catch (error) {
            console.error('Failed to fetch session list:', error)
        }
    }

    // 移除会话（从当前用户列表中移除）
    const removeChatSession = async (sessionId: number) => {
        try {
            await removeSession(sessionId)
            // 若移除的是当前会话，清空并停止轮询
            if (currentSession.value && currentSession.value.id === sessionId) {
                currentSession.value = null
                messageList.value = []
                stopMessagePolling()
            }
            // 刷新会话列表
            await fetchSessionList()
        } catch (error) {
            console.error('Failed to remove session:', error)
            throw error
        }
    }

    // 选择会话
    const selectSession = async (session: any) => {
        currentSession.value = session
        messageList.value = [] // 切换时清空消息
        await fetchMessages(session.id)
        // 标记已读
        if (session.unreadCount > 0) {
            markSessionAsRead(session.id)
        }
        // 开启消息轮询
        startMessagePolling(session.id)
    }

    // 创建并选择会话
    const startPrivateChat = async (targetUserId: number) => {
        try {
            const res = await createPrivateSession(targetUserId)
            if (res.data) {
                const session = res.data
                // 刷新列表以包含新会有
                await fetchSessionList()
                // 选中该会话
                const targetSession = sessionList.value.find(s => s.id === session.id)
                if (targetSession) {
                    selectSession(targetSession)
                }
            }
        } catch (error) {
            console.error('Failed to create private session:', error)
        }
    }

    // 获取消息记录
    const fetchMessages = async (sessionId: number) => {
        loading.value = true
        try {
            // 目前只拉取第一页，后续可做滚动加载
            const res = await getMessageList({ sessionId, page: 1, size: 50 })
            if (res.data) {
                // 后端倒序返回，前端反转为正序显示
                const records = res.data.records || []
                messageList.value = records.reverse()
            }
        } catch (error) {
            console.error('Failed to fetch messages:', error)
        } finally {
            loading.value = false
        }
    }

    // 发送消息
    const sendMsg = async (content: string, msgType: number = 1) => {
        if (!currentSession.value) return
        try {
            const res = await sendMessage({
                sessionId: currentSession.value.id,
                content,
                msgType
            })
            if (res.data) {
                messageList.value.push(res.data)
                // 发送成功后刷新会话列表以更新最后消息
                fetchSessionList()
            }
        } catch (error) {
            console.error('Failed to send message:', error)
        }
    }

    // 标记已读
    const markSessionAsRead = async (sessionId: number) => {
        try {
            await markAsRead(sessionId)
            // 本地更新未读数
            if (currentSession.value && currentSession.value.id === sessionId) {
                currentSession.value.unreadCount = 0
            }
            const session = sessionList.value.find(s => s.id === sessionId)
            if (session) session.unreadCount = 0
        } catch (error) {
            console.error('Failed to mark as read:', error)
        }
    }

    // 轮询会话列表
    const startSessionPolling = () => {
        fetchSessionList()
        if (sessionTimer.value) clearInterval(sessionTimer.value)
        sessionTimer.value = setInterval(() => {
            fetchSessionList()
        }, 5000) // 5秒一次
    }

    // 停止会话轮询
    const stopSessionPolling = () => {
        if (sessionTimer.value) {
            clearInterval(sessionTimer.value)
            sessionTimer.value = null
        }
    }

    // 轮询当前会话消息
    const startMessagePolling = (sessionId: number) => {
        if (messageTimer.value) clearInterval(messageTimer.value)
        messageTimer.value = setInterval(async () => {
            if (currentSession.value && currentSession.value.id === sessionId) {
                // 增量获取或全量刷新，这里简单做全量刷新
                const res = await getMessageList({ sessionId, page: 1, size: 50 })
                if (res.data && res.data.records) {
                    const newMessages = res.data.records.reverse()
                    if (newMessages.length > messageList.value.length ||
                        (newMessages.length > 0 && messageList.value.length > 0 &&
                            newMessages[newMessages.length - 1].id !== messageList.value[messageList.value.length - 1].id)) {
                        messageList.value = newMessages
                        // 有新消息时，如果是接收方，应该标记已读
                        if (currentSession.value.unreadCount > 0) {
                            markSessionAsRead(sessionId)
                        }
                    }
                }
            }
        }, 3000) // 3秒一次
    }

    // 停止消息轮询
    const stopMessagePolling = () => {
        if (messageTimer.value) {
            clearInterval(messageTimer.value)
            messageTimer.value = null
        }
    }

    // 全部停止
    const stopPolling = () => {
        stopSessionPolling()
        stopMessagePolling()
    }

    return {
        sessionList,
        currentSession,
        messageList,
        loading,
        unreadTotal,
        fetchSessionList,
        selectSession,
        startPrivateChat,
        sendMsg,
        removeChatSession,
        startSessionPolling,
        stopSessionPolling,
        startMessagePolling,
        stopMessagePolling,
        stopPolling
    }
})
