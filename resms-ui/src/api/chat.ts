import request from '@/utils/request'

/**
 * 获取会话列表
 */
export function getSessionList(config?: any) {
    return request({
        url: '/chat/session/list',
        method: 'get',
        ...(config || {})
    })
}

/**
 * 创建或获取私聊会话
 * @param targetUserId 目标用户ID
 */
export function createPrivateSession(targetUserId: number, config?: any) {
    return request({
        url: '/chat/session/private',
        method: 'post',
        params: { targetUserId },
        ...(config || {})
    })
}

/**
 * 获取消息历史
 * @param params { sessionId, page, size }
 */
export function getMessageList(params: { sessionId: number, page?: number, size?: number }, config?: any) {
    return request({
        url: '/chat/message/list',
        method: 'get',
        params,
        ...(config || {})
    })
}

/**
 * 发送消息
 * @param data { sessionId, content, msgType, fileUrl }
 */
export function sendMessage(data: { sessionId: number, content: string, msgType: number, fileUrl?: string }, config?: any) {
    return request({
        url: '/chat/message/send',
        method: 'post',
        data,
        ...(config || {})
    })
}

/**
 * 标记会话已读
 * @param sessionId 会话ID
 */
export function markAsRead(sessionId: number, config?: any) {
    return request({
        url: '/chat/session/read',
        method: 'put',
        params: { sessionId },
        ...(config || {})
    })
}

/**
 * 退出/移除会话
 */
export function removeSession(sessionId: number, config?: any) {
    return request({
        url: `/chat/session/${sessionId}`,
        method: 'delete',
        ...(config || {})
    })
}

/**
 * 上传聊天图片
 * @param file 图片文件
 */
export function uploadChatImage(file: File, config?: any) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/chat/upload/image',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        ...(config || {})
    })
}
