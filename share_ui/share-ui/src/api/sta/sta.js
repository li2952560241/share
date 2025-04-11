import request from '@/utils/request'

// 统计用户注册数据
export function getUserCount() {
  return request({
    url: '/sta/userCount',
    method: 'get'
  })
}

// 统计订单数据
export function getOrderCount(message) {
    return request({
      url: '/sta/orderData?message='+message,
      method: 'get'
    })
  }