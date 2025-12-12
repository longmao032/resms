import request from "@/utils/request";
import type { loginForm, loginResp } from "@/api/user/type";

enum API {
        LOGIN_URL = "/auth/login",
        USERINFO_URL = "/user/info",
        USER_PAGE_URL = "/user/page",
        USER_DETAIL_URL = "/user",
        USER_SAVE_URL = "/user",
        USER_UPDATE_URL = "/user",
        USER_DELETE_URL = "/user",
        USER_BATCH_DELETE_URL = "/user/batch",
        USER_STATUS_URL = "/user/status",
        USER_PASSWORD_URL = "/user/password",
        USER_RESET_PASSWORD_URL = "/user/reset-password",
        USER_CHECK_USERNAME_URL = "/user/check-username",
        USER_CHECK_PHONE_URL = "/user/check-phone",
        USER_CHECK_EMAIL_URL = "/user/check-email",
}

export const reqLogin = (data: loginForm) => request.post<any, loginResp>(API.LOGIN_URL, data);

export const reqUserInfo = () => request.get<any, loginResp>(API.USERINFO_URL);

// 用户管理相关接口
export const reqUserPage = (data: any) => request.post<any, any>(API.USER_PAGE_URL, data);

export const reqUserDetail = (id: number) => request.get<any, any>(`${API.USER_DETAIL_URL}/${id}`);

export const reqUserSave = (data: any) => request.post<any, any>(API.USER_SAVE_URL, data);

export const reqUserUpdate = (data: any) => request.put<any, any>(API.USER_UPDATE_URL, data);

export const reqUserDelete = (id: number) => request.delete<any, any>(`${API.USER_DELETE_URL}/${id}`);

export const reqUserBatchDelete = (data: any) => request.delete<any, any>(API.USER_BATCH_DELETE_URL, { data });

export const reqUserStatusUpdate = (data: any) => request.put<any, any>(API.USER_STATUS_URL, data);

export const reqUserPasswordUpdate = (data: any) => request.put<any, any>(API.USER_PASSWORD_URL, data);

export const reqUserPasswordReset = (id: number) => request.put<any, any>(`${API.USER_RESET_PASSWORD_URL}/${id}`);

export const reqCheckUsername = (params: any) => request.get<any, any>(API.USER_CHECK_USERNAME_URL, { params });

export const reqCheckPhone = (params: any) => request.get<any, any>(API.USER_CHECK_PHONE_URL, { params });

export const reqCheckEmail = (params: any) => request.get<any, any>(API.USER_CHECK_EMAIL_URL, { params });