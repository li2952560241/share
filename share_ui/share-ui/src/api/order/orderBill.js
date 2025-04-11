import request from '@/utils/request'

// 查询订单账单列表
export function listOrderBill(query) {
  return request({
    url: '/order/orderBill/list',
    method: 'get',
    params: query
  })
}

// 查询订单账单详细
export function getOrderBill(id) {
  return request({
    url: '/order/orderBill/' + id,
    method: 'get'
  })
}

// 新增订单账单
export function addOrderBill(data) {
  return request({
    url: '/order/orderBill',
    method: 'post',
    data: data
  })
}

// 修改订单账单
export function updateOrderBill(data) {
  return request({
    url: '/order/orderBill',
    method: 'put',
    data: data
  })
}

// 删除订单账单
export function delOrderBill(id) {
  return request({
    url: '/order/orderBill/' + id,
    method: 'delete'
  })
}
