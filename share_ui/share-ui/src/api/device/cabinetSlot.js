import request from '@/utils/request'

// 查询柜机插槽列表
export function listCabinetSlot(query) {
  return request({
    url: '/device/cabinetSlot/list',
    method: 'get',
    params: query
  })
}

// 查询柜机插槽详细
export function getCabinetSlot(id) {
  return request({
    url: '/device/cabinetSlot/' + id,
    method: 'get'
  })
}

// 新增柜机插槽
export function addCabinetSlot(data) {
  return request({
    url: '/device/cabinetSlot',
    method: 'post',
    data: data
  })
}

// 修改柜机插槽
export function updateCabinetSlot(data) {
  return request({
    url: '/device/cabinetSlot',
    method: 'put',
    data: data
  })
}

// 删除柜机插槽
export function delCabinetSlot(id) {
  return request({
    url: '/device/cabinetSlot/' + id,
    method: 'delete'
  })
}
