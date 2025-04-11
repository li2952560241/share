import request from '@/utils/request'

// 查询订单列表
export function listOrderInfo(query) {
  return request({
    url: '/order/orderInfo/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrderInfo(id) {
  return request({
    url: '/order/orderInfo/' + id,
    method: 'get'
  })
}

// 查询订单列表
export function listUserOrderInfo(userId) {
  return request({
    url: '/order/orderInfo/userList/' + userId,
    method: 'get'
  })
}

// 订单统计
export const getOrderStatisticsData = searchObj => {
  return request({
    url: `/order/orderInfo/getOrderStatisticsData`,
    method: 'get',
    params: searchObj,
  })
}
// 订单统计
export const getRegionOrderStatisticsData = searchObj => {
  return request({
    url: `/order/orderInfo/getRegionOrderStatisticsData`,
    method: 'get',
    params: searchObj,
  })
}
