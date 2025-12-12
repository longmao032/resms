import request from '@/utils/request';
import type { CommissionQuery} from './type';

enum Api {
  List = '/commission/list',
  Calculate = '/commission/calculate',
  Issue = '/commission/issue'
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