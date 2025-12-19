/**
 * 菜单管理API
 */
import request from '@/utils/request'
import type {
  MenuQueryParams,
  MenuStatusParams,
} from './type'

/**
 * 获取菜单树形列表
 */
export function getMenuTree(params?: MenuQueryParams) {
  return request({
    url: '/menu/tree',
    method: 'get',
    params
  })
}

/**
 * 更新菜单状态
 */
export function updateMenuStatus(data: MenuStatusParams) {
  return request({
    url: '/menu/status',
    method: 'put',
    data
  })
}
