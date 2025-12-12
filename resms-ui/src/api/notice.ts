import request from '@/utils/request'

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
}

export function getNoticeList(params: any) {
    return request({
        url: '/work-notice/list',
        method: 'get',
        params
    })
}

export function sendNotice(data: WorkNotice) {
    return request({
        url: '/work-notice/send',
        method: 'post',
        data
    })
}

export function readNotice(id: number) {
    return request({
        url: `/work-notice/read/${id}`,
        method: 'post'
    })
}

export function deleteNotice(id: number) {
    return request({
        url: `/work-notice/${id}`,
        method: 'delete'
    })
}
