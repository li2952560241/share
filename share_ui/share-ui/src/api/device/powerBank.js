import request from '@/utils/request'

// 删除充电宝
export function delPowerBank(id) {
    return request({
      url: '/device/powerBank/' + id,
      method: 'delete'
    })
  }
  
// 查询充电宝详细
export function getPowerBank(id) {
    return request({
      url: '/device/powerBank/' + id,
      method: 'get'
    })
  }
  
  // 修改充电宝
  export function updatePowerBank(data) {
    return request({
      url: '/device/powerBank',
      method: 'put',
      data: data
    })
  }

// 新增充电宝
export function addPowerBank(data) {
    return request({
      url: '/device/powerBank',
      method: 'post',
      data: data
    })
  }

// 查询充电宝列表
export function listPowerBank(query) {
  return request({
    url: '/device/powerBank/list',
    method: 'get',
    params: query
  })
}