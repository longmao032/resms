import request from '../request'

/**
 * 获取趋势统计数据
 * @param days 统计天数，默认7天
 */
export function getTrendStats(days = 7) {
  return request({
    url: `/statistics/trends?days=${days}`,
    method: 'get'
  })
}

/**
 * 获取待办事项统计
 */
export function getTodoStats() {
  return request({
    url: '/statistics/todos',
    method: 'get'
  })
}
