// 登录请求参数
export interface loginForm {
    username: string;
    password: string;
    rememberMe: boolean;
}

// 用户信息类型
export interface UserInfo {
    userId: number;
    username: string;
    realName: string;
    avatar: string;
    token: string;
    roleType: number;
    menus: Menu[];
    permissions: string[];
}

// 菜单类型
export interface Menu {
    id: number;
    name: string;
    code: string;
    type: string | null;
    parentId: number | null;
    path: string;
    component: string;
    icon: string;
    sortOrder: number;
    description: string | null;
    children: Menu[];
}

// 登录响应
export interface loginResp {
    code: number;
    status: boolean;
    message: string;
    data: UserInfo;
}

// 用户查询参数
export interface UserQueryParams {
    pageNum: number;
    pageSize: number;
    username?: string;
    realName?: string;
    phone?: string;
    email?: string;
    roleType?: number;
    status?: number;
    createTimeBegin?: string;
    createTimeEnd?: string;
}

// 用户保存参数
export interface UserSaveParams {
    id?: number;
    username: string;
    password?: string;
    confirmPassword?: string;
    realName: string;
    phone: string;
    email?: string;
    avatar?: string;
    roleType: number;
    status: number;
}

// 用户VO
export interface UserVO {
    id: number;
    username: string;
    realName: string;
    phone: string;
    email?: string;
    avatar?: string;
    roleType: number;
    roleName: string;
    status: number;
    statusText: string;
    createTime: string;
    updateTime?: string;
    lastLoginTime?: string;
}

// 用户分页响应
export interface UserPageVO {
    records: UserVO[];
    total: number;
    current: number;
    size: number;
    pages: number;
}

// 用户状态更新参数
export interface UserStatusParams {
    userIds: number[];
    status: number;
}

// 用户密码更新参数
export interface UserPasswordParams {
    userId: number;
    oldPassword: string;
    newPassword: string;
    confirmNewPassword: string;
}

// 角色选项
export interface RoleOption {
    value: number;
    label: string;
}
