export interface TransactionQuery {
  pageNum: number;
  pageSize: number;
  transactionNo?: string;
  customerName?: string;
  status?: number; // 交易状态
}

// 2. 列表展示/详情 VO (对应后端 TransactionVO)
export interface TransactionVO {
  id: number;
  transactionNo: string;

  // 关联信息
  houseId?: number;
  houseNo: string;
  unitName: string;
  customerId?: number;
  customerName: string;
  customerPhone: string;
  salesId?: number;
  salesName: string;

  // 金额信息
  dealPrice: number;
  deposit: number;
  downPayment: number;
  loanAmount: number;
  tailAmount?: number;
  paidAmount?: number;
  finalPayment?: number; // 尾款（后端计算）
  canApplyFinish?: boolean;

  // 状态与时间
  loanStatus: number;   // 0=未申请，1=审核中，2=已放款，3=未通过
  status: number;       // 0=待付定金，1=已付定金，2=已付首付，3=已过户，4=已完成，5=已取消
  managerAudit: number; // 0=待审核，1=已通过，2=已驳回
  finishAudit?: number; // 0=未申请，1=待审核，2=已通过，3=已驳回
  createTime: string;
  updateTime?: string;
}

// 3. 编辑表单 DTO (对应后端 TransactionUpdateDTO)
export interface TransactionForm {
  id: number;
  transactionNo: string; // 仅用于展示，不可改
  dealPrice: number;
  deposit: number;
  downPayment: number;
  loanAmount: number;
  loanStatus: number;
  tailAmount?: number;
  paidAmount?: number;
  finalPayment?: number;
  canApplyFinish?: boolean;
  status: number;
  managerAudit: number;
  finishAudit?: number;
}

// 4. 分页响应数据结构
export interface TransactionPageResult {
  records: TransactionVO[];
  total: number;
  size: number;
  current: number;
}

// 5. 新增交易 DTO
export interface TransactionAddDTO {
  houseId: number;
  customerId: number;
  salesId: number;
  dealPrice: number;
  deposit?: number;
  depositTime?: string;
  downPayment?: number;
  downPaymentTime?: string;
  loanAmount?: number;
  loanStatus?: number;
}

