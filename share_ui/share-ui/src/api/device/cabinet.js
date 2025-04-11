import request from '@/utils/request'

// 查询充电宝柜机列表
export function listCabinet(query) {
  return request({
    url: '/device/cabinet/list',
    method: 'get',
    params: query
  })
}

// 查询充电宝柜机详细
export function getCabinet(id) {
  return request({
    url: '/device/cabinet/' + id,
    method: 'get'
  })
}

// 新增充电宝柜机
export function addCabinet(data) {
  return request({
    url: '/device/cabinet',
    method: 'post',
    data: data
  })
}

// 修改充电宝柜机
export function updateCabinet(data) {
  return request({
    url: '/device/cabinet',
    method: 'put',
    data: data
  })
}

// 删除充电宝柜机
export function delCabinet(id) {
  return request({
    url: '/device/cabinet/' + id,
    method: 'delete'
  })
}

// 获取充电宝柜机全部详细信息
export function getAllInfo(id) {
  return request({
    url: '/device/cabinet/getAllInfo/' + id,
    method: 'get'
  })
}

// 设置站点与柜机关联时，根据关键字获取未绑定的柜机信息
export function searchNoUseList(keyword) {
  return request({
    url: '/device/cabinet/searchNoUseList/' + keyword,
    method: 'get'
  })
}