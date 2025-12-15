import type { ViewRecordQuery } from '@/api/viewRecord/type'

// 预约DTO
export interface AppointmentDTO {
    id?: number
    customerId?: number
    houseId?: number
    salesId?: number
    viewTime?: string
    appointType?: number
    customerFeedback?: string
    followAdvice?: string
    reason?: string // 取消原因
}

// 预约查询DTO
export interface AppointmentQueryDTO extends ViewRecordQuery {
    status?: number
    appointType?: number
}
