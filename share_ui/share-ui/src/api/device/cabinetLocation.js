import request from '@/utils/request'

// 查询柜机位置列表
export function listCabinetLocation(query) {
  return request({
    url: '/device/cabinetLocation/list',
    method: 'get',
    params: query
  })
}

// 查询柜机位置详细
export function getCabinetLocation(id) {
  return request({
    url: '/device/cabinetLocation/' + id,
    method: 'get'
  })
}

// 新增柜机位置
export function addCabinetLocation(data) {
  return request({
    url: '/device/cabinetLocation',
    method: 'post',
    data: data
  })
}

// 修改柜机位置
export function updateCabinetLocation(data) {
  return request({
    url: '/device/cabinetLocation',
    method: 'put',
    data: data
  })
}

// 删除柜机位置
export function delCabinetLocation(id) {
  return request({
    url: '/device/cabinetLocation/' + id,
    method: 'delete'
  })
}
