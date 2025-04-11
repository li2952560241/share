import request from '@/utils/request'

// 查询充电宝柜机列表
export function calculateAddress(latitude, longitude) {
  return request({
    url: '/device/map/calculateAddress/'+latitude+'/'+longitude,
    method: 'get'
  })
}

// 根据关键字查询经纬度
export function calculateLatLng(keyword) {
  return request({
    url: '/device/map/calculateLatLng/'+keyword,
    method: 'get'
  })
}



