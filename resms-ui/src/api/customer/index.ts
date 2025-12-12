// src/api/customer/index.ts
import request from "@/utils/request";
import type { Customer, CustomerQuery, CustomerPageResp } from "./type";

enum API {
  CUSTOMER_PAGE_URL = "/customer/page",
  CUSTOMER_URL = "/customer",
  CUSTOMER_BATCH_DELETE_URL = "/customer/batch",
}

// 分页查询客户
export const reqCustomerPage = (data: CustomerQuery) => 
  request.post<any, CustomerPageResp>(API.CUSTOMER_PAGE_URL, data);

// 获取客户详情
export const reqCustomerDetail = (id: number) => 
  request.get<any, Customer>(`${API.CUSTOMER_URL}/${id}`);

// 新增或更新客户
export const reqSaveOrUpdateCustomer = (data: Customer) => 
  request.post<any, boolean>(API.CUSTOMER_URL, data);

// 删除客户
export const reqDeleteCustomer = (id: number) => 
  request.delete<any, boolean>(`${API.CUSTOMER_URL}/${id}`);

// 批量删除客户
export const reqBatchDeleteCustomer = (ids: number[]) => 
  request.delete<any, boolean>(API.CUSTOMER_BATCH_DELETE_URL, { data: ids });