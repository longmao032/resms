// 角色查询参数
export interface RoleQueryParams {
    pageNum: number;
    pageSize: number;
    roleName?: string;
    roleCode?: string;
    status?: number;
}

// 角色保存参数
export interface RoleSaveParams {
    id?: number;
    roleName: string;
    roleCode: string;
    description?: string;
    dataScope: number;
    status: number;
}

// 角色VO
export interface RoleVO {
    id: number;
    roleName: string;
    roleCode: string;
    description?: string;
    dataScope: number;
    dataScopeText: string;
    status: number;
    statusText: string;
    createTime: string;
    updateTime?: string;
    userCount: number;
}

// 角色分页响应
export interface RolePageVO {
    records: RoleVO[];
    total: number;
    current: number;
    size: number;
    pages: number;
}

// 角色权限配置参数
export interface RolePermissionParams {
    roleId: number;
    permissionIds: number[];
}

// 数据权限范围选项
export interface DataScopeOption {
    value: number;
    label: string;
}
