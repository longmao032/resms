import request from "@/utils/request";

enum API {
        ROLE_PAGE_URL = "/role/page",
        ROLE_DETAIL_URL = "/role",
        ROLE_ALL_URL = "/role/all",
        ROLE_SAVE_URL = "/role",
        ROLE_UPDATE_URL = "/role",
        ROLE_DELETE_URL = "/role",
        ROLE_BATCH_DELETE_URL = "/role/batch",
        ROLE_ASSIGN_PERMISSIONS_URL = "/role/assign-permissions",
        ROLE_PERMISSIONS_URL = "/role",
        ROLE_CHECK_ROLE_NAME_URL = "/role/check-role-name",
        ROLE_CHECK_ROLE_CODE_URL = "/role/check-role-code",
}

// 角色管理相关接口
export const reqRolePage = (data: any) => request.post<any, any>(API.ROLE_PAGE_URL, data);

export const reqRoleDetail = (id: number) => request.get<any, any>(`${API.ROLE_DETAIL_URL}/${id}`);

export const reqRoleAll = () => request.get<any, any>(API.ROLE_ALL_URL);

export const reqRoleSave = (data: any) => request.post<any, any>(API.ROLE_SAVE_URL, data);

export const reqRoleUpdate = (data: any) => request.put<any, any>(API.ROLE_UPDATE_URL, data);

export const reqRoleDelete = (id: number) => request.delete<any, any>(`${API.ROLE_DELETE_URL}/${id}`);

export const reqRoleBatchDelete = (data: any) => request.delete<any, any>(API.ROLE_BATCH_DELETE_URL, { data });

export const reqRoleAssignPermissions = (data: any) => request.post<any, any>(API.ROLE_ASSIGN_PERMISSIONS_URL, data);

export const reqRolePermissions = (id: number) => request.get<any, any>(`${API.ROLE_PERMISSIONS_URL}/${id}/permissions`);

export const reqCheckRoleName = (params: any) => request.get<any, any>(API.ROLE_CHECK_ROLE_NAME_URL, { params });

export const reqCheckRoleCode = (params: any) => request.get<any, any>(API.ROLE_CHECK_ROLE_CODE_URL, { params });
