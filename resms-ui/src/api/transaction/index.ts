import request from '@/utils/request';
import type { TransactionQuery, TransactionPageResult } from './type';

import type {
  TransactionForm,
} from './type';

enum Api {
  List = '/transaction/list',
  Detail = '/transaction', // GET /{id}
  Update = '/transaction'  // PUT
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