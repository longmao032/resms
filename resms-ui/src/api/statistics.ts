import request from '@/utils/request';

/**
 * 获取房源统计数据
 */
export function getHouseStats() {
    return request({
        url: '/statistics/house',
        method: 'get'
    });
}

/**
 * 获取客户统计数据
 */
export function getCustomerStats() {
    return request({
        url: '/statistics/customer',
        method: 'get'
    });
}

/**
 * 获取交易统计数据
 */
export function getTransactionStats() {
    return request({
        url: '/statistics/transaction',
        method: 'get'
    });
}

/**
 * 获取收款统计数据
 */
export function getPaymentStats() {
    return request({
        url: '/statistics/payment',
        method: 'get'
    });
}

export function getCommissionStats(params?: any) {
    return request({
        url: '/statistics/commission',
        method: 'get',
        params
    });
}
