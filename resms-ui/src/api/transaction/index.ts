import request from '@/utils/request';
import type { TransactionQuery, TransactionPageResult } from './type';

import type {
  TransactionForm,
} from './type';

enum Api {
  List = '/transaction/list',
  Detail = '/transaction', // GET /{id}
  Update = '/transaction',  // PUT
  ManagerApprove = '/transaction/manager-approve',
  FinanceConfirm = '/transaction/finance-confirm',
  FinanceApplyFinish = '/transaction/finance-apply-finish',
  AdminFinishApprove = '/transaction/admin-finish-approve'
}

// 获取交易分页列表
export const reqTransactionList = (params: TransactionQuery) => {
  return request.get(Api.List, { params });
};

// 获取交易详情
export const reqGetTransactionDetail = (id: number) => {
  return request.get(`${Api.Detail}/${id}`);
};

// 更新交易信息
export const reqUpdateTransaction = (data: TransactionForm) => {
  return request.put(Api.Update, data);
};

// 新增交易
export const reqAddTransaction = (data: any) => {
  return request.post(Api.Update, data); // Contoller method is POST /transaction (same path as PUT update but POST)
};

// 经理审核（定金阶段）
export const reqManagerApprove = (id: number, approved: boolean, reason?: string) => {
  return request.put(`${Api.ManagerApprove}/${id}`, null, { params: { approved, reason } });
};

// 财务确认（尾款/完成阶段）
export const reqFinanceConfirm = (id: number, approved: boolean, reason?: string) => {
  return request.put(`${Api.FinanceConfirm}/${id}`, null, { params: { approved, reason } });
};

// 财务申请完成（需要管理员审核）
export const reqFinanceApplyFinish = (id: number, reason?: string) => {
  return request.put(`${Api.FinanceApplyFinish}/${id}`, null, { params: { reason } });
};

// 管理员完成审核
export const reqAdminFinishApprove = (id: number, approved: boolean, reason?: string) => {
  return request.put(`${Api.AdminFinishApprove}/${id}`, null, { params: { approved, reason } });
};

// 重新提交审核（销售将驳回的交易重新提交给经理审核）
export const reqResubmitAudit = (id: number) => {
  return request.put(`/transaction/resubmit/${id}`);
};

// 作废交易
export const reqVoidTransaction = (id: number, reason: string) => {
  return request.put(`/transaction/void/${id}`, null, { params: { reason } });
};