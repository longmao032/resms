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

// 菜单表单数据
export interface MenuFormData {
  id?: number
  menuName: string
  menuCode: string
  parentId: number
  menuType: number
  path?: string
  component?: string
  icon?: string
  sortOrder?: number
  description?: string
  status?: number
  visible?: number
  isCache?: number
  perms?: string
}

// 菜单排序项
export interface MenuSortItem {
  id: number
  sortOrder: number
}

// 状态更新参数
export interface MenuStatusParams {
  ids: number[]
  status: number
}

// 批量删除参数
export interface MenuBatchDeleteParams {
  ids: number[]
}

// 排序更新参数
export interface MenuSortParams {
  sortList: MenuSortItem[]
}

// 唯一性检查结果
export interface UniqueCheckResult {
  unique: boolean
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
