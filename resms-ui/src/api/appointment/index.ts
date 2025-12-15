import request from '@/utils/request'
import type { AppointmentDTO, AppointmentQueryDTO } from './type'

// 新增预约
export function addAppointment(data: AppointmentDTO) {
    return request({
        url: '/view-record/appointment',
        method: 'post',
        data
    })
}

// 确认预约
export function confirmAppointment(id: number) {
    return request({
        url: `/view-record/appointment/confirm/${id}`,
        method: 'put'
    })
}

// 完成带看
export function completeAppointment(id: number, feedback: string) {
    return request({
        url: `/view-record/appointment/complete/${id}`,
        method: 'put',
        params: { feedback }
    })
}

// 取消预约
export function cancelAppointment(id: number, reason: string) {
    return request({
        url: `/view-record/appointment/cancel/${id}`,
        method: 'put',
        params: { reason }
    })
}

// 获取预约列表 (复用带看记录的分页查询，增加状态筛选)
export function getAppointmentPage(data: AppointmentQueryDTO) {
    return request({
        url: '/view-record/page',
        method: 'post',
        data
    })
}

// 获取客户列表（用于下拉选择）
export function getCustomerList() {
    return request({
        url: '/customer/page',
        method: 'post',
        data: { pageNum: 1, pageSize: 1000 }
    })
}

// 获取房源列表（用于下拉选择）
export function getHouseList() {
    return request({
        url: '/house/list',
        method: 'get',
        params: { pageNum: 1, pageSize: 1000 }
    })
}

// 获取销售列表（用于下拉选择）
export function getSalesList() {
    return request({
        url: '/user/listByRole/2',
        method: 'get'
    })
}

// 导出类型供外部使用
export * from './type'
