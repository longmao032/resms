import request from '@/utils/request';
import type { PaymentVO, PaymentQuery, PaymentForm, PaymentStatisticsVO } from './type';

enum API {
  LIST = '/payment/list',
  ADD = '/payment',
  CONFIRM = '/payment', // + /{id}/confirm
  VOID = '/payment', // + /{id}/void
}

export const reqPaymentList = (params: PaymentQuery) => {
  // 处理日期范围
  const query = { ...params };
  if (params.dateRange && params.dateRange.length === 2) {
    query['startDate'] = params.dateRange[0];
    query['endDate'] = params.dateRange[1];
    delete query.dateRange;
  }
  return request.get<any, { records: PaymentVO[]; total: number }>(API.LIST, { params: query });
};

export const reqAddPayment = (data: PaymentForm) => {
  return request.post(API.ADD, data);
};

export const reqConfirmPayment = (id: number) => {
  return request.put(`${API.CONFIRM}/${id}/confirm`);
};

export const reqVoidPayment = (id: number, reason: string) => {
  return request.put(`${API.VOID}/${id}/void`, null, { params: { reason } });
};

export const reqUpdatePayment = (data: PaymentForm) => {
  return request.put('/payment', data);
};

// 收款统计
export const reqPaymentStatistics = () => {
  return request.get<any, any>('/payment/statistics');
};

// 上传收款凭证图片
export const reqUploadProof = (file: File, transactionId: number) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('transactionId', transactionId.toString());
  return request.post('/payment/uploadProof', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};