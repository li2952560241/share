import request from '@/utils/request'

//删除接口
// 删除柜机类型
export function delCabinetType(id) {
  return request({
    url: '/device/cabinetType/' + id,
    method: 'delete'
  })
}

// 查询柜机类型详细
export function getCabinetType(id) {
  return request({
    url: '/device/cabinetType/' + id,
    method: 'get'
  })
}

// 修改柜机类型
export function updateCabinetType(data) {
  return request({
    url: '/device/cabinetType',
    method: 'put',
    data: data
  })
}

//柜机类型添加
export function addCabinetType(cabinetType) {
    return request({
      url: '/device/cabinetType',
      method: 'post',
      data: cabinetType
    })
  }

//柜机类型分页列表
export function listCabinetType(query) {
    return request({
      url: '/device/cabinetType/list',
      method: 'get',
      params: query
    })
  }

  // 添加柜机时要选择柜机类型，获取全部柜机类型列表
  export function getCabinetTypeList() {
    return request({
      url: '/device/cabinetType/getCabinetTypeList',
      method: 'get'
    })
  }