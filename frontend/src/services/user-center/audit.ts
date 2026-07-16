// @ts-ignore
/* eslint-disable */
import request from '@/plugins/globalRequest';

export type AuditRecord = {
  id: number;
  userId?: number;
  userAccount: string;
  username?: string;
  module: string;
  action: string;
  result: 'success' | 'warning' | 'error';
  ip?: string;
  client?: string;
  operator?: string;
  detail?: string;
  createTime?: string;
};

export type AuditListParams = {
  current?: number;
  pageSize?: number;
  id?: number;
  keyword?: string;
  userAccount?: string;
  username?: string;
  module?: string;
  action?: string;
  result?: string;
  ip?: string;
  operator?: string;
  createTimeStart?: string;
  createTimeEnd?: string;
};

export type AuditListResult = {
  records: AuditRecord[];
  total: number;
  size: number;
  current: number;
  pages: number;
};

/** 获取访问审计列表 GET /api/audit/list */
export async function getAuditList(params?: AuditListParams, options?: { [key: string]: any }) {
  return request<AuditListResult>('/api/audit/list', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 删除单条审计记录 POST /api/audit/delete */
export async function deleteAuditLog(id: number, options?: { [key: string]: any }) {
  return request<boolean>('/api/audit/delete', {
    method: 'POST',
    params: {
      id,
    },
    ...(options || {}),
  });
}

/** 批量删除审计记录 POST /api/audit/delete/batch */
export async function batchDeleteAuditLog(ids: number[], options?: { [key: string]: any }) {
  return request<boolean>('/api/audit/delete/batch', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: ids,
    ...(options || {}),
  });
}

/** 清空全部审计记录 POST /api/audit/clear */
export async function clearAuditLog(options?: { [key: string]: any }) {
  return request<boolean>('/api/audit/clear', {
    method: 'POST',
    ...(options || {}),
  });
}
