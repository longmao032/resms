// 通知类型定义

export interface WorkNotice {
    id?: number
    noticeTitle: string
    noticeContent: string
    contentType?: number
    noticeType: number
    priority: number
    receiverType?: number
    receiverIds?: string
    status?: number
    sendTime?: string
    readCount?: number
    senderName?: string
    routerPath?: string
    isRead?: number
}

export interface NoticeQueryParams {
    pageNum: number
    pageSize: number
    keyword?: string
    noticeType?: number
    isRead?: number
}
