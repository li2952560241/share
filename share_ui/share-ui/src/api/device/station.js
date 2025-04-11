import request from '@/utils/request'

// 查询站点列表
export function listStation(query) {
  return request({
    url: '/device/station/list',
    method: 'get',
    params: query
  })
}

// 查询站点详细
export function getStation(id) {
  return request({
    url: '/device/station/' + id,
    method: 'get'
  })
}

// 新增站点
export function addStation(data) {
  return request({
    url: '/device/station',
    method: 'post',
    data: data
  })
}

// 修改站点
export function updateStation(data) {
  return request({
    url: '/device/station',
    method: 'put',
    data: data
  })
}

// 删除站点
export function delStation(id) {
  return request({
    url: '/device/station/' + id,
    method: 'delete'
  })
}

// 新增站点
export function setData(data) {
  return request({
    url: '/device/station/setData',
    method: 'post',
    data: data
  })
}

// 查询站点列表
export function nearbyStation(latitude, longitude) {
  return request({
    url: '/device/device/nearbyStationList/'+latitude+'/'+longitude,
    method: 'get'
  })
}