import request from '@/utils/request'
import type {
  HouseQueryParams,
  HouseFormData,
  HouseDetail,
  PageResult,
  ApiResponse
} from './type'

/**
 * 分页查询房源列表
 */
export function getHouseList(params: HouseQueryParams) {
  return request<ApiResponse<PageResult<HouseDetail>>>({
    url: '/house/list',
    method: 'get',
    params
  })
}

/**
 * 获取房源详情
 */
export function getHouseById(id: number) {
  return request<ApiResponse<HouseDetail>>({
    url: `/house/${id}`,
    method: 'get'
  })
}

/**
 * 获取房源详情（别名）
 */
export const getHouseDetail = getHouseById

/**
 * 新增房源
 */
export function addHouse(data: HouseFormData) {
  return request<ApiResponse<void>>({
    url: '/house/add',
    method: 'post',
    data
  })
}

/**
 * 更新房源
 */
export function updateHouse(data: HouseFormData) {
  return request<ApiResponse<void>>({
    url: '/house/update',
    method: 'put',
    data
  })
}

/**
 * 删除房源
 */
export function deleteHouse(id: number) {
  return request<ApiResponse<void>>({
    url: `/house/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除房源
 */
export function batchDeleteHouse(ids: number[]) {
  return request<ApiResponse<number>>({
    url: '/house/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 更新房源状态
 */
export function updateHouseStatus(id: number, status: number) {
  return request<ApiResponse<void>>({
    url: `/house/status/${id}/${status}`,
    method: 'put'
  })
}

/**
 * 批量更新房源状态
 */
export function batchUpdateHouseStatus(ids: number[], status: number) {
  return request<ApiResponse<number>>({
    url: '/house/status/batch',
    method: 'put',
    params: { ids, status }
  })
}

/**
 * 分配房源给销售
 */
export function assignHouseToSales(id: number, salesId: number) {
  return request<ApiResponse<void>>({
    url: `/house/assign/${id}/${salesId}`,
    method: 'put'
  })
}

/**
 * 批量分配房源给销售
 */
export function batchAssignHouseToSales(ids: number[], salesId: number) {
  return request<ApiResponse<number>>({
    url: '/house/assign/batch',
    method: 'put',
    params: { ids, salesId }
  })
}

/**
 * 审核房源
 */
export function auditHouse(id: number, approved: boolean, reason?: string) {
  return request<ApiResponse<void>>({
    url: `/house/audit/${id}`,
    method: 'put',
    params: { approved, reason }
  })
}

/**
 * 分页查询房源（用于下拉选择器）
 */
export function reqHousePage(params: any) {
  return request<ApiResponse<PageResult<HouseDetail>>>({
    url: '/house/list',
    method: 'get',
    params
  })
}
