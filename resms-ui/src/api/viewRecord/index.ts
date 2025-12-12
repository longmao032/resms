// src/api/viewRecord/index.ts
import request from "@/utils/request";
import type { ViewRecord, ViewRecordQuery, ViewRecordPageResp } from "./type";

enum API {
  PAGE_URL = "/view-record/page",
  BASE_URL = "/view-record",
}

export const reqViewRecordPage = (data: ViewRecordQuery) => 
  request.post<any, ViewRecordPageResp>(API.PAGE_URL, data);

export const reqSaveViewRecord = (data: ViewRecord) => 
  request.post<any, boolean>(API.BASE_URL, data);

export const reqUpdateViewRecord = (data: ViewRecord) => 
  request.put<any, boolean>(API.BASE_URL, data);

export const reqDeleteViewRecord = (id: number) => 
  request.delete<any, boolean>(`${API.BASE_URL}/${id}`);