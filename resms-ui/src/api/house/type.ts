/**
 * 房源管理相关类型定义
 */

// 房源查询参数
export interface HouseQueryParams {
  houseNo?: string
  layout?: string
  houseType?: number
  status?: number
  salesId?: number
  projectId?: number
  minArea?: number
  maxArea?: number
  minPrice?: number
  maxPrice?: number
  minFloor?: number
  maxFloor?: number
  orientation?: string
  decoration?: string
  pageNum?: number
  pageSize?: number
  orderBy?: string
  order?: string
}

// 房源表单数据
export interface HouseFormData {
  id?: number
  houseNo?: string
  area: number | null
  unitName?: string
  layout: string
  floor: number | null
  totalFloor: number | null
  orientation?: string
  decoration?: string
  price: number | null
  status?: number
  salesId?: number
  description?: string
  houseType: number | null
  buildingNo?: number
  projectId?: number
  roomNo?: string
}

// 房源详情数据
export interface HouseDetail {
  id: number
  houseNo: string
  area: number
  unitName?: string
  layout: string
  floor: number
  totalFloor: number
  orientation?: string
  decoration?: string
  price: number
  status: number
  statusText: string
  salesId?: number
  salesName?: string
  description?: string
  houseType: number
  houseTypeText: string
  buildingNo?: number
  projectId?: number
  projectName?: string
  province?: string
  city?: string
  district?: string
  address?: string
  fullAddress?: string
  roomNo?: string
  coverImage?: string
  images?: string[]
  projectInfo?: ProjectInfo
  createTime: string
  updateTime: string
  newHouseInfo?: NewHouseInfo
  secondHouseInfo?: SecondHouseInfo
}

// 项目信息
export interface ProjectInfo {
  id: number
  projectNo?: string
  projectName: string
  developer?: string
  propertyCompany?: string
  address?: string
  landArea?: number
  plotRatio?: number
  greeningRate?: number
  parkingRatio?: string
  totalHouses?: number
  propertyFee?: number
  buildingType?: string
  openingDate?: string
  deliveryDate?: string
  description?: string
  province?: string
  city?: string
  district?: string
  longitude?: number
  latitude?: number
  metroDistance?: number
  nearestMetro?: string
  schoolDistrict?: string
  businessCircle?: string
  coverImage?: string
  status?: number
  statusText?: string
  createTime?: string
  updateTime?: string
}

// 新房扩展信息
export interface NewHouseInfo {
  id: number
  houseId: number
  estateName?: string
  building?: string
  unit?: string
  room?: string
  openingDate?: string
  deliveryDate?: string
  saleStatus?: number
  saleStatusText?: string
  remark?: string
}

// 二手房扩展信息
export interface SecondHouseInfo {
  id: number
  houseId: number
  communityId?: number
  communityName?: string
  houseAge?: number
  propertyRight?: number
  lastTradeDate?: string
  mortgageStatus?: number
  mortgageStatusText?: string
  certificateStatus?: number
  certificateStatusText?: string
  remark?: string
}

// 分页结果
export interface PageResult<T> {
  total: number
  list: T[]
  pageNum: number
  pageSize: number
  pages: number
}

// API响应
export interface ApiResponse<T> {
  code: number
  status: boolean
  message: string
  data: T
}
