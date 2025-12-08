import request from '@/utils/request'

/**
 * 分页查询小区列表
 */
export const getCommunityPage = (data: any) => {
  return request.post('/community/page', data)
}

/**
 * 新增小区
 */
export const addCommunity = (data: any) => {
  return request.post('/community/add', data)
}

/**
 * 根据ID查询小区详情
 */
export const getCommunityById = (id: number) => {
  return request.get(`/community/${id}`)
}

/**
 * 更新小区
 */
export const updateCommunity = (data: any) => {
  return request.post('/community/update', data)
}

/**
 * 删除小区
 */
export const deleteCommunity = (id: number) => {
  return request.delete(`/community/${id}`)
}
