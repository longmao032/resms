/**
 * 菜单管理API
 */
import request from '@/utils/request'
import type {
  MenuTreeNode,
  MenuQueryParams,
  MenuFormData,
  MenuStatusParams,
  MenuBatchDeleteParams,
  MenuSortParams,
  UniqueCheckResult
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
 * 获取菜单详情
 */
export function getMenuById(id: number) {
  return request({
    url: `/menu/${id}`,
    method: 'get'
  })
}

/**
 * 获取父菜单选择树
 */
export function getParentMenuTree(excludeId?: number) {
  return request({
    url: '/menu/parent-tree',
    method: 'get',
    params: {
      excludeId
    }
  })
}

/**
 * 新增菜单
 */
export function saveMenu(data: MenuFormData) {
  return request({
    url: '/menu',
    method: 'post',
    data
  })
}

/**
 * 更新菜单
 */
export function updateMenu(data: MenuFormData) {
  return request({
    url: '/menu',
    method: 'put',
    data
  })
}

/**
 * 删除菜单
 */
export function deleteMenu(id: number) {
  return request({
    url: `/menu/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除菜单
 */
export function batchDeleteMenus(data: MenuBatchDeleteParams) {
  return request({
    url: '/menu/batch',
    method: 'delete',
    data
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

/**
 * 更新菜单排序
 */
export function updateMenuSort(data: MenuSortParams) {
  return request({
    url: '/menu/sort',
    method: 'put',
    data
  })
}

/**
 * 检查菜单编码是否唯一
 */
export function checkMenuCode(menuCode: string, excludeId?: number): Promise<UniqueCheckResult> {
  return request({
    url: '/menu/check-code',
    method: 'get',
    params: {
      menuCode,
      excludeId
    }
  }).then((res: any) => res.data)
}

/**
 * 检查菜单名称是否唯一
 */
export function checkMenuName(
  menuName: string,
  parentId: number,
  excludeId?: number
): Promise<UniqueCheckResult> {
  return request({
    url: '/menu/check-name',
    method: 'get',
    params: {
      menuName,
      parentId,
      excludeId
    }
  }).then((res: any) => res.data)
}
