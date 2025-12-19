/**
 * 菜单类型定义
 */

// 菜单树节点
export interface MenuTreeNode {
  id: number
  menuName: string
  menuCode: string
  parentId: number
  parentName?: string
  menuType: number
  menuTypeText?: string
  path?: string
  component?: string
  icon?: string
  sortOrder: number
  description?: string
  status: number
  statusText?: string
  visible?: number
  isCache?: number
  perms?: string
  createTime?: string
  updateTime?: string
  children?: MenuTreeNode[]
  hasChildren?: boolean
  level?: number
}

// 菜单查询参数
export interface MenuQueryParams {
  menuName?: string
  menuCode?: string
  menuType?: number
  status?: number
  parentId?: number
}

// 状态更新参数
export interface MenuStatusParams {
  ids: number[]
  status: number
}

// 菜单类型枚举
export enum MenuType {
  MENU = 1,    // 菜单
  BUTTON = 2,  // 按钮
  DATA = 3     // 数据权限
}

// 菜单状态枚举
export enum MenuStatus {
  DISABLED = 0, // 禁用
  ENABLED = 1   // 启用
}
