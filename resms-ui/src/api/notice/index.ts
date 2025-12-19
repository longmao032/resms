import request from '@/utils/request'
import type { WorkNotice, NoticeQueryParams } from './type'

// 获取通知列表
export function getNoticeList(params: NoticeQueryParams, config?: any) {
    return request({
        url: '/work-notice/list',
        method: 'get',
        params,
        ...(config || {})
    })
}

// 发送通知
export function sendNotice(data: WorkNotice, config?: any) {
    return request({
        url: '/work-notice/send',
        method: 'post',
        data,
        ...(config || {})
    })
}

// 标记已读
export function readNotice(id: number, config?: any) {
    return request({
        url: `/work-notice/read/${id}`,
        method: 'post',
        ...(config || {})
    })
}

// 删除通知
export function deleteNotice(id: number, config?: any) {
    return request({
        url: `/work-notice/${id}`,
        method: 'delete',
        ...(config || {})
    })
}

// 导出类型供外部使用
export * from './type'
