import request from '@/utils/request';

/**
 * 获取销售报表
 * @param params { startDate, endDate }
 */
export function getSaleReport(params: any) {
    return request({
        url: '/report/sale',
        method: 'get',
        params
    });
}

/**
 * 获取财务报表
 * @param params { startDate, endDate }
 */
export function getFinancialReport(params: any) {
    return request({
        url: '/report/financial',
        method: 'get',
        params
    });
}

/**
 * 获取客户报表
 * @param params { startDate, endDate }
 */
export function getCustomerReport(params: any) {
    return request({
        url: '/report/customer',
        method: 'get',
        params
    });
}

export function exportSaleReport(params: any) {
    return request({
        url: '/report/sale/export',
        method: 'get',
        params,
        responseType: 'blob'
    });
}

export function exportFinancialReport(params: any) {
    return request({
        url: '/report/financial/export',
        method: 'get',
        params,
        responseType: 'blob'
    });
}

export function exportCustomerReport(params: any) {
    return request({
        url: '/report/customer/export',
        method: 'get',
        params,
        responseType: 'blob'
    });
}
