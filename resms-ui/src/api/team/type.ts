/**
 * 团队管理相关类型定义
 */

// 团队查询参数
export interface TeamQueryParams {
  teamName?: string
  pageNum?: number
  pageSize?: number
}

// 团队成员（用于详情展示）
export interface TeamMember {
  userId: number
  realName: string
  phone: string
}

// 团队详情数据
export interface TeamDetail {
  id: number
  teamName: string
  managerId: number | null
  managerName?: string
  managerPhone?: string
  memberCount: number
  members?: TeamMember[] // 详情时返回
  createTime: string
  updateTime: string
}

// 团队表单数据（新增/编辑）
export interface TeamFormData {
  id?: number
  teamName: string
  managerId?: number
  memberIds: number[] // 穿梭框绑定的成员ID集合
}

// 用户选项（用于下拉框/穿梭框）
export interface UserOption {
  id: number
  realName: string
  username?: string
}

// 分页结果
export interface PageResult<T> {
  total: number
  list: T[] // 注意：后端根据MyBatis Plus可能是records，这里需根据request封装情况适配
  pageNum: number
  pageSize: number
  pages: number
}

// API响应
export interface ApiResponse<T> {
  code: number
  status: boolean
  message: string
  data: T
}

// 团队业绩查询参数
export interface TeamPerformanceQuery {
  teamName?: string
  startTime?: string
  endTime?: string
}

// 团队业绩视图对象 (对应后端 TeamPerformanceVO)
export interface TeamPerformanceVO {
  rank?: number
  teamId: number
  teamName: string
  managerName: string
  memberCount: number
  performanceTarget: number
  actualPerformance: number
  completionRate: number // 百分比数值，如 85.5
  transactionCount: number
  commissionTotal?: number
}

// 业绩统计概览 (前端计算用)
export interface PerformanceSummary {
  totalTarget: number
  totalActual: number
  avgCompletionRate: number
  totalTransactions: number
}