import request from "@/utils/request";

enum API {
        PERMISSION_PAGE_URL = "/permission/page",
        PERMISSION_DETAIL_URL = "/permission",
        PERMISSION_TREE_URL = "/permission/tree",
        MENU_TREE_URL = "/permission/menu/tree",
        PERMISSION_SAVE_URL = "/permission",
        PERMISSION_UPDATE_URL = "/permission",
        PERMISSION_DELETE_URL = "/permission",
        PERMISSION_BATCH_DELETE_URL = "/permission/batch",
        PERMISSION_STATUS_URL = "/permission/status",
        PERMISSION_CHECK_PERMISSION_NAME_URL = "/permission/check-permission-name",
        PERMISSION_CHECK_PERMISSION_CODE_URL = "/permission/check-permission-code",
        PERMISSION_SORT_URL = "/permission/sort",
}

// 权限管理相关接口
export const reqPermissionPage = (data: any) => request.post<any, any>(API.PERMISSION_PAGE_URL, data);

export const reqPermissionDetail = (id: number) => request.get<any, any>(`${API.PERMISSION_DETAIL_URL}/${id}`);

export const reqPermissionTree = () => request.get<any, any>(API.PERMISSION_TREE_URL);

export const reqPermissionSave = (data: any) => request.post<any, any>(API.PERMISSION_SAVE_URL, data);

export const reqPermissionUpdate = (data: any) => request.put<any, any>(API.PERMISSION_UPDATE_URL, data);

export const reqPermissionDelete = (id: number) => request.delete<any, any>(`${API.PERMISSION_DELETE_URL}/${id}`);

export const reqPermissionBatchDelete = (data: any) => request.delete<any, any>(API.PERMISSION_BATCH_DELETE_URL, { data });

export const reqPermissionStatusUpdate = (data: any) => request.put<any, any>(API.PERMISSION_STATUS_URL, data);

export const reqCheckPermissionName = (params: any) => request.get<any, any>(API.PERMISSION_CHECK_PERMISSION_NAME_URL, { params });

export const reqCheckPermissionCode = (params: any) => request.get<any, any>(API.PERMISSION_CHECK_PERMISSION_CODE_URL, { params });

export const reqMenuSort = (data: any) => request.put<any, any>(API.PERMISSION_SORT_URL, data);

export const reqMenuTree = () => request.get<any, any>(API.MENU_TREE_URL);
