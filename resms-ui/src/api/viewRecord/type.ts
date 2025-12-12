// src/api/viewRecord/type.ts
export interface ViewRecord {
  id?: number;
  customerId: number;
  houseId: number;
  salesId: number;
  viewTime: string;
  customerFeedback: string;
  followAdvice: string;
  // VO字段，仅用于展示
  customerName?: string;
  houseNo?: string;
  salesName?: string;
  // 新增字段
  status?: number;
  appointType?: number;
  cancelReason?: string;
}

export interface ViewRecordQuery {
  pageNum: number;
  pageSize: number;
  customerId?: number;
  houseId?: number;
  startTime?: string;
  endTime?: string;
}

export interface ViewRecordPageResp {
  records: ViewRecord[];
  total: number;
  size: number;
  current: number;
  pages: number;
}