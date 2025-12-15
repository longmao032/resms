import request from '@/utils/request'

/**
 * 获取会话列表
 */
export function getSessionList() {
    return request({
        url: '/chat/session/list',
        method: 'get'
    })
}

/**
 * 创建或获取私聊会话
 * @param targetUserId 目标用户ID
 */
export function createPrivateSession(targetUserId: number) {
    return request({
        url: '/chat/session/private',
        method: 'post',
        params: { targetUserId }
    })
}

/**
 * 获取消息历史
 * @param params { sessionId, page, size }
 */
export function getMessageList(params: { sessionId: number, page?: number, size?: number }) {
    return request({
        url: '/chat/message/list',
        method: 'get',
        params
    })
}

/**
 * 发送消息
 * @param data { sessionId, content, msgType, fileUrl }
 */
export function sendMessage(data: { sessionId: number, content: string, msgType: number, fileUrl?: string }) {
    return request({
        url: '/chat/message/send',
        method: 'post',
        data
    })
}

/**
 * 标记会话已读
 * @param sessionId 会话ID
 */
export function markAsRead(sessionId: number) {
    return request({
        url: '/chat/session/read',
        method: 'put',
        params: { sessionId }
    })
}

/**
 * 退出/移除会话
 */
export function removeSession(sessionId: number) {
    return request({
        url: `/chat/session/${sessionId}`,
        method: 'delete'
    })
}

/**
 * 上传聊天图片
 * @param file 图片文件
 */
export function uploadChatImage(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/chat/upload/image',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}
