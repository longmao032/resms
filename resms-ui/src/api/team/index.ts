import request from '@/utils/request'
import type {
  TeamQueryParams,
  TeamFormData,
  TeamDetail,
  UserOption,
  PageResult,
  ApiResponse,
  TeamPerformanceVO     // 新增
} from './type'

/**
 * 分页查询团队列表
 */
export function getTeamList(params: TeamQueryParams) {
  return request<ApiResponse<PageResult<TeamDetail>>>({
    url: '/team/list', // 假设后端路由为 /team/list 或 /team
    method: 'get',
    params
  })
}

/**
 * 获取团队详情
 */
export function getTeamById(id: number) {
  return request<ApiResponse<TeamDetail>>({
    url: `/team/${id}`,
    method: 'get'
  })
}

/**
 * 新增团队
 */
export function addTeam(data: TeamFormData) {
  return request<ApiResponse<void>>({
    url: '/team',
    method: 'post',
    data
  })
}

/**
 * 更新团队
 */
export function updateTeam(data: TeamFormData) {
  return request<ApiResponse<void>>({
    url: '/team',
    method: 'put',
    data
  })
}

/**
 * 删除团队
 */
export function deleteTeam(id: number) {
  return request<ApiResponse<void>>({
    url: `/team/${id}`,
    method: 'delete'
  })
}

/**
 * 获取可用用户列表（用于选择经理或成员）
 * @param roleType 角色类型：2=销售经理, 3=销售顾问
 */
export function getEnableUsers(roleType: number) {
  return request<ApiResponse<UserOption[]>>({
    url: `/user/teamListByRole/${roleType}`,
    method: 'get'
  })
}

export interface TeamPerformanceQuery {
  teamName?: string
  startTime?: string
  endTime?: string
}

/**
 * 获取团队业绩统计列表
 */
export function getTeamPerformance(params: TeamPerformanceQuery) {
  return request<ApiResponse<TeamPerformanceVO[]>>({
    url: '/team/performance', // 对应后端 controller 的统计接口
    method: 'get',
    params
  })
}

