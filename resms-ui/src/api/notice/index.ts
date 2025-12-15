import request from '@/utils/request'
import type { WorkNotice, NoticeQueryParams } from './type'

// 获取通知列表
export function getNoticeList(params: NoticeQueryParams) {
    return request({
        url: '/work-notice/list',
        method: 'get',
        params
    })
}

// 发送通知
export function sendNotice(data: WorkNotice) {
    return request({
        url: '/work-notice/send',
        method: 'post',
        data
    })
}

// 标记已读
export function readNotice(id: number) {
    return request({
        url: `/work-notice/read/${id}`,
        method: 'post'
    })
}

// 删除通知
export function deleteNotice(id: number) {
    return request({
        url: `/work-notice/${id}`,
        method: 'delete'
    })
}

// 导出类型供外部使用
export * from './type'
