// src/api/customer/type.ts

// 客户实体接口
export interface Customer {
  id?: number;
  customerNo?: string;
  realName: string;
  phone: string;
  idCard?: string;
  demandArea?: number;
  demandPrice?: number;
  demandLayout?: string;
  demandAreaRegion?: string;
  intentionLevel: number; // 1=高，2=中，3=低
  salesId?: number;
  source?: string;
  createTime?: string;
  updateTime?: string;
}

// 查询参数接口
export interface CustomerQuery {
  pageNum: number;
  pageSize: number;
  realName?: string;
  phone?: string;
  intentionLevel?: number | string;
}

// 分页响应接口
export interface CustomerPageResp {
  records: Customer[];
  total: number;
  size: number;
  current: number;
  pages: number;
}