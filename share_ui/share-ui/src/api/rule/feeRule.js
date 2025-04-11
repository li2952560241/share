import request from '@/utils/request'

// 查询费用规则列表
export function listFeeRule(query) {
  return request({
    url: '/rule/feeRule/list',
    method: 'get',
    params: query
  })
}

// 查询费用规则详细
export function getFeeRule(id) {
  return request({
    url: '/rule/feeRule/' + id,
    method: 'get'
  })
}

// 新增费用规则
export function addFeeRule(data) {
  return request({
    url: '/rule/feeRule',
    method: 'post',
    data: data
  })
}

// 修改费用规则
export function updateFeeRule(data) {
  return request({
    url: '/rule/feeRule',
    method: 'put',
    data: data
  })
}

// 删除费用规则
export function delFeeRule(id) {
  return request({
    url: '/rule/feeRule/' + id,
    method: 'delete'
  })
}

//设置站点与费用规则关联时，获取全部费用规则信息
export function getALLFeeRuleList() {
  return request({
    url: '/rule/feeRule/getALLFeeRuleList',
    method: 'get'
  })
}
