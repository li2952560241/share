import request from '@/utils/request'

// 查询订单操作日志记录列表
export function listOrderLog(query) {
  return request({
    url: '/order/orderLog/list',
    method: 'get',
    params: query
  })
}

// 查询订单操作日志记录详细
export function getOrderLog(id) {
  return request({
    url: '/order/orderLog/' + id,
    method: 'get'
  })
}

// 新增订单操作日志记录
export function addOrderLog(data) {
  return request({
    url: '/order/orderLog',
    method: 'post',
    data: data
  })
}

// 修改订单操作日志记录
export function updateOrderLog(data) {
  return request({
    url: '/order/orderLog',
    method: 'put',
    data: data
  })
}

// 删除订单操作日志记录
export function delOrderLog(id) {
  return request({
    url: '/order/orderLog/' + id,
    method: 'delete'
  })
}
