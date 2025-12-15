import request from '@/utils/request';
import type { CommissionQuery} from './type';

enum Api {
  List = '/commission/list',
  Calculate = '/commission/calculate',
  Issue = '/commission/issue',
  Create = '/commission/create',
  ByTransaction = '/commission/by-transaction'
}

// 获取列表
export const reqCommissionList = (params: CommissionQuery) => {
  return request.get(Api.List, { params });
};

// 核算
export const reqCalculateCommission = (id: number) => {
  return request.post(`${Api.Calculate}/${id}`);
};

// 发放
export const reqIssueCommission = (id: number) => {
  return request.post(`${Api.Issue}/${id}`);
};

export const reqGetCommissionByTransaction = (transactionId: number) => {
  return request.get(`${Api.ByTransaction}/${transactionId}`);
};

export const reqCreateCommissionByTransaction = (transactionId: number, rate: number) => {
  return request.post(`${Api.Create}/${transactionId}`, null, { params: { rate } });
};