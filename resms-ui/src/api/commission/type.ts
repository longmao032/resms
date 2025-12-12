
// 查询参数
export interface CommissionQuery {
  pageNum: number;
  pageSize: number;
  transactionNo?: string;
  salesName?: string;
  status?: number;
}

// 列表 VO
export interface CommissionVO {
  id: number;
  transactionNo: string;
  dealPrice: number;
  salesName: string;
  commissionRate: number;
  amount: number;
  status: number; // 0=待核算，1=已核算，2=已发放
  financeName?: string;
  calculateTime?: string;
  issueTime?: string;
}

// 分页响应
export interface CommissionPageResult {
  records: CommissionVO[];
  total: number;
  size: number;
  current: number;
}
