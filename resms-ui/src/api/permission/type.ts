// 权限查询参数
export interface PermissionQueryParams {
    pageNum: number;
    pageSize: number;
    permissionName?: string;
    permissionCode?: string;
    type?: number;
    status?: number;
}

// 权限保存参数
export interface PermissionSaveParams {
    id?: number;
    permissionName: string;
    permissionCode: string;
    parentId?: number;
    type: number;
    path?: string;
    component?: string;
    icon?: string;
    sort?: number;
    description?: string;
    status: number;
}

// 权限VO
export interface PermissionVO {
    id: number;
    permissionName: string;
    permissionCode: string;
    parentId?: number;
    type: number;
    typeText: string;
    path?: string;
    component?: string;
    icon?: string;
    sort: number;
    description?: string;
    status: number;
    statusText: string;
    createTime: string;
    updateTime?: string;
    children?: PermissionVO[];
}

// 权限分页响应
export interface PermissionPageVO {
    records: PermissionVO[];
    total: number;
    current: number;
    size: number;
    pages: number;
}

// 权限状态更新参数
export interface PermissionStatusParams {
    permissionIds: number[];
    status: number;
}

// 权限类型选项
export interface PermissionTypeOption {
    value: number;
    label: string;
}

// 父权限选项
export interface ParentPermissionOption {
    value: number;
    label: string;
    children?: ParentPermissionOption[];
}

// 菜单排序参数
export interface MenuSortParams {
    sortList: Array<{
        id: number;
        sortOrder: number;
    }>;
}
