import request from '@/utils/request';

/**
 * 获取销售报表
 * @param params { startDate, endDate }
 */
export function getSaleReport(params: any) {
    return request({
        url: '/api/report/sale',
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
        url: '/api/report/financial',
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
        url: '/api/report/customer',
        method: 'get',
        params
    });
}
