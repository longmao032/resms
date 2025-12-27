export interface PaymentVO {
  id: number;
  transactionId: number;
  transactionNo: string;
  dealPrice?: number;
  customerName: string;
  houseNo: string;
  paymentType: number; // 1:定金 2:首付...
  flowType: number; // 1:收 2:支
  amount: number;
  paymentStatus: number; // 0:待确认 1:有效 2:作废
  paymentTime: string;
  paymentMethod: string;
  receiptNo: string;
  proofUrl: string;
  payerInfo: string;
  financeName: string;
  remark: string;
}

export interface PaymentQuery extends PageQuery {
  transactionId?: number;
  paymentStatus?: number;
  receiptNo?: string;
  dateRange?: [string, string]; // 前端组件使用数组，发送时需拆分
}

export interface PaymentForm {
  id?: number;
  transactionId: number | null;
  paymentType: number;
  flowType: number;
  amount: number;
  paymentMethod: string;
  paymentTime: string;
  proofUrl: string;
  payerInfo: string;
  remark: string;
}

// 分页基础接口
interface PageQuery {
  pageNum: number;
  pageSize: number;
}

// 新增统计相关接口
export interface PaymentStatisticsVO {
  totalAmount: number;
  todayAmount: number;
  monthAmount: number;
  pendingAmount: number;
  trendDates: string[];
  trendAmounts: number[];
  typePieData: { name: string; value: number }[];
  salesRanking: { salesName: string; totalAmount: number }[];
}